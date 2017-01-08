package in.reweyou.reweyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.reweyou.reweyou.FragmentCommunicator;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.UserProfile;
import in.reweyou.reweyou.adapter.FeedAdapter;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.DividerItemDecoration;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.customView.PreCachingLayoutManager;
import in.reweyou.reweyou.model.FeedModel;
import in.reweyou.reweyou.utils.Constants;

import static in.reweyou.reweyou.utils.Constants.POSITION_SINGLE_POST;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_LOCATION;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_NEW_POST;
import static in.reweyou.reweyou.utils.Constants.dfs;


public class SecondFragment extends Fragment implements FragmentCommunicator {

    private static final String TAG = SecondFragment.class.getSimpleName();
    UserSessionManager session;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private RecyclerView recyclerView;
    private List<Object> messagelist = new ArrayList<>();
    private ProgressBar progressBar;
    private String location, formattedDate, number;
    private boolean loading = true;
    private FeedAdapter adapter;
    private String lastPostid;
    private int position = -1;
    private boolean cacheLoad = false;
    private String placename;
    private Activity mContext;
    private View layout;
    private String query;
    private String category;
    private boolean dataFetched;
    private Gson gson = new Gson();
    private HashMap<String, String> bodyHashMap;
    private HashMap<String, String> data;
    private SwipeRefreshLayout swipe;

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

        swipe = (SwipeRefreshLayout) layout.findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SecondFragment.this.onRefresh();
            }
        });

        swipe.setEnabled(false);
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
        progressBar.setVisibility(View.GONE);

        formattedDate = dfs.format(Calendar.getInstance().getTime());
        location = session.getLoginLocation();

        if (position == Constants.POSITION_FEED_TAB_MAIN_FEED || position == Constants.POSITION_SINGLE_POST || position == Constants.POSITION_CATEGORY_TAG || position == Constants.POSITION_SEARCH_TAB || position == 29) {
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


                    if (dy > 0) {
                        visibleItemCount = layoutManager.getChildCount();
                        totalItemCount = layoutManager.getItemCount();
                        pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                        if (!cacheLoad)
                            if (loading) {
                                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                    loading = false;
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
                            data.put("location", location);
                            data.put("postid", lastPostid);
                            data.put("number", number);
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

            formattedDate = dfs.format(Calendar.getInstance().getTime());
            makeRequest();

        } else Log.d(TAG, "loadFeeds: datafetched true");
    }


    private void makeRequest() {
        Log.d(TAG, "makeRequest: called");

        data = getBodyHashMap();

        try {
            JSONArray jsonArray = new JSONArray(agetDataa(position));
            Type listType = new TypeToken<List<FeedModel>>() {
            }.getType();
            List<FeedModel> myModelList = gson.fromJson(jsonArray.toString(), listType);
            onfetchResponse(myModelList, false);
            swipe.setEnabled(true);

        } catch (Exception e) {
            swipe.setEnabled(true);
            e.printStackTrace();
            AndroidNetworking.post(getUrl())
                    .addBodyParameter(data)
                    .setTag("test")
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            swipe.setEnabled(true);
                            savedata(response.toString(), position);
                            try {
                                Type listType = new TypeToken<List<FeedModel>>() {
                                }.getType();
                                List<FeedModel> myModelList = gson.fromJson(response.toString(), listType);
                                onfetchResponse(myModelList, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (isAdded())
                                    Toast.makeText(mContext, "couldn't connect", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            swipe.setEnabled(true);
                        }
                    });
        }
    }

    private String agetDataa(int position) {
        return session.getData(position);
    }

    private void savedata(String feedModels, int position) {
        if (position != POSITION_SINGLE_POST)
            session.saveData(feedModels, position);
    }

    private void onfetchResponse(List<FeedModel> feedModels, boolean flag) {
        Log.d(TAG, "userList size : " + feedModels.size());


        List<String> likeslist = session.getLikesList();

        List<Object> messagelist = new ArrayList<>();

        if (position == Constants.POSITION_FEED_TAB_MAIN_FEED) {
            messagelist.add(VIEW_TYPE_NEW_POST);
        }

        if (position == Constants.POSITION_FEED_TAB_MY_CITY) {
            messagelist.add(VIEW_TYPE_LOCATION);
        }

        for (FeedModel feedModel : feedModels) {
            if (likeslist.contains(feedModel.getPostId())) {
                feedModel.setLiked(true);
            }
            messagelist.add(feedModel);


        }

        lastPostid = feedModels.get(feedModels.size() - 1).getPostId();

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

            if (!flag)
                recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Log.d(TAG, "onGlobalLayout: called");

                        fetchnewData();


                        recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
        }
        cacheLoad = false;

    }

    private void fetchnewData() {

        AndroidNetworking.post(getUrl())
                .addBodyParameter(data)
                .setTag("test")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {

                        swipe.setRefreshing(false);
                        savedata(response.toString(), position);
                        try {
                            Type listType = new TypeToken<List<FeedModel>>() {
                            }.getType();

                            List<FeedModel> myModelList = gson.fromJson(response.toString(), listType);
                            onfetchResponse(myModelList, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (isAdded())
                                Toast.makeText(mContext, "couldn't connect", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        swipe.setRefreshing(false);
                        if (isAdded())
                            Toast.makeText(mContext, "couldn't connect", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    public void onRefresh() {

        //  Messages2();

        if (isAdded()) {
            formattedDate = dfs.format(Calendar.getInstance().getTime());
            Log.d(TAG, "onRefresh: called");

            fetchnewData();
        }
    }

    public void onNetChange() {
        if (isAdded()) {

            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                formattedDate = dfs.format(Calendar.getInstance().getTime());
                Log.d(TAG, "onRefresh: called");

                onRefresh();
            }

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
            case 29:
                return Constants.URL_MY_REPORTS;
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

    }


    public HashMap<String, String> getBodyHashMap() {
        HashMap<String, String> data = new HashMap<>();

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
            if (position != 29)
                data.put("number", number);
            else
                data.put("number", UserProfile.userprofilenumber);


            Log.d("dataaaaa", location + "   " + formattedDate + "  " + number);
        } else data.put("query", query);

        return data;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroyFragment: position" + position);
        super.onDestroy();


    }
}

