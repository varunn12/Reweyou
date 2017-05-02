package in.reweyou.reweyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.adapter.ForumAdapter;
import in.reweyou.reweyou.adapter.SuggestAdapter;
import in.reweyou.reweyou.adapter.YourGroupsAdapter;
import in.reweyou.reweyou.model.ForumModel;
import in.reweyou.reweyou.utils.Utils;

/**
 * Created by master on 1/5/17.
 */

public class ExploreFragment extends Fragment {
    private static final String TAG = ExploreFragment.class.getName();
    private Activity mContext;
    private ForumAdapter adapterExplore;
    private RecyclerView recyclerViewExplore;
    private TextView exploretextview;
    private RecyclerView recyclerViewYourGroups;
    private RecyclerView recyclerViewSuggested;
    private SuggestAdapter adapterSuggested;
    private TextView suggesttextview;
    private YourGroupsAdapter adapterYourGroups;
    private TextView yourgroupstextview;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_explore, container, false);
        recyclerViewExplore = (RecyclerView) layout.findViewById(R.id.explore_recycler_view);
        recyclerViewSuggested = (RecyclerView) layout.findViewById(R.id.explore_recycler_view_suggested);
        recyclerViewYourGroups = (RecyclerView) layout.findViewById(R.id.explore_recycler_view_your_groups);
        exploretextview = (TextView) layout.findViewById(R.id.aaa);
        suggesttextview = (TextView) layout.findViewById(R.id.text2);
        yourgroupstextview = (TextView) layout.findViewById(R.id.text3);
        recyclerViewYourGroups.setNestedScrollingEnabled(false);


        switch (Utils.backgroundCode) {
            case 0:
                break;
            case 1:
                exploretextview.setTextColor(mContext.getResources().getColor(R.color.background_blue_explore));
                suggesttextview.setTextColor(mContext.getResources().getColor(R.color.background_blue_explore));
                yourgroupstextview.setTextColor(mContext.getResources().getColor(R.color.background_blue_explore));
                break;
            case 3:
                exploretextview.setTextColor(mContext.getResources().getColor(R.color.background_pink_explore));
                suggesttextview.setTextColor(mContext.getResources().getColor(R.color.background_pink_explore));
                yourgroupstextview.setTextColor(mContext.getResources().getColor(R.color.background_pink_explore));
                break;
            case 2:
                exploretextview.setTextColor(mContext.getResources().getColor(R.color.background_green_explore));
                suggesttextview.setTextColor(mContext.getResources().getColor(R.color.background_green_explore));
                yourgroupstextview.setTextColor(mContext.getResources().getColor(R.color.background_green_explore));
                break;
        }

        recyclerViewExplore.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        ////
        recyclerViewSuggested.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, true));
        recyclerViewYourGroups.setLayoutManager(new GridLayoutManager(mContext, 2));
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
            getDataFromServer();

        }
    }

    private void getDataFromServer() {
        adapterExplore = new ForumAdapter(mContext);
        adapterSuggested = new SuggestAdapter(mContext);
        adapterYourGroups = new YourGroupsAdapter(mContext);
        recyclerViewExplore.setAdapter(adapterExplore);
        recyclerViewSuggested.setAdapter(adapterSuggested);
        recyclerViewYourGroups.setAdapter(adapterYourGroups);

        AndroidNetworking.get("https://reweyou.in/reviews/sampleforum.php")
                .setTag("report")
                .setPriority(Priority.HIGH)
                .build()
                .getAsParsed(new TypeToken<List<ForumModel>>() {
                }, new ParsedRequestListener<List<ForumModel>>() {

                    @Override
                    public void onResponse(final List<ForumModel> list) {
                        adapterExplore.add(list);
                        ////
                        adapterSuggested.add(list);
                        ///
                        adapterYourGroups.add(list);
                    }

                    @Override
                    public void onError(final ANError anError) {
                        Log.e(TAG, "run: error: " + anError.getErrorDetail());


                    }
                });
    }

}
