package in.reweyou.reweyou;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;
import com.klinker.android.sliding.MultiShrinkScroller;
import com.klinker.android.sliding.SlidingActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyou.adapter.CommentsAdapter;
import in.reweyou.reweyou.model.CommentModel;
import in.reweyou.reweyou.model.ReplyCommentModel;

public class CommentActivity extends SlidingActivity {

    private static final String TAG = CommentActivity.class.getName();

    @Override
    protected void configureScroller(MultiShrinkScroller scroller) {
        super.configureScroller(scroller);
        scroller.setIntermediateHeaderHeightRatio(0);

    }

    @Override
    public void init(Bundle savedInstanceState) {
        disableHeader();
        setContent(R.layout.content_comment);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final CommentsAdapter adapterComment = new CommentsAdapter(this);
        recyclerView.setAdapter(adapterComment);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.get("https://reweyou.in/reviews/samplecomment.php")
                        .setTag("report")
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Log.d(TAG, "onResponse: " + response);
                                Gson gson = new Gson();
                                List<Object> list = new ArrayList<>();

                                try {
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject json = response.getJSONObject(i);
                                        JSONArray jsonReply = json.getJSONArray("reply");
                                        CommentModel coModel = gson.fromJson(json.toString(), CommentModel.class);
                                        list.add(coModel);
                                        for (int j = 0; j < jsonReply.length(); j++) {
                                            JSONObject jsontemp = jsonReply.getJSONObject(j);
                                            ReplyCommentModel temp = gson.fromJson(jsontemp.toString(), ReplyCommentModel.class);
                                            list.add(temp);
                                        }


                                    }
                                    adapterComment.add(list);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(ANError anError) {

                            }
                        });
            }
        }, 500);

    }

}
