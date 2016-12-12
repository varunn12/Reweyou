package in.reweyou.reweyou;

import android.Manifest;
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
import com.bumptech.glide.request.target.Target;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.body.MultipartFormDataBody;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import in.reweyou.reweyou.classes.AppLocationService;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.HandleActivityResult;
import in.reweyou.reweyou.classes.MyLocation;
import in.reweyou.reweyou.classes.UploadOptions;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.utils.Constants;

import static in.reweyou.reweyou.classes.HandleActivityResult.HANDLE_IMAGE;
import static in.reweyou.reweyou.classes.HandleActivityResult.HANDLE_VIDEO;
import static in.reweyou.reweyou.classes.UploadOptions.PERMISSION_ALL_IMAGE;
import static in.reweyou.reweyou.classes.UploadOptions.PERMISSION_ALL_VIDEO;
import static in.reweyou.reweyou.classes.UploadOptions.PERMISSION_ALL_VIDEO_CAPTURE;
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

public class PostReport extends AppCompatActivity implements View.OnClickListener {


    public static final String UPLOAD_URL = "https://www.reweyou.in/reweyou/reporting.php";

    static final String[] PERMISSIONS_LOCATION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};


    private static final String PACKAGE_URL_SCHEME = "package:";
    private static final int PERMISSION_ALL = 1;
    private final int PREVIEW_TEXT = 3;
    private final int PREVIEW_IMAGE = 4;
    private final int PREVIEW_GIF = 5;
    private final int PREVIEW_VIDEO = 6;
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

    private Uri selectedImageUri;
    private ImageView previewThumbnailView;

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

        uploadOptions = new UploadOptions(this);
        uploadOptions.initOptions();

        session = new UserSessionManager(this);
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
                if (i.getStringExtra("dataImage") != null)
                    if (!i.getStringExtra("dataImage").isEmpty())
                        handleImageOrGif(Uri.parse(i.getStringExtra("dataImage")));
            } else if (i.hasExtra("dataVideo")) {
                if (i.getStringExtra("dataVideo") != null)
                    if (!i.getStringExtra("dataVideo").isEmpty())
                        handleVideo(Uri.parse(i.getStringExtra("dataVideo")));
            }


            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {

                Log.d("type", type);
                if (type.startsWith("image/")) {
                    Uri uri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
                    if (uri != null)
                        handleImageOrGif(uri);
                    else Log.w("uri", "null");
                } else if (type.startsWith("video/")) {
                    Uri uri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
                    if (uri != null)
                        handleVideo(uri);
                    else Log.w("uri", "null");

                }

            }
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

        //preview layout views
        previewImageView = (ImageView) findViewById(R.id.ImageShow);
        previewImageViewGif = (ImageView) findViewById(R.id.GifShow);
        previewImageCancel = (ImageView) findViewById(R.id.cancel);
        previewContainer = (RelativeLayout) findViewById(R.id.previewLayout);
        previewPlayVideoButton = (ImageView) findViewById(R.id.play);
        previewThumbnailView = (ImageView) findViewById(R.id.videoShow);
        previewThumbnailView.setColorFilter(Color.argb(120, 0, 0, 0));

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

    }

    private void onbutoon() {
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
                break;
            }
            case PERMISSION_ALL_IMAGE:

                String permission = permissions[0];
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission

                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(PostReport.this, permission);
                    if (!showRationale) {
                        showPermissionDeniedDialog();
                    } else
                        showPermissionRequiredDialog(permission);


                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    UploadOptions uploadOptions = new UploadOptions(PostReport.this);
                    uploadOptions.showImageOptions();
                }

                break;
            case PERMISSION_ALL_VIDEO_CAPTURE:
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
                        UploadOptions uploadOptions = new UploadOptions(PostReport.this);
                        uploadOptions.captureVideo();

                    }
                } else
                    Toast.makeText(PostReport.this, "Please allow all permissions", Toast.LENGTH_SHORT).show();
                break;
            case PERMISSION_ALL_VIDEO:
                String permission2 = permissions[0];
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission

                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(PostReport.this, permission2);
                    if (!showRationale) {
                        showPermissionDeniedDialog();
                    } else
                        showPermissionRequiredDialog(permission2);


                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    UploadOptions uploadOptions = new UploadOptions(PostReport.this);
                    uploadOptions.showVideogallery();
                }

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
                ActivityCompat.requestPermissions(PostReport.this, p, PERMISSION_ALL);

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

    private void handleImage(Uri data) {
        showPreviewViews(PREVIEW_IMAGE);
        Glide.with(PostReport.this).load(data).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(previewImageView);
        selectedImageUri = data;
        viewType = PREVIEW_IMAGE;
    }

    private void handleVideo(Uri uri) {
        {
            Log.d("uriname", uri.toString());
            if (uri.getScheme().equals("content"))
                selectedVideoPath = uploadOptions.getAbsolutePath(uri);
            else if (uri.getScheme().equals("file"))
                selectedVideoPath = uri.getPath();
            //selectedVideoPath=uri.getPath();
            Log.d("urinameweqw", selectedVideoPath);

            if (selectedVideoPath != null) {
                final File videoFile = new File(selectedVideoPath);
                int file_size = Integer.parseInt(String.valueOf(videoFile.length() / (1024 * 1024)));
                if (file_size < 5) {
                    showPreviewViews(PREVIEW_VIDEO);
                    Glide.with(PostReport.this).load(new File(selectedVideoPath)).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(previewThumbnailView);
                    viewType = PREVIEW_VIDEO;
                } else {
                    AlertDialogBox alertDialogBox = new AlertDialogBox(PostReport.this, "File size exceeded", "Please upload video upto 5 MB in size only...", "OKAY", null) {
                        @Override
                        public void onNegativeButtonClick(DialogInterface dialog) {
                    /*Not define*/
                        }

                        @Override
                        public void onPositiveButtonClick(DialogInterface dialog) {
                            dialog.dismiss();
                            clearAttachedMediaPaths();
                            hidePreviewViews();
                            showLogoContainer();

                        }
                    };
                    alertDialogBox.setCancellable(true);
                    alertDialogBox.show();

                }
            } else Log.w("uril", "null");
        }
    }

    private void handleGif(Uri uri) {
        showPreviewViews(PREVIEW_GIF);
        Log.d("uri", uri.toString());
        if (uri.getScheme().equals("content"))
            selectedGifPath = uploadOptions.getAbsolutePath(uri);
        else if (uri.getScheme().equals("file"))
            selectedGifPath = uri.getPath();

        Log.d("uripath", selectedGifPath);

        if (selectedGifPath != null) {
            Glide.with(PostReport.this).load(selectedGifPath).asGif().into(previewImageViewGif);
            viewType = PREVIEW_GIF;
        }
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

        final ProgressDialog uploading = ProgressDialog.show(PostReport.this, "Uploading File", "Please wait...", false, false);
        AsyncHttpPost post = new AsyncHttpPost(UPLOAD_URL);

        getTimeout(fileType, post);

        MultipartFormDataBody body = new MultipartFormDataBody();

        getUploadFileExtraParams(fileType, body, encodedImage);

        body.addStringPart(POST_REPORT_KEY_LOCATION, place);
        body.addStringPart(POST_REPORT_KEY_NAME, name);
        body.addStringPart(POST_REPORT_KEY_CATEGORY, currentSpinnerPositionString);
        body.addStringPart(POST_REPORT_KEY_ADDRESS, address);
        body.addStringPart(POST_REPORT_KEY_NUMBER, number);
        body.addStringPart(POST_REPORT_KEY_TAG, parameterEditTag);
        if (parameterHeadline != null)
            body.addStringPart(POST_REPORT_KEY_HEADLINE, parameterHeadline);
        body.addStringPart(POST_REPORT_KEY_DESCRIPTION, parameterDescription);
        body.addStringPart("token", session.getKeyAuthToken());
        body.addStringPart("deviceid", session.getDeviceid());
        post.setBody(body);
        AsyncHttpClient.getDefaultInstance().executeString(post, new AsyncHttpClient.StringCallback() {
            @Override
            public void onCompleted(Exception ex, AsyncHttpResponse source, String result) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                System.out.println("Server says: " + result);
                uploading.dismiss();
                if (result.equals("Successfully Uploaded")) {
                    openProfile();
                } else if (result.trim().equals(Constants.AUTH_ERROR)) {
                    Log.d("autherror", "errorauth");
                    session.logoutUser();
                } else if (result.isEmpty()) {
                    PostReport.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PostReport.this, "file upload time out!", Toast.LENGTH_SHORT).show();

                        }
                    });
                } else {
                    PostReport.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PostReport.this, "Something went wrong!", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
    }

    private AsyncHttpPost getTimeout(int fileType, AsyncHttpPost post) {
        switch (fileType) {
            case PREVIEW_IMAGE:
                post.setTimeout(20000);
                return post;
            case PREVIEW_VIDEO:
                post.setTimeout(120000);
                return post;
            case PREVIEW_GIF:
                post.setTimeout(30000);
                return post;
            case PREVIEW_TEXT:
                post.setTimeout(15000);
                return post;
            default:
                return null;
        }
    }

    private MultipartFormDataBody getUploadFileExtraParams(int fileType, MultipartFormDataBody body, String encodedImage) {
        switch (fileType) {
            case PREVIEW_IMAGE:
                body.addStringPart(POST_REPORT_KEY_REPORT, "image");
                body.addStringPart(POST_REPORT_KEY_IMAGE, encodedImage);
                return body;
            case PREVIEW_VIDEO:
                body.addFilePart("myFile", new File(selectedVideoPath));
                body.addStringPart(POST_REPORT_KEY_REPORT, "video");
                body.addStringPart(POST_REPORT_KEY_IMAGE, encodedImage);
                return body;
            case PREVIEW_GIF:
                body.addFilePart("myFile", new File(selectedGifPath));
                body.addStringPart(POST_REPORT_KEY_REPORT, "gif");
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
        int dataType = new HandleActivityResult().handleResult(requestCode, resultCode, data);
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

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                handleImage(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    private void handleImageOrGif(Uri uri) {


        getContentResolver().getType(uri);
        String extension = getContentResolver().getType(uri).substring(getContentResolver().getType(uri).lastIndexOf("/") + 1);
        Log.d("extension", extension);
        if (extension.equals("gif")) {
            handleGif(uri);
        } else {
            startImageCropActivity(uri);
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


}

