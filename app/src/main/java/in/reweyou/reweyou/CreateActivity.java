package in.reweyou.reweyou;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.klinker.android.sliding.MultiShrinkScroller;
import com.klinker.android.sliding.SlidingActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.util.List;

public class CreateActivity extends SlidingActivity {

    private static final String TAG = CreateActivity.class.getName();
    private ProgressBar linkpd;
    private CardView cd;
    private TextView headlinelink;
    private TextView descriptionlink;
    private RelativeLayout rl;
    private ImageView camerabtn;
    private ImageView linkbtn;
    private TextView linklink;
    private TextView create;
    private EditText editText;
    private ImagePicker imagePicker;
    private ImageView image;

    @Override
    protected void configureScroller(MultiShrinkScroller scroller) {
        super.configureScroller(scroller);
        scroller.setIntermediateHeaderHeightRatio(0);

    }

    @Override
    public void init(Bundle savedInstanceState) {
        disableHeader();
        enableFullscreen();

        setContent(R.layout.content_create);

        linkpd = (ProgressBar) findViewById(R.id.linkpd);
        cd = (CardView) findViewById(R.id.cd);
        image = (ImageView) findViewById(R.id.image);
        editText = (EditText) findViewById(R.id.groupname);
        headlinelink = (TextView) findViewById(R.id.headlinelink);
        descriptionlink = (TextView) findViewById(R.id.descriptionlink);
        linklink = (TextView) findViewById(R.id.linklink);
        rl = (RelativeLayout) findViewById(R.id.rl);
        linkbtn = (ImageView) findViewById(R.id.link);
        camerabtn = (ImageView) findViewById(R.id.camera);
        linkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editHeadline();
            }
        });
        create = (TextView) findViewById(R.id.create);
        create.setEnabled(false);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPost();
            }
        });
        initTextWatchers();
        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkStoragePermission();

                } else showPickImage();
            }
        });

    }

    private void checkStoragePermission() {
        Dexter.withActivity(this)
                .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        showPickImage();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(CreateActivity.this, "Storage Permission denied by user", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onPermissionGranted: " + response.isPermanentlyDenied());

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }

    private void uploadPost() {

    }

    private void initTextWatchers() {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText.getText().toString().trim().length() > 0) {
                    updateCreateTextUI(true);
                } else updateCreateTextUI(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateCreateTextUI(boolean b) {
        if (b) {
            create.setEnabled(true);
            create.setTextColor(this.getResources().getColor(R.color.main_background_pink));
            create.setBackground(this.getResources().getDrawable(R.drawable.border_pink));
        } else {
            create.setEnabled(false);
            create.setTextColor(this.getResources().getColor(R.color.grey_create));
            create.setBackground(this.getResources().getDrawable(R.drawable.border_grey));
        }
    }

    private void editHeadline() {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_insert_link, null);
        //  number=session.getMobileNumber();
        //Initizliaing confirm button fo dialog box and edittext of dialog box
        final Button buttonconfirm = (Button) confirmDialog.findViewById(R.id.buttonConfirm);
        final EditText link = (EditText) confirmDialog.findViewById(R.id.editTextlink);
        link.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    buttonconfirm.setBackground(CreateActivity.this.getResources().getDrawable(R.drawable.border_pink));
                    buttonconfirm.setTextColor(CreateActivity.this.getResources().getColor(R.color.main_background_pink));
                } else {
                    buttonconfirm.setBackground(CreateActivity.this.getResources().getDrawable(R.drawable.border_grey));
                    buttonconfirm.setTextColor(Color.parseColor("#9e9e9e"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setView(confirmDialog);

        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.show();

        //On the click of the confirm button from alert dialog
        buttonconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (link.getText().toString().trim().length() > 0) {
                    alertDialog.dismiss();

                    CreateActivity.this.onLinkPasted(link.getText().toString());


                } else alertDialog.dismiss();
            }
        });

    }

    private void onLinkPasted(String s) {
        cd.setVisibility(View.VISIBLE);
        rl.setVisibility(View.GONE);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                linkpd.setVisibility(View.VISIBLE);

            }
        });


        AndroidNetworking.post("https://damp-beyond-15607.herokuapp.com/previewlink.php")
                .addBodyParameter("url", s)
                .setTag("agr")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d(TAG, "onResponse: " + response);

                            linkpd.setVisibility(View.INVISIBLE);
                            JSONObject jsonObject = new JSONObject(response);
                            String reallink = jsonObject.getString("reallink");
                            String link = jsonObject.getString("link");
                            Log.d(TAG, "onResponse: " + response + "   " + link);
                            String title = jsonObject.getString("title");
                            String description = jsonObject.getString("description");

                            if (title != null)
                                headlinelink.setText(title);
                            if (description != null)
                                descriptionlink.setText(description);
                            if (link != null)
                                linklink.setText(reallink);

                        } catch (Exception e) {
                            rl.setVisibility(View.VISIBLE);
                            editText.setHint("Describe this link...");
                            cd.setVisibility(View.GONE);
                            Toast.makeText(CreateActivity.this, "Error in fetching data from link", Toast.LENGTH_SHORT).show();

                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        cd.setVisibility(View.GONE);
                        rl.setVisibility(View.VISIBLE);


                        Toast.makeText(CreateActivity.this, "Error in fetching data from link", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    public void showPickImage() {
        imagePicker = new ImagePicker(this);
        imagePicker.setImagePickerCallback(new ImagePickerCallback() {
                                               @Override
                                               public void onImagesChosen(List<ChosenImage> images) {

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
                            Toast.makeText(this, "Only image can be uploaded", Toast.LENGTH_SHORT).show();
                        } else {
                            startImageCropActivity(Uri.parse(images.get(0).getQueryUri()));
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong. ErrorCode: 19", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startImageCropActivity(Uri data) {
        CropImage.activity(data)
                .setActivityTitle("Crop Image")
                .setBackgroundColor(Color.parseColor("#90000000"))
                .setMinCropResultSize(200, 200)
                .setBorderCornerColor(getResources().getColor(R.color.colorPrimaryDark))
                .setBorderLineColor(getResources().getColor(R.color.colorPrimary))
                .setGuidelinesColor(getResources().getColor(R.color.divider))
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
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

    private void handleImage(final String s) {
        image.setVisibility(View.VISIBLE);
        rl.setVisibility(View.GONE);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Glide.with(CreateActivity.this).load(s).into(image);
            }
        });

      /*  Glide.with(this).load(s).asBitmap().toBytes().into(new SimpleTarget<byte[]>(150, 150) {
            @Override
            public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                String encodedImage = Base64.encodeToString(resource, Base64.DEFAULT);
               // uploadImage(encodedImage);
            }
        });*/

    }
}
