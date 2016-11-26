package in.reweyou.reweyou;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import in.reweyou.reweyou.classes.AppLocationService;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.LocationAddress;
import in.reweyou.reweyou.classes.UserSessionManager;

public class VideoUpload extends AppCompatActivity implements View.OnClickListener {

    private static final int SELECT_VIDEO = 3;
    Bitmap bmThumbnail;
    Location location;
    Boolean isInternetPresent = false;
    AppLocationService appLocationService;
    UserSessionManager session;
    ConnectionDetector cd;
    private Button button;
    private EditText editText;
    private  VideoView videoView;
    private Spinner staticSpinner;
    private String show;
    private String tag;
    private String place;
    private String name;
    private String address;
    private String image;
    private String number;

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);
        Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
        appLocationService = new AppLocationService(
                VideoUpload.this);
        cd = new ConnectionDetector(VideoUpload.this);
        session = new UserSessionManager(VideoUpload.this);
        videoView = (VideoView)findViewById(R.id.videoView);

        editText = (EditText)findViewById(R.id.Who);
        button=(Button)findViewById(R.id.btn_send);
        button.setTypeface(font);
        button.setOnClickListener(this);
        HashMap<String, String> user = session.getUserDetails();
        name = user.get(UserSessionManager.KEY_NAME);
        number = user.get(UserSessionManager.KEY_NUMBER);
        show = getIntent().getStringExtra("path");
        videoView.setVideoPath(show);
        bmThumbnail = ThumbnailUtils.createVideoThumbnail(show, MediaStore.Video.Thumbnails.MINI_KIND);
        image=getStringImage(bmThumbnail);
        videoView.start();
        staticSpinner = (Spinner) findViewById(R.id.static_spinner);
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.brew_array,
                        R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);
        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                tag = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tag = "General";
                // TODO Auto-generated method stub
            }
        });
    }

    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadVideo() {
        final String text = editText.getText().toString().trim();
        String format = "dd-MMM-yyyy hh:mm:ss a";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        final String timeStamp = sdf.format(new Date());

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LocationAddress locationAddress = new LocationAddress(VideoUpload.this);
        LocationAddress.getAddressFromLocation(latitude, longitude,
                VideoUpload.this, new GeocoderHandler());



        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(VideoUpload.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                if(s.equals("Successfully uploaded"))
                {
                   Intent feed= new Intent(VideoUpload.this,Feed.class);
                    startActivity(feed);
                    Log.d("Intent not working", "Intent not working");
                    finish();
                  //  Toast.makeText(VideoUpload.this,s,Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(VideoUpload.this,s,Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            protected String doInBackground(Void... params) {
               /* Upload u = new Upload();
                String msg = u.uploadVideo(show,name,place,timeStamp,text,tag,address,number,image);
                return msg;*/
                return null;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }

    @Override
    public void onClick(View v) {
        isInternetPresent = cd.isConnectingToInternet();
        if(isInternetPresent) {
            if(isLocationEnabled(this)) {
                location = appLocationService
                        .getLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    if (place != null) {
                        uploadVideo();
                    } else {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LocationAddress locationAddress = new LocationAddress(VideoUpload.this);
                        LocationAddress.getAddressFromLocation(latitude, longitude,
                                getApplicationContext(), new GeocoderHandler());
                        // Toast.makeText(CameraActivity.this,"Detecting current location...We need your current location for authenticity.",Toast.LENGTH_LONG).show();
                        button.setBackgroundResource(R.color.colorPrimary);
                        button.setText(R.string.send);
                    }
                } else {
                    location = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        if (place != null) {
                            uploadVideo();
                        } else {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            LocationAddress locationAddress = new LocationAddress(VideoUpload.this);
                            LocationAddress.getAddressFromLocation(latitude, longitude,
                                    getApplicationContext(), new GeocoderHandler());
                            //     Toast.makeText(CameraActivity.this,"Detecting current location...We need your current location for authenticity.",Toast.LENGTH_LONG).show();
                            button.setBackgroundResource(R.color.colorPrimary);
                            button.setText(R.string.send);
                        }
                    } else {
                        Toast.makeText(this, "Fetching Reporting Location", Toast.LENGTH_LONG).show();
                    }
                }
            }
            else
            {
                showSettingsAlert();
            }

        }
        else
        {
            Toast.makeText(VideoUpload.this,"You are not connected to Internet",Toast.LENGTH_SHORT).show();
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                VideoUpload.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        VideoUpload.this.startActivity(intent);
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

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            String fulladdress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    fulladdress = bundle.getString("add");
                    break;
                default:
                    locationAddress = "Unknown";
                    fulladdress = "Unknown";
            }
            place = locationAddress;
            address = fulladdress;
        }
    }

}