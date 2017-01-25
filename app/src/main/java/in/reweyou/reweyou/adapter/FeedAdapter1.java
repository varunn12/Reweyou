package in.reweyou.reweyou.adapter;


import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TimingLogger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.reweyou.reweyou.CategoryActivity;
import in.reweyou.reweyou.Comments1;
import in.reweyou.reweyou.Contacts;
import in.reweyou.reweyou.FullImage;
import in.reweyou.reweyou.LikesActivity;
import in.reweyou.reweyou.MyCityActivity;
import in.reweyou.reweyou.PostReport;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.SinglePostActivity;
import in.reweyou.reweyou.UserProfile;
import in.reweyou.reweyou.VideoDisplay;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.CustomTabActivityHelper;
import in.reweyou.reweyou.classes.UploadOptions;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.fragment.BaseFragment;
import in.reweyou.reweyou.model.FeedModel;
import in.reweyou.reweyou.utils.Constants;
import in.reweyou.reweyou.utils.ReportLoadingConstant;

import static in.reweyou.reweyou.utils.Constants.EDIT_URL;
import static in.reweyou.reweyou.utils.Constants.URL_LIKE;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_CITY_NO_REPORTS_YET;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_IMAGE;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_LOADING;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_LOCATION;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_NEW_POST;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_READING_NO_READERS;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_VIDEO;


public class FeedAdapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final String TAG = FeedAdapter1.class.getSimpleName();
    private final String preunlike = "preunlike";
    private final String prelike = "prelike";
    private final String errorunliking = "errorunliking";
    private final String errorliking = "errorliking";
    private final String daynightanim = "dayanim";

    private final int PRE_UNLIKE = 1;
    private final int PRE_LIKE = 2;
    private final int ERROR_UNLIKING = -1;
    private final int ERROR_LIKING = -2;


    private final int REBIND_NUM_LIKES = 11;
    private final int REBIND_NUM_REACTIONS = 12;
    private final int REBIND_TIME = 13;
    private final int REBIND_TAG = 13;
    private final int fragmentCategory;
    private final Rect bounds;
    private final Paint paint;
    private final BaseFragment fragment;

   /* private final int REBIND_HEADLINE = 13;
    private final int REBIND_DESCRIPTION = 13;
    private final int REBIND_PROFILEPIC = 13;
    private final int REBIND_DESCRIPTION = 13;
    private final int REBIND_DESCRIPTION = 13;
    private final int REBIND_DESCRIPTION = 13;*/


    Activity activity;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    CustomTabActivityHelper mCustomTabActivityHelper;
    UserSessionManager session;
    Uri uri;
    private UploadOptions uploadOption;
    private int qu_position = -3;
    private String placename;
    private List<FeedModel> messagelist;
    private Context mContext;
    private String id, postid;
    private EditText editTextHeadline;
    private Button buttonEdit;
    private String number;
    private String username;
    private boolean fragmentWithBox;
    private int numOfTopboxes = 0;

    public FeedAdapter1(Activity mContext, int fragmentCategory, UserSessionManager sessionManager, BaseFragment baseFragment) {
        this.mContext = mContext;
        this.fragmentCategory = fragmentCategory;
        this.messagelist = new ArrayList<>();
        this.session = sessionManager;
        this.fragment = baseFragment;
        bounds = new Rect();
        paint = new Paint();
        paint.setTextSize(28);

    }

    private static Bitmap drawToBitmap(Context context, final int layoutResId, final int width, final int height) {

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(layoutResId, null);
        layout.setDrawingCacheEnabled(true);
        layout.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST));
        layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());
        final Bitmap bmp = Bitmap.createBitmap(layout.getMeasuredWidth(), layout.getMeasuredHeight(), Bitmap.Config.ARGB_4444);
        final Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(layout.getDrawingCache(), 0, 0, new Paint());
        return bmp;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

   /* @Override
    public long getItemId(int position) {
        return position;
    }
*/

    @Override
    public long getItemId(int position) {
        Log.d(TAG, "getItemId: " + messagelist.get(position).getPostId());
        return Long.parseLong(messagelist.get(position).getPostId());
    }

    private void initTimer() {
        final CountDownTimer countDownTimer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                start();
                updateReportTime();
            }

        };

        countDownTimer.start();
    }

    private void updateReportTime() {
        notifyItemRangeChanged(0, messagelist.size(), REBIND_TIME);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Log.w(TAG, "onCreateViewHolder: called");
        switch (viewType) {
            case VIEW_TYPE_IMAGE:
                return new ImageViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_feed_adapter_image, viewGroup, false));
            case VIEW_TYPE_VIDEO:
                return new VideoViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_feed_adapter_video, viewGroup, false));
            case VIEW_TYPE_LOADING:
                return new LoadingViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_feed_adapter_loading, viewGroup, false));
            case VIEW_TYPE_NEW_POST:
                return new NewPostViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_feed_adapter_new_post, viewGroup, false));
            case VIEW_TYPE_LOCATION:
                return new LocationViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_feed_adapter_location1, viewGroup, false));
            case VIEW_TYPE_CITY_NO_REPORTS_YET:
                return new CityNoReportsviewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_feed_adapter_city_no_reports_yet, viewGroup, false));
            case VIEW_TYPE_READING_NO_READERS:
                return new ReadingNoReadersViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_feed_adapter_reading_no_readers, viewGroup, false));
            default:
                Log.d(TAG, "onCreateViewHolder: wekfdwenklfwenfewnjfdnwelfcnwencl'wencwqj998weuf8qe8f23");
                return null;

        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder2, final int position) {
        Log.d("aaa", "onBindViewHolder: called" + position);
        switch (viewHolder2.getItemViewType()) {
            case VIEW_TYPE_LOADING:
                break;
            case VIEW_TYPE_VIDEO:
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        bindVideo(position, viewHolder2);

                    }
                });
                break;
            case VIEW_TYPE_IMAGE:
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        bindImageOrGif(position, viewHolder2);

                    }
                });
                break;
            case VIEW_TYPE_LOCATION:
                //  bindLocation(position, viewHolder2);
                break;
            case VIEW_TYPE_NEW_POST:
                break;
        }
    }

    private void bindLocation(int position, RecyclerView.ViewHolder viewHolder2) {
        LocationViewHolder locationViewHolder = (LocationViewHolder) viewHolder2;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mHolder, int position, List<Object> payloads) {
        if (mHolder instanceof BaseViewHolder) {
            BaseViewHolder holder = (BaseViewHolder) mHolder;
            if (payloads.isEmpty())
                super.onBindViewHolder(holder, position, payloads);
            else if (payloads.contains(REBIND_TIME)) {
                Log.d(TAG, "onBindViewHolder: REBIND_TIME called");
            } else if (payloads.contains(prelike)) {
                Log.d("reach", prelike);


                holder.upicon.setColorFilter(ContextCompat.getColor(mContext, R.color.rank));
                holder.upvote.setTextColor(ContextCompat.getColor(mContext, R.color.rank));

                if (Integer.parseInt(messagelist.get(position).getReviews()) == 0)
                    holder.reviews.setText(String.valueOf(Integer.parseInt(messagelist.get(position).getReviews()) + 1) + " like");
                else
                    holder.reviews.setText(String.valueOf(Integer.parseInt(messagelist.get(position).getReviews()) + 1) + " likes");

                holder.reviews.setTypeface(Typeface.DEFAULT_BOLD);
                holder.reviews.setTextColor(mContext.getResources().getColor(R.color.rank));
                Log.d("likes", String.valueOf(Integer.parseInt(messagelist.get(position).getReviews()) + 1));
            } else if (payloads.contains(errorliking)) {
                Log.d("reach", errorliking);


                holder.upicon.setColorFilter(ContextCompat.getColor(mContext, R.color.main));
                holder.upvote.setTextColor(ContextCompat.getColor(mContext, R.color.main));


                if (Integer.parseInt(messagelist.get(position).getReviews()) == 0) {
                    holder.reviews.setTypeface(Typeface.DEFAULT);
                    holder.reviews.setTextColor(mContext.getResources().getColor(R.color.main));
                    holder.reviews.setText(String.valueOf(Integer.parseInt(messagelist.get(position).getReviews())) + " like");

                } else if (Integer.parseInt(messagelist.get(position).getReviews()) == 1)
                    holder.reviews.setText(String.valueOf(Integer.parseInt(messagelist.get(position).getReviews())) + " like");
                else
                    holder.reviews.setText(String.valueOf(Integer.parseInt(messagelist.get(position).getReviews())) + " likes");

            } else if (payloads.contains(preunlike)) {
                Log.d("reach", preunlike);

                holder.upicon.setColorFilter(ContextCompat.getColor(mContext, R.color.main));
                holder.upvote.setTextColor(ContextCompat.getColor(mContext, R.color.main));

                if (Integer.parseInt(messagelist.get(position).getReviews()) == 2) {
                    holder.reviews.setText(String.valueOf(Integer.parseInt(messagelist.get(position).getReviews()) - 1) + " like");

                } else if (Integer.parseInt(messagelist.get(position).getReviews()) == 1) {
                    holder.reviews.setTypeface(Typeface.DEFAULT);
                    holder.reviews.setTextColor(mContext.getResources().getColor(R.color.main));
                    holder.reviews.setText(String.valueOf(Integer.parseInt(messagelist.get(position).getReviews()) - 1) + " like");

                } else
                    holder.reviews.setText(String.valueOf(Integer.parseInt(messagelist.get(position).getReviews()) - 1) + " likes");
            } else if (payloads.contains(errorunliking)) {
                Log.d("reach", errorunliking);


                holder.upicon.setColorFilter(ContextCompat.getColor(mContext, R.color.rank));
                holder.upvote.setTextColor(ContextCompat.getColor(mContext, R.color.rank));

                holder.reviews.setTypeface(Typeface.DEFAULT_BOLD);
                holder.reviews.setTextColor(mContext.getResources().getColor(R.color.rank));
                holder.reviews.setText(String.valueOf(Integer.parseInt(messagelist.get(position).getReviews())) + " likes");
            }
        } else if (mHolder instanceof LocationViewHolder) {
            if (payloads.isEmpty())
                super.onBindViewHolder(mHolder, position, payloads);
            else if (payloads.contains(daynightanim)) {
                rundaynightanim((LocationViewHolder) mHolder);
            }
        } else super.onBindViewHolder(mHolder, position, payloads);

    }

    @Override
    public int getItemViewType(int position) {
        return messagelist.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    private void bindImageOrGif(final int position, RecyclerView.ViewHolder viewHolder2) {
        final ImageViewHolder viewHolder = (ImageViewHolder) viewHolder2;
        TimingLogger timings = new TimingLogger("abc", "methodA");

      /*  if (messagelist.get(position).getHeadline() != null) {
            Spannable spannable = new SpannableString(messagelist.get(position).getHeadline());
            Util.linkifyUrl(spannable, new CustomTabsOnClickListener(activity, mCustomTabActivityHelper));
            setDescription(viewHolder, position, spannable);

        }*/

        if (messagelist.get(position).getHeadline() != null) {
            if (messagelist.get(position).getHeadline().isEmpty())
                viewHolder.headline.setVisibility(View.GONE);
            else {
                viewHolder.headline.setVisibility(View.VISIBLE);
                viewHolder.headline.setText(messagelist.get(position).getHeadline());

            }
        }


        timings.addSplit("work A");

        setEditButton(viewHolder, position);
        timings.addSplit("work 2");

        setHeadline(viewHolder, position);
        timings.addSplit("work 3");


        setDate(viewHolder, position);
        timings.addSplit("work 4");


        setReporterProfilePic(viewHolder, position);

        timings.addSplit("work 5");

        setFeedLocation(viewHolder, position);
        timings.addSplit("work 6");

        setReporterName(viewHolder, position);
        timings.addSplit("work 7");

        setReactions(viewHolder, position);
        timings.addSplit("work 8");

        setLikesNumber(viewHolder, position);
        timings.addSplit("work 10");

        setLikedStatus(viewHolder, position);
        timings.addSplit("work 11");

        setFeedCategory(viewHolder, position);
        timings.dumpToLog();

        setFeedImage(viewHolder, position);


    }

    private void bindVideo(final int position, RecyclerView.ViewHolder viewHolder2) {
        final VideoViewHolder viewHolder = (VideoViewHolder) viewHolder2;
        if (messagelist.get(position).getHeadline() != null) {
            if (messagelist.get(position).getHeadline().isEmpty())
                viewHolder.headline.setVisibility(View.GONE);
            else {
                viewHolder.headline.setVisibility(View.VISIBLE);
                viewHolder.headline.setText(messagelist.get(position).getHeadline());

            }
        }

        setEditButton(viewHolder, position);

        setNumOfViews(viewHolder, position);

        setHeadline(viewHolder, position);


        setDate(viewHolder, position);

        setReporterProfilePic(viewHolder, position);

        setFeedVideoThumbnail(viewHolder, position);

        setFeedLocation(viewHolder, position);

        setReporterName(viewHolder, position);

        setReactions(viewHolder, position);


        setLikesNumber(viewHolder, position);

        setLikedStatus(viewHolder, position);

        setFeedCategory(viewHolder, position);
    }

    private void setEditButton(BaseViewHolder viewHolder, int position) {
        if (session.getMobileNumber().equals(messagelist.get(position).getNumber())) {
            viewHolder.menuEdit.setVisibility(View.VISIBLE);
        } else viewHolder.menuEdit.setVisibility(View.INVISIBLE);

    }

    private void setNumOfViews(VideoViewHolder viewHolder, int position) {
        if (messagelist.get(position).getPostviews().equals("0")) {
            viewHolder.views.setVisibility(View.GONE);
        } else if (messagelist.get(position).getPostviews().equals("1")) {
            viewHolder.views.setVisibility(View.VISIBLE);
            viewHolder.views.setText(messagelist.get(position).getPostviews() + " view");
        } else {
            viewHolder.views.setVisibility(View.VISIBLE);
            viewHolder.views.setText(messagelist.get(position).getPostviews() + " views");
        }
    }

    private void setLikedStatus(BaseViewHolder viewHolder, int position) {

        if (messagelist.get(position).isLiked()) {
            viewHolder.upicon.setColorFilter(ContextCompat.getColor(mContext, R.color.rank));
            viewHolder.upvote.setTextColor(ContextCompat.getColor(mContext, R.color.rank));

        } else {
            viewHolder.upicon.setColorFilter(ContextCompat.getColor(mContext, R.color.main));
            viewHolder.upvote.setTextColor(ContextCompat.getColor(mContext, R.color.main));
        }
    }

    private void setFeedCategory(BaseViewHolder viewHolder, int position) {
        viewHolder.source.setText('#' + messagelist.get(position).getCategory());

    }

    private void setLikesNumber(BaseViewHolder viewHolder, int position) {
        if (messagelist.get(position).getReviews().equals("0")) {
            viewHolder.reviews.setText("0 like");
            viewHolder.reviews.setTypeface(Typeface.DEFAULT);
            viewHolder.reviews.setTextColor(mContext.getResources().getColor(R.color.main));
        } else if (messagelist.get(position).getReviews().equals("1")) {
            viewHolder.reviews.setText("1 like");
            viewHolder.reviews.setTypeface(Typeface.DEFAULT_BOLD);
            viewHolder.reviews.setTextColor(mContext.getResources().getColor(R.color.rank));
        } else {
            viewHolder.reviews.setText(String.valueOf(Integer.parseInt(messagelist.get(position).getReviews())) + " likes");
            viewHolder.reviews.setTypeface(Typeface.DEFAULT_BOLD);

            viewHolder.reviews.setTextColor(mContext.getResources().getColor(R.color.rank));
        }

    }

    private void setReactions(BaseViewHolder viewHolder, int position) {
        Log.d(TAG, "setReactions: " + messagelist.get(position).getComments());
        if (messagelist.get(position).getComments().equals("0")) {
            viewHolder.app.setText("0 Reactions");
            viewHolder.name.setText("No Reactions yet");
            viewHolder.userName.setText("Be the first one to react...");
            viewHolder.rv.setVisibility(View.GONE);

        } else {
            viewHolder.rv.setVisibility(View.VISIBLE);

            viewHolder.app.setText(messagelist.get(position).getComments() + " Reactions");
            viewHolder.name.setText(messagelist.get(position).getFrom());
            viewHolder.userName.setText(messagelist.get(position).getReaction());

            /*if (!messagelist.get(position).getComments().equals("0") && messagelist.get(position).getReaction() != null) {
                //  viewHolder.rv.setVisibility(View.VISIBLE);

                Spannable spannables = new SpannableString(messagelist.get(position).getReaction());
                Util.linkifyUrl(spannables, new CustomTabsOnClickListener(activity, mCustomTabActivityHelper));
                viewHolder.userName.setText(spannables);
                viewHolder.userName.setMovementMethod(LinkMovementMethod.getInstance());

                viewHolder.name.setText(messagelist.get(position).getFrom());
            } else {
                // viewHolder.rv.setVisibility(View.GONE);
            }*/
        }

    }

    private void setReporterName(BaseViewHolder viewHolder, int position) {

        viewHolder.from.setText(messagelist.get(position).getName());

    }

    private void setFeedLocation(BaseViewHolder viewHolder, int position) {
        viewHolder.place.setText(messagelist.get(position).getLocation());

    }

    private void setFeedImage(ImageViewHolder viewHolder, int position) {
        TimingLogger timings = new TimingLogger("abc", "methodC");

        if (messagelist.get(position).getImage().isEmpty()) {
            if (messagelist.get(position).getGif().isEmpty()) {
                viewHolder.image.setVisibility(View.GONE);
            } else {
                viewHolder.image.setVisibility(View.VISIBLE);

                viewHolder.image.setAdjustViewBounds(false);
                Glide.with(mContext).load(messagelist.get(position).getGif()).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.ic_broken_image_black_48dp).into(viewHolder.image);
            }
        } else {
            // viewHolder.image.setAdjustViewBounds(true);
            viewHolder.image.setVisibility(View.VISIBLE);

            timings.addSplit("a");
            Glide.with(mContext).load(messagelist.get(position).getImage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.ic_broken_image_black_48dp).into(viewHolder.image);
            timings.addSplit("s");
            timings.dumpToLog();
        }
    }

    private void setFeedVideoThumbnail(final VideoViewHolder viewHolder, int position) {

        if (messagelist.get(position).getImage() == null || messagelist.get(position).getImage().isEmpty()) {
            viewHolder.image.setVisibility(View.GONE);
        } else {
            viewHolder.play.setVisibility(View.VISIBLE);
            viewHolder.image.setVisibility(View.VISIBLE);
            viewHolder.image.setColorFilter(Color.argb(120, 0, 0, 0)); // black Tint


            Glide.with(mContext).load(messagelist.get(position).getImage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.ic_broken_image_black_48dp).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    viewHolder.play.setVisibility(View.INVISIBLE);

                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    return false;
                }
            }).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(viewHolder.image);
        }
    }

    private void setReporterProfilePic(BaseViewHolder viewHolder, int position) {
        Glide.with(mContext).load(messagelist.get(position).getProfilepic()).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.download).into(viewHolder.profilepic);

    }

    private void setDate(BaseViewHolder viewHolder, int position) {
        viewHolder.date.setText(messagelist.get(position).getDate());
    }

    private void setDescription(BaseViewHolder viewHolder, int position, Spannable spannable) {

        if (messagelist.get(position).getHeadline() == null || messagelist.get(position).getHeadline().isEmpty())
            viewHolder.headline.setVisibility(View.GONE);
        else {
            viewHolder.headline.setVisibility(View.VISIBLE);
            viewHolder.headline.setText(spannable);
            viewHolder.headline.setMovementMethod(LinkMovementMethod.getInstance());
        }

    }

    private void setHeadline(BaseViewHolder viewHolder, int position) {
        if (messagelist.get(position).getHead() == null || messagelist.get(position).getHead().isEmpty())
            viewHolder.head.setVisibility(View.GONE);
        else {
            viewHolder.head.setVisibility(View.VISIBLE);
            viewHolder.head.setText(messagelist.get(position).getHead());
        }
    }

    public void remove() {
        messagelist.remove(messagelist.size() - 1);
        notifyItemRemoved(messagelist.size());
    }

    public void loadMore(List<FeedModel> messagelist2) {
        this.messagelist.addAll(messagelist2);
        notifyItemRangeInserted(this.messagelist.size() - messagelist2.size(), messagelist2.size());
    }

    private void takeScreenshot(CardView cv) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {

            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Pictures/Reweyou/Screenshot");

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("Reweyou", "failed to create directory");
                }
            }

            String mPath = mediaStorageDir.toString() + "/" + now + ".jpg";
            File imageFile = new File(mPath);
            uri = Uri.fromFile(imageFile);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 90;
            cv.setDrawingCacheEnabled(true);
            cv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
            loadBitmapFromView(cv).compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            ShareIntent();

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(b);
        v.draw(c);

        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final Bitmap b2 = drawToBitmap(mContext, R.layout.share_reweyou_tag, metrics.widthPixels, metrics.heightPixels);
        return combineImages(b, b2);
    }

    private Bitmap combineImages(Bitmap c, Bitmap s) {
        Bitmap cs = null;

        int width, height = 0;

        width = s.getWidth();
        height = c.getHeight() + s.getHeight();
        Log.d("width", "" + c.getWidth() + "     " + s.getWidth());
        Log.d("height", "" + c.getHeight() + "     " + s.getHeight());
        cs = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, 0f, c.getHeight(), null);
        return cs;
    }

    private void ShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Download Reweyou App to read and report. https://goo.gl/o5Kyqc");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/jpeg");
        //intent.setPackage("com.whatsapp");
        mContext.startActivity(Intent.createChooser(intent, "Share image using"));
    }

    private void editHeadline(int position) throws JSONException {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(mContext);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_edit, null);
        postid = messagelist.get(position).getPostId();
        //  number=session.getMobileNumber();
        //Initizliaing confirm button fo dialog box and edittext of dialog box
        buttonEdit = (Button) confirmDialog.findViewById(R.id.buttonConfirm);
        editTextHeadline = (EditText) confirmDialog.findViewById(R.id.editTextOtp);
        editTextHeadline.setText(messagelist.get(position).getHeadline());
        editTextHeadline.setSelection(editTextHeadline.getText().length());

        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Displaying the alert dialog
        alertDialog.show();

        //On the click of the confirm button from alert dialog
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hiding the alert dialog
                alertDialog.dismiss();
                //Displaying a progressbar
                final ProgressDialog loading = ProgressDialog.show(mContext, "Updating", "Please wait", false, false);
                //Getting the user entered otp from edittext
                final String headline = editTextHeadline.getText().toString().trim();
                //Creating an string request
                StringRequest stringRequest = new StringRequest(Request.Method.POST, EDIT_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //if the server response is success
                                if (response.equalsIgnoreCase("success")) {
                                    //dismissing the progressbar
                                    loading.dismiss();

                                    //Starting a new activity
                                    if (fragment != null)
                                        fragment.onRefresh();
                                } else {
                                    //Displaying a toast if the otp entered is wrong
                                    loading.dismiss();
                                    Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_LONG).show();

                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                alertDialog.dismiss();
                                loading.dismiss();
                                Toast.makeText(mContext, "Try again later", Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        //Adding the parameters otp and username
                        params.put("postid", postid);
                        params.put("headline", headline);
                        params.put("number", session.getMobileNumber());
                        params.put("token", session.getKeyAuthToken());
                        params.put("deviceid", session.getDeviceid());
                        return params;
                    }
                };
                //Adding the request to the queue
                RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                requestQueue.add(stringRequest);
            }
        });
    }

    private void makeRequest(final int adapterPosition) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LIKE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);
                        if (response.equals("like")) {
                            session.addlike(messagelist.get(adapterPosition).getPostId());
                            messagelist.get(adapterPosition).setLiked(true);
                            messagelist.get(adapterPosition).setReviews(String.valueOf((Integer.parseInt(messagelist.get(adapterPosition).getReviews()) + 1)));

                        } else if (response.equals("unlike")) {
                            session.removelike(messagelist.get(adapterPosition).getPostId());
                            messagelist.get(adapterPosition).setLiked(false);
                            messagelist.get(adapterPosition).setReviews(String.valueOf((Integer.parseInt(messagelist.get(adapterPosition).getReviews()) - 1)));


                        } else if (response.equals("Error")) {

                            if (messagelist.get(adapterPosition).isLiked()) {
                                notifyItemChanged(adapterPosition, errorunliking);

                            } else notifyItemChanged(adapterPosition, errorliking);

                            if (cd.isConnectingToInternet()) {
                                Toast.makeText(mContext, "Couldn't update", Toast.LENGTH_SHORT).show();
                            }

                        } else if (response.equals(Constants.AUTH_ERROR)) {

                            if (messagelist.get(adapterPosition).isLiked()) {
                                notifyItemChanged(adapterPosition, errorliking);

                            } else notifyItemChanged(adapterPosition, errorunliking);


                            if (cd.isConnectingToInternet()) {
                                Toast.makeText(mContext, "Authentication error", Toast.LENGTH_SHORT).show();
                            }

                        }


                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (messagelist.get(adapterPosition).isLiked()) {
                            notifyItemChanged(adapterPosition, errorunliking);

                        } else notifyItemChanged(adapterPosition, errorliking);


                        if (cd.isConnectingToInternet()) {
                            Toast.makeText(mContext, "Couldn't update", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(mContext, "No Internet connectivity", Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("from", messagelist.get(adapterPosition).getNumber());
                data.put("postid", messagelist.get(adapterPosition).getPostId());
                data.put("number", session.getMobileNumber());
                data.put("token", session.getKeyAuthToken());
                data.put("deviceid", session.getDeviceid());
                return data;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        Log.d("onViewRecycled", "called");
        if (holder != null) {
            if (holder instanceof BaseViewHolder) {
                BaseViewHolder baseViewHolder = (BaseViewHolder) holder;
                Glide.clear(baseViewHolder.profilepic);
                if (baseViewHolder instanceof ImageViewHolder) {
                    //  Log.d("onviewclear", "called");
                    Glide.clear(((ImageViewHolder) baseViewHolder).image);
                } else if (baseViewHolder instanceof VideoViewHolder) {
                    Glide.clear(((VideoViewHolder) baseViewHolder).image);

                }

            }
        }
        super.onViewRecycled(holder);
    }

    public void clearlist() {
        if (messagelist.size() > 0 && ReportLoadingConstant.fragmentListWithBoxAtTop.contains(fragmentCategory)) {
            Log.d("mmm", "clearlist: ");
            FeedModel feedModel = messagelist.get(0);
            messagelist.clear();
            messagelist.add(feedModel);
        } else messagelist.clear();

       /* if (fragmentCategory == ReportLoadingConstant.FRAGMENT_CATEGORY_CITY)
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    notifyItemChanged(0, daynightanim);
                }
            });*/
    }

    public void add1(FeedModel feedModel) {
        messagelist.add(feedModel);

    }

    public void add9(FeedModel feedModel) {
        messagelist.add(0, feedModel);

    }

    public void add5(FeedModel feedModel) {
        messagelist.add(feedModel);
        numOfTopboxes = 1;
    }

    public void add11() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (ReportLoadingConstant.fragmentListWithBoxAtTop.contains(fragmentCategory))
                    notifyItemRangeChanged(1, messagelist.size() - 1);
                else notifyItemRangeChanged(0, messagelist.size());
            }
        });

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (fragmentCategory == ReportLoadingConstant.FRAGMENT_CATEGORY_CITY)
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            notifyItemChanged(0, daynightanim);
                        }
                    });
            }
        });
    }

    public void add2() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (fragmentCategory == ReportLoadingConstant.FRAGMENT_CATEGORY_CITY)
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            notifyItemChanged(0, daynightanim);
                        }
                    });
            }
        });
    }


    public void add10() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                notifyItemRangeInserted(0, 10);

            }
        });

    }

    public void add6(FeedModel feedModel) {
        messagelist.add(feedModel);

        new Handler().post(new Runnable() {

            @Override
            public void run() {
                notifyItemInserted(messagelist.size() - 1);

            }
        });

    }

    public void removeLoading() {
        messagelist.remove(messagelist.size() - 1);
    }

    private void rundaynightanim(final LocationViewHolder viewHolder2) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ValueAnimator valueAnimator1 = ValueAnimator.ofFloat(pxFromDp(mContext, 0), pxFromDp(mContext, 54));
                valueAnimator1.setInterpolator(new DecelerateInterpolator());
                valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        viewHolder2.daynight.setAlpha((value / pxFromDp(mContext, 54)));
                        viewHolder2.daynight.setTranslationY(-value);

                    }
                });

                valueAnimator1.setDuration(1200);
                valueAnimator1.start();

            }
        }, 1200);
    }

    public void add14(FeedModel feedModel) {
        if (ReportLoadingConstant.fragmentListWithBoxAtTop.contains(fragmentCategory)) {
            messagelist.add(1, feedModel);

        } else messagelist.add(0, feedModel);
    }

    public void add15(final int temp) {
        Log.d(TAG, "add15: called");
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (ReportLoadingConstant.fragmentListWithBoxAtTop.contains(fragmentCategory)) {
                    notifyItemRangeInserted(1, temp);
                } else notifyItemRangeChanged(0, temp);
            }
        });
    }

    public void removeLoading1() {
        messagelist.remove(messagelist.size() - 1);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                notifyItemRemoved(messagelist.size());

            }
        });

    }


    private class BaseViewHolder extends RecyclerView.ViewHolder {
        protected ImageView profilepic, overflow, sendmessage;
        protected TextView headline;
        protected TextView upvote, head;
        protected TextView place;
        protected TextView icon;
        protected ImageView reaction;
        protected TextView date;
        protected TextView tv;
        protected TextView from;
        protected CardView cv;
        protected TextView reviews, source;
        protected TextView app;
        private ImageView menuEdit;
        private RelativeLayout actions;
        private LinearLayout linearLayout;
        private ImageView upicon;
        private TextView name, userName;
        private RelativeLayout rv;


        public BaseViewHolder(View view) {
            super(view);
            cv = (CardView) itemView.findViewById(R.id.cv);
            headline = (TextView) view.findViewById(R.id.Who);
            head = (TextView) view.findViewById(R.id.head);
            name = (TextView) view.findViewById(R.id.name);
            userName = (TextView) view.findViewById(R.id.userName);
            rv = (RelativeLayout) view.findViewById(R.id.rv);
            place = (TextView) view.findViewById(R.id.place);
            reaction = (ImageView) view.findViewById(R.id.comment);
            date = (TextView) view.findViewById(R.id.date);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            sendmessage = (ImageView) view.findViewById(R.id.action_msg);
            profilepic = (ImageView) view.findViewById(R.id.profilepic);
            from = (TextView) view.findViewById(R.id.from);
            reviews = (TextView) view.findViewById(R.id.reviews);
            app = (TextView) view.findViewById(R.id.app);
            upvote = (TextView) view.findViewById(R.id.upvote);
            source = (TextView) view.findViewById(R.id.source);
            linearLayout = (LinearLayout) view.findViewById(R.id.like);
            upicon = (ImageView) view.findViewById(R.id.upicon);
            actions = (RelativeLayout) view.findViewById(R.id.actions);
            menuEdit = (ImageView) view.findViewById(R.id.action_edit);


            sendmessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (session.checkLoginSplash()) {
                        Constants.suggestpostid = messagelist.get(getAdapterPosition()).getPostId();
                        Intent i = new Intent(mContext, Contacts.class);
                        mContext.startActivity(i);
                    } else
                        Toast.makeText(mContext, "Sign in to share this post", Toast.LENGTH_SHORT).show();

                }
            });

            menuEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (session.getMobileNumber().equals(messagelist.get(getAdapterPosition()).getNumber())) {
                            editHeadline(getAdapterPosition());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            reviews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, LikesActivity.class);
                    i.putExtra("postid", (messagelist.get((getAdapterPosition()))).getPostId());
                    mContext.startActivity(i);
                    ((Activity) mContext).overridePendingTransition(0, 0);

                }
            });
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (session.checkLoginSplash()) {
                        if (messagelist.get(getAdapterPosition()).isLiked()) {
                            notifyItemChanged(getAdapterPosition(), preunlike);
                        } else {
                            notifyItemChanged(getAdapterPosition(), prelike);
                        }
                        makeRequest(getAdapterPosition());
                    } else
                        Toast.makeText(mContext, "Sign in to like this post", Toast.LENGTH_SHORT).show();
                }
            });


            overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (new UploadOptions((Activity) mContext).showShareOptions()) {
                        takeScreenshot(cv);
                    }
                }
            });

            profilepic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        Bundle bundle = new Bundle();
                        bundle.putString("myData", messagelist.get(getAdapterPosition()).getNumber());
                        Intent in = new Intent(mContext, UserProfile.class);
                        in.putExtras(bundle);
                        mContext.startActivity(in);
                        ((Activity) mContext).overridePendingTransition(0, 0);

                    } else {
                        Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            from.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle bundle = new Bundle();
                    bundle.putString("myData", messagelist.get(getAdapterPosition()).getNumber());
                    Intent in = new Intent(mContext, UserProfile.class);
                    in.putExtras(bundle);
                    mContext.startActivity(in);
                    ((Activity) mContext).overridePendingTransition(0, 0);


                }
            });


            app.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (qu_position != 15) {
                        Bundle bundle = new Bundle();
                        bundle.putString("myData", messagelist.get(getAdapterPosition()).getPostId());
                        bundle.putString("headline", messagelist.get(getAdapterPosition()).getHeadline());
                        bundle.putString("image", messagelist.get(getAdapterPosition()).getImage());
                        Intent in = new Intent(mContext, Comments1.class);

                        in.putExtras(bundle);
                        mContext.startActivity(in);

                        ((Activity) mContext).overridePendingTransition(0, 0);
                    } else {
                        ((SinglePostActivity) mContext).changetab();

                    }
                }
            });

            actions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in = new Intent(mContext, MyCityActivity.class);
                    in.putExtra("place", messagelist.get(getAdapterPosition()).getLocation());
                    mContext.startActivity(in);
                    ((Activity) mContext).overridePendingTransition(0, 0);
                }
            });

            headline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (session.getMobileNumber().equals(messagelist.get(getAdapterPosition()).getNumber())) {
                            editHeadline(getAdapterPosition());
                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
            source.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    session = new UserSessionManager(mContext);
                    session.setCategory(messagelist.get(getAdapterPosition()).getCategory());
                    Intent in = new Intent(mContext, CategoryActivity.class);
                    mContext.startActivity(in);
                    ((Activity) mContext).overridePendingTransition(0, 0);

                }
            });
            rv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (qu_position != 15) {
                        Bundle bundle = new Bundle();
                        bundle.putString("myData", messagelist.get(getAdapterPosition()).getPostId());
                        bundle.putString("headline", messagelist.get(getAdapterPosition()).getHeadline());
                        bundle.putString("image", messagelist.get(getAdapterPosition()).getImage());
                        Intent in = new Intent(mContext, Comments1.class);
                        ((Activity) mContext).overridePendingTransition(0, 0);

                        in.putExtras(bundle);
                        mContext.startActivity(in);
                        ((Activity) mContext).overridePendingTransition(0, 0);

                    } else {
                        ((SinglePostActivity) mContext).changetab();

                    }
                }
            });


        }
    }

    private class ImageViewHolder extends BaseViewHolder {
        protected ImageView image;


        public ImageViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (messagelist.get((getAdapterPosition())).getGif().isEmpty()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("myData", messagelist.get((getAdapterPosition())).getImage());
                        bundle.putString("tag", messagelist.get((getAdapterPosition())).getCategory());
                        bundle.putString("headline", messagelist.get((getAdapterPosition())).getHead());
                        Intent in = new Intent(mContext, FullImage.class);
                        in.putExtras(bundle);
                        mContext.startActivity(in);
                    }

                }
            });

        }
    }

    private class VideoViewHolder extends BaseViewHolder {
        protected ImageView image;
        private ImageView play;
        private TextView views;


        public VideoViewHolder(View view) {
            super(view);


            image = (ImageView) view.findViewById(R.id.image);
            play = (ImageView) view.findViewById(R.id.play);
            views = (TextView) view.findViewById(R.id.views);


            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    increaseViewsRequest();
                    Intent in = new Intent(mContext, VideoDisplay.class);
                    in.putExtra("myData", messagelist.get(getAdapterPosition()).getVideo());
                    in.putExtra("tag", messagelist.get(getAdapterPosition()).getCategory());
                    in.putExtra("headline", messagelist.get(getAdapterPosition()).getHead());
                    if (messagelist.get(getAdapterPosition()).getHeadline() != null)
                        in.putExtra("description", messagelist.get(getAdapterPosition()).getHeadline());
                    mContext.startActivity(in);

                }
            });


        }

        private void increaseViewsRequest() {


            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_INCREASE_VIEWS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("responseviews", response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("responseviewserror", "eeror");

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    Log.d("po", messagelist.get(getAdapterPosition()).getPostId());
                    Log.d("powwww", messagelist.get(getAdapterPosition()).getPostviews());
                    map.put("postid", messagelist.get(getAdapterPosition()).getPostId());
                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(stringRequest);

        }

    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(View view) {
            super(view);
        }
    }

    private class NewPostViewHolder extends RecyclerView.ViewHolder {
        LinearLayout con;
        CardView cv;

        NewPostViewHolder(View view) {
            super(view);
            con = (LinearLayout) view.findViewById(R.id.newCon);
            ImageView img = (ImageView) view.findViewById(R.id.pic);
            cv = (CardView) view.findViewById(R.id.cv);
            ImageView anim = (ImageView) view.findViewById(R.id.animimg);
            AnimationDrawable ad = (AnimationDrawable) mContext.getResources().getDrawable(R.drawable.animation);
            anim.setImageDrawable(ad);
            ad.setOneShot(false);
            ad.start();

            Glide.with(mContext).load(session.getProfilePicture()).error(R.drawable.download).into(img);
            con.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(mContext, PostReport.class);
                    mContext.startActivity(in);
                }
            });

            new UploadOptions(mContext, view, true);
        }


    }

    private class LocationViewHolder extends RecyclerView.ViewHolder {
        private ImageView daynight;
        private TextView edit;
        private TextView location;
        private TextView location1;
        private TextView location2;
        private TextView location3;

        public LocationViewHolder(View inflate) {
            super(inflate);

            daynight = (ImageView) inflate.findViewById(R.id.daynight);
            edit = (TextView) inflate.findViewById(R.id.edit);
            location = (TextView) inflate.findViewById(R.id.locationText);
            location.setText(session.getCustomLocation());

            Boolean isNight;
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            isNight = hour < 6 || hour > 18;
            if (isNight) {
                daynight.setImageResource(R.drawable.ic_camera_night_mode);
            } else daynight.setImageResource(R.drawable.ic_sunny);

            daynight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ValueAnimator valueAnimator1 = ValueAnimator.ofFloat(0, 360 * 3);
                            valueAnimator1.setInterpolator(new DecelerateInterpolator());
                            valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    float value = (float) animation.getAnimatedValue();
                                    daynight.setRotation(value);
                                }
                            });

                            valueAnimator1.setDuration(1000);
                            valueAnimator1.start();

                        }
                    }, 100);
                }
            });

            location1 = (TextView) inflate.findViewById(R.id.editText1);
            location1.setText(session.getCustomLocation1());
            location2 = (TextView) inflate.findViewById(R.id.editText2);
            location2.setText(session.getCustomLocation2());
            location3 = (TextView) inflate.findViewById(R.id.editText3);
            location3.setText(session.getCustomLocation3());

            location1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    location.setText(session.getCustomLocation1());

                    fragment.onLocationSet(session.getCustomLocation1());
                }
            });

            location2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    location.setText(session.getCustomLocation2());

                    fragment.onLocationSet(session.getCustomLocation2());
                }
            });

            location3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    location.setText(session.getCustomLocation3());

                    fragment.onLocationSet(session.getCustomLocation3());
                }
            });


            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater li = LayoutInflater.from(mContext);
                    //Creating a view to get the dialog box
                    View confirmDialog = li.inflate(R.layout.dialog_edit_location, null);
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                    // Include dialog.xml file
                    dialog.setView(confirmDialog);
                    final AlertDialog alertDialog = dialog.create();

                    // Set dialog title
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


                    // set values for custom dialog components - text, image and button
                    final EditText editText = (EditText) confirmDialog.findViewById(R.id.editTextLocation);
                    final EditText editText1 = (EditText) confirmDialog.findViewById(R.id.editTextsubLocation1);
                    final EditText editText2 = (EditText) confirmDialog.findViewById(R.id.editTextsubLocation2);
                    final EditText editText3 = (EditText) confirmDialog.findViewById(R.id.editTextsubLocation3);

                    editText.setText(session.getCustomLocation());
                    editText.setSelection(editText.getText().length());

                    editText1.setText(session.getCustomLocation1());
                    editText1.setSelection(editText1.getText().length());

                    editText2.setText(session.getCustomLocation2());
                    editText2.setSelection(editText2.getText().length());
                    editText3.setText(session.getCustomLocation3());
                    editText3.setSelection(editText3.getText().length());

                    Button button = (Button) confirmDialog.findViewById(R.id.buttonConfirm);
                    alertDialog.show();


                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            alertDialog.dismiss();
                            if (editText.getText().toString().trim().length() == 0) {

                            } else {
                                String s = editText.getText().toString();
                                String s1 = editText1.getText().toString();
                                String s2 = editText2.getText().toString();
                                String s3 = editText3.getText().toString();

                                fragment.onLocationSet(editText.getText().toString());
                                location.setText(editText.getText().toString());
                                location1.setText(editText1.getText().toString());
                                location2.setText(editText2.getText().toString());
                                location3.setText(editText3.getText().toString());
                                session.saveCustomLocation(editText.getText().toString());
                                session.saveCustomsubLocation1(editText1.getText().toString());
                                session.saveCustomsubLocation2(editText2.getText().toString());
                                session.saveCustomsubLocation3(editText3.getText().toString());

                            }
                        }
                    });

                }
            });
        }

    }

    private class CityNoReportsviewHolder extends RecyclerView.ViewHolder {
        public CityNoReportsviewHolder(View inflate) {
            super(inflate);
        }
    }

    private class ReadingNoReadersViewHolder extends RecyclerView.ViewHolder {
        public ReadingNoReadersViewHolder(View inflate) {
            super(inflate);
        }
    }


}