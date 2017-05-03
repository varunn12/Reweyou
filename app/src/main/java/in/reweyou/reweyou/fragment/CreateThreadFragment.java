package in.reweyou.reweyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.reweyou.reweyou.R;

/**
 * Created by master on 24/2/17.
 */

public class CreateThreadFragment extends Fragment {


    private Activity mContext;

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

        TextView tvpost = (TextView) layout.findViewById(R.id.tvpost);
        TextView tvshare = (TextView) layout.findViewById(R.id.tvshare);

        tvpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandpost.getVisibility() == View.VISIBLE) {
                    expandpost.setVisibility(View.GONE);
                } else {
                    expandpost.setVisibility(View.VISIBLE);
                    expandshare.setVisibility(View.GONE);
                }

            }
        });
        tvshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandshare.getVisibility() == View.VISIBLE) {
                    expandshare.setVisibility(View.GONE);
                } else {
                    expandshare.setVisibility(View.VISIBLE);
                    expandpost.setVisibility(View.GONE);
                }

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


}
