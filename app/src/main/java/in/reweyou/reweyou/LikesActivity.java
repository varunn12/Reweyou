package in.reweyou.reweyou;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.gson.reflect.TypeToken;
import com.klinker.android.sliding.MultiShrinkScroller;
import com.klinker.android.sliding.SlidingActivity;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.List;

import in.reweyou.reweyou.adapter.LikesAdapter;
import in.reweyou.reweyou.model.LikesModel;

public class LikesActivity extends SlidingActivity {

    private static final String TAG = LikesActivity.class.getName();
    private RecyclerView recyclerview;
    private TextView numLikes;
    private String numlikes;
    private String reviewid;
    private AVLoadingIndicatorView loadingcircularinit;
    private TextView nolikes;

    @Override
    protected void configureScroller(MultiShrinkScroller scroller) {
        super.configureScroller(scroller);
        scroller.setIntermediateHeaderHeightRatio(0);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        disableHeader();

        setContent(R.layout.activity_likes);

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        nolikes = (TextView) findViewById(R.id.nolikes);
        recyclerview = (RecyclerView) findViewById(R.id.recycler_view);
        numLikes = (TextView) findViewById(R.id.numlikes);
        loadingcircularinit = (AVLoadingIndicatorView) findViewById(R.id.loading);
        loadingcircularinit.show();

        if (getIntent() != null) {
            reviewid = getIntent().getStringExtra("reviewid");
            numlikes = getIntent().getStringExtra("numLikes");

            numLikes.setText(" (" + numlikes + ")");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadLikesListfromServer();
            }
        }, 400);
    }

    private void loadLikesListfromServer() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("reviewid", reviewid);
        AndroidNetworking.post("https://reweyou.in/reviews/like_list.php")
                .setTag("likeslist")
                .addBodyParameter(hashMap)
                .setPriority(Priority.HIGH)
                .build()
                .getAsParsed(new TypeToken<List<LikesModel>>() {
                }, new ParsedRequestListener<List<LikesModel>>() {

                    @Override
                    public void onResponse(List<LikesModel> list) {

                        loadingcircularinit.setVisibility(View.GONE);

                        if (list.size() == 0)
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    nolikes.setVisibility(View.VISIBLE);

                                }
                            }, 200);
                        else {
                            LikesAdapter likesAdapter = new LikesAdapter(LikesActivity.this);
                            likesAdapter.add(list);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(LikesActivity.this);
                            recyclerview.setLayoutManager(linearLayoutManager);
                            recyclerview.setAdapter(likesAdapter);
                        }

                    }

                    @Override
                    public void onError(final ANError anError) {
                        Log.e(TAG, "run: error: " + anError.getErrorDetail());
                        loadingcircularinit.setVisibility(View.GONE);

                        Toast.makeText(LikesActivity.this, "Couldn't connect", Toast.LENGTH_SHORT).show();

                    }
                });

    }
}
