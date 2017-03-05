package in.reweyou.reweyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;

import in.reweyou.reweyou.Feed;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.adapter.IssueAdapter;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.classes.VerticalSpaceItemDecorator;
import in.reweyou.reweyou.customView.PreCachingLayoutManager;
import in.reweyou.reweyou.model.IssueModel;

/**
 * Created by master on 24/2/17.
 */

public class IssueFragment extends Fragment {

    private static final String TAG = IssueFragment.class.getName();
    private RecyclerView recyclerView;
    private Activity mContext;
    private IssueAdapter adapter;
    private SwipeRefreshLayout swipe;
    private String currenttag = "All";
    private UserSessionManager userSessionManager;
    private TextView noissue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userSessionManager = new UserSessionManager(mContext);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_second, container, false);
        swipe = (SwipeRefreshLayout) layout.findViewById(R.id.swipe);
        noissue = (TextView) layout.findViewById(R.id.noissue);
/*
        swipe.setProgressViewOffset(false, 0, (int) pxFromDp(mContext, 55));
*/
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadReportsfromServer(currenttag);
            }
        });

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        PreCachingLayoutManager preCachingLayoutManager = new PreCachingLayoutManager(mContext);
        recyclerView.setLayoutManager(preCachingLayoutManager);

        VerticalSpaceItemDecorator verticalSpaceItemDecorator = new VerticalSpaceItemDecorator((int) pxFromDp(mContext, 6));
        recyclerView.addItemDecoration(verticalSpaceItemDecorator);
        return layout;
    }

    public void loadreportsafteredit() {
        loadReportsfromServer(currenttag);
    }
    public void loadReportsfromServer(String requesttag) {
        currenttag = requesttag;

        swipe.setRefreshing(true);
        Log.d(TAG, "loadReportsfromServer: " + currenttag);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("tags", currenttag);
        hashMap.put("number", userSessionManager.getMobileNumber());
        String url;
        if (mContext instanceof Feed)
            url = "https://reweyou.in/reviews/topics.php";
        else
            url = "https://reweyou.in/reviews/myreviews.php";

        AndroidNetworking.post(url)
                .addBodyParameter(hashMap)
                .setTag("report")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsParsed(new TypeToken<List<IssueModel>>() {
                }, new ParsedRequestListener<List<IssueModel>>() {

                    @Override
                    public void onResponse(List<IssueModel> list) {
                        adapter.add(list);
                        if (list.size() == 0) {
                            if (mContext instanceof Feed) {
                                noissue.setVisibility(View.VISIBLE);
                                noissue.setText("No issues yet");
                            } else
                                noissue.setVisibility(View.VISIBLE);
                        } else {
                            noissue.setVisibility(View.GONE);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipe.setRefreshing(false);
                            }
                        }, 1200);
                    }

                    @Override
                    public void onError(final ANError anError) {
                        Log.e(TAG, "run: error: " + anError.getErrorDetail());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipe.setRefreshing(false);
                                if (isAdded() && !anError.getErrorDetail().equals("requestCancelled"))
                                    Toast.makeText(mContext, "Couldn't connect", Toast.LENGTH_SHORT).show();


                            }
                        }, 1200);


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
            adapter = new IssueAdapter(mContext, IssueFragment.this);
            recyclerView.setAdapter(adapter);
            loadReportsfromServer(currenttag);
        }
    }


}
