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
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.adapter.ForumAdapter;
import in.reweyou.reweyou.adapter.YourGroupsAdapter;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.GroupModel;
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
    private YourGroupsAdapter adapterYourGroups;
    private TextView yourgroupstextview;
    private UserSessionManager userSessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSessionManager = new UserSessionManager(mContext);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_explore, container, false);
        recyclerViewExplore = (RecyclerView) layout.findViewById(R.id.explore_recycler_view);
        recyclerViewYourGroups = (RecyclerView) layout.findViewById(R.id.explore_recycler_view_your_groups);
        exploretextview = (TextView) layout.findViewById(R.id.aaa);
        yourgroupstextview = (TextView) layout.findViewById(R.id.text3);

        recyclerViewYourGroups.setNestedScrollingEnabled(false);


        switch (Utils.backgroundCode) {
            case 0:
                break;
            case 1:
                exploretextview.setTextColor(mContext.getResources().getColor(R.color.background_blue_explore));
                yourgroupstextview.setTextColor(mContext.getResources().getColor(R.color.background_blue_explore));
                break;
            case 3:
                exploretextview.setTextColor(mContext.getResources().getColor(R.color.background_pink_explore));
                yourgroupstextview.setTextColor(mContext.getResources().getColor(R.color.background_pink_explore));
                break;
            case 2:
                exploretextview.setTextColor(mContext.getResources().getColor(R.color.background_green_explore));
                yourgroupstextview.setTextColor(mContext.getResources().getColor(R.color.background_green_explore));
                break;
        }

        recyclerViewExplore.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        ////
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, LinearLayoutManager.VERTICAL, false);

        recyclerViewYourGroups.setLayoutManager(gridLayoutManager);
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
        adapterYourGroups = new YourGroupsAdapter(mContext);
        recyclerViewExplore.setAdapter(adapterExplore);
        recyclerViewYourGroups.setAdapter(adapterYourGroups);

        AndroidNetworking.post("https://www.reweyou.in/google/discover_groups.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .setTag("fetchgroups")
                .setPriority(Priority.HIGH)
                .build()
                /*.getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: "+response);
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });*/
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray jsonarray) {
                        try {
                            Gson gson = new Gson();
                            JSONObject jsonobject = jsonarray.getJSONObject(0);
                            Log.d(TAG, "onResponse: " + jsonobject);

                            JSONArray followjsonarray = jsonobject.getJSONArray("followed");
                            JSONArray explorejsonarray = jsonobject.getJSONArray("explore");

                            List<GroupModel> explorelist = new ArrayList<GroupModel>();
                            List<GroupModel> followlist = new ArrayList<GroupModel>();
                            for (int i = 0; i < explorejsonarray.length(); i++) {
                                JSONObject jsonObject = explorejsonarray.getJSONObject(i);
                                GroupModel groupModel = gson.fromJson(jsonObject.toString(), GroupModel.class);
                                explorelist.add(0, groupModel);
                            }

                            for (int i = 0; i < followjsonarray.length(); i++) {
                                JSONObject jsonObject = followjsonarray.getJSONObject(i);
                                GroupModel groupModel = gson.fromJson(jsonObject.toString(), GroupModel.class);
                                followlist.add(0, groupModel);
                            }
                            adapterExplore.add(explorelist);
                            adapterYourGroups.add(followlist);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError);
                    }
                });


    }

    public void refreshlist() {
        Log.d(TAG, "refreshlist: reached");
        getDataFromServer();
    }
}
