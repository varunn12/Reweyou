package in.reweyou.reweyou;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.reweyou.reweyou.adapter.CommentsAdapter;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.CommentModel;
import in.reweyou.reweyou.model.ReplyCommentModel;

public class CommentActivity extends AppCompatActivity {

    private static final String TAG = CommentActivity.class.getName();
    public EditText editText;
    private ImageView send;
    private TextView replyheader;
    private UserSessionManager userSessionManager;
    private String threadid;
    private String tempcommentid;
    private TextView nocommenttxt;
    private CommentsAdapter adapterComment;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        try {
            threadid = getIntent().getStringExtra("threadid");
        } catch (Exception e) {
            e.printStackTrace();
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        editText = (EditText) findViewById(R.id.edittext);
        send = (ImageView) findViewById(R.id.send);
        nocommenttxt = (TextView) findViewById(R.id.commenttxt);

        userSessionManager = new UserSessionManager(this);
        replyheader = (TextView) findViewById(R.id.t2);

        adapterComment = new CommentsAdapter(this);
        recyclerView.setAdapter(adapterComment);

        initSendButton();
        initTextWatcherEditText();

        getData();


    }

    private void getData() {
        swipeRefreshLayout.setRefreshing(true);
        replyheader.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.post("https://www.reweyou.in/google/list_comments.php")
                        .addBodyParameter("uid", userSessionManager.getUID())
                        .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                        .addBodyParameter("threadid", threadid)
                        .setTag("report")

                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                swipeRefreshLayout.setRefreshing(false);

                                Log.d(TAG, "onResponse: " + response);
                                Gson gson = new Gson();
                                List<Object> list = new ArrayList<>();

                                try {
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject json = response.getJSONObject(i);
                                        CommentModel coModel = gson.fromJson(json.toString(), CommentModel.class);
                                        list.add(coModel);

                                        if (json.has("reply")) {
                                            JSONArray jsonReply = json.getJSONArray("reply");

                                            for (int j = 0; j < jsonReply.length(); j++) {
                                                JSONObject jsontemp = jsonReply.getJSONObject(j);
                                                ReplyCommentModel temp = gson.fromJson(jsontemp.toString(), ReplyCommentModel.class);
                                                list.add(temp);
                                            }
                                        }

                                    }
                                    adapterComment.add(list);
                                    if (list.size() == 0) {
                                        nocommenttxt.setVisibility(View.VISIBLE);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(ANError anError) {
                                swipeRefreshLayout.setRefreshing(false);

                                Log.d(TAG, "onError: " + anError);
                                Toast.makeText(CommentActivity.this, "couldn't connect", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }, 500);
    }

    private void initSendButton() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) CommentActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                if (editText.getText().toString().trim().length() > 0) {

                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("uid", userSessionManager.getUID());
                    hashMap.put("threadid", threadid);
                    hashMap.put("image", "");
                    String url;
                    if (replyheader.getVisibility() == View.VISIBLE) {
                        url = "https://www.reweyou.in/google/create_reply.php";
                        // hashMap.put("commentid", tempcommentid);
                        hashMap.put("commentid", tempcommentid);
                        hashMap.put("reply", editText.getText().toString());

                        Log.d(TAG, "onClick: reply");
                    } else {
                        url = "https://www.reweyou.in/google/create_comments.php";
                        hashMap.put("comment", editText.getText().toString().trim());

                    }
                    editText.setText("");


                    AndroidNetworking.post(url)
                            .addBodyParameter(hashMap)
                            .setTag("comment")
                            .setPriority(Priority.HIGH)
                            .build()
                            .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG, "onResponse: " + response);
                                    //   Toast.makeText(CommentActivity.this,response,Toast.LENGTH_SHORT).show();
                                    if (response.equals("Comment created")) {
                                        getData();
                                    } else if (response.equals("Reply created")) {
                                        getData();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.d(TAG, "onError: anerror" + anError);
                                }
                            });
                }


            }
        });
        send.setClickable(false);
        send.setTag("0");
    }

    private void initTextWatcherEditText() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 0) {
                    send.setTag("0");
                    send.setClickable(false);
                    send.animate().scaleY(0.0f).scaleX(0.0f).setDuration(200).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            send.setImageResource(R.drawable.button_send_disable);
                            send.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(new DecelerateInterpolator()).setDuration(300);
                        }
                    });

                } else if (s.toString().trim().length() > 0) {

                    send.setClickable(true);

                    if (!send.getTag().toString().equals("1")) {
                        send.setTag("1");
                        send.animate().scaleY(0.0f).scaleX(0.0f).setDuration(200).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                send.setImageResource(R.drawable.button_send_comments);

                                send.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(new DecelerateInterpolator()).setDuration(300);
                            }
                        });
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void passClicktoEditText(String s, String commentid) {
        this.tempcommentid = commentid;
        if (replyheader.getVisibility() == View.GONE) {
            replyheader.setVisibility(View.VISIBLE);
            editText.setHint("Write a reply...");

        } else {
            replyheader.setVisibility(View.GONE);
            editText.setHint("Write a comment...");

        }

        replyheader.setText("Reply to " + s);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.performClick();
        final InputMethodManager inputMethodManager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
}
