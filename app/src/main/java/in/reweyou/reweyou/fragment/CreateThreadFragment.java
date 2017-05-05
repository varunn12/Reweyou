package in.reweyou.reweyou.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import in.reweyou.reweyou.GroupActivity;
import in.reweyou.reweyou.R;

/**
 * Created by master on 24/2/17.
 */

public class CreateThreadFragment extends Fragment {


    private static final String TAG = CreateThreadFragment.class.getName();
    private Activity mContext;
    private ImageView img;
    private TextView imgtext;
    private EditText description;
    private TextView create;
    private String imgUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_create_thread, container, false);
        final LinearLayout expandpost = (LinearLayout) layout.findViewById(R.id.expandcreatepostcontainer);
        final LinearLayout expandshare = (LinearLayout) layout.findViewById(R.id.expandsharelinkcontainer);
        final LinearLayout expandthoughts = (LinearLayout) layout.findViewById(R.id.expandcreatethoughtscontainer);

        TextView tvpost = (TextView) layout.findViewById(R.id.tvpost);
        TextView tvshare = (TextView) layout.findViewById(R.id.tvshare);
        TextView tvthoughts = (TextView) layout.findViewById(R.id.tvthoughts);

        tvthoughts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandthoughts.getVisibility() == View.VISIBLE) {
                    expandthoughts.setVisibility(View.GONE);
                } else expandthoughts.setVisibility(View.VISIBLE);

            }
        });
        tvpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandpost.getVisibility() == View.VISIBLE) {
                    expandpost.setVisibility(View.GONE);
                } else expandpost.setVisibility(View.VISIBLE);

            }
        });
        tvshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandshare.getVisibility() == View.VISIBLE) {
                    expandshare.setVisibility(View.GONE);
                } else expandshare.setVisibility(View.VISIBLE);

            }
        });


        img = (ImageView) layout.findViewById(R.id.image);
        imgtext = (TextView) layout.findViewById(R.id.imgtext);
        description = (EditText) layout.findViewById(R.id.description);

        create = (TextView) layout.findViewById(R.id.post2);
        create.setEnabled(false);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // uploadGroup();
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkStoragePermission();

                } else ((GroupActivity) mContext).showPickImage(2);
            }
        });

        initTextWatchers();


        return layout;
    }

    private void checkStoragePermission() {
        Dexter.withActivity(mContext)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        ((GroupActivity) mContext).showPickImage(2);
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

    public void onImageChoosen(String s) {
        this.imgUrl = s;
        Glide.with(mContext).load(s).into(img);
        imgtext.setVisibility(View.GONE);
    }

    private void initTextWatchers() {


        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
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
            create.setTextColor(mContext.getResources().getColor(R.color.main_background_pink));
            create.setBackground(mContext.getResources().getDrawable(R.drawable.border_pink));
        } else {
            create.setEnabled(false);
            create.setTextColor(mContext.getResources().getColor(R.color.grey_create));
            create.setBackground(mContext.getResources().getDrawable(R.drawable.border_grey));
        }
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


}
