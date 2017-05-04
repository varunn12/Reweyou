package in.reweyou.reweyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.bumptech.glide.Glide;

import in.reweyou.reweyou.ForumMainActivity;
import in.reweyou.reweyou.R;

/**
 * Created by master on 24/2/17.
 */

public class ProfileFragment extends Fragment {


    private Activity mContext;
    private EditText username;
    private ImageView image;
    private Button continuebutton;

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

        continuebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, ForumMainActivity.class));
                mContext.finish();
            }
        });

        return layout;
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
}
