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
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import in.reweyou.reweyou.fragment.CreateFragment;
import in.reweyou.reweyou.fragment.ExploreFragment;
import in.reweyou.reweyou.fragment.ForumFragment;
import in.reweyou.reweyou.fragment.UserInfoFragment;
import in.reweyou.reweyou.utils.Utils;

public class ForumMainActivity extends AppCompatActivity {

    private static final String TAG = ForumMainActivity.class.getName();
    private ImagePicker imagePicker;
    private PagerAdapter pagerAdapter;
    private int positionFragment = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView back = (ImageView) findViewById(R.id.backgroundimageview);


        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab tabCall1 = tabLayout.getTabAt(0);
        tabCall1.setIcon(R.drawable.tab1_selector);
        TabLayout.Tab tabCall2 = tabLayout.getTabAt(1);
        tabCall2.setIcon(R.drawable.tab2_selector);
        TabLayout.Tab tabCall3 = tabLayout.getTabAt(2);
        tabCall3.setIcon(R.drawable.tab3_selector);
        TabLayout.Tab tabCall4 = tabLayout.getTabAt(3);
        tabCall4.setIcon(R.drawable.tab4_selector);

        switch (Utils.backgroundCode) {
            case 0:
                break;
            case 1:
                back.setColorFilter(ForumMainActivity.this.getResources().getColor(R.color.main_background_blue_alpha));
                tabLayout.setSelectedTabIndicatorColor(ForumMainActivity.this.getResources().getColor(R.color.main_background_blue));
                break;
            case 2:
                back.setColorFilter(ForumMainActivity.this.getResources().getColor(R.color.main_background_green_alpha));
                tabLayout.setSelectedTabIndicatorColor(ForumMainActivity.this.getResources().getColor(R.color.main_background_green));
                break;
            case 3:
                back.setColorFilter(ForumMainActivity.this.getResources().getColor(R.color.main_background_pink_alpha));
                tabLayout.setSelectedTabIndicatorColor(ForumMainActivity.this.getResources().getColor(R.color.main_background_pink));
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
                .setFixAspectRatio(true)
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
                if (positionFragment == 3)
                    handleImage(result.getUri().toString());
                else if (positionFragment == 2) {
                    CreateFragment createFragment = (CreateFragment) pagerAdapter.getRegisteredFragment(2);
                    createFragment.onImageChoosen(result.getUri().toString());
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                imagePicker.submit(data);
            } else if (requestCode == Utils.REQ_CODE_GROP_ACITIVTY) {
                ((ExploreFragment) pagerAdapter.getRegisteredFragment(1)).refreshlist();
            }
        }


    }

    private void handleImage(String s) {
        UserInfoFragment userInfoFragment = (UserInfoFragment) pagerAdapter.getRegisteredFragment(3);
        userInfoFragment.onImageChoosen(s);

        Glide.with(this).load(s).asBitmap().toBytes().into(new SimpleTarget<byte[]>(150, 150) {
            @Override
            public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                String encodedImage = Base64.encodeToString(resource, Base64.DEFAULT);
                uploadImage(encodedImage);
            }
        });
    }


    private void uploadImage(String resource) {
        AndroidNetworking.post("")
                .addBodyParameter("image", resource)
                .addBodyParameter("token", "token")
                .addBodyParameter("userid", "Apg")
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        UserInfoFragment userInfoFragment = (UserInfoFragment) pagerAdapter.getRegisteredFragment(3);
                        userInfoFragment.onImageUpload();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError);
                    }
                });
    }


    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forum, menu);
        return true;
    }

    public void showExploreGroupFragment() {

    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        private String[] tabs = getResources().getStringArray(R.array.tabs);

        private PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 3)
                return new UserInfoFragment();
            else if (position == 1)
                return new ExploreFragment();
            else if (position == 2)
                return new CreateFragment();
            else
                return new ForumFragment();
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


    }
}
