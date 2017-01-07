package in.reweyou.reweyou.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
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
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import in.reweyou.reweyou.classes.CustomTabsOnClickListener;
import in.reweyou.reweyou.classes.UploadOptions;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.classes.Util;
import in.reweyou.reweyou.fragment.SecondFragment;
import in.reweyou.reweyou.model.FeedModel;
import in.reweyou.reweyou.utils.Constants;

import static in.reweyou.reweyou.utils.Constants.EDIT_URL;
import static in.reweyou.reweyou.utils.Constants.URL_LIKE;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_IMAGE;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_LOADING;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_LOCATION;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_NEW_POST;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_VIDEO;


public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final String TAG = FeedAdapter.class.getSimpleName();
    private final String preunlike = "preunlike";
    private final String prelike = "prelike";
    private final String errorunliking = "errorunliking";
    private final String errorliking = "errorliking";

    private final int PRE_UNLIKE = 1;
    private final int PRE_LIKE = 2;
    private final int ERROR_UNLIKING = -1;
    private final int ERROR_LIKING = -2;


    private final int REBIND_NUM_LIKES = 11;
    private final int REBIND_NUM_REACTIONS = 12;
    private final int REBIND_TIME = 13;
    private final int REBIND_TAG = 13;

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
    private SecondFragment fragment;
    private String placename;
    private List<Object> messagelist;
    private Context mContext;
    private String id, postid;
    private EditText editTextHeadline;
    private Button buttonEdit;
    private String number;
    private String username;


    public FeedAdapter(Context context, List<Object> mlist, SecondFragment secondFragment) {
        this.mContext = context;
        activity = (Activity) context;
        this.messagelist = mlist;
        cd = new ConnectionDetector(mContext);
        mCustomTabActivityHelper = new CustomTabActivityHelper();
        session = new UserSessionManager(mContext);
        uploadOption = new UploadOptions((Activity) mContext);
        this.fragment = secondFragment;

        initTimer();

    }

    public FeedAdapter(Context context, List<Object> mlist, String placename, SecondFragment secondFragment) {
        this.mContext = context;
        activity = (Activity) context;
        this.messagelist = mlist;
        cd = new ConnectionDetector(mContext);
        mCustomTabActivityHelper = new CustomTabActivityHelper();
        session = new UserSessionManager(mContext);
        this.placename = placename;
        Log.d("reach", "constr    " + this.placename + "   " + placename);

        this.fragment = secondFragment;

    }

    public FeedAdapter(Context context, List<Object> mlist, String placename, SecondFragment secondFragment, int qu_position) {

        this.mContext = context;
        activity = (Activity) context;
        this.messagelist = mlist;
        cd = new ConnectionDetector(mContext);
        mCustomTabActivityHelper = new CustomTabActivityHelper();
        session = new UserSessionManager(mContext);
        this.placename = placename;
        Log.d("reach", "constr    " + this.placename + "   " + placename);

        this.qu_position = qu_position;
        this.fragment = secondFragment;

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
                return new LocationViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_feed_adapter_location, viewGroup, false));
            default:
                return null;

        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder2, final int position) {
        //Log.d("view", String.valueOf(viewHolder2.getItemViewType()));
        Log.w(TAG, "onBindViewHolder: called");
        switch (viewHolder2.getItemViewType()) {
            case VIEW_TYPE_LOADING:
                break;
            case VIEW_TYPE_VIDEO:
                bindVideo(position, viewHolder2);
                break;
            case VIEW_TYPE_IMAGE:
                bindImageOrGif(position, viewHolder2);
                break;
            case VIEW_TYPE_LOCATION:
                bindLocation(position, viewHolder2);
                break;
        }
    }

    private void bindLocation(int position, RecyclerView.ViewHolder viewHolder2) {
        Log.d("locaton", "here");
        LocationViewHolder locationViewHolder = (LocationViewHolder) viewHolder2;
        if (placename != null)
            locationViewHolder.location.setText(placename);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mHolder, int position, List<Object> payloads) {

        if (mHolder instanceof BaseViewHolder) {
            BaseViewHolder holder = (BaseViewHolder) mHolder;
            if (payloads.isEmpty())
                super.onBindViewHolder(holder, position, payloads);
            else if (payloads.contains(REBIND_TIME)) {
                Log.d(TAG, "onBindViewHolder: REBIND_TIME called");
                setDate(holder, position);
            } else if (payloads.contains(prelike)) {
                Log.d("reach", prelike);

                holder.upicon.setImageResource(R.drawable.ic_thumb_up_primary_16px);
                holder.upvote.setTextColor(mContext.getResources().getColor(R.color.rank));
                if (Integer.parseInt(((FeedModel) messagelist.get(position)).getReviews()) == 0)
                    holder.reviews.setText(String.valueOf(Integer.parseInt(((FeedModel) messagelist.get(position)).getReviews()) + 1) + " like");
                else
                    holder.reviews.setText(String.valueOf(Integer.parseInt(((FeedModel) messagelist.get(position)).getReviews()) + 1) + " likes");

                holder.reviews.setTypeface(Typeface.DEFAULT_BOLD);
                holder.reviews.setTextColor(mContext.getResources().getColor(R.color.rank));
                Log.d("likes", String.valueOf(Integer.parseInt(((FeedModel) messagelist.get(position)).getReviews()) + 1));
            } else if (payloads.contains(errorliking)) {
                Log.d("reach", errorliking);

                holder.upicon.setImageResource(R.drawable.ic_thumb_up_black_16px);
                holder.upvote.setTextColor(mContext.getResources().getColor(R.color.likeText));
                if (Integer.parseInt(((FeedModel) messagelist.get(position)).getReviews()) == 0) {
                    holder.reviews.setTypeface(Typeface.DEFAULT);
                    holder.reviews.setTextColor(mContext.getResources().getColor(R.color.main));
                    holder.reviews.setText(String.valueOf(Integer.parseInt(((FeedModel) messagelist.get(position)).getReviews())) + " like");

                } else if (Integer.parseInt(((FeedModel) messagelist.get(position)).getReviews()) == 1)
                    holder.reviews.setText(String.valueOf(Integer.parseInt(((FeedModel) messagelist.get(position)).getReviews())) + " like");
                else
                    holder.reviews.setText(String.valueOf(Integer.parseInt(((FeedModel) messagelist.get(position)).getReviews())) + " likes");

            } else if (payloads.contains(preunlike)) {
                Log.d("reach", preunlike);
                holder.upicon.setImageResource(R.drawable.ic_thumb_up_black_16px);
                holder.upvote.setTextColor(mContext.getResources().getColor(R.color.likeText));
                if (Integer.parseInt(((FeedModel) messagelist.get(position)).getReviews()) == 2) {
                    holder.reviews.setText(String.valueOf(Integer.parseInt(((FeedModel) messagelist.get(position)).getReviews()) - 1) + " like");

                } else if (Integer.parseInt(((FeedModel) messagelist.get(position)).getReviews()) == 1) {
                    holder.reviews.setTypeface(Typeface.DEFAULT);
                    holder.reviews.setTextColor(mContext.getResources().getColor(R.color.main));
                    holder.reviews.setText(String.valueOf(Integer.parseInt(((FeedModel) messagelist.get(position)).getReviews()) - 1) + " like");

                } else
                    holder.reviews.setText(String.valueOf(Integer.parseInt(((FeedModel) messagelist.get(position)).getReviews()) - 1) + " likes");
            } else if (payloads.contains(errorunliking)) {
                Log.d("reach", errorunliking);

                holder.upicon.setImageResource(R.drawable.ic_thumb_up_primary_16px);
                holder.upvote.setTextColor(mContext.getResources().getColor(R.color.rank));
                holder.reviews.setTypeface(Typeface.DEFAULT_BOLD);
                holder.reviews.setTextColor(mContext.getResources().getColor(R.color.rank));
                holder.reviews.setText(String.valueOf(Integer.parseInt(((FeedModel) messagelist.get(position)).getReviews())) + " likes");
            }
        } else super.onBindViewHolder(mHolder, position, payloads);

    }

    @Override
    public int getItemViewType(int position) {
        if (messagelist.get(position) instanceof FeedModel) {
            switch (((FeedModel) messagelist.get(position)).getViewType()) {
                case VIEW_TYPE_IMAGE:
                    return VIEW_TYPE_IMAGE;
                case VIEW_TYPE_VIDEO:
                    return VIEW_TYPE_VIDEO;
                default:
                    return super.getItemViewType(position);
            }
        } else if (messagelist.get(position) instanceof Integer) {
            switch ((int) messagelist.get(position)) {
                case VIEW_TYPE_LOADING:
                    return VIEW_TYPE_LOADING;
                case VIEW_TYPE_NEW_POST:
                    return VIEW_TYPE_NEW_POST;
                case VIEW_TYPE_LOCATION:
                    return VIEW_TYPE_LOCATION;
                default:
                    return super.getItemViewType(position);
            }
        } else return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return (null != messagelist ? messagelist.size() : 0);
    }

    private void bindImageOrGif(final int position, RecyclerView.ViewHolder viewHolder2) {
        final ImageViewHolder viewHolder = (ImageViewHolder) viewHolder2;
        if (((FeedModel) messagelist.get(position)).getHeadline() != null) {
            Spannable spannable = new SpannableString(((FeedModel) messagelist.get(position)).getHeadline());
            Util.linkifyUrl(spannable, new CustomTabsOnClickListener(activity, mCustomTabActivityHelper));
            setDescription(viewHolder, position, spannable);

        }
        setEditButton(viewHolder, position);
        setHeadline(viewHolder, position);


        setDate(viewHolder, position);

        setReporterProfilePic(viewHolder, position);

        setFeedImage(viewHolder, position);

        setFeedLocation(viewHolder, position);

        setReporterName(viewHolder, position);

        setReactions(viewHolder, position);

        setReactionsNumber(viewHolder, position);

        setLikesNumber(viewHolder, position);

        setLikedStatus(viewHolder, position);

        setFeedCategory(viewHolder, position);


    }


    private void bindVideo(final int position, RecyclerView.ViewHolder viewHolder2) {
        final VideoViewHolder viewHolder = (VideoViewHolder) viewHolder2;

        if (((FeedModel) messagelist.get(position)).getHeadline() != null) {
            Spannable spannable = new SpannableString(((FeedModel) messagelist.get(position)).getHeadline());
            Util.linkifyUrl(spannable, new CustomTabsOnClickListener(activity, mCustomTabActivityHelper));
            setDescription(viewHolder, position, spannable);

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

        setReactionsNumber(viewHolder, position);

        setLikesNumber(viewHolder, position);

        setLikedStatus(viewHolder, position);

        setFeedCategory(viewHolder, position);
    }

    private void setEditButton(BaseViewHolder viewHolder, int position) {
        if (session.getMobileNumber().equals(((FeedModel) messagelist.get(position)).getNumber())) {
            viewHolder.menuEdit.setVisibility(View.VISIBLE);
        } else viewHolder.menuEdit.setVisibility(View.GONE);

    }

    private void setNumOfViews(VideoViewHolder viewHolder, int position) {
        if (((FeedModel) messagelist.get(position)).getPostviews().equals("0")) {
            viewHolder.views.setVisibility(View.GONE);
        } else if (((FeedModel) messagelist.get(position)).getPostviews().equals("1")) {
            viewHolder.views.setVisibility(View.VISIBLE);
            viewHolder.views.setText(((FeedModel) messagelist.get(position)).getPostviews() + " view");
        } else {
            viewHolder.views.setVisibility(View.VISIBLE);
            viewHolder.views.setText(((FeedModel) messagelist.get(position)).getPostviews() + " views");
        }
    }

    private void setLikedStatus(BaseViewHolder viewHolder, int position) {

        if (((FeedModel) messagelist.get(position)).isLiked()) {
            viewHolder.upicon.setImageResource(R.drawable.ic_thumb_up_primary_16px);
            viewHolder.upvote.setTextColor(ContextCompat.getColor(mContext, R.color.rank));

        } else {
            viewHolder.upicon.setImageResource(R.drawable.ic_thumb_up_black_16px);
            viewHolder.upvote.setTextColor(ContextCompat.getColor(mContext, R.color.main));
        }
    }

    private void setFeedCategory(BaseViewHolder viewHolder, int position) {
        viewHolder.source.setVisibility(View.VISIBLE);
        viewHolder.source.setText('#' + ((FeedModel) messagelist.get(position)).getCategory());

    }

    private void setLikesNumber(BaseViewHolder viewHolder, int position) {
        if (((FeedModel) messagelist.get(position)).getReviews().equals("0")) {
            viewHolder.reviews.setText("0 like");
            viewHolder.reviews.setTypeface(Typeface.DEFAULT);
            viewHolder.reviews.setTextColor(mContext.getResources().getColor(R.color.main));
        } else if (((FeedModel) messagelist.get(position)).getReviews().equals("1")) {
            viewHolder.reviews.setText("1 like");
            viewHolder.reviews.setTypeface(Typeface.DEFAULT_BOLD);
            viewHolder.reviews.setTextColor(mContext.getResources().getColor(R.color.rank));
        } else {
            viewHolder.reviews.setText(String.valueOf(Integer.parseInt(((FeedModel) messagelist.get(position)).getReviews())) + " likes");
            viewHolder.reviews.setTypeface(Typeface.DEFAULT_BOLD);

            viewHolder.reviews.setTextColor(mContext.getResources().getColor(R.color.rank));
        }

    }

    private void setReactions(BaseViewHolder viewHolder, int position) {
        if (((FeedModel) messagelist.get(position)).getComments() == null || ((FeedModel) messagelist.get(position)).getComments().isEmpty())
            viewHolder.app.setText("0 Reactions");
        else {
            viewHolder.app.setText(((FeedModel) messagelist.get(position)).getComments() + " Reactions");
            if (!((FeedModel) messagelist.get(position)).getComments().equals("0") && ((FeedModel) messagelist.get(position)).getReaction() != null) {
                viewHolder.rv.setVisibility(View.VISIBLE);

                Spannable spannables = new SpannableString(((FeedModel) messagelist.get(position)).getReaction());
                Util.linkifyUrl(spannables, new CustomTabsOnClickListener(activity, mCustomTabActivityHelper));
                viewHolder.userName.setText(spannables);
                viewHolder.userName.setMovementMethod(LinkMovementMethod.getInstance());

                viewHolder.name.setText(((FeedModel) messagelist.get(position)).getFrom());
            } else {
                viewHolder.rv.setVisibility(View.GONE);
            }
        }

    }

    private void setReporterName(BaseViewHolder viewHolder, int position) {

        viewHolder.from.setVisibility(View.VISIBLE);
        viewHolder.from.setText(((FeedModel) messagelist.get(position)).getName());

    }

    private void setFeedLocation(BaseViewHolder viewHolder, int position) {
        viewHolder.place.setText(((FeedModel) messagelist.get(position)).getLocation());

    }

    private void setFeedImage(ImageViewHolder viewHolder, int position) {
        viewHolder.image.setVisibility(View.VISIBLE);
        if (((FeedModel) messagelist.get(position)).getImage().isEmpty()) {
            if (((FeedModel) messagelist.get(position)).getGif().isEmpty()) {
                viewHolder.image.setVisibility(View.GONE);
            } else {
                viewHolder.image.setAdjustViewBounds(false);
                Glide.with(mContext).load(((FeedModel) messagelist.get(position)).getGif()).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.ic_broken_image_black_48dp).dontAnimate().into(viewHolder.image);
            }
        } else {
            viewHolder.image.setAdjustViewBounds(true);
            Glide.with(mContext).load(((FeedModel) messagelist.get(position)).getImage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).error(R.drawable.ic_broken_image_black_48dp).dontAnimate().into(viewHolder.image);
        }
    }

    private void setFeedVideoThumbnail(final VideoViewHolder viewHolder, int position) {

        if (((FeedModel) messagelist.get(position)).getImage() == null || ((FeedModel) messagelist.get(position)).getImage().isEmpty()) {
            viewHolder.image.setVisibility(View.GONE);
        } else {
            viewHolder.play.setVisibility(View.VISIBLE);
            viewHolder.image.setVisibility(View.VISIBLE);
            viewHolder.image.setColorFilter(Color.argb(120, 0, 0, 0)); // black Tint


            Glide.with(mContext).load(((FeedModel) messagelist.get(position)).getImage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.ic_broken_image_black_48dp).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    viewHolder.play.setVisibility(View.INVISIBLE);

                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    return false;
                }
            }).dontAnimate().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(viewHolder.image);
        }
    }

    private void setReactionsNumber(BaseViewHolder viewHolder, int position) {
        if (((FeedModel) messagelist.get(position)).getComments() == null || ((FeedModel) messagelist.get(position)).getComments().isEmpty())
            viewHolder.app.setText("0 Reaction");
        else {
            viewHolder.app.setText(((FeedModel) messagelist.get(position)).getComments() + " Reactions");
            if (!((FeedModel) messagelist.get(position)).getComments().equals("0") && ((FeedModel) messagelist.get(position)).getReaction() != null) {
                viewHolder.rv.setVisibility(View.VISIBLE);

                Spannable spannables = new SpannableString(((FeedModel) messagelist.get(position)).getReaction());
                Util.linkifyUrl(spannables, new CustomTabsOnClickListener(activity, mCustomTabActivityHelper));
                viewHolder.userName.setText(spannables);
                viewHolder.userName.setMovementMethod(LinkMovementMethod.getInstance());

                viewHolder.name.setText(((FeedModel) messagelist.get(position)).getFrom());
            } else {
                viewHolder.rv.setVisibility(View.GONE);
            }
        }
    }

    private void setReporterProfilePic(BaseViewHolder viewHolder, int position) {
        Glide.with(mContext).load(((FeedModel) messagelist.get(position)).getProfilepic()).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(R.drawable.download).error(R.drawable.download).fallback(R.drawable.download).dontAnimate().into(viewHolder.profilepic);

    }

    private void setDate(BaseViewHolder viewHolder, int position) {
        viewHolder.date.setVisibility(View.VISIBLE);
        viewHolder.date.setText(((FeedModel) messagelist.get(position)).getDate());
    }

    private void setDescription(BaseViewHolder viewHolder, int position, Spannable spannable) {

        if (((FeedModel) messagelist.get(position)).getHeadline() == null || ((FeedModel) messagelist.get(position)).getHeadline().isEmpty())
            viewHolder.headline.setVisibility(View.GONE);
        else {
            viewHolder.headline.setVisibility(View.VISIBLE);
            viewHolder.headline.setText(spannable);
            viewHolder.headline.setMovementMethod(LinkMovementMethod.getInstance());
        }

    }

    private void setHeadline(BaseViewHolder viewHolder, int position) {
        if (((FeedModel) messagelist.get(position)).getHead() == null || ((FeedModel) messagelist.get(position)).getHead().isEmpty())
            viewHolder.head.setVisibility(View.GONE);
        else {
            viewHolder.head.setVisibility(View.VISIBLE);
            viewHolder.head.setText(((FeedModel) messagelist.get(position)).getHead());
        }
    }

    public void add() {
        messagelist.add(VIEW_TYPE_LOADING);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                notifyItemInserted(messagelist.size() - 1);
            }
        });
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
        postid = ((FeedModel) messagelist.get(position)).getPostId();
        //  number=session.getMobileNumber();
        //Initizliaing confirm button fo dialog box and edittext of dialog box
        buttonEdit = (Button) confirmDialog.findViewById(R.id.buttonConfirm);
        editTextHeadline = (EditText) confirmDialog.findViewById(R.id.editTextOtp);
        editTextHeadline.setText(((FeedModel) messagelist.get(position)).getHeadline());
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
                        if (response.equals("like")) {
                            session.addlike(((FeedModel) messagelist.get(adapterPosition)).getPostId());
                            ((FeedModel) messagelist.get(adapterPosition)).setLiked(true);
                            ((FeedModel) messagelist.get(adapterPosition)).setReviews(String.valueOf((Integer.parseInt(((FeedModel) messagelist.get(adapterPosition)).getReviews()) + 1)));

                        } else if (response.equals("unlike")) {
                            session.removelike(((FeedModel) messagelist.get(adapterPosition)).getPostId());
                            ((FeedModel) messagelist.get(adapterPosition)).setLiked(false);
                            ((FeedModel) messagelist.get(adapterPosition)).setReviews(String.valueOf((Integer.parseInt(((FeedModel) messagelist.get(adapterPosition)).getReviews()) - 1)));


                        } else if (response.equals("Error")) {

                            if (((FeedModel) messagelist.get(adapterPosition)).isLiked()) {
                                notifyItemChanged(adapterPosition, errorunliking);

                            } else notifyItemChanged(adapterPosition, errorliking);

                            if (cd.isConnectingToInternet()) {
                                Toast.makeText(mContext, "Couldn't update", Toast.LENGTH_SHORT).show();
                            }

                        } else if (response.equals(Constants.AUTH_ERROR)) {

                            if (((FeedModel) messagelist.get(adapterPosition)).isLiked()) {
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
                        if (((FeedModel) messagelist.get(adapterPosition)).isLiked()) {
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
                data.put("from", ((FeedModel) messagelist.get(adapterPosition)).getNumber());
                data.put("postid", ((FeedModel) messagelist.get(adapterPosition)).getPostId());
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
        // Log.d("onViewRecycled", "called");
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

    private class BaseViewHolder extends RecyclerView.ViewHolder {
        protected ImageView profilepic, overflow, sendmessage;
        protected TextView headline, upvote, head;
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
                    Intent i = new Intent(mContext, Contacts.class);
                    i.putExtra("postid", (((FeedModel) messagelist.get(getAdapterPosition())).getPostId()));
                    mContext.startActivity(i);
                }
            });

            menuEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (session.getMobileNumber().equals(((FeedModel) messagelist.get(getAdapterPosition())).getNumber())) {
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
                    i.putExtra("postid", ((FeedModel) messagelist.get(getAdapterPosition())).getPostId());
                    mContext.startActivity(i);
                    ((Activity) mContext).overridePendingTransition(0, 0);

                }
            });
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((FeedModel) messagelist.get(getAdapterPosition())).isLiked()) {
                        Log.d("reach here", "here");
                        notifyItemChanged(getAdapterPosition(), preunlike);

                    } else {
                        notifyItemChanged(getAdapterPosition(), prelike);
                    }
                    makeRequest(getAdapterPosition());
                }
            });


            overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (uploadOption.showShareOptions()) {
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
                        bundle.putString("myData", ((FeedModel) messagelist.get(getAdapterPosition())).getNumber());
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
                    bundle.putString("myData", ((FeedModel) messagelist.get(getAdapterPosition())).getNumber());
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
                        bundle.putString("myData", ((FeedModel) messagelist.get(getAdapterPosition())).getPostId());
                        bundle.putString("headline", ((FeedModel) messagelist.get(getAdapterPosition())).getHeadline());
                        bundle.putString("image", ((FeedModel) messagelist.get(getAdapterPosition())).getImage());
                        Intent in = new Intent(mContext, Comments1.class);

                        in.putExtras(bundle);
                        mContext.startActivity(in);

                        ((Activity) mContext).overridePendingTransition(0, 0);
                    } else {
                        ((SinglePostActivity) mContext).changetab();

                    }
                }
            });
            reaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (qu_position != 15) {
                        Bundle bundle = new Bundle();
                        bundle.putString("myData", ((FeedModel) messagelist.get(getAdapterPosition())).getPostId());
                        bundle.putString("headline", ((FeedModel) messagelist.get(getAdapterPosition())).getHeadline());
                        bundle.putString("image", ((FeedModel) messagelist.get(getAdapterPosition())).getImage());
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
                    in.putExtra("place", ((FeedModel) messagelist.get(getAdapterPosition())).getLocation());
                    mContext.startActivity(in);
                    ((Activity) mContext).overridePendingTransition(0, 0);
                }
            });

            headline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (session.getMobileNumber().equals(((FeedModel) messagelist.get(getAdapterPosition())).getNumber())) {
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
                    session.setCategory(((FeedModel) messagelist.get(getAdapterPosition())).getCategory());
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
                        bundle.putString("myData", ((FeedModel) messagelist.get(getAdapterPosition())).getPostId());
                        bundle.putString("headline", ((FeedModel) messagelist.get(getAdapterPosition())).getHeadline());
                        bundle.putString("image", ((FeedModel) messagelist.get(getAdapterPosition())).getImage());
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
                    if (((FeedModel) messagelist.get(getAdapterPosition())).getGif().isEmpty()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("myData", ((FeedModel) messagelist.get(getAdapterPosition())).getImage());
                        bundle.putString("tag", ((FeedModel) messagelist.get(getAdapterPosition())).getCategory());
                        bundle.putString("headline", ((FeedModel) messagelist.get(getAdapterPosition())).getHead());
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
                    in.putExtra("myData", ((FeedModel) messagelist.get(getAdapterPosition())).getVideo());
                    in.putExtra("tag", ((FeedModel) messagelist.get(getAdapterPosition())).getCategory());
                    in.putExtra("headline", ((FeedModel) messagelist.get(getAdapterPosition())).getHead());
                    if (((FeedModel) messagelist.get(getAdapterPosition())).getHeadline() != null)
                        in.putExtra("description", ((FeedModel) messagelist.get(getAdapterPosition())).getHeadline());
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
                    Log.d("po", ((FeedModel) messagelist.get(getAdapterPosition())).getPostId());
                    Log.d("powwww", ((FeedModel) messagelist.get(getAdapterPosition())).getPostviews());
                    map.put("postid", ((FeedModel) messagelist.get(getAdapterPosition())).getPostId());
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

        NewPostViewHolder(View view) {
            super(view);
            con = (LinearLayout) view.findViewById(R.id.newCon);
            ImageView img = (ImageView) view.findViewById(R.id.pic);
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
        private TextView edit;
        private TextView location;

        public LocationViewHolder(View inflate) {
            super(inflate);

            edit = (TextView) inflate.findViewById(R.id.edit);
            location = (TextView) inflate.findViewById(R.id.locationText);

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


                    // set values for custom dialog components - text, image and button
                    final EditText editText = (EditText) confirmDialog.findViewById(R.id.editTextLocation);
                    Button button = (Button) confirmDialog.findViewById(R.id.buttonConfirm);
                    alertDialog.show();

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            alertDialog.dismiss();
                            if (editText.getText().toString().trim().length() == 0) {

                            } else {
                                fragment.onLocationSet(editText.getText().toString());
                            }
                        }
                    });

                }
            });
        }

    }

}