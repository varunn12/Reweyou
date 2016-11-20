package in.reweyou.reweyou;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
    static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int REQUEST_TAKE_GALLERY_VIDEO = 5;
    private static final int IMAGE = 11;
    private static final int VIDEO = 12;
    public ImageView btn_camera;
    public ImageView btn_video;
    public ImageView btn_gif;
    Location location;
    AppLocationService appLocationService;
    ConnectionDetector cd;
    UserSessionManager session;
    String selectedImagePath;
    Boolean isInternetPresent = false;
    PermissionsChecker checker;
    private ImageView button;
    private EditText description, editTag, headline;
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
    // private LinearLayout optionsLayout;
/*    private TextInputLayout til_description;
    private TextInputLayout til_tag;
    private TextInputLayout til_headline;*/
    private int position_spinner = -1;
    private Uri uri;
    private String mCurrentPhotoPath;
    private String videoFilePath;
    private RelativeLayout previewLayout;
    private ImageView imagecancel;
    private String selectedVideoPath;
    private ImageView play;
    private int viewType = -1;
    private UploadOptions uploadOptions;
    private LinearLayout logo;


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
        if (previewLayout.getVisibility() == View.VISIBLE || headline.getText().toString().trim().length() > 0 || editTag.getText().toString().trim().length() > 0 || description.getText().toString().trim().length() > 0) {
            AlertDialogBox alertDialogBox = new AlertDialogBox(ShowImage.this, "Discard Report?", "All your changes will be lost", "Yes", "No") {
                @Override
                void onNegativeButtonClick(DialogInterface dialog) {

                }

                @Override
                void onPositiveButtonClick(DialogInterface dialog) {
                    dialog.dismiss();
                    ShowImage.super.onBackPressed();
                }
            };
            alertDialogBox.setCancellable(true);
            alertDialogBox.show();
        } else super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);

        initToolbar();

        uploadOptions = new UploadOptions(this);
        uploadOptions.initOptions();

        session = new UserSessionManager(getApplicationContext());
        checker = new PermissionsChecker(this);
        mycity = session.getLoginLocation();
        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        cd = new ConnectionDetector(ShowImage.this);
        appLocationService = new AppLocationService(
                ShowImage.this);
        utils = new ImageLoadingUtils(this);
        imageview = (ImageView) findViewById(R.id.ImageShow);
        imagecancel = (ImageView) findViewById(R.id.cancel);
        description = (EditText) findViewById(R.id.Who);
        editTag = (EditText) findViewById(R.id.tag);

        headline = (EditText) findViewById(R.id.head);
        button = (ImageView) findViewById(R.id.btn_send);
//        button.setTypeface(font);
        button.setOnClickListener(this);

        logo = (LinearLayout) findViewById(R.id.logo);
        previewLayout = (RelativeLayout) findViewById(R.id.previewLayout);
        // String show = getIntent().getStringExtra("path");

        HashMap<String, String> user = session.getUserDetails();
        name = user.get(UserSessionManager.KEY_NAME);
        number = user.get(UserSessionManager.KEY_NUMBER);
        play = (ImageView) findViewById(R.id.play);
        play.setVisibility(View.GONE);

        Intent i = getIntent();
        if (i != null) {
            if (i.hasExtra("dataImage")) {
                handleImage(i.getStringExtra("dataImage"));
            } else if (i.hasExtra("dataVideo")) {
                handleVideo(i.getStringExtra("dataVideo"));
            }
        }


        previewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (viewType) {
                    case IMAGE:
                        if (selectedImagePath != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("myData", selectedImagePath);
                            Intent in = new Intent(ShowImage.this, FullImage.class);
                            in.putExtras(bundle);
                            startActivity(in);
                        }
                        return;
                    case VIDEO:
                        Bundle bundle = new Bundle();
                        bundle.putString("myData", selectedVideoPath);
                        Intent in = new Intent(ShowImage.this, Videorow.class);
                        in.putExtras(bundle);
                        startActivity(in);

                }

            }
        });


        initCategorySpinner();


        addTextWatcher();

        imagecancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logo.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
                previewLayout.setVisibility(View.GONE);
                selectedImagePath = null;
                selectedVideoPath = null;
                imageview.setColorFilter(null);
                //optionsLayout.setVisibility(View.VISIBLE);
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

    private void addTextWatcher() {
        editTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              /*  if (s.toString().trim().length() > 0) {
                    til_tag.setErrorEnabled(false);
                } else til_tag.setError("Cannot be empty");*/
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        headline.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               /* if (s.toString().trim().length() > 0) {
                    til_headline.setErrorEnabled(false);
                } else til_headline.setError("Cannot be empty");*/
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               /* if (s.toString().trim().length() > 0) {
                    til_description.setErrorEnabled(false);
                } else til_description.setError("Cannot be empty");*/
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /*editTag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
               if(!hasFocus) til_tag.setErrorEnabled(false);
            }
        });
        headline.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) til_headline.setErrorEnabled(false);
            }
        });
        description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) til_description.setErrorEnabled(false);
            }
        });*/


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
                        if (staticSpinner.getSelectedItemPosition() == 0)
                            Toast.makeText(ShowImage.this, "Select a category", Toast.LENGTH_SHORT).show();
                        else if (headline.getText().toString().trim().length() == 0)
                            Toast.makeText(ShowImage.this, "Headline cannot be empty", Toast.LENGTH_SHORT).show();
                        else if (editTag.getText().toString().trim().length() == 0)
                            Toast.makeText(ShowImage.this, "Tag cannot be empty", Toast.LENGTH_SHORT).show();
                        else if (description.getText().toString().trim().length() == 0)
                            Toast.makeText(ShowImage.this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
                        else
                            compressImage();

                    } else {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LocationAddress locationAddress = new LocationAddress(ShowImage.this);
                        LocationAddress.getAddressFromLocation(latitude, longitude,
                                getApplicationContext(), new GeocoderHandler());
                        // Toast.makeText(CameraActivity.this,"Detecting current location...We need your current location for authenticity.",Toast.LENGTH_LONG).show();
                        button.setImageResource(R.drawable.button_send);
                        //button.setText(R.string.send);
                    }
                } else {
                    location = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        if (place != null) {
                            if (staticSpinner.getSelectedItemPosition() == 0)
                                Toast.makeText(ShowImage.this, "Select a category", Toast.LENGTH_SHORT).show();
                            else if (headline.getText().toString().trim().length() == 0)
                                Toast.makeText(ShowImage.this, "Headline cannot be empty", Toast.LENGTH_SHORT).show();
                            else if (editTag.getText().toString().trim().length() == 0)
                                Toast.makeText(ShowImage.this, "Tag cannot be empty", Toast.LENGTH_SHORT).show();
                            else if (description.getText().toString().trim().length() == 0)
                                Toast.makeText(ShowImage.this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
                            else
                                compressImage();
                        } else {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            LocationAddress locationAddress = new LocationAddress(ShowImage.this);
                            LocationAddress.getAddressFromLocation(latitude, longitude,
                                    getApplicationContext(), new GeocoderHandler());
                            //     Toast.makeText(CameraActivity.this,"Detecting current location...We need your current location for authenticity.",Toast.LENGTH_LONG).show();
                            button.setImageResource(R.drawable.button_send);
                            // button.setText(R.string.send);
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
            previewLayout.setVisibility(View.VISIBLE);
            play.setVisibility(View.VISIBLE);
            logo.setVisibility(View.GONE);
            imageview.setColorFilter(Color.argb(150, 255, 255, 255)); // White Tint


            Glide.with(ShowImage.this).load(new File(data)).into(imageview);
            selectedVideoPath = data;
        } else {
            AlertDialogBox alertDialogBox = new AlertDialogBox(ShowImage.this, "File size exceeded", "Please upload video upto 5 MB in size only...", "OKAY", null) {
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
        play.setVisibility(View.GONE);
        logo.setVisibility(View.GONE);
        imageview.setColorFilter(null);
        selectedImagePath = uploadOptions.getAbsolutePath(Uri.parse(data));
        previewLayout.setVisibility(View.VISIBLE);
        Glide.with(ShowImage.this).load(selectedImagePath).into(imageview);
        viewType = IMAGE;
    }


    public void compressImage() {

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
        } else uploadImage(null);

    }

    private void uploadImage(String encodedImage) {

        tag = editTag.getText().toString().trim();
        final String text = description.getText().toString().trim();
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
                if (image != null)
                    param.put(KEY_IMAGE, image);
                param.put(KEY_LOCATION, place);
                param.put(KEY_NAME, name);
                param.put(KEY_TAG, tag);
                param.put("type", type);
                param.put("headline", heads);
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

        if (editTag.getText().toString().trim().length() > 0 && headline.getText().toString().trim().length() > 0 && description.getText().toString().trim().length() > 0) {
            if (position_spinner > 0) {
                UploadImage u = new UploadImage();
                u.execute();

            } else Toast.makeText(ShowImage.this, "Choose a Category", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(ShowImage.this, "Check details", Toast.LENGTH_SHORT).show();


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

