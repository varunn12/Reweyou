package in.reweyou.reweyou;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import in.reweyou.reweyou.classes.AppLocationService;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.HandleActivityResult;
import in.reweyou.reweyou.classes.ImageLoadingUtils;
import in.reweyou.reweyou.classes.LocationAddress;
import in.reweyou.reweyou.classes.RequestHandler;
import in.reweyou.reweyou.classes.UploadOptions;
import in.reweyou.reweyou.classes.UserSessionManager;

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
import static in.reweyou.reweyou.utils.Constants.POST_REPORT_KEY_TOKEN;

public class PostReport extends AppCompatActivity implements View.OnClickListener {


    public static final String UPLOAD_URL = "https://www.reweyou.in/reweyou/reporting.php";

    static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int IMAGE = 11;
    private static final int VIDEO = 12;
    private static final int TYPE_GIF = 19;
    private static final int TYPE_IMAGE = 20;
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
        }


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

        //preview layout views
        previewImageView = (ImageView) findViewById(R.id.ImageShow);
        previewImageCancel = (ImageView) findViewById(R.id.cancel);
        previewContainer = (RelativeLayout) findViewById(R.id.previewLayout);
        previewPlayVideoButton = (ImageView) findViewById(R.id.play);
        //click listners for preview layout views
        setClickListeners();


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
        }
    }

    private boolean validateFields() {
        if (staticSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(PostReport.this, "Select a category", Toast.LENGTH_SHORT).show();
            return false;
        } else if (headline.getText().toString().trim().length() == 0) {
            Toast.makeText(PostReport.this, "Headline cannot be empty", Toast.LENGTH_SHORT).show();
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
                                } else {
                                    Toast.makeText(PostReport.this, s, Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            protected String doInBackground(Void... params) {
                                Upload2 u = new Upload2();
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
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
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
        super.onActivityResult(requestCode, resultCode, data);
        int dataType = new HandleActivityResult().handleResult(requestCode, resultCode, data);
        switch (dataType) {
            case HANDLE_IMAGE:
                handleImage(data.getData().toString());
                break;
            case HANDLE_VIDEO:
                handleVideo(uploadOptions.getAbsolutePath(data.getData()));
                break;
            default:
                finish();
        }

    }

    private void handleVideo(String data) {

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

            previewImageView.setAdjustViewBounds(true);

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

    private void handleImage(String data) {


        previewPlayVideoButton.setVisibility(View.GONE);
        logoContainer.setVisibility(View.GONE);
        previewImageView.setColorFilter(null);

        selectedImagePath = uploadOptions.getAbsolutePath(Uri.parse(data));

        String type = selectedImagePath.substring(selectedImagePath.lastIndexOf(".") + 1);
        if (type.equals("gif")) {
            selectedImagePath = uploadOptions.getAbsolutePath(Uri.parse(data));
            previewContainer.setVisibility(View.VISIBLE);
            previewImageView.setAdjustViewBounds(false);
            Glide.with(PostReport.this).load(selectedImagePath).asGif().into(previewImageView);
            viewType = IMAGE;
        } else {
            selectedImagePath = uploadOptions.getAbsolutePath(Uri.parse(data));
            previewContainer.setVisibility(View.VISIBLE);
            previewImageView.setAdjustViewBounds(true);

            Glide.with(PostReport.this).load(selectedImagePath).into(previewImageView);
            viewType = IMAGE;
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
                } else {
                    Toast.makeText(PostReport.this, s, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            protected String doInBackground(Void... params) {
                Upload2 u = new Upload2();
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
                } else {
                    Toast.makeText(PostReport.this, "Check details and try again.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();

                HashMap<String, String> param = new HashMap<>();

                param.put(POST_REPORT_KEY_HEADLINE, parameterHeadline);
                if (image != null)
                param.put(POST_REPORT_KEY_IMAGE, image);
                param.put(POST_REPORT_KEY_LOCATION, place);
                param.put(POST_REPORT_KEY_NAME, name);
                param.put(POST_REPORT_KEY_TAG, parameterEditTag);
                param.put(POST_REPORT_KEY_CATEGORY, currentSpinnerPositionString);
                param.put(POST_REPORT_KEY_TIME, timeStamp);
                param.put(POST_REPORT_KEY_ADDRESS, address);
                param.put(POST_REPORT_KEY_NUMBER, number);
                param.put(POST_REPORT_KEY_DESCRIPTION, parameterDescription);
                param.put(POST_REPORT_KEY_TOKEN, session.getKeyAuthToken());
                if (image != null)
                    param.put(POST_REPORT_KEY_REPORT, "image");


                String result = rh.sendPostRequest(UPLOAD_URL, param);
                return result;
            }
        }

        if (editTag.getText().toString().trim().length() > 0 && headline.getText().toString().trim().length() > 0 && description.getText().toString().trim().length() > 0) {
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

