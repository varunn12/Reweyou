package in.reweyou.reweyou;

import android.Manifest;
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
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.VideoPicker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.callbacks.VideoPickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.kbeanie.multipicker.api.entity.ChosenVideo;
import com.kbeanie.multipicker.utils.IntentUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import in.reweyou.reweyou.classes.AppLocationService;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.MyLocation;
import in.reweyou.reweyou.classes.UploadOptions;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.customView.CustomSigninDialog;
import in.reweyou.reweyou.utils.Constants;

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

public class PostReport extends AppCompatActivity implements View.OnClickListener, ImagePickerCallback, VideoPickerCallback {


    public static final String UPLOAD_URL = "https://www.reweyou.in/reweyou/reporting.php";

    static final String[] PERMISSIONS_LOCATION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};


    private static final String PACKAGE_URL_SCHEME = "package:";
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 1;
    private static final String TAG = PostReport.class.getSimpleName();
    private static final int PERMISSION_STORAGE_REQUEST_CODE = 12;
    private static final int PERMISSION_STORAGE_REQUEST_CODE_VIDEO = 13;
    private static final int PERMISSION_VIDEO_CAPTURE_REQUEST_CODE = 14;
    private final int PREVIEW_TEXT = 3;
    private final int PREVIEW_IMAGE = 4;
    private final int PREVIEW_GIF = 5;
    private final int PREVIEW_VIDEO = 6;
    private final String[] PERMISSION_VIDEO_CAPTURE = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    Location location;
    AppLocationService appLocationService;
    ConnectionDetector cd;
    UserSessionManager session;
    String selectedGifPath;
    Boolean isInternetPresent = false;
    PermissionsChecker checker;
    private ImageView sendButton;
    private EditText description, editTag, headline;
    private ImageView previewImageView;
    private String place;
    private String address, mycity;
    private String name;
    private String tag, currentSpinnerPositionString;
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
    private String parameterHeadline;
    private String parameterEditTag;
    private String parameterDescription;
    private LinearLayout bottomContainer;
    private View bottomline;
    private ImageView previewImageViewGif;
    private boolean activityOpen;
    private String selectedImageUri;
    private ImageView previewThumbnailView;
    private RelativeLayout hangingNoti;
    private TextView editlocation;
    private ImagePicker imagePicker;
    private VideoPicker videoPicker;

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

        findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    showPickImage();
                }
            }
        });

        findViewById(R.id.btn_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVideoOptions();

            }
        });

        findViewById(R.id.btn_gif).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    showPickImage();
                }
            }
        });


        session = new UserSessionManager(this);

        if (!session.checkLoginSplash()) {
            final CustomSigninDialog customSigninDialog = new CustomSigninDialog(PostReport.this);
            customSigninDialog.show();
        }

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

           /* if (i.hasExtra("dataImage")) {
                if (i.getStringExtra("dataImage") != null)
                    if (!i.getStringExtra("dataImage").isEmpty())
                        handleImageOrGif(Uri.parse(i.getStringExtra("dataImage")));
            } *//*else if (i.hasExtra("dataVideo")) {
                if (i.getStringExtra("dataVideo") != null)
                    if (!i.getStringExtra("dataVideo").isEmpty())
                        handleVideo(Uri.parse(i.getStringExtra("dataVideo")));
            }*//*
*/

            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                handleMultipleShares(type);

                /*Log.d("type", type);
                if (type.startsWith("image/")) {
                    Uri uri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
                    if (uri != null)
                        handleImageOrGif(uri);
                    else Log.w("uri", "null");
                } else if (type.startsWith("video/")) {
                   *//**//* Uri uri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
                    if (uri != null)
                        handleVideo(uri);
                    else Log.w("uri", "null");*//**//*

                }*/

            }

        }
        showHangingNoti();
    }

    private void handleMultipleShares(String type) {
        if (type.startsWith("image")) {
            ImagePicker picker = new ImagePicker(this);
            picker.setImagePickerCallback(this);
            picker.submit(IntentUtils.getPickerIntentForSharing(getIntent()));
        } else if (type.startsWith("video")) {
            VideoPicker picker = new VideoPicker(this);
            picker.setVideoPickerCallback(this);
            picker.submit(IntentUtils.getPickerIntentForSharing(getIntent()));
        }
    }

    private void showPickImage() {
        imagePicker = new ImagePicker(PostReport.this);
        imagePicker.setImagePickerCallback(new ImagePickerCallback() {
                                               @Override
                                               public void onImagesChosen(List<ChosenImage> images) {


                                                   // Display images


                                                   onImageChoosenbyUser(images);

                                               }

                                               @Override
                                               public void onError(String message) {
                                                   // Do error handling
                                                   Log.e(TAG, "onError: " + message);
                                               }
                                           }
        );

        imagePicker.shouldGenerateMetadata(true);
        imagePicker.shouldGenerateThumbnails(false);
        imagePicker.pickImage();

    }

    private void onImageChoosenbyUser(List<ChosenImage> images) {
        if (images != null) {

            try {

                Log.d(TAG, "onImagesChosen: size" + images.size());
                if (images.size() > 0) {
                    Log.d(TAG, "onImagesChosen: path" + images.get(0).getOriginalPath() + "  %%%   " + images.get(0).getThumbnailSmallPath());

                    if (images.get(0).getOriginalPath() != null) {
                        Log.d(TAG, "onImagesChosen: " + images.get(0).getFileExtensionFromMimeTypeWithoutDot());
                        if (images.get(0).getFileExtensionFromMimeTypeWithoutDot().equals("gif")) {
                            handleGif(images.get(0).getOriginalPath());

                        } else {
                            startImageCropActivity(Uri.parse(images.get(0).getQueryUri()));
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(PostReport.this, "Something went wrong. ErrorCode: 19", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showPickVideo() {
        videoPicker = new VideoPicker(PostReport.this);
        videoPicker.setVideoPickerCallback(new VideoPickerCallback() {
            @Override
            public void onVideosChosen(List<ChosenVideo> list) {
                onVideoChoosenByUser(list);

            }

            @Override
            public void onError(String s) {

            }
        });
        videoPicker.shouldGenerateMetadata(true);
        videoPicker.shouldGeneratePreviewImages(false);
        videoPicker.pickVideo();
    }

    private void onVideoChoosenByUser(List<ChosenVideo> list) {
        if (list != null) {
            try {
                if (list.size() > 0) {
                    Log.d(TAG, "onVideosChosen: " + list.get(0).getOriginalPath());
                    if (list.get(0).getOriginalPath() != null) {
                        Log.d(TAG, "onVideosChosen: " + list.get(0).getSize());
                        if ((list.get(0).getSize() / (1024 * 1024)) < 5)
                            handleVideo(list.get(0).getOriginalPath());
                        else
                            Toast.makeText(PostReport.this, "File size exceeds 5 MB", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(PostReport.this, "Something went wrong. ErrorCode: 20", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void showHangingNoti() {


        if (!session.getFirstLoad()) {
            session.setFirstLoad();
            hangingNoti.setVisibility(View.VISIBLE);
            TranslateAnimation mAnimation = new TranslateAnimation(
                    TranslateAnimation.ABSOLUTE, 0f,
                    TranslateAnimation.ABSOLUTE, 0f,
                    TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                    TranslateAnimation.RELATIVE_TO_PARENT, 0.03f);
            mAnimation.setDuration(400);
            mAnimation.setRepeatCount(-1);
            mAnimation.setRepeatMode(Animation.REVERSE);
            mAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            hangingNoti.setAnimation(mAnimation);
            hangingNoti.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hangingNoti.setAnimation(null);
                    hangingNoti.setVisibility(View.GONE);
                    hangingNoti.setOnTouchListener(null);

                    return true;
                }


            });
        }
    }


    private void setClickListeners() {
        previewImageCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewContainer.setVisibility(View.GONE);
                hidePreviewViews();
                showLogoContainer();
                clearAttachedMediaPaths();
            }
        });

    }

    private void showLogoContainer() {
        logoContainer.setVisibility(View.VISIBLE);
    }

    private void clearAttachedMediaPaths() {
        selectedGifPath = null;
        selectedImageUri = null;
        selectedVideoPath = null;
        viewType = -1;
    }

    private void startFullImageActivity() {
        Bundle bundle = new Bundle();
        bundle.putString("myData", String.valueOf(selectedImageUri));
        Intent in = new Intent(PostReport.this, FullImage.class);
        in.putExtras(bundle);
        startActivity(in);
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
        editlocation = (TextView) findViewById(R.id.editlocation);

        //preview layout views
        previewImageView = (ImageView) findViewById(R.id.ImageShow);
        previewImageViewGif = (ImageView) findViewById(R.id.GifShow);
        previewImageCancel = (ImageView) findViewById(R.id.cancel);
        previewContainer = (RelativeLayout) findViewById(R.id.previewLayout);
        previewPlayVideoButton = (ImageView) findViewById(R.id.play);
        previewThumbnailView = (ImageView) findViewById(R.id.videoShow);
        previewThumbnailView.setColorFilter(Color.argb(120, 0, 0, 0));

        hangingNoti = (RelativeLayout) findViewById(R.id.haning);
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

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (bottomContainer != null && bottomline != null) {
                                bottomContainer.setVisibility(View.VISIBLE);
                                bottomline.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 100);

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

    }

    private void onbutoon() {


        if (!session.checkLoginSplash()) {
            final CustomSigninDialog customSigninDialog = new CustomSigninDialog(PostReport.this);
            customSigninDialog.show();
        } else {
            if (editlocation.getText().toString().trim().length() > 0) {
                place = editlocation.getText().toString();
                address = place;
                if (validateFields()) {

                    switch (viewType) {
                        case PREVIEW_IMAGE:
                            compressSelectedImage();
                            break;
                        case PREVIEW_VIDEO:
                            compressVideo();
                            break;
                        case PREVIEW_GIF:
                            compressGif();
                            break;
                        default:
                            uploadFile(PREVIEW_TEXT, null);
                    }

                }
            } else {
                if (!hasPermissions(this, PERMISSIONS_LOCATION)) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, PERMISSION_LOCATION_REQUEST_CODE);
                } else
                    permissionGranted();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: " + requestCode);
        switch (requestCode) {
            case PERMISSION_LOCATION_REQUEST_CODE: {

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
                break;
            }
            case PERMISSION_STORAGE_REQUEST_CODE:

                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Permission denied by user", Toast.LENGTH_SHORT).show();

                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showPickImage();
                }

                break;
            case PERMISSION_STORAGE_REQUEST_CODE_VIDEO:

                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Permission denied by user", Toast.LENGTH_SHORT).show();

                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showPickVideo();
                }

                break;
            case PERMISSION_VIDEO_CAPTURE_REQUEST_CODE:
                boolean temp = false;
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            temp = true;
                            break;
                        }
                    }
                    if (temp)
                        Toast.makeText(PostReport.this, "Please allow all permissions", Toast.LENGTH_SHORT).show();
                    else {
                        startActivity(new Intent(PostReport.this, VideoCapturetest.class));

                    }
                } else
                    Toast.makeText(PostReport.this, "Please allow all permissions", Toast.LENGTH_SHORT).show();
                break;

        }
    }


    private void permissionGranted() {

        Log.d("rea", "1233");

        final ProgressDialog pd = new ProgressDialog(PostReport.this);

        pd.setCancelable(false);
        pd.setMessage("Fetching current location! Please Wait.");
        pd.show();


        if (isLocationEnabled(PostReport.this)) {
            final MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                @Override
                public void gotLocation(Location location) {
                    //Got the location!
                    if (location != null) {
                        Log.d("location", String.valueOf(location.getLatitude()));
                        String getaddress = getCompleteAddressString(location.getLatitude(), location.getLongitude());
                        if (!getaddress.isEmpty()) {
                            address = getaddress;
                        } else {
                            address = session.getLoginLocation();
                            place = address;
                        }
                    } else {
                        place = session.getLoginLocation();
                        address = place;
                    }
                    pd.dismiss();
                    Log.d("place", place);
                    Log.d("address", address);

                    PostReport.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (validateFields()) {

                                switch (viewType) {
                                    case PREVIEW_IMAGE:
                                        compressSelectedImage();
                                        break;
                                    case PREVIEW_VIDEO:
                                        compressVideo();
                                        break;
                                    case PREVIEW_GIF:
                                        compressGif();
                                        break;
                                    default:
                                        uploadFile(PREVIEW_TEXT, null);
                                }

                            }
                        }
                    });


                }


            };

            MyLocation myLocation = new MyLocation();
            if (!myLocation.getLocation(this, locationResult)) {
                address = session.getLoginLocation();
                place = address;
                pd.dismiss();
                Log.d("place", place);
                Log.d("address", address);
                if (validateFields()) {
                    switch (viewType) {
                        case PREVIEW_IMAGE:
                            compressSelectedImage();
                            break;
                        case PREVIEW_VIDEO:
                            compressVideo();
                            break;
                        case PREVIEW_GIF:
                            compressGif();
                            break;
                        default:
                            uploadFile(PREVIEW_TEXT, null);
                    }

                }
            }

        } else {
            address = session.getLoginLocation();
            place = address;
            pd.dismiss();
            Log.d("place", place);
            Log.d("address", address);
            if (validateFields()) {

                switch (viewType) {
                    case PREVIEW_IMAGE:
                        compressSelectedImage();
                        break;
                    case PREVIEW_VIDEO:
                        compressVideo();
                        break;
                    case PREVIEW_GIF:
                        compressGif();
                        break;
                    default:
                        uploadFile(PREVIEW_TEXT, null);
                }

            }
        }

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
            public void onNegativeButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                //registerUser();
                permissionGranted();
            }

            @Override
            public void onPositiveButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                String[] p = {permission};
                ActivityCompat.requestPermissions(PostReport.this, p, PERMISSION_LOCATION_REQUEST_CODE);

            }
        };
        alertDialogBox.setCancellable(true);
        alertDialogBox.show();
    }


    private void showPermissionDeniedDialog() {
        AlertDialogBox alertDialogBox = new AlertDialogBox(PostReport.this, "Permission Denied", getResources().getString(R.string.permission_denied_location), "settings", "okay") {
            @Override
            public void onNegativeButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                // registerUser();
                permissionGranted();
            }

            @Override
            public void onPositiveButtonClick(DialogInterface dialog) {
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
        Log.d("here", "here");
        Glide.with(PostReport.this).load(new File(selectedVideoPath)).asBitmap()
                .toBytes(Bitmap.CompressFormat.JPEG, 60)
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
                          public void onResourceReady(final byte[] resource, GlideAnimation<? super byte[]> ignore) {
                              final String encodedImage = Base64.encodeToString(resource, Base64.DEFAULT);

                              uploadVideo(encodedImage);

                          }

                          @Override
                          public void onLoadFailed(Exception ex, Drawable ignore) {
                              Log.d("ex", ex.getMessage());

                          }
                      }

                );

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void handleImage(String data) {
        clearAttachedMediaPaths();
        showPreviewViews(PREVIEW_IMAGE);
        Glide.with(PostReport.this).load(data).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(previewImageView);
        selectedImageUri = data;
        viewType = PREVIEW_IMAGE;
    }

    private void handleVideo(String path) {
        clearAttachedMediaPaths();
        selectedVideoPath = path;
        showPreviewViews(PREVIEW_VIDEO);
        Glide.with(PostReport.this).load(new File(selectedVideoPath)).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(previewThumbnailView);
        viewType = PREVIEW_VIDEO;

    }

    private void handleGif(String path) {
        clearAttachedMediaPaths();
        showPreviewViews(PREVIEW_GIF);
        selectedGifPath = path;
        Glide.with(PostReport.this).load(selectedGifPath).asGif().into(previewImageViewGif);
        viewType = PREVIEW_GIF;
    }

    private void showPreviewViews(int code) {
        hidePreviewViews();
        showPreviewContainer();
        switch (code) {
            case PREVIEW_IMAGE:
                showImagePreviewViews();
                break;
            case PREVIEW_VIDEO:
                showVideoPreviewViews();
                break;
            case PREVIEW_GIF:
                showGifPreviewViews();
                break;
        }

    }

    private void showGifPreviewViews() {
        previewImageViewGif.setVisibility(View.VISIBLE);
    }

    private void showVideoPreviewViews() {
        previewPlayVideoButton.setVisibility(View.VISIBLE);
        previewThumbnailView.setVisibility(View.VISIBLE);
    }

    private void showImagePreviewViews() {
        previewImageView.setVisibility(View.VISIBLE);
    }

    private void showPreviewContainer() {
        previewContainer.setVisibility(View.VISIBLE);
    }

    private void hidePreviewViews() {
        previewImageView.setVisibility(View.GONE);
        previewImageViewGif.setVisibility(View.GONE);
        previewPlayVideoButton.setVisibility(View.GONE);
        previewThumbnailView.setVisibility(View.GONE);
    }

    public void compressGif() {

        uploadSelectedGif();

    }

    private void compressSelectedImage() {
        if (selectedImageUri != null) {
            Glide
                    .with(this)
                    .load(selectedImageUri)
                    .asBitmap()
                    .toBytes(Bitmap.CompressFormat.JPEG, 95)
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
        } else Log.w("compressSelectedImage", "selected path is null");
    }

    private void uploadImage(final String encodedImage) {
        uploadFile(PREVIEW_IMAGE, encodedImage);
    }

    private void uploadVideo(String encodedImage) {
        uploadFile(PREVIEW_VIDEO, encodedImage);
    }

    private void uploadSelectedGif() {
        uploadFile(PREVIEW_GIF, null);
    }

    private void uploadFile(int fileType, String encodedImage) {

        final ProgressDialog uploading = ProgressDialog.show(PostReport.this, "Uploading", "Please wait...", false, false);

        RequestParams params = new RequestParams();
        try {
            getUploadFileExtraParams(fileType, params, encodedImage);
            params.add(POST_REPORT_KEY_LOCATION, place);
            params.add(POST_REPORT_KEY_NAME, name);
            params.add(POST_REPORT_KEY_CATEGORY, currentSpinnerPositionString);
            params.add(POST_REPORT_KEY_ADDRESS, address);
            params.add(POST_REPORT_KEY_NUMBER, number);
            params.add(POST_REPORT_KEY_TAG, parameterEditTag);
            if (parameterHeadline != null)
                params.add(POST_REPORT_KEY_HEADLINE, parameterHeadline);
            params.add(POST_REPORT_KEY_DESCRIPTION, parameterDescription);
            params.add("token", session.getKeyAuthToken());
            params.add("deviceid", session.getDeviceid());

            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

            client.setMaxRetriesAndTimeout(0, 0);
            client.setTimeout(10000);
            client.setResponseTimeout(60000);
            client.post(UPLOAD_URL, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    uploading.dismiss();
                    Log.w("reach", "onsuccess");

                    try {
                        Log.w("reach", "onsuccess1");
                        String result = new String(responseBody, "UTF-8");
                        if (result.equals("Successfully Uploaded")) {
                            openProfile();
                        } else if (result.trim().equals(Constants.AUTH_ERROR)) {
                            Log.d("autherror", "errorauth");
                            session.logoutUser();

                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        Log.w("reach", "onerror2");

                    }

                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                    uploading.dismiss();
                    Log.e(TAG, "onFailure: statusCode: " + statusCode);
                    Log.w("reach", "one3");


                }

                @Override
                public void onRetry(int retryNo) {
                    super.onRetry(retryNo);
                }

                @Override
                public void onStart() {
                    super.onStart();
                    Log.w("reach", "start");

                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("error", "filenull");
        }


    }

    private RequestParams getUploadFileExtraParams(int fileType, RequestParams body, String encodedImage) throws FileNotFoundException {
        switch (fileType) {
            case PREVIEW_IMAGE:
                body.add(POST_REPORT_KEY_REPORT, "image");
                body.add(POST_REPORT_KEY_IMAGE, encodedImage);
                return body;
            case PREVIEW_VIDEO:
                body.put("myFile", new File(selectedVideoPath));
                body.add(POST_REPORT_KEY_REPORT, "video");
                body.add(POST_REPORT_KEY_IMAGE, encodedImage);
                return body;
            case PREVIEW_GIF:
                body.put("myFile", new File(selectedGifPath));
                body.add(POST_REPORT_KEY_REPORT, "gif");
                return body;
            case PREVIEW_TEXT:
                return body;
            default:
                return body;
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("reached", "activigty");
        super.onActivityResult(requestCode, resultCode, data);
       /* int dataType = new HandleActivityResult().handleResult(requestCode, resultCode, data);
        switch (dataType) {
            case HANDLE_IMAGE:
                Uri uri = data.getData();
                if (uri != null)
                    handleImageOrGif(uri);
                else Log.w("uri", "null");
                break;
            case HANDLE_VIDEO:
                Uri uri2 = data.getData();
                if (uri2 != null)
                    handleVideo(uri2);
                else Log.w("uri", "null");
                break;
            default:
                break;
        }
*/

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                handleImage(result.getUri().toString());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                imagePicker.submit(data);
            }
            if (requestCode == Picker.PICK_VIDEO_DEVICE) {
                videoPicker.submit(data);
            }
        }

    }

    private void startImageCropActivity(Uri data) {
        CropImage.activity(data)
                .setActivityTitle("Crop Image")
                .setBackgroundColor(Color.parseColor("#90000000"))
                .setBorderCornerColor(getResources().getColor(R.color.colorPrimaryDark))
                .setBorderLineColor(getResources().getColor(R.color.colorPrimary))
                .setGuidelinesColor(getResources().getColor(R.color.divider))
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    public void onBackPressed() {
        if (previewContainer.getVisibility() == View.VISIBLE || headline.getText().toString().trim().length() > 0 || editTag.getText().toString().trim().length() > 0 || description.getText().toString().trim().length() > 0) {
            AlertDialogBox alertDialogBox = new AlertDialogBox(PostReport.this, "Discard Report?", "All your changes will be lost", "Yes", "No") {
                @Override
                public void onNegativeButtonClick(DialogInterface dialog) {

                }

                @Override
                public void onPositiveButtonClick(DialogInterface dialog) {
                    dialog.dismiss();
                    PostReport.super.onBackPressed();
                }
            };
            alertDialogBox.setCancellable(true);
            alertDialogBox.show();
        } else super.onBackPressed();
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                place = returnedAddress.getLocality();
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("loction address", "" + strReturnedAddress.toString());
            } else {
                Log.w("loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("loction address", "Canont get Address!");
        }
        return strAdd;
    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Storage Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Storage Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE_REQUEST_CODE);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Storage Permission is auto granted for sdk<23");
            return true;
        }
    }

    public boolean isStoragePermissionGrantedVideo() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Storage Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Storage Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE_REQUEST_CODE_VIDEO);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Storage Permission is auto granted for sdk<23");
            return true;
        }
    }

    public boolean isVideoCapturePermissionGranted() {
        if (hasPermissions(this, PERMISSION_VIDEO_CAPTURE)) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_VIDEO_CAPTURE_REQUEST_CODE);
            return false;
        }
    }

    public void showVideoOptions() {
        Context context = PostReport.this;

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

    private void showVideogallery() {
        if (isStoragePermissionGrantedVideo()) {
            showPickVideo();
        }
    }

    private void captureVideo() {
        if (isVideoCapturePermissionGranted()) {
            startActivity(new Intent(PostReport.this, VideoCapturetest.class));
        }
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
    public void onImagesChosen(List<ChosenImage> list) {
        onImageChoosenbyUser(list);
    }

    @Override
    public void onError(String s) {
        Toast.makeText(PostReport.this, "Something went wrong", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onVideosChosen(List<ChosenVideo> list) {
        onVideoChoosenByUser(list);

    }
}

