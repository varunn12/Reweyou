package in.reweyou.reweyou;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.LinearLayout;
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
import com.wang.avi.AVLoadingIndicatorView;

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
    private AVLoadingIndicatorView loadingcircular;
    private String status;
    private View divider2;
    private LinearLayout b1;
    private TextView ratetext;
    private LinearLayout c1;
    private int numrating = 0;
    private TextView noreviewyet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        loadingcircular = (AVLoadingIndicatorView) findViewById(R.id.avi);
        loadingcircular.show();
        sessionManager = new UserSessionManager(this);

        final ImageView ra1 = (ImageView) findViewById(R.id.ra1);
        final ImageView ra2 = (ImageView) findViewById(R.id.ra2);
        final ImageView ra3 = (ImageView) findViewById(R.id.ra3);
        final ImageView ra4 = (ImageView) findViewById(R.id.ra4);
        final ImageView ra5 = (ImageView) findViewById(R.id.ra5);
        ra1.setColorFilter(Color.parseColor("#e0e0e0"));
        ra2.setColorFilter(Color.parseColor("#e0e0e0"));
        ra3.setColorFilter(Color.parseColor("#e0e0e0"));
        ra4.setColorFilter(Color.parseColor("#e0e0e0"));
        ra5.setColorFilter(Color.parseColor("#e0e0e0"));
        ra1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ra1.setColorFilter(Color.parseColor("#29B6F6"));
                ra2.setColorFilter(Color.parseColor("#e0e0e0"));
                ra3.setColorFilter(Color.parseColor("#e0e0e0"));
                ra4.setColorFilter(Color.parseColor("#e0e0e0"));
                ra5.setColorFilter(Color.parseColor("#e0e0e0"));

                numrating = 1;

            }
        });

        ra2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ra1.setColorFilter(Color.parseColor("#29B6F6"));
                ra2.setColorFilter(Color.parseColor("#29B6F6"));
                ra3.setColorFilter(Color.parseColor("#e0e0e0"));
                ra4.setColorFilter(Color.parseColor("#e0e0e0"));
                ra5.setColorFilter(Color.parseColor("#e0e0e0"));
                numrating = 2;


            }
        });

        ra3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ra1.setColorFilter(Color.parseColor("#29B6F6"));
                ra2.setColorFilter(Color.parseColor("#29B6F6"));
                ra3.setColorFilter(Color.parseColor("#29B6F6"));
                ra4.setColorFilter(Color.parseColor("#e0e0e0"));
                ra5.setColorFilter(Color.parseColor("#e0e0e0"));
                numrating = 3;


            }
        });

        ra4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ra1.setColorFilter(Color.parseColor("#29B6F6"));
                ra2.setColorFilter(Color.parseColor("#29B6F6"));
                ra3.setColorFilter(Color.parseColor("#29B6F6"));
                ra4.setColorFilter(Color.parseColor("#29B6F6"));

                ra5.setColorFilter(Color.parseColor("#e0e0e0"));
                numrating = 4;

            }
        });

        ra5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ra1.setColorFilter(Color.parseColor("#29B6F6"));
                ra2.setColorFilter(Color.parseColor("#29B6F6"));
                ra3.setColorFilter(Color.parseColor("#29B6F6"));
                ra4.setColorFilter(Color.parseColor("#29B6F6"));
                ra5.setColorFilter(Color.parseColor("#29B6F6"));

                numrating = 5;

            }
        });


        tvheadline = (TextView) findViewById(R.id.headline);
        noreviewyet = (TextView) findViewById(R.id.noreviewyet);
        ratetext = (TextView) findViewById(R.id.ratetext);
        c1 = (LinearLayout) findViewById(R.id.c1);
        b1 = (LinearLayout) findViewById(R.id.b1);
        divider2 = findViewById(R.id.divider2);
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
                        if (sessionManager.checkLoginSplash()) {
                            if (numrating == 0) {
                                Toast.makeText(ReviewActivity.this, "Please rate the issue", Toast.LENGTH_SHORT).show();
                            } else if (edittext.getText().toString().trim().length() == 0) {
                                Toast.makeText(ReviewActivity.this, "Your review cannot be empty", Toast.LENGTH_SHORT).show();

                            } else
                                updateReview();
                        } else showlogindialog();

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

        if (!sessionManager.checkLoginSplash())
            edittext.setHint("Sign in to review...");

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
            status = i.getStringExtra("status");
        }
        getSupportActionBar().setTitle(tag);

        tvheadline.setText(headline);
        tvdescription.setText(description);
        tvrating.setText(rating);
        tvreview.setText(review);
        tvuser.setText("By- " + name);

        if (!gifurl.isEmpty()) {
            Glide.with(ReviewActivity.this).load(gifurl).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image);

        } else if (!imageurl.isEmpty())
            Glide.with(ReviewActivity.this).load(imageurl).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image);
        else image.setVisibility(View.GONE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        PreCachingLayoutManager preCachingLayoutManager = new PreCachingLayoutManager(this);
        recyclerView.setNestedScrollingEnabled(false);
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

        if (status.equals("true")) {
            ratetext.setVisibility(View.GONE);
            b1.setVisibility(View.GONE);
            c1.setVisibility(View.GONE);
            divider2.setVisibility(View.GONE);
        }
    }

    private void updateReview() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("topicid", topicid);
        hashMap.put("number", sessionManager.getMobileNumber());
        hashMap.put("rating", String.valueOf(numrating));
        hashMap.put("description", edittext.getText().toString());
        hashMap.put("token", sessionManager.getKeyAuthToken());
        hashMap.put("deviceid", sessionManager.getDeviceid());
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
                        Log.d(TAG, "onResponse: " + response);


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
        hashMap.put("token", sessionManager.getKeyAuthToken());
        hashMap.put("deviceid", sessionManager.getDeviceid());
        AndroidNetworking.post("https://reweyou.in/reviews/reviews.php")
                .addBodyParameter(hashMap)
                .setTag("report")
                .setPriority(Priority.HIGH)
                .build()
                .getAsParsed(new TypeToken<List<ReviewModel>>() {
                }, new ParsedRequestListener<List<ReviewModel>>() {

                    @Override
                    public void onResponse(List<ReviewModel> list) {
                        if (list.size() == 0)
                            noreviewyet.setVisibility(View.VISIBLE);
                        else noreviewyet.setVisibility(View.GONE);
                        loadingcircular.hide();
                        adapter.add(list);
                        Log.d(TAG, "onResponse: lis" + list.size());

                    }

                    @Override
                    public void onError(final ANError anError) {
                        loadingcircular.hide();
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

    @Override
    protected void onNewIntent(Intent intent) {
        try {

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
                status = i.getStringExtra("status");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ReviewActivity.this, Feed.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
        finish();
    }
}
