package in.reweyou.reweyou.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kbeanie.multipicker.api.ImagePicker;

import in.reweyou.reweyou.ForumMainActivity;
import in.reweyou.reweyou.LoginActivity;
import in.reweyou.reweyou.R;

/**
 * Created by master on 24/2/17.
 */

public class ProfileFragment extends Fragment {


    private static final String TAG = ProfileFragment.class.getName();
    private Activity mContext;
    private EditText username;
    private ImageView image;
    private Button continuebutton;
    private ImagePicker imagePicker;
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_profile_login, container, false);
        username = (EditText) layout.findViewById(R.id.username);
        image = (ImageView) layout.findViewById(R.id.image);
        continuebutton = (Button) layout.findViewById(R.id.continu);
        progressBar = (ProgressBar) layout.findViewById(R.id.pd);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGallery();
            }
        });
        layout.findViewById(R.id.editphoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGallery();
            }
        });
        continuebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, ForumMainActivity.class));
                mContext.finish();
            }
        });

        return layout;
    }

    private void showGallery() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkStoragePermission();

        } else ((LoginActivity) mContext).showPickImage();
    }


    private void checkStoragePermission() {
        Dexter.withActivity(mContext)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        ((LoginActivity) mContext).showPickImage();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(mContext, "Storage Permission denied by user", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onPermissionGranted: " + response.isPermanentlyDenied());

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            mContext = (Activity) context;
        else throw new IllegalArgumentException("Context should be an instance of Activity");
    }

    @Override
    public void onDestroy() {
        mContext = null;
        super.onDestroy();

    }

    public float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isAdded()) {


        }
    }


    public void onsignin(String givenName, Uri photoUrl) {
        Log.d("ProfileFragment", "onsignin: reached 1 postition");
        if (username != null) {
            username.setText(givenName);
            username.setSelection(givenName.length());
        }
        Glide.with(ProfileFragment.this).load(photoUrl).into(image);


    }

    public void onImageChoosen(String s) {
        progressBar.setVisibility(View.VISIBLE);

    }

    public void onImageUpload() {
        //write glide code here


    }
}
