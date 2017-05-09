package in.reweyou.reweyou;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.fragment.ChatFragment;
import in.reweyou.reweyou.fragment.CreateThreadFragment;
import in.reweyou.reweyou.fragment.ForumFragment;
import in.reweyou.reweyou.fragment.GroupInfoFragment;
import in.reweyou.reweyou.fragment.UserInfoFragment;
import in.reweyou.reweyou.utils.Utils;

public class GroupActivity extends AppCompatActivity {

    private static final String TAG = GroupActivity.class.getName();
    private ImageView back;
    private int positionFragment = -1;
    private ImagePicker imagePicker;
    private PagerAdapter pagerAdapter;
    private LinearLayout uploadingContainer;
    private UserSessionManager userSessionManager;
    private TextView okbbutton;
    private ProgressBar uploadingpd;
    private TextView uploadingtext;
    private Intent intentData;
    private String image1encoded = "";
    private String image2encoded = "";
    private String image3encoded = "";
    private String image4encoded = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("Rajneeti");

        userSessionManager = new UserSessionManager(this);
        initUploadingContainer();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        back = (ImageView) findViewById(R.id.backgroundimageview);
        setBackgroundtint();

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void initUploadingContainer() {
        uploadingContainer = (LinearLayout) findViewById(R.id.uploadingContainer);
        uploadingtext = (TextView) findViewById(R.id.uploadingtext);
        uploadingpd = (ProgressBar) findViewById(R.id.uploadingprogressbar);
        okbbutton = (TextView) findViewById(R.id.okbutton);
        okbbutton.setTag("1");
        okbbutton.setVisibility(View.GONE);
        okbbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (okbbutton.getTag().equals("1"))
                    uploadingContainer.setVisibility(View.GONE);
                else if (okbbutton.getTag().equals("0")) {
                    uploadGroup();

                }
            }
        });

    }

    private void setBackgroundtint() {
        switch (Utils.backgroundCode) {
            case 0:
                break;
            case 1:
                back.setColorFilter(this.getResources().getColor(R.color.main_background_blue_alpha));
                break;
            case 2:
                back.setColorFilter(this.getResources().getColor(R.color.main_background_green_alpha));
                break;
            case 3:
                back.setColorFilter(this.getResources().getColor(R.color.main_background_pink_alpha));
                break;
        }
    }


    public void showPickImage(int i) {
        this.positionFragment = i;
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
                .setAspectRatio(1, 1)
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

                CreateThreadFragment createFragment = (CreateThreadFragment) pagerAdapter.getRegisteredFragment(2);
                createFragment.onImageChoosen(result.getUri().toString());

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        if (requestCode == 19) {
            if (resultCode == RESULT_OK) {
                this.intentData = data;

                compressImages();
            }
        } else if (resultCode == RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                imagePicker.submit(data);
            }
        }

    }

    private void compressImages() {
        final int count = intentData.getIntExtra("counter", 0);
        if (count > 0) {
            Glide.with(this).load(intentData.getStringExtra("image1")).asBitmap().toBytes().into(new SimpleTarget<byte[]>(150, 150) {
                @Override
                public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                    image1encoded = Base64.encodeToString(resource, Base64.DEFAULT);
                    if (count > 1)
                        Glide.with(GroupActivity.this).load(intentData.getStringExtra("image2")).asBitmap().toBytes().into(new SimpleTarget<byte[]>(150, 150) {
                            @Override
                            public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                                image2encoded = Base64.encodeToString(resource, Base64.DEFAULT);
                                if (count > 2)
                                    Glide.with(GroupActivity.this).load(intentData.getStringExtra("image3")).asBitmap().toBytes().into(new SimpleTarget<byte[]>(150, 150) {
                                        @Override
                                        public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                                            image3encoded = Base64.encodeToString(resource, Base64.DEFAULT);
                                            if (count > 3)
                                                Glide.with(GroupActivity.this).load(intentData.getStringExtra("image4")).asBitmap().toBytes().into(new SimpleTarget<byte[]>(150, 150) {
                                                    @Override
                                                    public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                                                        image4encoded = Base64.encodeToString(resource, Base64.DEFAULT);
                                                        uploadGroup();
                                                    }
                                                });
                                            else uploadGroup();

                                        }
                                    });
                                else uploadGroup();

                            }
                        });
                    else uploadGroup();

                }
            });
        } else uploadGroup();
    }

    private void uploadGroup() {

        Intent data = intentData;
        showUploading();
        AndroidNetworking.post("https://www.reweyou.in/google/create_threads.php")
                .addBodyParameter("groupname", "Photography")
                .addBodyParameter("description", data.getStringExtra("description"))
                .addBodyParameter("link", data.getStringExtra("link"))
                .addBodyParameter("image1", image1encoded)
                .addBodyParameter("image2", image2encoded)
                .addBodyParameter("image3", image3encoded)
                .addBodyParameter("image4", image4encoded)
                .addBodyParameter("type", data.getStringExtra("type"))
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .setTag("uploadpost")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);
                        if (response.equals("Thread created")) {
                            showUploadSuccessful();

                        } else {
                            showFailedUpload();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError);
                        showFailedUpload();
                    }
                });

    }

    private void showUploading() {
        uploadingpd.setVisibility(View.VISIBLE);
        uploadingtext.setText("Uploading Post");
        okbbutton.setText("OK");
        okbbutton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_layer, 0, 0, 0);
        okbbutton.setVisibility(View.GONE);
        uploadingContainer.setVisibility(View.VISIBLE);
    }

    private void showUploadSuccessful() {
        uploadingpd.setVisibility(View.INVISIBLE);
        uploadingtext.setText("Upload successful");
        okbbutton.setTag("1");
        okbbutton.setText("OK");
        okbbutton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_layer, 0, 0, 0);
        okbbutton.setVisibility(View.VISIBLE);

    }

    private void showFailedUpload() {
        uploadingpd.setVisibility(View.INVISIBLE);
        uploadingtext.setText("Upload failed");
        okbbutton.setText("Retry");

        okbbutton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_refresh_layer, 0, 0, 0);
        okbbutton.setTag("0");
        okbbutton.setVisibility(View.VISIBLE);

    }

    private void handleImage(String s) {
        UserInfoFragment userInfoFragment = (UserInfoFragment) pagerAdapter.getRegisteredFragment(3);
        userInfoFragment.onImageChoosen(s);

        Glide.with(this).load(s).asBitmap().toBytes().into(new SimpleTarget<byte[]>(150, 150) {
            @Override
            public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                String encodedImage = Base64.encodeToString(resource, Base64.DEFAULT);
                //uploadImage(encodedImage);
            }
        });
    }

    public void startCreateActivity() {
        uploadingContainer.setVisibility(View.GONE);
        startActivityForResult(new Intent(GroupActivity.this, CreateActivity.class), 19);
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        private String[] tabs = getResources().getStringArray(R.array.group_tab);

        private PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {


            if (position == 0)
                return new GroupInfoFragment();
            else if (position == 2)
                return new ChatFragment();
            else return new ForumFragment();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }


        @Override
        public int getCount() {
            return tabs.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

    }


}
