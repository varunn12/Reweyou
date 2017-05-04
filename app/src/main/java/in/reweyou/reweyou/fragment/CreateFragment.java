package in.reweyou.reweyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import in.reweyou.reweyou.R;

/**
 * Created by master on 24/2/17.
 */

public class CreateFragment extends Fragment {


    private Activity mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_create, container, false);
        keyboardListener();
        return layout;
    }

    private void keyboardListener() {
        mContext.findViewById(R.id.rootlayout).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mContext.findViewById(R.id.rootlayout).getWindowVisibleDisplayFrame(r);
                int heightDiff = mContext.findViewById(R.id.rootlayout).getRootView().getHeight() - (r.bottom - r.top);

                // int heightDiff = findViewById(R.id.main_content).getRootView().getHeight() - findViewById(R.id.main_content).getHeight();

                //Log.d(TAG, "onGlobalLayout: height"+heightDiff+"   "+findViewById(R.id.main_content).getRootView().getHeight()+    "    "+(r.bottom - r.top));
                if (heightDiff > pxFromDp(mContext, 150)) { // if more than 100 pixels, its probably a keyboard...
                    //ok now we know the keyboard is up...
                    mContext.findViewById(R.id.tabLayout).setVisibility(View.INVISIBLE);
                    mContext.findViewById(R.id.tabLayout).setAlpha(0);


                } else {
                    //ok now we know the keyboard is down...
                    mContext.findViewById(R.id.tabLayout).setVisibility(View.VISIBLE);
                    mContext.findViewById(R.id.tabLayout).animate().alpha(1).setDuration(300).start();


                }
            }
        });
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
