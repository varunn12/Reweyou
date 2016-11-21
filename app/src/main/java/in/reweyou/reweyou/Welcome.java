package in.reweyou.reweyou;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import in.reweyou.reweyou.classes.UserSessionManager;

public class Welcome extends Activity implements View.OnClickListener {
    static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE = 0;
    int REQUEST_CAMERA = 1, SELECT_FILE = 0;
    SharedPreferences sharedPreferences;
    UserSessionManager session;
    PermissionsChecker checker;
    Uri uri;
    private Button CustomCamera, Gallery;
    private Button home;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("ShaPreferences", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_welcome);
        checker = new PermissionsChecker(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean firstTime = sharedPreferences.getBoolean("first", true);

            CustomCamera = (Button) findViewById(R.id.CustomCamera);
            Gallery=(Button)findViewById(R.id.Gallery);
            Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
            home = (Button) findViewById(R.id.Feed);
            TextView welcome = (TextView) findViewById(R.id.welcome);
            session = new UserSessionManager(Welcome.this);
            HashMap<String, String> user = session.getUserDetails();
            // get name
            String username = user.get(UserSessionManager.KEY_NAME);
            welcome.setText(Html.fromHtml("Hi <b>" + username + "!</b>"));
            home.setOnClickListener(this);
            Gallery.setOnClickListener(this);
            home.setTypeface(font);
            CustomCamera.setTypeface(font);
            Gallery.setTypeface(font);
            CustomCamera.setOnClickListener(this);
            if (savedInstanceState != null) {
                // Restore value of members from saved state
                mCurrentPhotoPath = savedInstanceState.getString("Path");

            } else {
                // Probably initialize members with default values for a new instance
                mCurrentPhotoPath = null;
            }
    }

    @Override
    public void onClick(View v) {
        if(v==home)
        {
            Intent i = new Intent(this, Feed.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
        if(v==CustomCamera) {
            if (checker.lacksPermissions(PERMISSIONS)) {
                startPermissionsActivity();
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = null;
                photoFile = getOutputMediaFile();
                uri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
        if(v==Gallery)
        {
            if (checker.lacksPermissions(PERMISSIONS)) {
                startPermissionsActivity();
            }
            else {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // 2. pick image only
                intent.setType("image/*");
                // 3. start activity
                startActivityForResult(intent, SELECT_FILE);
                UILApplication.getInstance().trackEvent("Gallery", "Gallery", "For Pics");
            }
        }
    }
    private File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Reweyou");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Reweyou", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
        mCurrentPhotoPath = "file:" + mediaFile.getAbsolutePath();
        return mediaFile;
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && reqCode==REQUEST_CAMERA) {
            //Fetches the thumbnail only not the whole picture
            /*   Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
           Intent intent = new Intent(Feed.this, CameraActivity.class);
           intent.putExtra("image", thumbnail);
             */ //    startActivity(intent);
            // Bundle extras = getIntent().getExtras();
            //Uri uriFromPath = (Uri)extras.get(MediaStore.EXTRA_OUTPUT);
//            String show = uri.toString();
            Intent intent = new Intent(Welcome.this, CameraActivity.class);
         //   Log.d("URI",show);
            Log.d("Intent", mCurrentPhotoPath);
            intent.putExtra("path", mCurrentPhotoPath);
            startActivity(intent);
            finish();
        }
        if (resCode == Activity.RESULT_OK && reqCode==SELECT_FILE && data != null) {
            Uri uriFromPath = data.getData();
            String show = uriFromPath.toString();
            Intent intent = new Intent(this, PostReport.class);
            intent.putExtra("path", show);
            startActivity(intent);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString("Path", mCurrentPhotoPath);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }
}
