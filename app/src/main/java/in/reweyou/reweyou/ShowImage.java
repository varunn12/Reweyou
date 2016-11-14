package in.reweyou.reweyou;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import in.reweyou.reweyou.classes.AppLocationService;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.ImageLoadingUtils;
import in.reweyou.reweyou.classes.LocationAddress;
import in.reweyou.reweyou.classes.RequestHandler;
import in.reweyou.reweyou.classes.UserSessionManager;

public class ShowImage extends AppCompatActivity implements View.OnClickListener {
    public static final String KEY_IMAGE = "image";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_TAG = "tag";
    public static final String KEY_TIME = "time";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_TEXT = "headline";
    public static final String KEY_NAME = "name";
    //public static final String UPLOAD_URL = "https://www.reweyou.in/reweyou/upload_report.php";
    public static final String UPLOAD_URL = "https://www.reweyou.in/reweyou/test_report.php";
    static final String[] PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_CODE = 0;
    Location location;
    AppLocationService appLocationService;
    ConnectionDetector cd;
    UserSessionManager session;
    String selectedImagePath;
    Boolean isInternetPresent = false;
    PermissionsChecker checker;
    private Button button;
    private EditText editText, editTag, head;
    private ImageView imageview;
    private Bitmap bitmap;
    private String place;
    private Bitmap Correctbmp;
    private String address, mycity;
    private String name;
    private String tag, type;
    private ImageLoadingUtils utils;
    private Spinner staticSpinner;
    private String number;
    private Toolbar toolbar;

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
        setContentView(R.layout.activity_camera);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Upload Report");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        session = new UserSessionManager(getApplicationContext());
        checker = new PermissionsChecker(this);
        mycity = session.getLoginLocation();
        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        cd = new ConnectionDetector(ShowImage.this);
        appLocationService = new AppLocationService(
                ShowImage.this);
        utils = new ImageLoadingUtils(this);
        imageview = (ImageView) findViewById(R.id.ImageShow);
        editText = (EditText) findViewById(R.id.Who);
        editTag = (EditText) findViewById(R.id.EditTag);
        head = (EditText) findViewById(R.id.head);
        button = (Button) findViewById(R.id.btn_send);
        button.setTypeface(font);
        button.setOnClickListener(this);

        String show = getIntent().getStringExtra("path");

        HashMap<String, String> user = session.getUserDetails();
        name = user.get(UserSessionManager.KEY_NAME);
        number = user.get(UserSessionManager.KEY_NUMBER);

        selectedImagePath = getAbsolutePath(Uri.parse(show));

        Glide.with(ShowImage.this).load(selectedImagePath).override(400, 400).into(imageview);

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
                if (position > 0) {
                    // get spinner value
                    type = (String) parent.getItemAtPosition(position);
                    //Toast.makeText(getActivity(), tag, Toast.LENGTH_LONG).show();
                    // Messages();
                } else {
                    // show toast select gender
                    //            Toast.makeText(ShowImage.this,"Select a category",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Toast.makeText(ShowImage.this,"Select a category",Toast.LENGTH_SHORT).show();
                // TODO Auto-generated method stub
            }
        });


    }

    @Override
    public void onClick(View v) {
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            if (isLocationEnabled(this)) {
                location = appLocationService
                        .getLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    if (place != null) {
                        compressImage();
                    } else {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LocationAddress locationAddress = new LocationAddress(ShowImage.this);
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
                            if (type.equals("Select Category")) {
                                Toast.makeText(ShowImage.this, "Select a category", Toast.LENGTH_SHORT).show();
                            } else {
                                compressImage();
                            }
                        } else {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            LocationAddress locationAddress = new LocationAddress(ShowImage.this);
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
            } else {
                showSettingsAlert();
            }

        } else {
            Toast.makeText(ShowImage.this, "You are not connected to Internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ShowImage.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        ShowImage.this.startActivity(intent);
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


    @Override
    protected void onResume() {
        super.onResume();

        if (checker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void compressImage() {

        Glide
                .with(this)
                .load(selectedImagePath)
                .asBitmap()
                .toBytes(Bitmap.CompressFormat.JPEG, 60)
                .fitCenter()
                .atMost()
                .override(800, 800)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(new SimpleTarget<byte[]>() {
                    @Override
                    public void onLoadStarted(Drawable ignore) {
                        // started async load
                    }

                    @Override
                    public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> ignore) {
                        String encodedImage = Base64.encodeToString(resource, Base64.DEFAULT);
                        uploadImage(encodedImage);
                    }

                    @Override
                    public void onLoadFailed(Exception ex, Drawable ignore) {
                        Log.d("ex", ex.getMessage());
                    }
                });


    }

    private void uploadImage(String encodedImage) {

        tag = editTag.getText().toString().trim();
        final String text = editText.getText().toString().trim();
        final String heads = head.getText().toString().trim();
        final String image = encodedImage;
        String format = "dd-MMM-yyyy hh:mm:ss a";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        final String timeStamp = sdf.format(new Date());


        class UploadImage extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ShowImage.this, "Please wait...", "uploading", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.trim().equals("Successfully Uploaded")) {
                    openProfile();
                } else {
                    Toast.makeText(ShowImage.this, "Check details and try again.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                HashMap<String, String> param = new HashMap<String, String>();
                param.put(KEY_TEXT, text);
                param.put(KEY_IMAGE, image);
                param.put(KEY_LOCATION, place);
                param.put(KEY_NAME, name);
                param.put(KEY_TAG, tag);
                param.put("type", type);
                param.put("head", heads);
                param.put(KEY_TIME, timeStamp);
                param.put(KEY_ADDRESS, address);
                param.put("number", number);
                Log.d("DATE", timeStamp);
                Log.d("DATE", text);
                Log.d("DATE", place);
                Log.d("DATE", address);
                Log.d("DATE", name);
                Log.d("DATE", tag);

                String result = rh.sendPostRequest(UPLOAD_URL, param);
                return result;
            }
        }
        UploadImage u = new UploadImage();
        u.execute();

    }

    private void openProfile() {
        // Starting TokenTest
        Intent i = new Intent(this, Feed.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish(); // Call once you redirect to another activity
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
                    locationAddress = mycity;
                    fulladdress = mycity;
            }
            place = locationAddress;
            address = fulladdress;
        }
    }
}

