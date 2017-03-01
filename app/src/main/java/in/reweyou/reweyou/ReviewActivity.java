package in.reweyou.reweyou;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;

import in.reweyou.reweyou.adapter.ReviewAdapter;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.customView.CustomSigninDialog;
import in.reweyou.reweyou.customView.PreCachingLayoutManager;
import in.reweyou.reweyou.model.ReviewModel;

public class ReviewActivity extends AppCompatActivity {

    private static final String TAG = ReviewActivity.class.getName();
    private RecyclerView recyclerView;
    private ReviewAdapter adapter;
    private String headline;
    private String description;
    private String rating;
    private String user;
    private String review;
    private String tag;
    private TextView tvheadline, tvdescription, tvrating, tvreview, tvuser;
    private ImageView image;
    private String imageurl;
    private String topicid;
    private String videourl;
    private String gifurl;
    private String name;
    private UserSessionManager sessionManager;
    private ImageView send;
    private EditText edittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sessionManager = new UserSessionManager(this);
        tvheadline = (TextView) findViewById(R.id.headline);
        tvdescription = (TextView) findViewById(R.id.description);
        tvrating = (TextView) findViewById(R.id.rating);
        tvreview = (TextView) findViewById(R.id.review);
        tvuser = (TextView) findViewById(R.id.user);
        image = (ImageView) findViewById(R.id.image);
        send = (ImageView) findViewById(R.id.send);
        edittext = (EditText) findViewById(R.id.desc);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) ReviewActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (sessionManager.checkLoginSplash())
                        updateReview();
                        else showlogindialog();

                    }
                });

            }
        });
        send.setClickable(false);
        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    send.setClickable(false);
                    send.setImageResource(R.drawable.button_send_disable);

                } else {
                    send.setClickable(true);
                    send.setImageResource(R.drawable.button_send_comments);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        if (getIntent() != null) {
            Intent i = getIntent();
            headline = i.getStringExtra("headline");
            description = i.getStringExtra("description");
            rating = i.getStringExtra("rating");
            user = i.getStringExtra("user");
            review = i.getStringExtra("review");
            tag = i.getStringExtra("tag");
            imageurl = i.getStringExtra("image");
            videourl = i.getStringExtra("video");
            name = i.getStringExtra("name");
            gifurl = i.getStringExtra("gif");
            topicid = i.getStringExtra("topicid");
        }
        getSupportActionBar().setTitle(tag);

        tvheadline.setText(headline);
        tvdescription.setText(description);
        tvrating.setText(rating);
        tvreview.setText(review);
        tvuser.setText(name);

        if (!gifurl.isEmpty()) {
            Glide.with(ReviewActivity.this).load(gifurl).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image);

        } else
            Glide.with(ReviewActivity.this).load(imageurl).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        PreCachingLayoutManager preCachingLayoutManager = new PreCachingLayoutManager(this);

        recyclerView.setLayoutManager(preCachingLayoutManager);
        /*VerticalSpaceItemDecorator verticalSpaceItemDecorator = new VerticalSpaceItemDecorator((int) pxFromDp(this, 6));
        recyclerView.addItemDecoration(verticalSpaceItemDecorator);*/
        adapter = new ReviewAdapter(this);
        recyclerView.setAdapter(adapter);
        loadReportsfromServer();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context mContext = ReviewActivity.this;
                if (!videourl.isEmpty()) {
                    Intent in = new Intent(mContext, VideoDisplay.class);
                    in.putExtra("myData", videourl);
                    in.putExtra("tag", tag);
                    in.putExtra("headline", headline);
                    if (headline != null)
                        in.putExtra("description", headline);
                    mContext.startActivity(in);

                } else if (!imageurl.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("myData", imageurl);
                    bundle.putString("tag", tag);
                    bundle.putString("headline", headline);
                    Intent in = new Intent(mContext, FullImage.class);
                    in.putExtras(bundle);
                    mContext.startActivity(in);
                }
            }
        });
    }

    private void updateReview() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("topicid", topicid);
        hashMap.put("number", sessionManager.getMobileNumber());
        hashMap.put("rating", "5");
        hashMap.put("description", edittext.getText().toString());
        edittext.setText("");

        AndroidNetworking.post("https://reweyou.in/reviews/post_reviews.php")
                .addBodyParameter(hashMap)
                .setTag("repordt")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        loadReportsfromServer();


                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "error: " + anError.getErrorDetail());
                        Toast.makeText(ReviewActivity.this, "Couldn't connect", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void loadReportsfromServer() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("topicid", topicid);
        hashMap.put("number", sessionManager.getMobileNumber());
        AndroidNetworking.post("https://reweyou.in/reviews/reviews.php")
                .addBodyParameter(hashMap)
                .setTag("report")
                .setPriority(Priority.HIGH)
                .build()
                .getAsParsed(new TypeToken<List<ReviewModel>>() {
                }, new ParsedRequestListener<List<ReviewModel>>() {

                    @Override
                    public void onResponse(List<ReviewModel> list) {
                        adapter.add(list);
                        Log.d(TAG, "onResponse: lis" + list.size());

                    }

                    @Override
                    public void onError(final ANError anError) {
                        Log.e(TAG, "error: " + anError.getErrorDetail());

                        /*new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipe.setRefreshing(false);
                                if (isAdded() && !anError.getErrorDetail().equals("requestCancelled"))
                                    Toast.makeText(mContext, "Couldn't connect", Toast.LENGTH_SHORT).show();


                            }
                        }, 1200);*/


                    }
                });
    }

    public float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    private void showlogindialog() {
        CustomSigninDialog customSigninDialog = new CustomSigninDialog(ReviewActivity.this);
        customSigninDialog.show();

    }
}
