package in.reweyou.reweyou;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import in.reweyou.reweyou.classes.AppLocationService;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.HandleActivityResult;
import in.reweyou.reweyou.classes.ImageLoadingUtils;
import in.reweyou.reweyou.classes.RequestHandler;
import in.reweyou.reweyou.classes.UploadOptions;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.utils.Constants;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationManagerProvider;

import static in.reweyou.reweyou.classes.HandleActivityResult.HANDLE_IMAGE;
import static in.reweyou.reweyou.classes.HandleActivityResult.HANDLE_VIDEO;
import static in.reweyou.reweyou.utils.Constants.POST_REPORT_KEY_ADDRESS;
import static in.reweyou.reweyou.utils.Constants.POST_REPORT_KEY_CATEGORY;
import static in.reweyou.reweyou.utils.Constants.POST_REPORT_KEY_DESCRIPTION;
import static in.reweyou.reweyou.utils.Constants.POST_REPORT_KEY_HEADLINE;
import static in.reweyou.reweyou.utils.Constants.POST_REPORT_KEY_IMAGE;
import static in.reweyou.reweyou.utils.Constants.POST_REPORT_KEY_LOCATION;
import static in.reweyou.reweyou.utils.Constants.POST_REPORT_KEY_NAME;
import static in.reweyou.reweyou.utils.Constants.POST_REPORT_KEY_NUMBER;
import static in.reweyou.reweyou.utils.Constants.POST_REPORT_KEY_REPORT;
import static in.reweyou.reweyou.utils.Constants.POST_REPORT_KEY_TAG;
import static in.reweyou.reweyou.utils.Constants.POST_REPORT_KEY_TIME;

public class PostReport extends AppCompatActivity implements View.OnClickListener {


    public static final String UPLOAD_URL = "https://www.reweyou.in/reweyou/reporting.php";

    static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    static final String[] PERMISSIONS_LOCATION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int IMAGE = 11;
    private static final int VIDEO = 12;
    private static final int TYPE_GIF = 19;
    private static final int TYPE_IMAGE = 20;
    private static final String PACKAGE_URL_SCHEME = "package:";
    private static final int PERMISSION_ALL = 1;
    Location location;
    AppLocationService appLocationService;
    ConnectionDetector cd;
    UserSessionManager session;
    String selectedImagePath;
    Boolean isInternetPresent = false;
    PermissionsChecker checker;
    private ImageView sendButton;
    private EditText description, editTag, headline;
    private ImageView previewImageView;
    private String place;
    private String address, mycity;
    private String name;
    private String tag, currentSpinnerPositionString;
    private ImageLoadingUtils utils;
    private Spinner staticSpinner;
    private String number;
    private Toolbar toolbar;
    private int position_spinner = -1;

    private RelativeLayout previewContainer;
    private ImageView previewImageCancel;
    private String selectedVideoPath;
    private ImageView previewPlayVideoButton;
    private int viewType = -1;
    private UploadOptions uploadOptions;
    private LinearLayout logoContainer;
    private String format;
    private SimpleDateFormat sdf;
    private String parameterHeadline;
    private String parameterEditTag;
    private String parameterDescription;
    private LinearLayout bottomContainer;
    private View bottomline;
    private ImageView previewImageViewGif;
    private boolean activityOpen;
    private boolean gotLocation;
    private boolean reachedHere;

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
        setContentView(R.layout.activity_post_report);

        initToolbar();

        initViews();

        format = "dd-MMM-yyyy hh:mm:ss a";
        sdf = new SimpleDateFormat(format);

        uploadOptions = new UploadOptions(this);
        uploadOptions.initOptions();

        session = new UserSessionManager(getApplicationContext());
        checker = new PermissionsChecker(this);
        mycity = session.getLoginLocation();
        cd = new ConnectionDetector(PostReport.this);
        appLocationService = new AppLocationService(PostReport.this);

        sendButton.setOnClickListener(this);

        HashMap<String, String> user = session.getUserDetails();
        name = user.get(UserSessionManager.KEY_NAME);
        number = user.get(UserSessionManager.KEY_NUMBER);

        initCategorySpinner();


        Intent i = getIntent();
        if (i != null) {

            if (i.hasExtra("dataImage")) {
                handleImage(i.getStringExtra("dataImage"));
            } else if (i.hasExtra("dataVideo")) {
                handleVideo(i.getStringExtra("dataVideo"));
            }

            //Image directly choosen from gallery
          /*  String action = i.getAction();
            Bundle extras = i.getExtras();

            if (Intent.ACTION_SEND.equals(action)) {
                if (extras.containsKey(Intent.EXTRA_STREAM)) {
                    Uri uri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
                    Log.d("gallery", uri.toString());
                    handleImage(uri.toString());
                }
            }*/


            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {

                Log.d("type", type);
                if (type.startsWith("image/")) {
                    Uri uri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
                    Log.d("gallery", uri.toString());
                    handleImage(uri.toString());
                } else if (type.startsWith("video/")) {
                    Uri uri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
                    Log.d("gallery", uri.toString());
                    handleVideo(uploadOptions.getAbsolutePath(uri));
                }

            }
        }

        SmartLocation.with(PostReport.this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        SmartLocation.with(PostReport.this).geocoding()
                                .reverse(location, new OnReverseGeocodingListener() {
                                    @Override
                                    public void onAddressResolved(Location location, List<Address> list) {
                                        for (int i = 0; i < list.size(); i++) {
                                            Log.d("result", list.get(0).getLocality());
                                        }

                                    }
                                });
                    }
                });
    }

    private void setClickListeners() {
        previewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (viewType) {
                    case IMAGE:
                        if (selectedImagePath != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("myData", selectedImagePath);
                            Intent in = new Intent(PostReport.this, FullImage.class);
                            in.putExtras(bundle);
                            startActivity(in);
                        }
                        return;
                    case VIDEO:
                        Bundle bundle = new Bundle();
                        bundle.putString("myData", selectedVideoPath);
                        Intent in = new Intent(PostReport.this, Videorow.class);
                        in.putExtras(bundle);
                        startActivity(in);
                        return;
                }
            }
        });

        previewImageCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoContainer.setVisibility(View.VISIBLE);
                previewPlayVideoButton.setVisibility(View.GONE);
                previewContainer.setVisibility(View.GONE);
                selectedImagePath = null;
                selectedVideoPath = null;
                previewImageView.setColorFilter(null);
            }
        });

    }

    private void initViews() {


        //activity layout views
        description = (EditText) findViewById(R.id.Who);
        editTag = (EditText) findViewById(R.id.tag);
        headline = (EditText) findViewById(R.id.head);
        sendButton = (ImageView) findViewById(R.id.btn_send);
        logoContainer = (LinearLayout) findViewById(R.id.logo);
        bottomContainer = (LinearLayout) findViewById(R.id.l1);
        bottomline = findViewById(R.id.line);

        //preview layout views
        previewImageView = (ImageView) findViewById(R.id.ImageShow);
        previewImageViewGif = (ImageView) findViewById(R.id.GifShow);
        previewImageCancel = (ImageView) findViewById(R.id.cancel);
        previewContainer = (RelativeLayout) findViewById(R.id.previewLayout);
        previewPlayVideoButton = (ImageView) findViewById(R.id.play);
        //click listners for preview layout views
        setClickListeners();

        keyboardListener();

    }

    private void keyboardListener() {
        findViewById(R.id.main_content).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                findViewById(R.id.main_content).getWindowVisibleDisplayFrame(r);
                int heightDiff = findViewById(R.id.main_content).getRootView().getHeight() - (r.bottom - r.top);

                if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                    //ok now we know the keyboard is up...
                    logoContainer.setVisibility(View.GONE);
                    bottomContainer.setVisibility(View.GONE);
                    bottomline.setVisibility(View.GONE);
                    toolbar.setVisibility(View.GONE);


                } else {
                    //ok now we know the keyboard is down...
                    logoContainer.setVisibility(View.VISIBLE);
                    bottomContainer.setVisibility(View.VISIBLE);
                    bottomline.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initCategorySpinner() {
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
                position_spinner = position;
                if (position > 0) {
                    // get spinner value
                    currentSpinnerPositionString = (String) parent.getItemAtPosition(position);
                    //Toast.makeText(getActivity(), tag, Toast.LENGTH_LONG).show();
                    // Messages();
                } else {
                    // show toast select gender
                    //            Toast.makeText(PostReport.this,"Select a category",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Toast.makeText(PostReport.this,"Select a category",Toast.LENGTH_SHORT).show();
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    public void onClick(View v) {

        onbutoon();

/*
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            if (isLocationEnabled(this)) {
                location = appLocationService
                        .getLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    if (place != null) {
                        if (validateFields()) {
                            if (selectedImagePath != null) {
                                compressImageOrGif();
                            } else if (selectedVideoPath != null) {
                                compressVideo();
                            } else uploadImage(null);
                        }
                    } else {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LocationAddress locationAddress = new LocationAddress(PostReport.this);
                        LocationAddress.getAddressFromLocation(latitude, longitude,
                                getApplicationContext(), new GeocoderHandler());
                        // Toast.makeText(CameraActivity.this,"Detecting current location...We need your current location for authenticity.",Toast.LENGTH_LONG).show();
                        sendButton.setImageResource(R.drawable.button_send);
                        //sendButton.setText(R.string.send);
                    }
                } else {
                    location = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        if (place != null) {
                            if (validateFields()) {
                                if (selectedImagePath != null) {
                                    compressImageOrGif();
                                } else if (selectedVideoPath != null) {
                                    compressVideo();
                                }
                            }
                        } else {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            LocationAddress locationAddress = new LocationAddress(PostReport.this);
                            LocationAddress.getAddressFromLocation(latitude, longitude,
                                    getApplicationContext(), new GeocoderHandler());
                            //     Toast.makeText(CameraActivity.this,"Detecting current location...We need your current location for authenticity.",Toast.LENGTH_LONG).show();
                            sendButton.setImageResource(R.drawable.button_send);
                            // sendButton.setText(R.string.send);
                        }
                    } else {
                        Toast.makeText(this, "Fetching Reporting Location", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                showSettingsAlert();
            }

        } else {
            Toast.makeText(PostReport.this, "You are not connected to Internet", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void onbutoon() {
        gotLocation = false;
        reachedHere = false;
        if (!hasPermissions(this, PERMISSIONS_LOCATION)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, PERMISSION_ALL);
        } else
            permissionGranted();
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {

                String permission = permissions[0];
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission

                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(PostReport.this, permission);
                    if (!showRationale) {
                        showPermissionDeniedDialog();
                    } else
                        showPermissionRequiredDialog(permission);
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // registerUser();
                    permissionGranted();
                }
            }
        }
    }

    private void permissionGranted() {
        Log.d("rea", "1233");


        if (SmartLocation.with(PostReport.this).location().state().locationServicesEnabled()) {
            Log.d("rea", "123321e12e");
            if (isGooglePlayServicesAvailable(this)) {
                if (SmartLocation.with(PostReport.this).location(new LocationManagerProvider()).state().isGpsAvailable()) {
                    final ProgressDialog pd = new ProgressDialog(PostReport.this);
                    pd.setMessage("Fetching current location! Please Wait.");
                    pd.show();
                    getLocation(pd);


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (activityOpen) {
                                if (!gotLocation) {
                                    reachedHere = true;
                                    pd.dismiss();
                                    showGPStimedialog();
                                }
                            }
                        }
                    }, 5500);

                } else if (SmartLocation.with(PostReport.this).location().state().isNetworkAvailable()) {
                    Log.d("rea", "1666");
                    final ProgressDialog pd = new ProgressDialog(PostReport.this);
                    pd.setMessage("Fetching current location! Please Wait.");
                    pd.show();
                    getLocation(pd);

                }

            }
        } else {
            showSettingsAlert();
        }


    }

    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    private void showGPStimedialog() {
        final AlertDialogBox alertDialogBox = new AlertDialogBox(PostReport.this, "Time out", "Please change your location accuracy to either Network or High Accuracy", "Settings", "Dismiss") {
            @Override
            void onNegativeButtonClick(DialogInterface dialog) {
                dialog.dismiss();
            }

            @Override
            void onPositiveButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                PostReport.this.startActivity(intent);

            }
        };
        alertDialogBox.show();
    }

    private void getLocation(final ProgressDialog pd) {
        SmartLocation.with(PostReport.this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {

                        gotLocation = true;
                        if (!reachedHere) {

                            SmartLocation.with(PostReport.this).geocoding()
                                    .reverse(location, new OnReverseGeocodingListener() {
                                        @Override
                                        public void onAddressResolved(Location location, List<Address> list) {
                                            pd.dismiss();
                                            Log.d("result", list.get(0).getLocality() + "     " + list.get(0).toString());
                                            address = list.get(0).toString();
                                            place = list.get(0).getLocality();
                                            if (validateFields()) {
                                                if (selectedImagePath != null) {
                                                    compressImageOrGif();
                                                } else if (selectedVideoPath != null) {
                                                    compressVideo();
                                                } else uploadImage(null);
                                            }

                                        }
                                    });
                        }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();

        activityOpen = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityOpen = false;
    }

    private void showPermissionRequiredDialog(final String permission) {
        AlertDialogBox alertDialogBox = new AlertDialogBox(PostReport.this, "Permission Required", getResources().getString(R.string.permission_required_location), "grant", "deny") {
            @Override
            void onNegativeButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                //registerUser();
                permissionGranted();
            }

            @Override
            void onPositiveButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                String[] p = {permission};
                ActivityCompat.requestPermissions(PostReport.this, p, PERMISSION_ALL);

            }
        };
        alertDialogBox.setCancellable(true);
        alertDialogBox.show();
    }


    private void showPermissionDeniedDialog() {
        AlertDialogBox alertDialogBox = new AlertDialogBox(PostReport.this, "Permission Denied", getResources().getString(R.string.permission_denied_location), "settings", "okay") {
            @Override
            void onNegativeButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                // registerUser();
                permissionGranted();
            }

            @Override
            void onPositiveButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                startAppSettings();

            }
        };
        alertDialogBox.setCancellable(true);
        alertDialogBox.show();
    }

    private void startAppSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

    private boolean validateFields() {
        if (staticSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(PostReport.this, "Select a category", Toast.LENGTH_SHORT).show();
            return false;
        } else if (editTag.getText().toString().trim().length() == 0) {
            Toast.makeText(PostReport.this, "Tag cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (description.getText().toString().trim().length() == 0) {
            Toast.makeText(PostReport.this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            updateUploadDataFields();
            return true;
        }

    }

    private void updateUploadDataFields() {
        parameterHeadline = headline.getText().toString();
        parameterEditTag = editTag.getText().toString();
        parameterDescription = description.getText().toString();
    }

    private void compressVideo() {
        Glide.with(PostReport.this).load(new File(selectedVideoPath)).asBitmap()
                .toBytes(Bitmap.CompressFormat.JPEG, 70)
                .fitCenter()
                .atMost()
                .override(700, 700)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(new SimpleTarget<byte[]>() {
                    @Override
                    public void onLoadStarted(Drawable ignore) {
                        // started async load
                    }

                    @Override
                    public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> ignore) {
                        final String encodedImage = Base64.encodeToString(resource, Base64.DEFAULT);

                        class UploadVideo extends AsyncTask<Void, Void, String> {

                            ProgressDialog uploading;

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                uploading = ProgressDialog.show(PostReport.this, "Uploading File", "Please wait...", false, false);
                            }

                            @Override
                            protected void onPostExecute(String s) {
                                super.onPostExecute(s);
                                uploading.dismiss();
                                if (s.equals("Successfully Uploaded")) {
                                    Intent feed = new Intent(PostReport.this, Feed.class);
                                    startActivity(feed);
                                    Log.d("Intent not working", "Intent not working");
                                    finish();
                                    //  Toast.makeText(VideoUpload.this,s,Toast.LENGTH_SHORT).show();
                                } else if (s.trim().equals(Constants.AUTH_ERROR)) {
                                    session.logoutUser();
                                } else {
                                    Toast.makeText(PostReport.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            protected String doInBackground(Void... params) {
                                Upload2 u = new Upload2(PostReport.this);
                                String s = u.uploadVideo(selectedVideoPath, selectedVideoPath, parameterHeadline, parameterEditTag, currentSpinnerPositionString, parameterDescription, place, address, sdf.format(new Date()), encodedImage, false, true, false, number, name, session.getKeyAuthToken());
                                return s;
                            }
                        }

                        UploadVideo uv = new UploadVideo();
                        uv.execute();
                    }

                    @Override
                    public void onLoadFailed(Exception ex, Drawable ignore) {
                        Log.d("ex", ex.getMessage());

                    }
                });

    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                PostReport.this);
        alertDialog.setTitle("Location disabled");
        alertDialog.setMessage("Enable Location Provider from settings menu.");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        PostReport.this.startActivity(intent);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("reached", "activigty");
        super.onActivityResult(requestCode, resultCode, data);
        int dataType = new HandleActivityResult().handleResult(requestCode, resultCode, data);
        switch (dataType) {
            case HANDLE_IMAGE: {
                handleImage(data.getData().toString());
            }
            break;
            case HANDLE_VIDEO: {
                handleVideo(uploadOptions.getAbsolutePath(data.getData()));
            }
            break;
            default:
                finish();
        }

    }

    private void handleVideo(String data) {

        if (data == null) {
            Toast.makeText(PostReport.this, "File path not supported", Toast.LENGTH_SHORT).show();
        } else {
            File videoFile = new File(data);
            int file_size = Integer.parseInt(String.valueOf(videoFile.length() / (1024 * 1024)));
            if (file_size < 5) {
                viewType = VIDEO;
                //Log.d("path2", data);
                // selectedVideoPath = uploadOptions.getAbsolutePath(Uri.parse(data));//Log.d("path", selectedVideoPath);
                previewContainer.setVisibility(View.VISIBLE);
                previewPlayVideoButton.setVisibility(View.VISIBLE);
                logoContainer.setVisibility(View.GONE);
                previewImageView.setColorFilter(Color.argb(150, 255, 255, 255)); // White Tint

                previewImageView.setVisibility(View.VISIBLE);
                previewImageViewGif.setVisibility(View.GONE);

                Glide.with(PostReport.this).load(new File(data)).override(800, 800).into(previewImageView);
                selectedVideoPath = data;
            } else {
                AlertDialogBox alertDialogBox = new AlertDialogBox(PostReport.this, "File size exceeded", "Please upload video upto 5 MB in size only...", "OKAY", null) {
                    @Override
                    void onNegativeButtonClick(DialogInterface dialog) {
                    /*Not define*/
                    }

                    @Override
                    void onPositiveButtonClick(DialogInterface dialog) {
                        dialog.dismiss();

                    }
                };
                alertDialogBox.setCancellable(true);
                alertDialogBox.show();
            }
        }
    }

    private void handleImage(String data) {

        if (data == null) {
            Toast.makeText(PostReport.this, "File path not supported", Toast.LENGTH_SHORT).show();
        } else {
            previewPlayVideoButton.setVisibility(View.GONE);
            logoContainer.setVisibility(View.GONE);
            previewImageView.setColorFilter(null);
            previewContainer.setVisibility(View.VISIBLE);

            selectedImagePath = uploadOptions.getAbsolutePath(Uri.parse(data));

            String type = selectedImagePath.substring(selectedImagePath.lastIndexOf(".") + 1);
            if (type.equals("gif")) {
                selectedImagePath = uploadOptions.getAbsolutePath(Uri.parse(data));
                previewImageView.setVisibility(View.GONE);
                previewImageViewGif.setVisibility(View.VISIBLE);
                Glide.with(PostReport.this).load(selectedImagePath).asGif().into(previewImageViewGif);
                viewType = IMAGE;
            } else {

                previewImageView.setVisibility(View.VISIBLE);
                previewImageViewGif.setVisibility(View.GONE);
                selectedImagePath = uploadOptions.getAbsolutePath(Uri.parse(data));
                Glide.with(PostReport.this).load(selectedImagePath).into(previewImageView);
                viewType = IMAGE;
            }
        }
    }

    public void compressImageOrGif() {
        switch (getSelectedFileType(selectedImagePath)) {
            case TYPE_GIF:
                uploadSelectedGif();
                break;
            case TYPE_IMAGE:
                uploadSelectedImage();
        }
    }

    private void uploadSelectedImage() {
        if (selectedImagePath != null) {
            Glide
                    .with(this)
                    .load(selectedImagePath)
                    .asBitmap()
                    .toBytes(Bitmap.CompressFormat.JPEG, 90)
                    .fitCenter()
                    .atMost()
                    .override(1000, 1000)
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
        } else Log.w("uploadSelectedImage", "selected path is null");
    }

    private void uploadSelectedGif() {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(PostReport.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                if (s.equals("Successfully Uploaded")) {
                    Intent feed = new Intent(PostReport.this, Feed.class);
                    startActivity(feed);
                    Log.d("Intent not working", "Intent not working");
                    finish();
                } else if (s.trim().equals(Constants.AUTH_ERROR)) {
                    session.logoutUser();
                } else {
                    Toast.makeText(PostReport.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            protected String doInBackground(Void... params) {
                Upload2 u = new Upload2(PostReport.this);
                String s = u.uploadVideo(selectedImagePath, selectedImagePath, parameterHeadline, parameterEditTag, currentSpinnerPositionString, parameterDescription, place, address, sdf.format(new Date()), null, false, false, true, number, name, session.getKeyAuthToken());
                return s;
            }
        }

        UploadVideo uv = new UploadVideo();
        uv.execute();
    }

    private int getSelectedFileType(String selectedImagePath) {
        String type = selectedImagePath.substring(selectedImagePath.lastIndexOf(".") + 1);
        if (type.equals("gif")) {
            return TYPE_GIF;
        } else return TYPE_IMAGE;
    }

    private void uploadImage(String encodedImage) {

        tag = editTag.getText().toString().trim();
        final String heads = headline.getText().toString().trim();
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
                loading = ProgressDialog.show(PostReport.this, "Please wait...", "uploading", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                if (s.trim().equals("Successfully Uploaded")) {
                    openProfile();
                } else if (s.trim().equals(Constants.AUTH_ERROR)) {
                    session.logoutUser();
                } else {
                    Toast.makeText(PostReport.this, "Check details and try again.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();

                HashMap<String, String> param = new HashMap<>();


                if (image != null)
                    param.put(POST_REPORT_KEY_IMAGE, image);
                param.put(POST_REPORT_KEY_LOCATION, place);
                param.put(POST_REPORT_KEY_NAME, name);
                param.put(POST_REPORT_KEY_CATEGORY, currentSpinnerPositionString);
                param.put(POST_REPORT_KEY_TIME, timeStamp);
                param.put(POST_REPORT_KEY_ADDRESS, address);
                param.put(POST_REPORT_KEY_NUMBER, number);
                param.put(POST_REPORT_KEY_TAG, parameterEditTag);
                if (parameterHeadline != null)
                    param.put(POST_REPORT_KEY_HEADLINE, parameterHeadline);
                param.put(POST_REPORT_KEY_DESCRIPTION, parameterDescription);
                param.put("token", session.getKeyAuthToken());
                param.put("deviceid", session.getDeviceid());


                if (image != null)
                    param.put(POST_REPORT_KEY_REPORT, "image");


                String result = rh.sendPostRequest(UPLOAD_URL, param);
                return result;
            }
        }

        if (editTag.getText().toString().trim().length() > 0 && description.getText().toString().trim().length() > 0) {
            if (position_spinner > 0) {
                UploadImage u = new UploadImage();
                u.execute();

            } else Toast.makeText(PostReport.this, "Choose a Category", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(PostReport.this, "Check details", Toast.LENGTH_SHORT).show();


    }

    private void openProfile() {
        Intent i = new Intent(this, Feed.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Upload Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (previewContainer.getVisibility() == View.VISIBLE || headline.getText().toString().trim().length() > 0 || editTag.getText().toString().trim().length() > 0 || description.getText().toString().trim().length() > 0) {
            AlertDialogBox alertDialogBox = new AlertDialogBox(PostReport.this, "Discard Report?", "All your changes will be lost", "Yes", "No") {
                @Override
                void onNegativeButtonClick(DialogInterface dialog) {

                }

                @Override
                void onPositiveButtonClick(DialogInterface dialog) {
                    dialog.dismiss();
                    PostReport.super.onBackPressed();
                }
            };
            alertDialogBox.setCancellable(true);
            alertDialogBox.show();
        } else super.onBackPressed();
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

