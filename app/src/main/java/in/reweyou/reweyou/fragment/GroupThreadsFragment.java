package in.reweyou.reweyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import in.reweyou.reweyou.GroupActivity;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.adapter.FeeedsAdapter;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.ThreadModel;

/**
 * Created by master on 24/2/17.
 */

public class GroupThreadsFragment extends Fragment {


    private static final String TAG = GroupThreadsFragment.class.getName();
    private Activity mContext;
    private RecyclerView recyclerView;
    private FeeedsAdapter feeedsAdapter;
    private UserSessionManager userSessionManager;
    private CardView nopostcard;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button createpost;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSessionManager = new UserSessionManager(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_group_threads, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        nopostcard = (CardView) layout.findViewById(R.id.nopostcard);
        FloatingActionButton fab = (FloatingActionButton) layout.findViewById(R.id.fab);
        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swiperefresh);
        createpost = (Button) layout.findViewById(R.id.createpost);

        createpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        ((GroupActivity) mContext).startCreateActivity();
                    }
                });
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((GroupActivity) mContext).startCreateActivity();

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
            feeedsAdapter = new FeeedsAdapter(mContext);
            recyclerView.setAdapter(feeedsAdapter);

            getData();


        }
    }

    private void getData() {
        nopostcard.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
        AndroidNetworking.post("https://www.reweyou.in/google/list_threads.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .addBodyParameter("groupid", getArguments().getString("groupid"))
                .setTag("report")
                .setPriority(Priority.HIGH)
                .build()
                .getAsParsed(new TypeToken<List<ThreadModel>>() {
                }, new ParsedRequestListener<List<ThreadModel>>() {

                    @Override
                    public void onResponse(final List<ThreadModel> list) {
                        swipeRefreshLayout.setRefreshing(false);
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                if (list.size() == 0) {
                                    nopostcard.setVisibility(View.VISIBLE);
                                } else
                                    feeedsAdapter.add(list);
                            }
                        });

                    }

                    @Override
                    public void onError(final ANError anError) {
                        Log.e(TAG, "run: error: " + anError.getErrorDetail());

                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }


    public void refreshList() {
        getData();

    }
}
