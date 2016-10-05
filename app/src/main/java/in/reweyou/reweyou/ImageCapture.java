package in.reweyou.reweyou;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import in.reweyou.reweyou.classes.AppLocationService;


public class ImageCapture extends Activity implements Callback,
        OnClickListener {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private Button flipCamera;
    private Button flashCameraButton;
    private Button captureImage;
    private ImageButton ibRetake;
    private Button video;
    private ImageButton ibUse;
    private Button feed;
    private String selectedImagePath;
    private int cameraId;
    private String mCurrentPhotoPath;
    private boolean flashmode = false;
    private int rotation;
    private Location location;
    AppLocationService appLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);
        appLocationService = new AppLocationService(
                ImageCapture.this);
        location = appLocationService
                .getLocation(LocationManager.GPS_PROVIDER);
        if (location!= null) {
            //
        }
        else {
            location=appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
            Log.d("Location", String.valueOf(location));
            if (location!=null) {
                //
            } else {
                showSettingsAlert();
            }
        }
        // camera surface view created  
        cameraId = CameraInfo.CAMERA_FACING_BACK;
        Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
        flipCamera = (Button) findViewById(R.id.flipCamera);
        flashCameraButton = (Button) findViewById(R.id.flash);
        captureImage = (Button) findViewById(R.id.captureImage);
        ibUse = (ImageButton) findViewById(R.id.ibUse);
        feed = (Button)findViewById(R.id.feed);
        video = (Button)findViewById(R.id.video);
        ibRetake = (ImageButton) findViewById(R.id.ibRetake);
        flipCamera.setTypeface(font);
        flashCameraButton.setTypeface(font);
        captureImage.setTypeface(font);
        feed.setTypeface(font);
        video.setTypeface(font);

        ibUse.setOnClickListener(this);
        video.setOnClickListener(this);
        flipCamera.setOnClickListener(this);
        feed.setOnClickListener(this);
        captureImage.setOnClickListener(this);
        ibRetake.setOnClickListener(this);
        flashCameraButton.setOnClickListener(this);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Camera.getNumberOfCameras() > 1) {
            flipCamera.setVisibility(View.VISIBLE);
        }
        if (!getBaseContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH)) {
            flashCameraButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
       if (!openCamera(CameraInfo.CAMERA_FACING_BACK)) {
            alertCameraDialog();
        }
    }

    private boolean openCamera(int id) {
        boolean result = false;
        cameraId = id;
        releaseCamera();
        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (camera != null) {
            try {
                setUpCamera(camera);
                camera.setErrorCallback(new ErrorCallback() {

                    @Override
                    public void onError(int error, Camera camera) {

                    }
                });
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
                releaseCamera();
            }
        }
        return result;
    }

    private void setUpCamera(Camera c) {
        if (camera != null) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            rotation = getWindowManager().getDefaultDisplay().getRotation();
            int degree = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degree = 0;
                    break;
                case Surface.ROTATION_90:
                    degree = 90;
                    break;
                case Surface.ROTATION_180:
                    degree = 180;
                    break;
                case Surface.ROTATION_270:
                    degree = 270;
                    break;

                default:
                    break;
            }

            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                // frontFacing
                rotation = (info.orientation + degree) % 330;
                rotation = (360 - rotation) % 360;
            } else {
                // Back-facing
                rotation = (info.orientation - degree + 360) % 360;
            }
            c.setDisplayOrientation(rotation);
            Parameters params = c.getParameters();

            showFlashButton(params);

            List<String> focusModes = params.getSupportedFlashModes();
            if (focusModes != null) {
                if (focusModes
                        .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    params.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }
            }

            params.setRotation(rotation);
        }
    }

    private void showFlashButton(Parameters params) {
        boolean showFlash = (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH) && params.getFlashMode() != null)
                && params.getSupportedFlashModes() != null
                && params.getSupportedFocusModes().size() > 1;

        flashCameraButton.setVisibility(showFlash ? View.VISIBLE
                : View.INVISIBLE);

    }

    private void releaseCamera() {
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.setErrorCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
            camera = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other
        // applications
        //releaseMediaRecorder();
        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // when on Pause, release camera in order to be used from other
        // applications
        //releaseMediaRecorder();
        if (!openCamera(CameraInfo.CAMERA_FACING_BACK)) {
            alertCameraDialog();
        }
    }



    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flash:
                flashOnButton();
                break;
            case R.id.flipCamera:
                flipCamera();
                break;
            case R.id.captureImage:
                camera.takePicture(null, null, mPicture);
                captureImage.setVisibility(View.GONE);
                ibRetake.setVisibility(View.VISIBLE);
                ibUse.setVisibility(View.VISIBLE);
                feed.setVisibility(View.GONE);
                break;
            case R.id.ibUse:
                send();
                break;
            case R.id.feed:
                Intent in = new Intent(ImageCapture.this, Feed.class);
                startActivity(in);
                finish();
                break;
            case R.id.ibRetake:
                selectedImagePath = getRealPathFromURI(mCurrentPhotoPath);
                deleteFile(new File(selectedImagePath));
                camera.startPreview();
                captureImage.setVisibility(View.VISIBLE);
                ibRetake.setVisibility(View.GONE);
                ibUse.setVisibility(View.GONE);
                feed.setVisibility(View.VISIBLE);
                break;
            case R.id.video:
                Intent video = new Intent(ImageCapture.this, VideoCapture.class);
                startActivity(video);
                finish();
                break;
            default:
                break;
        }
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

    private File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Reweyou");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Reweyou", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getAbsolutePath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");

        mCurrentPhotoPath = "file:" + mediaFile.getAbsolutePath();
        return mediaFile;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            // Display on screen..send to LoSt server
            FileOutputStream outStream = null;
            File pictureFile = getOutputMediaFile();
            // Write to SD Card
            try {
                outStream = new FileOutputStream(pictureFile);
                outStream.write(data);
                outStream.flush();
                outStream.close();

                Log.d("TAG", "onPictureTaken - wrote bytes:  to " + data.length + "" + mCurrentPhotoPath);

                refreshGallery(pictureFile);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    public void send() {
        Intent in = new Intent(ImageCapture.this, ImageActivity.class);
        in.putExtra("path",mCurrentPhotoPath);
        startActivity(in);
        finish();
    }
    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }
    private void flipCamera() {
        int id = (cameraId == CameraInfo.CAMERA_FACING_BACK ? CameraInfo.CAMERA_FACING_FRONT
                : CameraInfo.CAMERA_FACING_BACK);
        if (!openCamera(id)) {
            alertCameraDialog();
        }
    }

    private void alertCameraDialog() {
        AlertDialog.Builder dialog = createAlert(ImageCapture.this,
                "Camera info", "error to open camera");
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        dialog.show();
    }

    private Builder createAlert(Context context, String title, String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(
                new ContextThemeWrapper(context,
                        android.R.style.Theme_Holo_Light_Dialog));
        dialog.setIcon(R.mipmap.ic_launcher);
        if (title != null)
            dialog.setTitle(title);
        else
            dialog.setTitle("Information");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        return dialog;

    }

    private void flashOnButton() {
        if (camera != null) {
            try {
                Parameters param = camera.getParameters();
                param.setFlashMode(!flashmode ? Parameters.FLASH_MODE_TORCH
                        : Parameters.FLASH_MODE_OFF);
                camera.setParameters(param);
                flashmode = !flashmode;
            } catch (Exception e) {
                // TODO: handle exception  
            }

        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ImageCapture.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        ImageCapture.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

}  