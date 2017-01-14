package in.reweyou.reweyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.adapter.FeedAdapter1;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.classes.VerticalSpaceItemDecorator;
import in.reweyou.reweyou.customView.PreCachingLayoutManager;
import in.reweyou.reweyou.model.FeedModel;
import in.reweyou.reweyou.utils.Constants;

import static in.reweyou.reweyou.utils.ReportLoadingConstant.FRAGMENT_CATEGORY_CITY;
import static in.reweyou.reweyou.utils.ReportLoadingConstant.FRAGMENT_CATEGORY_MY_PROFILE;
import static in.reweyou.reweyou.utils.ReportLoadingConstant.FRAGMENT_CATEGORY_NEWS;
import static in.reweyou.reweyou.utils.ReportLoadingConstant.FRAGMENT_CATEGORY_READING;
import static in.reweyou.reweyou.utils.ReportLoadingConstant.FRAGMENT_CATEGORY_REPORTER_PROFILE;
import static in.reweyou.reweyou.utils.ReportLoadingConstant.FRAGMENT_CATEGORY_SEARCH;
import static in.reweyou.reweyou.utils.ReportLoadingConstant.FRAGMENT_CATEGORY_SINGLE_POST;
import static in.reweyou.reweyou.utils.ReportLoadingConstant.FRAGMENT_CATEGORY_TAG;
import static in.reweyou.reweyou.utils.ReportLoadingConstant.REQUEST_PARAMS_CITY;
import static in.reweyou.reweyou.utils.ReportLoadingConstant.REQUEST_PARAMS_NUMBER;
import static in.reweyou.reweyou.utils.ReportLoadingConstant.REQUEST_PARAMS_SINGLE_POST;
import static in.reweyou.reweyou.utils.ReportLoadingConstant.REQUEST_PARAMS_TAG;
import static in.reweyou.reweyou.utils.ReportLoadingConstant.fragmentCategoryList;
import static in.reweyou.reweyou.utils.ReportLoadingConstant.fragmentListLoadOnStart;
import static in.reweyou.reweyou.utils.ReportLoadingConstant.fragmentListWithBoxAtTop;
import static in.reweyou.reweyou.utils.ReportLoadingConstant.fragmentListWithCache;

/**
 * Created by master on 11/1/17.
 */

public class BaseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    public static final String TAG_FRAGMENT_CATEGORY = "default";
    private static final String TAG = BaseFragment.class.getSimpleName();
    public static final java.lang.String TAG_SINGLE_POST_ID = "singlepostid";

    private int FRAGMENT_CATEGORY = -1;

    private Activity mContext;
    private SwipeRefreshLayout swipe;
    private RecyclerView recyclerView;
    private UserSessionManager sessionManager;

    private String singlepostid;
    private String reporterNumber;
    private String selectedcity = "lucknow";
    private String selectedtag;

    private Gson gson = new Gson();

    private List<FeedModel> reportsList = new ArrayList<>();
    private Type listType = new TypeToken<List<FeedModel>>() {
    }.getType();
    private FeedAdapter1 feedAdapter1;
    private boolean dataLoaded;
    private List<String> likeslist;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getIntentExtras();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_second, container, false);

        initViews(layout);

        return layout;

    }

    private void initViews(View layout) {
        initCommonViews(layout);
        initCategorySpecificViews(layout);
    }

    private void initCategorySpecificViews(View layout) {
        swipe = (SwipeRefreshLayout) layout.findViewById(R.id.swipe);
        swipe.setOnRefreshListener(this);

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new PreCachingLayoutManager(mContext));
        VerticalSpaceItemDecorator verticalSpaceItemDecorator = new VerticalSpaceItemDecorator(12);
        recyclerView.addItemDecoration(verticalSpaceItemDecorator);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initCommonViews(View layout) {

    }

    private void initFragmentCategory(int fragmentCategory) {
        if (fragmentCategoryList.contains(fragmentCategory))
            FRAGMENT_CATEGORY = fragmentCategory;
        else
            throw new NullPointerException("You must provide a valid Fragment Category");
    }


    public void getIntentExtras() {

        int fragmentCategory = getArguments().getInt(TAG_FRAGMENT_CATEGORY, -1);
        Log.d(TAG, "getIntentExtras: fragmentCategory " + fragmentCategory);
        initFragmentCategory(fragmentCategory);

        String singlepostid = getArguments().getString(TAG_SINGLE_POST_ID, null);
        validateSinglePostId(singlepostid);
    }

    private void validateSinglePostId(String singlepostid) {
        if (FRAGMENT_CATEGORY == FRAGMENT_CATEGORY_SINGLE_POST) {
            if (singlepostid == null)
                throw new NullPointerException("single post id cannot be null");
            else this.singlepostid = singlepostid;
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

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRefresh() {

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isAdded()) {
            sessionManager = new UserSessionManager(mContext);
            likeslist = sessionManager.getLikesList();

            feedAdapter1 = new FeedAdapter1(mContext, reportsList, FRAGMENT_CATEGORY, sessionManager);
            recyclerView.setAdapter(feedAdapter1);
            if (fragmentListLoadOnStart.contains(FRAGMENT_CATEGORY)) {
                loadReports();
            }
        }
    }

    private void loadReports() {
        //loadReportsfromCache();
        loadReportsfromServer();
    }

    private void loadReportsfromCache() {
        if (getfeedfromCache(FRAGMENT_CATEGORY) == null)
            loadReportsfromServer();
        else parseCacheString(getfeedfromCache(FRAGMENT_CATEGORY));
    }

    private void parseCacheString(String s) {

        reportsList = gson.fromJson(s, listType);

        if (fragmentListWithBoxAtTop.contains(FRAGMENT_CATEGORY)) {

        } else {

        }

    }

    private void loadReportsfromServer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipe.setRefreshing(true);

            }
        }, 700);
        Log.d(TAG, "loadReportsfromServer: called");
        AndroidNetworking.post(getUrl())
                .addBodyParameter(getBodyHashMap())
                .setTag("report")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsParsed(new TypeToken<List<FeedModel>>() {
                }, new ParsedRequestListener<List<FeedModel>>() {

                    @Override
                    public void onResponse(List<FeedModel> list) {
                        dataLoaded = true;
                        if (isAdded()) {
                            if (!isResponseEmpty(list)) {
                                Log.d(TAG, "onResponse: response is not empty");
                                for (FeedModel feedModel : list) {
                                    if (likeslist.contains(feedModel.getPostId())) {
                                        feedModel.setLiked(true);
                                    }
                                    feedAdapter1.add(feedModel);

                                }
                                if (fragmentListWithCache.contains(FRAGMENT_CATEGORY))
                                    saveResponseToCache(list);
                            } else {
                                Log.w(TAG, "onResponse: list is empty");
                                if (FRAGMENT_CATEGORY == FRAGMENT_CATEGORY_CITY)
                                    feedAdapter1.addnoreports(FRAGMENT_CATEGORY_CITY);
                                else if (FRAGMENT_CATEGORY == FRAGMENT_CATEGORY_READING)
                                    feedAdapter1.addnoreports(FRAGMENT_CATEGORY_READING);
                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    swipe.setRefreshing(false);

                                }
                            }, 2000);

                        }
                    }

                    @Override
                    public void onError(final ANError anError) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipe.setRefreshing(false);
                                if (isAdded()&&!anError.getErrorDetail().equals("requestCancelled"))
                                    Toast.makeText(mContext, "Couldn't connect", Toast.LENGTH_SHORT).show();


                            }
                        }, 2000);


                    }
                });
    }

    private void saveResponseToCache(List<FeedModel> list) {
        switch (FRAGMENT_CATEGORY)

        {
            case FRAGMENT_CATEGORY_NEWS:
                sessionManager.saveNewsReportsinCache(list);
                return;
            case FRAGMENT_CATEGORY_MY_PROFILE:
                return;
            case FRAGMENT_CATEGORY_REPORTER_PROFILE:
                return;
            case FRAGMENT_CATEGORY_READING:
                return;
        }

    }

    private void clearList() {
        feedAdapter1.clearlist();
    }

    private boolean isResponseEmpty(List<FeedModel> list) {
        if (list.size() == 0) {
            return true;
        } else return false;

    }

    private String getfeedfromCache(int position) {
        return sessionManager.getData(position);
    }

    private void savedata(String feedModels, int position) {
       /* if (position != POSITION_SINGLE_POST)
            session.saveData(feedModels, position);*/
    }

    private String getUrl() {
        switch (FRAGMENT_CATEGORY) {
            case FRAGMENT_CATEGORY_NEWS:
                return Constants.NEWS_FEED_URL;
            case FRAGMENT_CATEGORY_READING:
                return Constants.READING_URL;
            case FRAGMENT_CATEGORY_CITY:
                return Constants.MY_CITY_URL;
            case FRAGMENT_CATEGORY_SEARCH:
                return Constants.SEARCH_QUERY;
            case FRAGMENT_CATEGORY_SINGLE_POST:
                return Constants.MY_SINGLE_ACTIVITY;
            case FRAGMENT_CATEGORY_TAG:
                return Constants.CATEGORY_FEED_URL;
            case FRAGMENT_CATEGORY_MY_PROFILE:
                return Constants.URL_MY_REPORTS;
            default:
                throw new IllegalArgumentException("No url for the requested fragment category");
        }
    }

    public HashMap<String, String> getBodyHashMap() {
        HashMap<String, String> data = new HashMap<>();

        switch (FRAGMENT_CATEGORY) {
            case FRAGMENT_CATEGORY_NEWS:
                data.put(REQUEST_PARAMS_NUMBER, sessionManager.getMobileNumber());
                break;
            case FRAGMENT_CATEGORY_SINGLE_POST:
                data.put(REQUEST_PARAMS_SINGLE_POST, singlepostid);
                break;
            case FRAGMENT_CATEGORY_REPORTER_PROFILE:
                data.put(REQUEST_PARAMS_NUMBER, reporterNumber);
                break;
            case FRAGMENT_CATEGORY_CITY:
                data.put(REQUEST_PARAMS_CITY, selectedcity);
                break;
            case FRAGMENT_CATEGORY_TAG:
                data.put(REQUEST_PARAMS_TAG, selectedtag);
                break;
            case FRAGMENT_CATEGORY_READING:
                data.put(REQUEST_PARAMS_NUMBER, sessionManager.getMobileNumber());
                break;
            default:
                throw new IllegalArgumentException("no request params available for this fragment category");
        }

        return data;
    }

    public void loadfeeds() {
        AndroidNetworking.cancelAll();
        if (!dataLoaded)
            loadReports();
    }


}
