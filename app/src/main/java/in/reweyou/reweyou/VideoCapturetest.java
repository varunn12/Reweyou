package in.reweyou.reweyou;

/**
 * Created by Reweyou on 2/1/2016.
 */

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import in.reweyou.reweyou.media.CameraHelper;

public class VideoCapturetest extends AppCompatActivity implements OnClickListener, TextureView.SurfaceTextureListener {

    protected static final int RESULT_ERROR = 0x00000001;
    private static final int MAX_VIDEO_DURATION = 20 * 1000;
    private static final int ID_TIME_COUNT = 0x1006;
    private static final String TAG = VideoCapturetest.class.getSimpleName();
    private File mOutputFile;
    private TextureView mSurfaceView;
    private ImageView iv_cancel, iv_ok, iv_record, image;
    private TextView tv_counter;
    private boolean isRecording = false;

    private MediaRecorder mMediaRecorder;
    private Camera mCamera;

    private List<Size> mSupportVideoSizes;

    private String filePath;

    private boolean mIsRecording = false;
    private boolean shootingVideo = false;
    private boolean firstLoad = false;
    private RelativeLayout proceedContainer;
    private boolean activityPaused = false;
    private ImageView btn_cancel;
    private ImageView btn_proceed;
    private ImageView btn_flash;
    private Camera.Parameters parameters;
    private boolean isflashOn = false;
    private ImageView btn_swap_camera;
    private int currentCameraId;
    private CountDownTimer timer;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ID_TIME_COUNT:
                    if (mIsRecording) {
                        if (msg.arg1 > msg.arg2) {
                            // mTvTimeCount.setVisibility(View.INVISIBLE);
                            tv_counter.setText("00:00");
                            // stopRecord();
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

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_capture);

        initView();
        timer = new CountDownTimer(20000, 1000) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);

                tv_counter.setText("00:" + String.format("%02d", seconds));

                // recodeing code
            }

            public void onFinish() {
                //finish action

                tv_counter.setVisibility(View.GONE);
                if (isRecording) {
                    // BEGIN_INCLUDE(stop_release_media_recorder)

                    // stop recording and release camera
                    try {
                        mMediaRecorder.stop();  // stop the recording
                    } catch (RuntimeException e) {
                        // RuntimeException is thrown when stop() is called immediately after start().
                        // In this case the output file is not properly constructed ans should be deleted.
                        Log.d(TAG, "RuntimeException: stop() is called immediately after start()");
                        //noinspection ResultOfMethodCallIgnored
                        mOutputFile.delete();
                    }
                    releaseMediaRecorder(); // release the MediaRecorder object
                    mCamera.lock();         // take camera access back from MediaRecorder

                    // inform the user that recording has stopped
                    onVideoRecorded();
                    isRecording = false;
                    releaseCamera();
                    // END_INCLUDE(stop_release_media_recorder)

                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRecording = false;
        activityPaused = true;
        tv_counter.setVisibility(View.INVISIBLE);

        // if we are using MediaRecorder, release it first

        timer.cancel();
        releaseMediaRecorder();
        // release the camera immediately on pause event
        releaseCamera();
    }

    private void initView() {

        proceedContainer = (RelativeLayout) findViewById(R.id.proceedContainer);
        proceedContainer.setVisibility(View.GONE);

        btn_cancel = (ImageView) findViewById(R.id.btn_cancel);
        btn_proceed = (ImageView) findViewById(R.id.btn_proceed);
        btn_flash = (ImageView) findViewById(R.id.iv_flash);
        btn_swap_camera = (ImageView) findViewById(R.id.iv_swap_camera);

        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialogBox alertDialogBox = new AlertDialogBox(VideoCapturetest.this, "Alert", "Do you want to save the recording for future usage?", "Yes", "Discard") {
                    @Override
                    void onNegativeButtonClick(DialogInterface dialog) {
                        if (mOutputFile != null) {
                            mOutputFile.delete();
                            dialog.dismiss();
                            finish();
                        }
                    }

                    @Override
                    void onPositiveButtonClick(DialogInterface dialog) {
                        dialog.dismiss();
                        finish();
                    }
                };
                alertDialogBox.setCancellable(true);
                alertDialogBox.show();

            }
        });

        btn_proceed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOutputFile != null) {
                    if (mOutputFile.length() > 0) {
                        Intent i = new Intent(VideoCapturetest.this, PostReport.class);
                        i.putExtra("dataVideo", mOutputFile.getPath());
                        Log.d("pathhh", mOutputFile.getPath());
                        startActivity(i);
                    }
                }

            }
        });

        btn_flash.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null) {
                    mCamera.lock();
                    if (parameters != null) {
                        if (!isflashOn) {
                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            mCamera.setParameters(parameters);
                            isflashOn = true;
                            btn_flash.setImageResource(R.drawable.ic_flash_on_white_24px);
                        } else {
                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            mCamera.setParameters(parameters);
                            isflashOn = false;
                            btn_flash.setImageResource(R.drawable.ic_flash_off_white_24px);

                        }

                        mCamera.unlock();
                    }
                }
            }

        });

        mSurfaceView = (TextureView) findViewById(R.id.surfaceView);
        iv_record = (ImageView) findViewById(R.id.iv_record);
        iv_cancel = (ImageView) findViewById(R.id.iv_cancel);
        iv_ok = (ImageView) findViewById(R.id.iv_ok);
        image = (ImageView) findViewById(R.id.image);
       /* iv_record.setImageResource(R.mipmap.start);
        iv_record.setVisibility(View.VISIBLE);
        iv_ok.setVisibility(View.GONE);
        iv_cancel.setVisibility(View.GONE);*/
        tv_counter = (TextView) findViewById(R.id.timer);
        tv_counter.setVisibility(View.GONE);
        iv_cancel.setOnClickListener(this);
        iv_ok.setOnClickListener(this);
        iv_record.setOnClickListener(this);
        image.setOnClickListener(this);

        mSurfaceView.setSurfaceTextureListener(this);

    }

    private void exit(final int resultCode, final Intent data) {
        if (mIsRecording) {
            new AlertDialog.Builder(VideoCapturetest.this)
                    .setTitle("Video Recorder")
                    .setMessage("Do you want to exit?")
                    .setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    //  stopRecord();
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

    private void releaseCamera() {
        if (mCamera != null) {
            // release the camera for other applications
            mCamera.release();
            mCamera = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private boolean prepareVideoRecorder() {

        proceedContainer.setVisibility(View.GONE);
        // BEGIN_INCLUDE (configure_preview)
        mCamera = CameraHelper.getDefaultCameraInstance();
        mCamera.setDisplayOrientation(90);

        // We need to make sure that our preview and recording video size are supported by the
        // camera. Query camera to find all the sizes and choose the optimal size given the
        // dimensions of our preview surface.
        parameters = mCamera.getParameters();
        /*List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> mSupportedVideoSizes = parameters.getSupportedVideoSizes();*/
       /* Camera.Size optimalSize = CameraHelper.getOptimalVideoSize(mSupportedVideoSizes,
                mSupportedPreviewSizes, mSurfaceView.getWidth(), mSurfaceView.getHeight());
*/
        // Use the same size for recording profile.
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        profile.videoFrameWidth = 640;
        profile.audioCodec = MediaRecorder.AudioEncoder.AAC;
        profile.videoCodec = MediaRecorder.VideoEncoder.H264;
        profile.videoFrameHeight = 480;
        profile.videoBitRate = 850000;
        profile.audioBitRate = 20000;
        profile.audioChannels = 1;

        // likewise for the camera object itself.
        parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
        mCamera.setParameters(parameters);
        try {
            // Requires API level 11+, For backward compatibility use {@link setPreviewDisplay}
            // with {@link SurfaceView}
            mCamera.setPreviewTexture(mSurfaceView.getSurfaceTexture());
            mCamera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "Surface texture is unavailable or unsuitable" + e.getMessage());
            return false;
        }
        // END_INCLUDE (configure_preview)


        // BEGIN_INCLUDE (configure_media_recorder)
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);


        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setOrientationHint(90);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(profile);

        // Step 4: Set output file
        mOutputFile = CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_VIDEO);
        if (mOutputFile == null) {
            return false;
        }
        mMediaRecorder.setOutputFile(mOutputFile.getPath());


        // END_INCLUDE (configure_media_recorder)

        // Step 5: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            iv_record.setImageResource(R.drawable.solid_circle_red_white_border);


            // clear recorder configuration
            mMediaRecorder.reset();
            // release the recorder object
            mMediaRecorder.release();
            mMediaRecorder = null;
            if (mOutputFile != null)
                if (mOutputFile.length() == 0) {
                    mOutputFile.delete();
                    Log.d("dewjdbewdbw", "ewduweduiweduiwgdwedwedweu    Delete");
                }

            // Lock camera for later use i.e taking it back from MediaRecorder.
            // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
            mCamera.lock();
        }
    }

    public void onCaptureClick(View view) {

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        prepareVideoRecorder();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //openCamera();
        if (proceedContainer.getVisibility() != View.VISIBLE)
            if (activityPaused) {
                prepareVideoRecorder();
                activityPaused = false;
            }

    }

    @Override
    protected void onStart() {
        super.onStart();


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
                if (isRecording) {

                    timer.onFinish();
                    // BEGIN_INCLUDE(stop_release_media_recorder)

                    // stop recording and release camera
                   /* try {
                        mMediaRecorder.stop();  // stop the recording
                    } catch (RuntimeException e) {
                        // RuntimeException is thrown when stop() is called immediately after start().
                        // In this case the output file is not properly constructed ans should be deleted.
                        Log.d(TAG, "RuntimeException: stop() is called immediately after start()");
                        //noinspection ResultOfMethodCallIgnored
                        mOutputFile.delete();
                    }
                    releaseMediaRecorder(); // release the MediaRecorder object
                    mCamera.lock();         // take camera access back from MediaRecorder

                    // inform the user that recording has stopped
                    onVideoRecorded();
                    isRecording = false;
                    releaseCamera();
                    // END_INCLUDE(stop_release_media_recorder)*/

                } else {
                    tv_counter.setVisibility(View.VISIBLE);
                    timer.start();
                    // BEGIN_INCLUDE(prepare_start_media_recorder)

                    new MediaPrepareTask().execute(null, null, null);

                    // END_INCLUDE(prepare_start_media_recorder)

                }
                break;

            case R.id.image:
                Intent video = new Intent(VideoCapturetest.this, ImageCapture.class);
                startActivity(video);
                finish();
                break;

            default:
                break;
        }
    }

    private void onVideoRecorded() {
        proceedContainer.setVisibility(View.VISIBLE);
        iv_record.setVisibility(View.INVISIBLE);

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

    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            // initialize video camera
           /* if (prepareVideoRecorder()) {*/
            // Camera is available and unlocked, MediaRecorder is prepared,
            // now you can start recording
            mMediaRecorder.start();
            isRecording = true;
            /*} else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                return false;
            }*/
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                VideoCapturetest.this.finish();
            }

            // inform the user that recording has started
            //setCaptureButtonText("Stop");

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            iv_record.setImageResource(R.drawable.solid_circle_red_white_border_stop);
            btn_flash.setVisibility(View.INVISIBLE);
        }
    }

    private class SizeComparator implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            return rhs.width - lhs.width;
        }
    }

}
