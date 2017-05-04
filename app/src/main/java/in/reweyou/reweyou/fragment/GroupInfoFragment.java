package in.reweyou.reweyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import in.reweyou.reweyou.R;

/**
 * Created by master on 24/2/17.
 */

public class GroupInfoFragment extends Fragment {


    private Activity mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_group_info, container, false);

        ImageView img = (ImageView) layout.findViewById(R.id.image);
        TextView groupname = (TextView) layout.findViewById(R.id.groupname);
        TextView shortdes = (TextView) layout.findViewById(R.id.shortdescription);
        TextView description = (TextView) layout.findViewById(R.id.description);
        TextView members = (TextView) layout.findViewById(R.id.members);
        TextView threads = (TextView) layout.findViewById(R.id.threads);

        Glide.with(mContext).load("https://media.giphy.com/media/m82Z6oaf0TF1S/giphy.gif").into(img);
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
