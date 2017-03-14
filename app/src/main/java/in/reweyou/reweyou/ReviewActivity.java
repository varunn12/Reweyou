package in.reweyou.reweyou;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.gson.reflect.TypeToken;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.List;

import in.reweyou.reweyou.adapter.ReviewAdapter;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.customView.CustomSigninDialog;
import in.reweyou.reweyou.customView.PreCachingLayoutManager;
import in.reweyou.reweyou.model.ReviewModel;

public class ReviewActivity extends AppCompatActivity {

    private static final String TAG = ReviewActivity.class.getName();
    private static final int PERMISSION_STORAGE_REQUEST_CODE = 12;
    private RecyclerView recyclerView;
    private ReviewAdapter adapter;
    private String headline;
    private String description;
    private String rating;
    private String user;
    private String review;
    private String tag;
    private TextView tvheadline, tvdescription, tvrating, tvreview, tvuser, remove;
    private ImageView image;
    private String imageurl;
    private String topicid;
    private String videourl;
    private String gifurl;
    private String name;
    private UserSessionManager sessionManager;
    private ImageView send, reim;
    private EditText edittext;
    private AVLoadingIndicatorView loadingcircular;
    private String status;
    private View divider2;
    private LinearLayout b1;
    private TextView ratetext;
    private LinearLayout c1;
    private int numrating = 0;
    private TextView noreviewyet;
    private ImageView camera;
    private ImagePicker imagePicker;
    private String selectedImageUri;
    private String encodedImage;
    private ProgressDialog progressDialog;
    private String privacy;
    private String passcode = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        loadingcircular = (AVLoadingIndicatorView) findViewById(R.id.avi);
        loadingcircular.show();
        sessionManager = new UserSessionManager(this);

        final ImageView ra1 = (ImageView) findViewById(R.id.ra1);
        final ImageView ra2 = (ImageView) findViewById(R.id.ra2);
        final ImageView ra3 = (ImageView) findViewById(R.id.ra3);
        final ImageView ra4 = (ImageView) findViewById(R.id.ra4);
        final ImageView ra5 = (ImageView) findViewById(R.id.ra5);
        ra1.setColorFilter(Color.parseColor("#e0e0e0"));
        ra2.setColorFilter(Color.parseColor("#e0e0e0"));
        ra3.setColorFilter(Color.parseColor("#e0e0e0"));
        ra4.setColorFilter(Color.parseColor("#e0e0e0"));
        ra5.setColorFilter(Color.parseColor("#e0e0e0"));

        ra1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ra1.setColorFilter(ContextCompat.getColor(ReviewActivity.this, R.color.rating1));
                ra2.setColorFilter(Color.parseColor("#e0e0e0"));
                ra3.setColorFilter(Color.parseColor("#e0e0e0"));
                ra4.setColorFilter(Color.parseColor("#e0e0e0"));
                ra5.setColorFilter(Color.parseColor("#e0e0e0"));

                numrating = 1;

            }
        });

        ra2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ra1.setColorFilter(ContextCompat.getColor(ReviewActivity.this, R.color.rating2));
                ra2.setColorFilter(ContextCompat.getColor(ReviewActivity.this, R.color.rating2));
                ra3.setColorFilter(Color.parseColor("#e0e0e0"));
                ra4.setColorFilter(Color.parseColor("#e0e0e0"));
                ra5.setColorFilter(Color.parseColor("#e0e0e0"));
                numrating = 2;


            }
        });

        ra3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ra1.setColorFilter(ContextCompat.getColor(ReviewActivity.this, R.color.rating3));
                ra2.setColorFilter(ContextCompat.getColor(ReviewActivity.this, R.color.rating3));
                ra3.setColorFilter(ContextCompat.getColor(ReviewActivity.this, R.color.rating3));

                ra4.setColorFilter(Color.parseColor("#e0e0e0"));
                ra5.setColorFilter(Color.parseColor("#e0e0e0"));
                numrating = 3;


            }
        });

        ra4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ra1.setColorFilter(ContextCompat.getColor(ReviewActivity.this, R.color.rating4));
                ra2.setColorFilter(ContextCompat.getColor(ReviewActivity.this, R.color.rating4));
                ra3.setColorFilter(ContextCompat.getColor(ReviewActivity.this, R.color.rating4));
                ra4.setColorFilter(ContextCompat.getColor(ReviewActivity.this, R.color.rating4));


                ra5.setColorFilter(Color.parseColor("#e0e0e0"));
                numrating = 4;

            }
        });

        ra5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ra1.setColorFilter(ContextCompat.getColor(ReviewActivity.this, R.color.rating5));
                ra2.setColorFilter(ContextCompat.getColor(ReviewActivity.this, R.color.rating5));
                ra3.setColorFilter(ContextCompat.getColor(ReviewActivity.this, R.color.rating5));
                ra4.setColorFilter(ContextCompat.getColor(ReviewActivity.this, R.color.rating5));
                ra5.setColorFilter(ContextCompat.getColor(ReviewActivity.this, R.color.rating5));


                numrating = 5;

            }
        });


        tvheadline = (TextView) findViewById(R.id.headline);
        remove = (TextView) findViewById(R.id.remove);
        noreviewyet = (TextView) findViewById(R.id.noreviewyet);
        ratetext = (TextView) findViewById(R.id.ratetext);
        c1 = (LinearLayout) findViewById(R.id.c1);
        b1 = (LinearLayout) findViewById(R.id.b1);
        divider2 = findViewById(R.id.divider2);
        tvdescription = (TextView) findViewById(R.id.description);
        tvrating = (TextView) findViewById(R.id.rating);
        tvreview = (TextView) findViewById(R.id.review);
        tvuser = (TextView) findViewById(R.id.user);
        image = (ImageView) findViewById(R.id.image);
        reim = (ImageView) findViewById(R.id.reim);
        send = (ImageView) findViewById(R.id.send);
        camera = (ImageView) findViewById(R.id.btn_image);
        edittext = (EditText) findViewById(R.id.desc);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) ReviewActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
                if (privacy.equals("Private"))
                    showPassCodeDialog();
                else
                    uploadReview();


            }
        });
        send.setClickable(false);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    showPickImage();
                }
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAttachedMediaPaths();
                reim.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
            }
        });
        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    send.setClickable(false);
                    send.setImageResource(R.drawable.button_send_disable);

                } else {
                    send.setClickable(true);
                    send.setImageResource(R.drawable.button_send_comments);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!sessionManager.checkLoginSplash())
            edittext.setHint("Sign in to review...");

        if (getIntent() != null) {
            Intent i = getIntent();
            headline = i.getStringExtra("headline");
            description = i.getStringExtra("description");
            rating = i.getStringExtra("rating");
            user = i.getStringExtra("user");
            review = i.getStringExtra("review");
            tag = i.getStringExtra("tag");
            imageurl = i.getStringExtra("image");
            videourl = i.getStringExtra("video");
            name = i.getStringExtra("name");
            gifurl = i.getStringExtra("gif");
            topicid = i.getStringExtra("topicid");
            status = i.getStringExtra("status");
            privacy = i.getStringExtra("privacy");
        }


        getSupportActionBar().setTitle(tag);

        tvheadline.setText(headline);
        tvdescription.setText(description);
        tvrating.setText(rating);
        tvreview.setText(review);
        tvuser.setText("By- " + name);

        if (!gifurl.isEmpty()) {
            Glide.with(ReviewActivity.this).load(gifurl).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image);

        } else if (!imageurl.isEmpty())
            Glide.with(ReviewActivity.this).load(imageurl).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image);
        else image.setVisibility(View.GONE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        PreCachingLayoutManager preCachingLayoutManager = new PreCachingLayoutManager(this);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(preCachingLayoutManager);
        /*VerticalSpaceItemDecorator verticalSpaceItemDecorator = new VerticalSpaceItemDecorator((int) pxFromDp(this, 6));
        recyclerView.addItemDecoration(verticalSpaceItemDecorator);*/
        adapter = new ReviewAdapter(this);
        recyclerView.setAdapter(adapter);
        loadReportsfromServer();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context mContext = ReviewActivity.this;
                if (!videourl.isEmpty()) {
                    Intent in = new Intent(mContext, VideoDisplay.class);
                    in.putExtra("myData", videourl);
                    in.putExtra("tag", tag);
                    in.putExtra("headline", headline);
                    if (headline != null)
                        in.putExtra("description", headline);
                    mContext.startActivity(in);

                } else if (!imageurl.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("myData", imageurl);
                    bundle.putString("tag", tag);
                    bundle.putString("headline", headline);
                    Intent in = new Intent(mContext, FullImage.class);
                    in.putExtras(bundle);
                    mContext.startActivity(in);
                }
            }
        });

        if (status.equals("true")) {
            ratetext.setVisibility(View.GONE);
            b1.setVisibility(View.GONE);
            c1.setVisibility(View.GONE);
            divider2.setVisibility(View.GONE);
        }
    }

    private void uploadReview() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (sessionManager.checkLoginSplash()) {
                    if (numrating == 0) {
                        Toast.makeText(ReviewActivity.this, "Please rate the issue", Toast.LENGTH_SHORT).show();
                    } else if (edittext.getText().toString().trim().length() == 0) {
                        Toast.makeText(ReviewActivity.this, "Your review cannot be empty", Toast.LENGTH_SHORT).show();

                    } else {
                        if (selectedImageUri != null)
                            updateReviewWithImage();
                        else updateReview();
                    }
                } else showlogindialog();

            }
        });
    }

    private void updateReviewWithImage() {
        progressDialog = new ProgressDialog(ReviewActivity.this);
        progressDialog.setMessage("Uploading. Please Wait...");
        progressDialog.show();
        if (selectedImageUri != null) {
            Glide
                    .with(this)
                    .load(selectedImageUri)
                    .asBitmap()
                    .toBytes(Bitmap.CompressFormat.JPEG, 98)
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
                            encodedImage = Base64.encodeToString(resource, Base64.DEFAULT);
                            updateReview();
                        }

                        @Override
                        public void onLoadFailed(Exception ex, Drawable ignore) {
                            Log.d("ex", ex.getMessage());
                        }
                    });


        }
    }

    private void updateReview() {
        send.setClickable(false);
        remove.setClickable(false);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("topicid", topicid);
        hashMap.put("number", sessionManager.getMobileNumber());
        hashMap.put("name", sessionManager.getUsername());
        hashMap.put("rating", String.valueOf(numrating));
        hashMap.put("description", edittext.getText().toString());
        hashMap.put("token", sessionManager.getKeyAuthToken());
        hashMap.put("deviceid", sessionManager.getDeviceid());
        hashMap.put("passcode", passcode);
        hashMap.put("privacy", privacy);

        if (encodedImage != null)
            hashMap.put("image", encodedImage);



        AndroidNetworking.post("https://reweyou.in/reviews/post_reviews.php")
                .addBodyParameter(hashMap)
                .setTag("repordt")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (progressDialog != null)
                            progressDialog.dismiss();


                        Log.d(TAG, "onResponse: " + response);
                        if (response.equals("reviewed")) {
                            edittext.setText("");
                            loadReportsfromServer();

                            ratetext.setVisibility(View.GONE);
                            b1.setVisibility(View.GONE);
                            c1.setVisibility(View.GONE);
                            divider2.setVisibility(View.GONE);
                            clearAttachedMediaPaths();
                            reim.setVisibility(View.GONE);
                            remove.setVisibility(View.GONE);
                        } else if (response.equals("Passcode is incorrect")) {
                            Toast.makeText(ReviewActivity.this, "Passcode is incorrect", Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        if (progressDialog != null) progressDialog.dismiss();

                        Log.e(TAG, "error: " + anError.getErrorDetail());
                        Toast.makeText(ReviewActivity.this, "Couldn't connect", Toast.LENGTH_SHORT).show();
                        send.setClickable(true);
                        remove.setClickable(true);


                    }
                });
    }

    private void loadReportsfromServer() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("topicid", topicid);
        hashMap.put("number", sessionManager.getMobileNumber());
        hashMap.put("token", sessionManager.getKeyAuthToken());
        hashMap.put("deviceid", sessionManager.getDeviceid());
        AndroidNetworking.post("https://reweyou.in/reviews/reviews.php")
                .addBodyParameter(hashMap)
                .setTag("report")
                .setPriority(Priority.HIGH)
                .build()
                .getAsParsed(new TypeToken<List<ReviewModel>>() {
                }, new ParsedRequestListener<List<ReviewModel>>() {

                    @Override
                    public void onResponse(List<ReviewModel> list) {
                        if (list.size() == 0)
                            noreviewyet.setVisibility(View.VISIBLE);
                        else noreviewyet.setVisibility(View.GONE);
                        loadingcircular.hide();
                        adapter.add(list);
                        Log.d(TAG, "onResponse: lis" + list.size());

                    }

                    @Override
                    public void onError(final ANError anError) {
                        loadingcircular.hide();
                        Log.e(TAG, "error: " + anError.getErrorDetail());

                        /*new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipe.setRefreshing(false);
                                if (isAdded() && !anError.getErrorDetail().equals("requestCancelled"))
                                    Toast.makeText(mContext, "Couldn't connect", Toast.LENGTH_SHORT).show();


                            }
                        }, 1200);*/


                    }
                });
    }

    public float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    private void showlogindialog() {
        CustomSigninDialog customSigninDialog = new CustomSigninDialog(ReviewActivity.this);
        customSigninDialog.show();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        try {

            if (getIntent() != null) {
                Intent i = getIntent();
                headline = i.getStringExtra("headline");
                description = i.getStringExtra("description");
                rating = i.getStringExtra("rating");
                user = i.getStringExtra("user");
                review = i.getStringExtra("review");
                tag = i.getStringExtra("tag");
                imageurl = i.getStringExtra("image");
                videourl = i.getStringExtra("video");
                name = i.getStringExtra("name");
                gifurl = i.getStringExtra("gif");
                topicid = i.getStringExtra("topicid");
                status = i.getStringExtra("status");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ReviewActivity.this, Feed.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
        finish();
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Storage Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Storage Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE_REQUEST_CODE);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Storage Permission is auto granted for sdk<23");
            return true;
        }
    }

    private void showPickImage() {
        imagePicker = new ImagePicker(ReviewActivity.this);
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
                            // handleGif(images.get(0).getOriginalPath());
                            Toast.makeText(ReviewActivity.this, "Only image can be uploaded", Toast.LENGTH_SHORT).show();
                        } else {
                            startImageCropActivity(Uri.parse(images.get(0).getQueryUri()));
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(ReviewActivity.this, "Something went wrong. ErrorCode: 19", Toast.LENGTH_SHORT).show();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: " + requestCode);
        switch (requestCode) {

            case PERMISSION_STORAGE_REQUEST_CODE:

                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Permission denied by user", Toast.LENGTH_SHORT).show();

                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showPickImage();
                }

                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("reached", "activigty");
        super.onActivityResult(requestCode, resultCode, data);

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
        }

    }

    private void handleImage(String data) {
        clearAttachedMediaPaths();
        showPreviewViews();
        Glide.with(ReviewActivity.this).load(data).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(reim);
        selectedImageUri = data;
    }

    private void showPreviewViews() {
        reim.setVisibility(View.VISIBLE);
        remove.setVisibility(View.VISIBLE);
    }

    private void clearAttachedMediaPaths() {

        selectedImageUri = null;

    }

    private void showPassCodeDialog() {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(ReviewActivity.this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_passcode, null);
        //  number=session.getMobileNumber();
        //Initizliaing confirm button fo dialog box and edittext of dialog box
        Button buttonEdit = (Button) confirmDialog.findViewById(R.id.buttonConfirm);
        final EditText editTextpassCode = (EditText) confirmDialog.findViewById(R.id.passcode);


        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(ReviewActivity.this);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Displaying the alert dialog
        alertDialog.show();

        //On the click of the confirm button from alert dialog
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (editTextpassCode.getText().toString().trim().length() == 4) {
                    alertDialog.dismiss();
                    passcode = editTextpassCode.getText().toString();
                    uploadReview();

                } else
                    Toast.makeText(ReviewActivity.this, "Passcode must be of 4 digits", Toast.LENGTH_SHORT).show();
            }
        });

    }


}

