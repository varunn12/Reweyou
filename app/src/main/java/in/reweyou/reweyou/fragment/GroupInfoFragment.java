package in.reweyou.reweyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.classes.UserSessionManager;

/**
 * Created by master on 24/2/17.
 */

public class GroupInfoFragment extends Fragment {


    private static final String TAG = GroupInfoFragment.class.getName();
    private Activity mContext;
    private String groupid;
    private boolean isfollowed;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_group_info, container, false);

        final UserSessionManager userSessionManager = new UserSessionManager(mContext);
        final Button btnfollow = (Button) layout.findViewById(R.id.btn_follow);
        ImageView img = (ImageView) layout.findViewById(R.id.image);
        final TextView groupname = (TextView) layout.findViewById(R.id.groupname);
        TextView shortdes = (TextView) layout.findViewById(R.id.shortdescription);
        TextView description = (TextView) layout.findViewById(R.id.description);
        final TextView members = (TextView) layout.findViewById(R.id.members);
        TextView threads = (TextView) layout.findViewById(R.id.threads);
        final ProgressBar pd = (ProgressBar) layout.findViewById(R.id.pd);
        try {
            groupname.setText(getArguments().getString("groupname"));
            members.setText(getArguments().getString("members"));
            groupid = getArguments().getString("groupid");
            isfollowed = getArguments().getBoolean("follow");

            if (isfollowed) {
                btnfollow.setText("Leave");
                btnfollow.setTextColor(mContext.getResources().getColor(R.color.main_background_pink));
                btnfollow.setBackground(mContext.getResources().getDrawable(R.drawable.rectangular_border_pink));

            } else {
                btnfollow.setText("Join");
                btnfollow.setTextColor(mContext.getResources().getColor(R.color.white));
                btnfollow.setBackground(mContext.getResources().getDrawable(R.drawable.rectangular_solid_pink));
            }
            Glide.with(mContext).load(getArguments().getString("image")).into(img);
            btnfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnfollow.setVisibility(View.INVISIBLE);
                    pd.setVisibility(View.VISIBLE);

                    AndroidNetworking.post("https://www.reweyou.in/google/follow_groups.php")
                            .addBodyParameter("groupid", groupid)
                            .addBodyParameter("groupname", getArguments().getString("groupname"))
                            .addBodyParameter("uid", userSessionManager.getUID())
                            .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                            .setTag("uploadpost")
                            .setPriority(Priority.HIGH)
                            .build()
                            .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG, "onResponse: " + response);
                                    if (response.equals("Followed")) {
                                        btnfollow.setText("Leave");
                                        btnfollow.setTextColor(mContext.getResources().getColor(R.color.main_background_pink));
                                        btnfollow.setBackground(mContext.getResources().getDrawable(R.drawable.rectangular_border_pink));

                                        btnfollow.setVisibility(View.VISIBLE);
                                        pd.setVisibility(View.GONE);
                                        Toast.makeText(mContext, "You are now following " + groupname, Toast.LENGTH_SHORT).show();
                                        mContext.setResult(Activity.RESULT_OK);
                                    } else if (response.equals("Unfollowed")) {
                                        btnfollow.setText("Join");
                                        btnfollow.setTextColor(mContext.getResources().getColor(R.color.white));
                                        btnfollow.setBackground(mContext.getResources().getDrawable(R.drawable.rectangular_solid_pink));
                                        btnfollow.setVisibility(View.VISIBLE);
                                        pd.setVisibility(View.GONE);
                                        mContext.setResult(Activity.RESULT_OK);
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.d(TAG, "onError: " + anError);
                                    Toast.makeText(mContext, "Connection problem", Toast.LENGTH_SHORT).show();
                                    btnfollow.setVisibility(View.VISIBLE);
                                    pd.setVisibility(View.GONE);
                                }
                            });

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
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


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isAdded()) {


        }
    }


}
