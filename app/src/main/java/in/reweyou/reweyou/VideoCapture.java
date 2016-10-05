package in.reweyou.reweyou;

/**
 * Created by Reweyou on 2/1/2016.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VideoCapture extends Activity implements
        SurfaceHolder.Callback, OnClickListener {

    protected static final int RESULT_ERROR = 0x00000001;

    private static final int MAX_VIDEO_DURATION = 20 * 1000;
    private static final int ID_TIME_COUNT = 0x1006;

    private SurfaceView mSurfaceView;
    private ImageView iv_cancel, iv_ok, iv_record,image;
    private TextView tv_counter;

    private SurfaceHolder mSurfaceHolder;
    private MediaRecorder mMediaRecorder;
    private Camera mCamera;

    private List<Size> mSupportVideoSizes;

    private String filePath;

    private boolean mIsRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_capture);

        initView();
    }

    private void initView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        iv_record = (ImageView) findViewById(R.id.iv_record);
        iv_cancel = (ImageView) findViewById(R.id.iv_cancel);
        iv_ok = (ImageView) findViewById(R.id.iv_ok);
        image=(ImageView)findViewById(R.id.image);
        iv_record.setImageResource(R.mipmap.start);
        iv_record.setVisibility(View.VISIBLE);
        iv_ok.setVisibility(View.GONE);
        iv_cancel.setVisibility(View.GONE);
        tv_counter = (TextView) findViewById(R.id.timer);
        tv_counter.setVisibility(View.GONE);
        iv_cancel.setOnClickListener(this);
        iv_ok.setOnClickListener(this);
        iv_record.setOnClickListener(this);
        image.setOnClickListener(this);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
    }

    private void exit(final int resultCode, final Intent data) {
        if (mIsRecording) {
            new AlertDialog.Builder(VideoCapture.this)
                    .setTitle("Video Recorder")
                    .setMessage("Do you want to exit?")
                    .setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    stopRecord();
                                    if (resultCode == RESULT_CANCELED) {
                                        if (filePath != null)
                                            deleteFile(new File(filePath));
                                    }
                                    setResult(resultCode, data);
                                    finish();
                                }
                            })
                    .setNegativeButton("no",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                }
                            }).show();
            return;
        }
        if (resultCode == RESULT_CANCELED) {
            if (filePath != null)
                deleteFile(new File(filePath));
        }
        setResult(resultCode, data);
        finish();
    }

    private void deleteFile(File delFile) {
        if (delFile == null) {
            return;
        }
        final File file = new File(delFile.getAbsolutePath());
        delFile = null;
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (file.exists()) {
                    file.delete();
                }
            }
        }.start();
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case ID_TIME_COUNT:
                    if (mIsRecording) {
                        if (msg.arg1 > msg.arg2) {
                            // mTvTimeCount.setVisibility(View.INVISIBLE);
                            tv_counter.setText("00:00");
                            stopRecord();
                        } else {
                            tv_counter.setText("00:0" + (msg.arg2 - msg.arg1));
                            Message msg2 = mHandler.obtainMessage(ID_TIME_COUNT,
                                    msg.arg1 + 1, msg.arg2);
                            mHandler.sendMessageDelayed(msg2, 1000);
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private void openCamera() {
        try {
            this.mCamera = Camera.open();
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setRotation(90);
            System.out.println(parameters.flatten());
            parameters.set("orientation", "portrait");
            mCamera.setParameters(parameters);
            mCamera.lock();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                try {
                    mCamera.setDisplayOrientation(90);
                } catch (NoSuchMethodError e) {
                    e.printStackTrace();
                }
            }
            mSupportVideoSizes = parameters.getSupportedVideoSizes();
            if (mSupportVideoSizes == null || mSupportVideoSizes.isEmpty()) {
                String videoSize = parameters.get("video-size");
                Log.i("Reweyou", videoSize);
                mSupportVideoSizes = new ArrayList<Camera.Size>();
                if (!VideoCapture.isEmpty(videoSize)) {
                    String[] size = videoSize.split("x");
                    if (size.length > 1) {
                        try {
                            int width = Integer.parseInt(size[0]);
                            int height = Integer.parseInt(size[1]);
                            mSupportVideoSizes.add(mCamera.new Size(width,
                                    height));
                        } catch (Exception e) {
                            Log.e("Reweyou", e.toString());
                        }
                    }
                }
            }
            for (Size size : mSupportVideoSizes) {
                Log.i("Reweyou", size.width + "<>" + size.height);
            }
        } catch (Exception e) {
            Log.e("Reweyou", "Open Camera error\n" + e.toString());
        }
    }

    private boolean initVideoRecorder() {
        if (mCamera == null) {
            mCamera = Camera.open();
            mCamera.unlock();
        } else {
            mCamera.unlock();
        }
        mMediaRecorder = new MediaRecorder();

        mMediaRecorder.setCamera(mCamera);

        try {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            CamcorderProfile lowProfile = CamcorderProfile
                    .get(CamcorderProfile.QUALITY_LOW);
            CamcorderProfile hightProfile = CamcorderProfile
                    .get(CamcorderProfile.QUALITY_HIGH);
            if (lowProfile != null && hightProfile != null) {
                lowProfile.audioCodec = MediaRecorder.AudioEncoder.AAC;
                lowProfile.duration = hightProfile.duration;
                lowProfile.videoCodec = MediaRecorder.VideoEncoder.H264;
                lowProfile.videoFrameRate = hightProfile.videoFrameRate;
                lowProfile.videoBitRate = 1500000 > hightProfile.videoBitRate ? hightProfile.videoBitRate
                        : 1500000;
                if (mSupportVideoSizes != null && !mSupportVideoSizes.isEmpty()) {
                    int width = 640;
                    int height = 480;
                    Collections.sort(mSupportVideoSizes, new SizeComparator());
                    int lwd = mSupportVideoSizes.get(0).width;
                    for (Size size : mSupportVideoSizes) {
                        int wd = Math.abs(size.width - 640);
                        if (wd < lwd) {
                            width = size.width;
                            height = size.height;
                            lwd = wd;
                        } else {
                            break;
                        }
                    }
                    lowProfile.videoFrameWidth = width;
                    lowProfile.videoFrameHeight = height;
                }

                mMediaRecorder.setProfile(lowProfile);
            }
        } catch (Exception e) {
            try {
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (mSupportVideoSizes != null && !mSupportVideoSizes.isEmpty()) {
                Collections.sort(mSupportVideoSizes, new SizeComparator());
                Size size = mSupportVideoSizes.get(0);
                try {
                    mMediaRecorder.setVideoSize(size.width, size.height);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    mMediaRecorder.setVideoSize(640, 480);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        }

        File f = null;
        try {
            f = setUpVideoFile();
            filePath = f.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            filePath = null;
        }
        mMediaRecorder.setOutputFile(filePath);

        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            try {
                mMediaRecorder.setOrientationHint(90);
            } catch (NoSuchMethodError e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("VideoPreview",
                    "IllegalStateException preparing MediaRecorder: "
                            + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d("VideoPreview",
                    "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (Exception e) {
            releaseMediaRecorder();
            e.printStackTrace();
        }
        return true;
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void startRecord() {
        try {
            if (initVideoRecorder()) {
                mMediaRecorder.start();
                iv_record.setImageResource(R.mipmap.stop);
            } else {
                releaseMediaRecorder();
                iv_record.setImageResource(R.mipmap.start);
            }
            tv_counter.setVisibility(View.VISIBLE);
            tv_counter.setText("00:" + (MAX_VIDEO_DURATION / 1000));
            Message msg = mHandler.obtainMessage(ID_TIME_COUNT, 1,
                    MAX_VIDEO_DURATION / 1000);
            mHandler.sendMessage(msg);
            mIsRecording = true;
        } catch (Exception e) {
            showShortToast("problem while capturing video");
            e.printStackTrace();
            exit(RESULT_ERROR, null);
        }
    }

    private void stopRecord() {
        try {
            mMediaRecorder.stop();
        } catch (Exception e) {
            if (new File(filePath) != null
                    && new File(filePath).exists()) {
                new File(filePath).delete();
            }
        }
        releaseMediaRecorder();
        mCamera.lock();
        iv_record.setImageResource(R.drawable.abc_btn_radio_to_on_mtrl_000);
        mIsRecording = false;

        iv_record.setVisibility(View.GONE);
        iv_cancel.setVisibility(View.VISIBLE);
        iv_ok.setVisibility(View.VISIBLE);
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo(); // Since API level 9
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    @Override
    protected void onResume() {
        super.onResume();
        openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit(RESULT_CANCELED, null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.iv_ok:
                Intent data = new Intent();
                if (filePath != null) {
                    data.putExtra("videopath", filePath);
                }
                exit(RESULT_OK, data);
                break;

            case R.id.iv_cancel:
              //  exit(RESULT_CANCELED, null);
                initView();
                break;

            case R.id.iv_record:
                if (mIsRecording) {
                    stopRecord();
                } else {
                    startRecord();
                }
                break;

            case R.id.image:
                Intent video = new Intent(VideoCapture.this, ImageCapture.class);
                startActivity(video);
                finish();
                break;

            default:
                break;
        }
    }

    protected void showShortToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private File setUpVideoFile() throws IOException {

        File videoFile = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {

            File storageDir = new File(
                    Environment.getExternalStorageDirectory(), "Reweyou")
                    .getParentFile();

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }
            videoFile = File.createTempFile("VID"
                            + System.currentTimeMillis() + "_",
                    ".mp4", storageDir);
        } else {
            Log.v(getString(R.string.app_name),
                    "External storage is not mounted READ/WRITE.");
        }

        return videoFile;
    }

    private class SizeComparator implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            return rhs.width - lhs.width;
        }
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

}
