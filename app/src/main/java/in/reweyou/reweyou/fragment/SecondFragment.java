package in.reweyou.reweyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import in.reweyou.reweyou.FragmentCommunicator;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.adapter.FeedAdapter;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.DividerItemDecoration;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.customView.PreCachingLayoutManager;
import in.reweyou.reweyou.customView.swipeRefresh.PullRefreshLayout;
import in.reweyou.reweyou.model.FeedModel;
import in.reweyou.reweyou.utils.Constants;
import in.reweyou.reweyou.utils.MyJSON;

import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_LOCATION;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_NEW_POST;


public class SecondFragment extends Fragment implements FragmentCommunicator {

    private static final String TAG = SecondFragment.class.getSimpleName();
    PullRefreshLayout swipeLayout;
    UserSessionManager session;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private RecyclerView recyclerView;
    private List<Object> messagelist = new ArrayList<>();
    private ProgressBar progressBar;
    private String location, formattedDate, number;
    private boolean loading = true;
    private SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH);
    private FeedAdapter adapter;
    private Calendar c;
    private String lastPostid;
    private int position = -1;
    private boolean cacheLoad = false;
    private String placename;
    private Activity mContext;
    private TextView topBar;
    private LinearLayout hangingNoti;
    private View layout;
    private String query;
    private int minPostid;
    private String category;
    private boolean firstTimeLoad = true;
    private boolean dataFetched;
    private boolean scrollFlag;

    public SecondFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt("position");
        placename = getArguments().getString("place");
        category = getArguments().getString("category");
        query = getArguments().getString("query");

        session = new UserSessionManager(mContext);

        if (query != null)
            Log.d("pos", query);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_second, container, false);

        topBar = (TextView) layout.findViewById(R.id.no);

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(ContextCompat.getDrawable(mContext, R.drawable.line));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new PreCachingLayoutManager(mContext));
        final PreCachingLayoutManager layoutManager = (PreCachingLayoutManager) recyclerView.getLayoutManager();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        setScrollListener(layoutManager);


        number = session.getMobileNumber();


        //Progress bar
        progressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);


        swipeLayout = (PullRefreshLayout) layout.findViewById(R.id.swipeRefreshLayout);
        swipeLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SecondFragment.this.onRefresh();
            }
        });


        formattedDate = df.format(Calendar.getInstance().getTime());
        location = session.getLoginLocation();

        if (position == Constants.POSITION_FEED_TAB_MAIN_FEED || position == Constants.POSITION_SINGLE_POST || position == Constants.POSITION_CATEGORY_TAG || position == Constants.POSITION_SEARCH_TAB) {
            loadFeeds();
            Log.d(TAG, "onCreateView: loadfeeds called");
        }

        return layout;
    }

    private void setScrollListener(final PreCachingLayoutManager layoutManager) {
        if (position != Constants.POSITION_CATEGORY_TAG && position != Constants.POSITION_SINGLE_POST && position != Constants.POSITION_SEARCH_TAB)
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    Log.d(TAG, "onScrolled: " + dx + "   " + dy);


                    if (dy > 0) //check for scroll down
                    {
                        visibleItemCount = layoutManager.getChildCount();
                        totalItemCount = layoutManager.getItemCount();
                        pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                        if (!cacheLoad)
                            if (loading) {
                                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                    loading = false;

                                    Log.v("...", "Last Item Wow !");
                                    adapter.add();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            makeLoadMoreRequest();
                                        }
                                    }, 1000);

                                }
                            }
                    }


                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        Log.d(TAG, "onScrollStateChanged: dragging");
                    } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        Log.d(TAG, "onScrollStateChanged: idle");
                    } else if (newState == RecyclerView.SCROLL_STATE_SETTLING)
                        Log.d(TAG, "onScrollStateChanged: settling");

                    super.onScrollStateChanged(recyclerView, newState);
                }

                private void makeLoadMoreRequest() {
                    final long mRequestStartTime = System.currentTimeMillis();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, getUrl(),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    long totalRequestTime = System.currentTimeMillis() - mRequestStartTime;
                                    Log.d("tme", String.valueOf(totalRequestTime));
                                    Log.d("Response", response);
                                    List<String> likeslist = session.getLikesList();
                                    Log.d("likeslist", String.valueOf(likeslist));
                                    List<FeedModel> list = new ArrayList<FeedModel>();
                                    JSONArray parentArray = null;
                                    try {
                                        parentArray = new JSONArray(response);
                                        adapter.remove();

                                        Gson gson = new Gson();
                                        Log.d("lenght", String.valueOf(parentArray.length()));
                                        for (int i = 0; i < parentArray.length(); i++) {
                                            JSONObject finalObject = parentArray.getJSONObject(i);
                                            FeedModel feedModel = gson.fromJson(finalObject.toString(), FeedModel.class);

                                            if (likeslist.contains(feedModel.getPostId())) {
                                                Log.d("true", feedModel.getPostId() + "    ");

                                                feedModel.setLiked(true);

                                            }


                                            list.add(feedModel);
                                            if (i == parentArray.length() - 1) {
                                                // formattedDate = feedModel.getDate1();
                                                //Log.d("last", feedModel.getCategory());
                                                lastPostid = feedModel.getPostId();
                                            }
                                        }

                                        adapter.loadMore(list);
                                        loading = true;

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.d("ex", e.getMessage());

                                    }

                                }

                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    long totalRequestTime = System.currentTimeMillis() - mRequestStartTime;
                                    Log.d("tme", String.valueOf(totalRequestTime));

                                    if (error instanceof NetworkError)
                                        Log.e("error", error.getMessage());
                                    // Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                                    adapter.remove();
                                    loading = true;
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> data = new HashMap<>();
                            if (position != 2) {
                                data.put("location", location);
                                data.put("postid", lastPostid);
                                data.put("number", number);
                            } else {
                                data.put("postid", String.valueOf(minPostid));
                                data.put("location", location);
                                data.put("postid", lastPostid);
                                data.put("number", number);
                            }
                            Log.d("minid", String.valueOf(data));
                            return data;
                        }
                    };


                    if (isAdded()) {
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                        requestQueue.add(stringRequest);
                    }

                }
            });
    }


    public void loadFeeds() {
        Log.d(TAG, "loadFeeds: called");
        if (!dataFetched) {

            topBar.setVisibility(View.GONE);
            formattedDate = df.format(Calendar.getInstance().getTime());
            makeRequest();

        } else Log.d(TAG, "loadFeeds: datafetched true");
    }


    private void makeRequest() {
        Log.d(TAG, "makeRequest: called");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ResponseSecond", response);
                        JSONArray parentArray;
                        try {
                            parentArray = new JSONArray(response);

                            List<String> likeslist = session.getLikesList();

                            List<Object> messagelist = new ArrayList<>();

                            if (position == Constants.POSITION_FEED_TAB_MAIN_FEED) {
                                messagelist.add(VIEW_TYPE_NEW_POST);
                            }

                            if (position == Constants.POSITION_FEED_TAB_MY_CITY) {
                                messagelist.add(VIEW_TYPE_LOCATION);
                            }

                            Gson gson = new Gson();

                            Log.d(TAG, "onResponse: Size: " + parentArray.length());

                            for (int i = 0; i < parentArray.length(); i++) {
                                JSONObject feedObject = parentArray.getJSONObject(i);
                                FeedModel feedModel = gson.fromJson(feedObject.toString(), FeedModel.class);

                                if (likeslist.contains(feedModel.getPostId())) {
                                    feedModel.setLiked(true);
                                }

                                if (i == 0) {
                                    minPostid = Integer.parseInt(feedModel.getPostId());
                                }

                                if (minPostid > Integer.parseInt(feedModel.getPostId())) {
                                    minPostid = Integer.parseInt(feedModel.getPostId());
                                }


                                messagelist.add(feedModel);

                                if (i == parentArray.length() - 1) {
                                    lastPostid = feedModel.getPostId();
                                    Log.d(TAG, "onResponse: lastPostid: " + lastPostid);
                                    Log.d(TAG, "onResponse: minPostid: " + minPostid);

                                }
                            }
                            progressBar.setVisibility(View.GONE);

                            if (isAdded()) {
                                if (position == Constants.POSITION_SINGLE_POST) {
                                    adapter = new FeedAdapter(mContext, messagelist, placename, SecondFragment.this, position);
                                } else if (position == Constants.POSITION_FEED_TAB_MY_CITY)
                                    adapter = new FeedAdapter(mContext, messagelist, placename, SecondFragment.this);
                                else
                                    adapter = new FeedAdapter(mContext, messagelist, SecondFragment.this);

                                dataFetched = true;
                                recyclerView.setAdapter(adapter);
                            } else
                                Log.w(TAG, "onResponse: fragment got detached when setting adapter");
                            swipeLayout.setRefreshing(false);

                            cacheLoad = false;
                            if (isAdded()) {
                                if (position != 19 && position != 15 && position != Constants.POSITION_SEARCH_TAB)
                                    MyJSON.saveData(getContext(), response, position);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);

                        if (error instanceof NoConnectionError) {
                            showNoti("No internet connectivity");
                        } else if (error instanceof TimeoutError) {
                            showNoti("poor internet connectivity");
                        } else if (error instanceof NetworkError || error instanceof ParseError || error instanceof ServerError) {
                            showNoti("something went wrong");
                        }
                        swipeLayout.setRefreshing(false);

                        if (isAdded()) {
                            String respo = null;
                            if (position != 19 && position != 15 && position != Constants.POSITION_SEARCH_TAB)
                                respo = MyJSON.getData(mContext, position);

                            if (respo != null) {
                                JSONArray parentArray = null;
                                try {
                                    parentArray = new JSONArray(respo);
                                    Log.d("aaa", String.valueOf(parentArray));

                                    Gson gson = new Gson();
                                    for (int i = 0; i < parentArray.length(); i++) {
                                        JSONObject finalObject = parentArray.getJSONObject(i);
                                        FeedModel feedModel = gson.fromJson(finalObject.toString(), FeedModel.class);
                                        messagelist.add(feedModel);
                                    }
                                    adapter = new FeedAdapter(mContext, messagelist, SecondFragment.this);
                                    dataFetched = true;

                                    recyclerView.setAdapter(adapter);

                                    cacheLoad = true;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }

                    private void showNoti(String msg) {
                        topBar.setText(msg);
                        topBar.setVisibility(View.VISIBLE);
/*

                        if (!session.getFirstLoad()) {
                            session.setFirstLoad();
                            hangingNoti.setVisibility(View.VISIBLE);
                            TranslateAnimation mAnimation = new TranslateAnimation(
                                    TranslateAnimation.ABSOLUTE, 0f,
                                    TranslateAnimation.ABSOLUTE, 0f,
                                    TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                                    TranslateAnimation.RELATIVE_TO_PARENT, 0.05f);
                            mAnimation.setDuration(500);
                            mAnimation.setRepeatCount(-1);
                            mAnimation.setRepeatMode(Animation.REVERSE);
                            mAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                            hangingNoti.setAnimation(mAnimation);
                            // recyclerView.setBackground((Color.parseColor("#50000000")));
                            hangingNoti.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    hangingNoti.setAnimation(null);
                                    hangingNoti.setVisibility(View.GONE);
                                    hangingNoti.setOnTouchListener(null);

                                    return true;
                                }


                            });
                        }
*/

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();

                if (position != Constants.POSITION_SEARCH_TAB) {
                    if (position == Constants.POSITION_FEED_TAB_MY_CITY)
                            data.put("location", placename);
                    else if (position == Constants.POSITION_SINGLE_POST) {
                            data.put("query", query);
                    } else if (position == Constants.POSITION_CATEGORY_TAG) {
                            if (category != null)
                                data.put("category", category);
                        } else
                            data.put("location", location);
                        data.put("date", formattedDate);
                        data.put("number", number);

                    Log.d("dataaaaa", location + "   " + formattedDate + "  " + number);
                    } else data.put("query", query);

                return data;
            }
        };

        if (isAdded()) {
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(stringRequest);
        }
    }


    public void onRefresh() {

        //  Messages2();

        if (isAdded()) {
            formattedDate = df.format(Calendar.getInstance().getTime());
            Log.d(TAG, "onRefresh: called");

            topBar.setVisibility(View.GONE);
            makeRequest();
        }
    }

    public void onNetChange() {


        if (isAdded()) {

            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                formattedDate = df.format(Calendar.getInstance().getTime());
                Log.d(TAG, "onRefresh: called");

                topBar.setVisibility(View.GONE);
                makeRequest();
            } else topBar.setVisibility(View.VISIBLE);

        }
    }

    public String getUrl() {
        switch (position) {
            case Constants.POSITION_FEED_TAB_MAIN_FEED:
                return Constants.FEED_URL;
            case Constants.POSITION_FEED_TAB_2:
                return Constants.TRENDING_URL;
            case Constants.POSITION_FEED_TAB_3:
                return Constants.READING_URL;
            case Constants.POSITION_FEED_TAB_MY_CITY:
                return Constants.MY_CITY_URL;
            case Constants.POSITION_SEARCH_TAB:
                return Constants.SEARCH_QUERY;
            case Constants.POSITION_SINGLE_POST:
                return Constants.MY_SINGLE_ACTIVITY;
            case Constants.POSITION_CATEGORY_TAG:
                return Constants.CATEGORY_FEED_URL;
            default:
                return null;

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: called");

        if (context instanceof Activity)
            mContext = (Activity) context;
        else Log.w(TAG, "onAttach: " + "context is null");
    }


    public void onLocationSet(String location) {
        this.placename = location;
        onRefresh();
    }

    @Override
    public void passDataToFragment(boolean net) {

        Log.d("posi", String.valueOf(position));
        if (net)
            onRefresh();
        else if (topBar != null) {
            topBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy:" + position + " called");
        super.onDestroy();
    }


}

