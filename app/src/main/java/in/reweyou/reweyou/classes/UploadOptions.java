package in.reweyou.reweyou.classes;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import in.reweyou.reweyou.ApplicationClass;
import in.reweyou.reweyou.PermissionsChecker;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.VideoCapturetest;

/**
 * Created by master on 18/11/16.
 */

public class UploadOptions {

    public static final int PERMISSION_ALL_IMAGE = 24;
    public static final int PERMISSION_ALL_VIDEO_CAPTURE = 25;
    public static final int PERMISSION_ALL_VIDEO = 26;
    public static final int PERMISSION_ALL_SHARE = 39;
    public static final int PERMISSION_ALL_PROFILE_PIC = 40;
    private static final int PERMISSION_ALL = 23;
    private final String[] PERMISSION_IMAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    private final String[] PERMISSION_VIDEO = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final int REQUEST_CODE = 0;
    private final int REQUEST_TAKE_GALLERY_VIDEO = 5;
    private boolean b;
    private View view;
    private Context context;
    private PermissionsChecker checker;


    private int REQUEST_CAMERA = 0, SELECT_IMAGE_FILE = 1, REQUEST_VIDEO = 3;
    private ImageView btn_camera;
    private ImageView btn_video;
    private ImageView btn_gif;


    public UploadOptions(Activity context) {
        this.context = context;
        checker = new PermissionsChecker(context);
       /* if (checker.lacksPermissions(PERMISSION)) {
            startPermissionsActivity();
        }*/
    }

    public UploadOptions(Context mContext, View view, boolean b) {
        this.context = mContext;
        this.view = view;
        this.b = b;
        checker = new PermissionsChecker(context);
      /*  if (checker.lacksPermissions(PERMISSION)) {
            startPermissionsActivity();
        }*/
        initOptions();
    }


    public void showVideoOptions() {


        AlertDialog.Builder getImageFrom = new AlertDialog.Builder(context);
        getImageFrom.setTitle("Select Video from:");
        final CharSequence[] opsChars = {context.getResources().getString(R.string.shootVideo), context.getResources().getString(R.string.opengallery)};
        getImageFrom.setItems(opsChars, new android.content.DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {

                    captureVideo();


                } else if (which == 1) {

                    showVideogallery();

                }
                dialog.dismiss();
            }
        });
        getImageFrom.show();


    }

    public void showVideogallery() {
        if (!hasPermissions(context, PERMISSION_IMAGE)) {
            ActivityCompat.requestPermissions((Activity) context, PERMISSION_IMAGE, PERMISSION_ALL_VIDEO);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            intent.setType("video/*");
            ((Activity) context).startActivityForResult(intent, REQUEST_TAKE_GALLERY_VIDEO);
        }

    }

    public void captureVideo() {
        if (!hasPermissions(context, PERMISSION_VIDEO)) {
            ActivityCompat.requestPermissions((Activity) context, PERMISSION_VIDEO, PERMISSION_ALL_VIDEO_CAPTURE);
        } else
            context.startActivity(new Intent(context, VideoCapturetest.class));
    }

    private void showGIFptions() {
        showImageOptions();

    }

    public void showImageOptions() {
        if (!hasPermissions(context, PERMISSION_IMAGE)) {
            ActivityCompat.requestPermissions((Activity) context, PERMISSION_IMAGE, PERMISSION_ALL_IMAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            ((Activity) context).startActivityForResult(intent, SELECT_IMAGE_FILE);
            ApplicationClass.getInstance().trackEvent("Gallery", "Gallery", "For Pics");
        }
    }

    public Boolean showShareOptions() {
        if (!hasPermissions(context, PERMISSION_IMAGE)) {
            ActivityCompat.requestPermissions((Activity) context, PERMISSION_IMAGE, PERMISSION_ALL_SHARE);
            return false;
        } else return true;
    }

    public Boolean showprofilepicOptions() {
        if (!hasPermissions(context, PERMISSION_IMAGE)) {
            ActivityCompat.requestPermissions((Activity) context, PERMISSION_IMAGE, PERMISSION_ALL_PROFILE_PIC);
            return false;
        } else return true;
    }



    public void initOptions() {
        if (!b) {
            btn_camera = (ImageView) ((Activity) context).findViewById(R.id.btn_camera);
            btn_video = (ImageView) ((Activity) context).findViewById(R.id.btn_video);
            btn_gif = (ImageView) ((Activity) context).findViewById(R.id.btn_gif);
        } else {
            btn_camera = (ImageView) view.findViewById(R.id.btn_camera);
            btn_video = (ImageView) view.findViewById(R.id.btn_video);
            btn_gif = (ImageView) view.findViewById(R.id.btn_gif);
            Log.d("fjwfnw", "***************");
        }
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageOptions();
            }
        });
        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVideoOptions();
            }
        });
        btn_gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGIFptions();
            }
        });
    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = ((Activity) context).managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


}
