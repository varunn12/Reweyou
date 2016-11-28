package in.reweyou.reweyou.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.reweyou.reweyou.CategoryActivity;
import in.reweyou.reweyou.Comments1;
import in.reweyou.reweyou.FullImage;
import in.reweyou.reweyou.MyCityActivity;
import in.reweyou.reweyou.PostReport;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.UserProfile;
import in.reweyou.reweyou.Videorow;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.CustomTabActivityHelper;
import in.reweyou.reweyou.classes.CustomTabsOnClickListener;
import in.reweyou.reweyou.classes.UploadOptions;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.classes.Util;
import in.reweyou.reweyou.model.MpModel;
import in.reweyou.reweyou.utils.Constants;

import static in.reweyou.reweyou.utils.Constants.EDIT_URL;
import static in.reweyou.reweyou.utils.Constants.SUGGEST_URL;
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
    Activity activity;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    CustomTabActivityHelper mCustomTabActivityHelper;
    UserSessionManager session;
    Uri uri;
    private List<MpModel> messagelist;
    private Context mContext;
    private Bitmap bm;
    private String id, postid;
    private EditText editTextHeadline;
    private AppCompatButton buttonEdit;
    private String number;
    private String username;

    public FeedAdapter() {

    }

    public FeedAdapter(Context context, List<MpModel> mlist) {
        this.mContext = context;
        activity = (Activity) context;
        this.messagelist = mlist;
        cd = new ConnectionDetector(mContext);
        mCustomTabActivityHelper = new CustomTabActivityHelper();
        session = new UserSessionManager(mContext);

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
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

        switch (viewHolder2.getItemViewType()) {
            case VIEW_TYPE_LOADING:
                break;
            case VIEW_TYPE_VIDEO:
                bindVideo(position, viewHolder2);
                break;
            case VIEW_TYPE_IMAGE:
                bindImageOrGif(position, viewHolder2);
                break;

          /*  default:
                bindImageOrGif(position, viewHolder2);
                break;*/

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mHolder, int position, List<Object> payloads) {

        if (mHolder instanceof BaseViewHolder) {
            BaseViewHolder holder = (BaseViewHolder) mHolder;
            if (payloads.isEmpty())
                super.onBindViewHolder(holder, position, payloads);
            else if (payloads.contains(prelike)) {
                Log.d("reach", prelike);

                holder.upicon.setImageResource(R.drawable.ic_thumb_up_primary_16px);
                holder.upvote.setTextColor(mContext.getResources().getColor(R.color.rank));
                holder.reviews.setText(String.valueOf(Integer.parseInt(messagelist.get(position).getReviews()) + 1) + " likes");
                Log.d("likes", String.valueOf(Integer.parseInt(messagelist.get(position).getReviews()) + 1));
            } else if (payloads.contains(errorliking)) {
                Log.d("reach", errorliking);

                holder.upicon.setImageResource(R.drawable.ic_thumb_up_black_16px);
                holder.upvote.setTextColor(mContext.getResources().getColor(R.color.likeText));
                holder.reviews.setText(String.valueOf(Integer.parseInt(messagelist.get(position).getReviews())) + " likes");
            } else if (payloads.contains(preunlike)) {
                Log.d("reach", preunlike);
                holder.upicon.setImageResource(R.drawable.ic_thumb_up_black_16px);
                holder.upvote.setTextColor(mContext.getResources().getColor(R.color.likeText));
                holder.reviews.setText(String.valueOf(Integer.parseInt(messagelist.get(position).getReviews()) - 1) + " likes");
            } else if (payloads.contains(errorunliking)) {
                Log.d("reach", errorunliking);

                holder.upicon.setImageResource(R.drawable.ic_thumb_up_primary_16px);
                holder.upvote.setTextColor(mContext.getResources().getColor(R.color.rank));
                holder.reviews.setText(String.valueOf(Integer.parseInt(messagelist.get(position).getReviews())) + " likes");
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        switch (messagelist.get(position).getViewType()) {
            case VIEW_TYPE_IMAGE:
                return VIEW_TYPE_IMAGE;
            case VIEW_TYPE_VIDEO:
                return VIEW_TYPE_VIDEO;
            case VIEW_TYPE_LOADING:
                return VIEW_TYPE_LOADING;
            case VIEW_TYPE_NEW_POST:
                return VIEW_TYPE_NEW_POST;
            case VIEW_TYPE_LOCATION:
                return VIEW_TYPE_LOCATION;
            default:
                return super.getItemViewType(position);
        }
    }

    @Override
    public int getItemCount() {
        return (null != messagelist ? messagelist.size() : 0);
    }

    private void bindImageOrGif(final int position, RecyclerView.ViewHolder viewHolder2) {
        final ImageViewHolder viewHolder = (ImageViewHolder) viewHolder2;

        Spannable spannable = new SpannableString(messagelist.get(position).getHeadline());
        Util.linkifyUrl(spannable, new CustomTabsOnClickListener(activity, mCustomTabActivityHelper));


        setHeadline(viewHolder, position);

        setDescription(viewHolder, position, spannable);

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
        Spannable spannable = new SpannableString(messagelist.get(position).getHeadline());
        Util.linkifyUrl(spannable, new CustomTabsOnClickListener(activity, mCustomTabActivityHelper));


        viewHolder.headline.setText(spannable);
        viewHolder.headline.setMovementMethod(LinkMovementMethod.getInstance());


        setHeadline(viewHolder, position);

        setDescription(viewHolder, position, spannable);

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

    private void setLikedStatus(BaseViewHolder viewHolder, int position) {

        if (messagelist.get(position).isLiked()) {
            viewHolder.upicon.setImageResource(R.drawable.ic_thumb_up_primary_16px);
            viewHolder.upvote.setTextColor(ContextCompat.getColor(mContext, R.color.rank));

        } else {
            viewHolder.upicon.setImageResource(R.drawable.ic_thumb_up_black_16px);
            viewHolder.upvote.setTextColor(ContextCompat.getColor(mContext, R.color.likeText));
        }
    }

    private void setFeedCategory(BaseViewHolder viewHolder, int position) {
        viewHolder.source.setVisibility(View.VISIBLE);
        viewHolder.source.setText('#' + messagelist.get(position).getCategory());

    }

    private void setLikesNumber(BaseViewHolder viewHolder, int position) {
        if (messagelist.get(position).getReviews().isEmpty() || messagelist.get(position).getReviews() == null)
            viewHolder.reviews.setText("0 likes");
        else
            viewHolder.reviews.setText(String.valueOf(Integer.parseInt(messagelist.get(position).getReviews())) + " likes");

    }

    private void setReactions(BaseViewHolder viewHolder, int position) {
        if (messagelist.get(position).getComments() == null || messagelist.get(position).getComments().isEmpty())
            viewHolder.app.setText("0 Reactions");
        else {
            viewHolder.app.setText(messagelist.get(position).getComments() + " Reactions");
            if (!messagelist.get(position).getComments().equals("0") && messagelist.get(position).getReaction() != null) {
                viewHolder.rv.setVisibility(View.VISIBLE);

                Spannable spannables = new SpannableString(messagelist.get(position).getReaction());
                Util.linkifyUrl(spannables, new CustomTabsOnClickListener(activity, mCustomTabActivityHelper));
                viewHolder.userName.setText(spannables);
                viewHolder.userName.setMovementMethod(LinkMovementMethod.getInstance());

                viewHolder.name.setText(messagelist.get(position).getFrom());
            } else {
                viewHolder.rv.setVisibility(View.GONE);
            }
        }

    }

    private void setReporterName(BaseViewHolder viewHolder, int position) {

        viewHolder.from.setVisibility(View.VISIBLE);
        viewHolder.from.setText(messagelist.get(position).getName());

    }

    private void setFeedLocation(BaseViewHolder viewHolder, int position) {
        viewHolder.place.setText(messagelist.get(position).getLocation());

    }

    private void setFeedImage(ImageViewHolder viewHolder, int position) {
        viewHolder.image.setVisibility(View.VISIBLE);
        if (messagelist.get(position).getImage().isEmpty()) {
            if (messagelist.get(position).getGif().isEmpty()) {
                viewHolder.image.setVisibility(View.GONE);
            } else
                Glide.with(mContext).load(messagelist.get(position).getGif()).asGif().placeholder(R.drawable.irongrip).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.ic_error).dontAnimate().into(viewHolder.image);
        } else
            Glide.with(mContext).load(messagelist.get(position).getImage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.ic_error).dontAnimate().into(viewHolder.image);

    }

    private void setFeedVideoThumbnail(VideoViewHolder viewHolder, int position) {

        if (messagelist.get(position).getImage() == null || messagelist.get(position).getImage().isEmpty()) {
            viewHolder.image.setVisibility(View.GONE);
        } else {
            viewHolder.image.setVisibility(View.VISIBLE);
            viewHolder.image.setColorFilter(Color.argb(120, 0, 0, 0)); // White Tint

            Glide.with(mContext).load(messagelist.get(position).getImage()).placeholder(R.drawable.irongrip).diskCacheStrategy(DiskCacheStrategy.SOURCE).fallback(R.drawable.ic_reload).error(R.drawable.ic_error).dontAnimate().into(viewHolder.image);
        }
    }

    private void setReactionsNumber(BaseViewHolder viewHolder, int position) {
        if (messagelist.get(position).getComments() == null || messagelist.get(position).getComments().isEmpty())
            viewHolder.app.setText("0 Reactions");
        else {
            viewHolder.app.setText(messagelist.get(position).getComments() + " Reactions");
            if (!messagelist.get(position).getComments().equals("0") && messagelist.get(position).getReaction() != null) {
                viewHolder.rv.setVisibility(View.VISIBLE);

                Spannable spannables = new SpannableString(messagelist.get(position).getReaction());
                Util.linkifyUrl(spannables, new CustomTabsOnClickListener(activity, mCustomTabActivityHelper));
                viewHolder.userName.setText(spannables);
                viewHolder.userName.setMovementMethod(LinkMovementMethod.getInstance());

                viewHolder.name.setText(messagelist.get(position).getFrom());
            } else {
                viewHolder.rv.setVisibility(View.GONE);
            }
        }
    }

    private void setReporterProfilePic(BaseViewHolder viewHolder, int position) {
        Glide.with(mContext).load(messagelist.get(position).getProfilepic()).placeholder(R.drawable.download).error(R.drawable.download).fallback(R.drawable.download).dontAnimate().into(viewHolder.profilepic);

    }

    private void setDate(BaseViewHolder viewHolder, int position) {
        viewHolder.date.setVisibility(View.VISIBLE);
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

    public void add() {
        MpModel loader = new MpModel();
        messagelist.add(loader);
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

    public void loadMore(List<MpModel> messagelist2) {
        this.messagelist.addAll(messagelist2);
        notifyItemRangeInserted(this.messagelist.size() - messagelist2.size(), messagelist2.size());
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {

            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Reweyou/Screenshot");

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("Reweyou", "failed to create directory");
                }
            }

            String mPath = mediaStorageDir.toString() + "/" + now + ".jpg";
            File imageFile = new File(mPath);
            uri = Uri.fromFile(imageFile);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bm.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            e.printStackTrace();
        }
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
        buttonEdit = (AppCompatButton) confirmDialog.findViewById(R.id.buttonConfirm);
        editTextHeadline = (EditText) confirmDialog.findViewById(R.id.editTextOtp);
        editTextHeadline.setText(messagelist.get(position).getHeadline());
        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();

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
                        // params.put("number",number);
                        return params;
                    }
                };
                //Adding the request to the queue
                RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                requestQueue.add(stringRequest);
            }
        });
    }

    private void showPopupMenu(View view) {
        // inflate menu

        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_story, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    private void suggest(int position) {
        id = messagelist.get(position).getPostId();
        username = session.getUsername();
        number = session.getMobileNumber();
        Log.e("id", id);
        Log.e("username", username);
        Log.e("number", number);
        if (messagelist.get(position).getReaders().equals("100")) {
            Toast.makeText(mContext, "This feature will be added in next update", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, SUGGEST_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.trim().equals("success")) {
                                //button.setText("Reviewed");
                            } else {
                                Toast.makeText(mContext, response, Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("postid", id);
                    map.put("username", username);
                    map.put("number", number);
                    return map;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(stringRequest);
        }
    }

    private void makeRequest(final int adapterPosition) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LIKE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("like")) {
                            notifyItemChanged(adapterPosition, prelike);
                            session.addlike(messagelist.get(adapterPosition).getPostId());
                            messagelist.get(adapterPosition).setLiked(true);

                        } else if (response.equals("unlike")) {
                            notifyItemChanged(adapterPosition, preunlike);
                            session.removelike(messagelist.get(adapterPosition).getPostId());
                            messagelist.get(adapterPosition).setLiked(false);


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
                    Glide.clear(((ImageViewHolder) baseViewHolder).image);
                } else if (baseViewHolder instanceof VideoViewHolder) {
                    Glide.clear(((VideoViewHolder) baseViewHolder).image);
                }
            }
        }
        super.onViewRecycled(holder);
    }

    private class BaseViewHolder extends RecyclerView.ViewHolder {
        protected ImageView profilepic, overflow;
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
            profilepic = (ImageView) view.findViewById(R.id.profilepic);
            from = (TextView) view.findViewById(R.id.from);
            reviews = (TextView) view.findViewById(R.id.reviews);
            app = (TextView) view.findViewById(R.id.app);
            upvote = (TextView) view.findViewById(R.id.upvote);
            source = (TextView) view.findViewById(R.id.source);
            linearLayout = (LinearLayout) view.findViewById(R.id.like);
            upicon = (ImageView) view.findViewById(R.id.upicon);


            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (messagelist.get(getAdapterPosition()).isLiked()) {
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
                    cv.setDrawingCacheEnabled(true);
                    cv.buildDrawingCache();
                    bm = cv.getDrawingCache();
                    showPopupMenu(overflow);
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
                    } else {
                        Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            from.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        Bundle bundle = new Bundle();
                        bundle.putString("myData", messagelist.get(getAdapterPosition()).getNumber());
                        Intent in = new Intent(mContext, UserProfile.class);
                        in.putExtras(bundle);
                        mContext.startActivity(in);
                    } else {
                        Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            app.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("myData", messagelist.get(getAdapterPosition()).getPostId());
                    bundle.putString("headline", messagelist.get(getAdapterPosition()).getHeadline());
                    bundle.putString("image", messagelist.get(getAdapterPosition()).getImage());
                    Intent in = new Intent(mContext, Comments1.class);

                    in.putExtras(bundle);
                    mContext.startActivity(in);

                    ((Activity) mContext).overridePendingTransition(0, 0);
                }
            });
            reaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("myData", messagelist.get(getAdapterPosition()).getPostId());
                    bundle.putString("headline", messagelist.get(getAdapterPosition()).getHeadline());
                    bundle.putString("image", messagelist.get(getAdapterPosition()).getImage());
                    Intent in = new Intent(mContext, Comments1.class);
                    in.putExtras(bundle);
                    mContext.startActivity(in);
                    ((Activity) mContext).overridePendingTransition(0, 0);

                }
            });
            place.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in = new Intent(mContext, MyCityActivity.class);
                    in.putExtra("place", messagelist.get(getAdapterPosition()).getLocation());
                    mContext.startActivity(in);
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
                }
            });
            rv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("myData", messagelist.get(getAdapterPosition()).getPostId());
                    bundle.putString("headline", messagelist.get(getAdapterPosition()).getHeadline());
                    bundle.putString("image", messagelist.get(getAdapterPosition()).getImage());
                    Intent in = new Intent(mContext, Comments1.class);
                    ((Activity) mContext).overridePendingTransition(0, 0);

                    in.putExtras(bundle);
                    mContext.startActivity(in);
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
                    if (messagelist.get(getAdapterPosition()).getGif().isEmpty()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("myData", messagelist.get(getAdapterPosition()).getImage());
                        bundle.putString("headline", messagelist.get(getAdapterPosition()).getHeadline());
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


        public VideoViewHolder(View view) {
            super(view);


            image = (ImageView) view.findViewById(R.id.image);


            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        Bundle bundle = new Bundle();
                        bundle.putString("myData", messagelist.get(getAdapterPosition()).getVideo());

                        Intent in = new Intent(mContext, Videorow.class);
                        in.putExtras(bundle);
                        mContext.startActivity(in);
                    } else {
                        Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View view) {
            super(view);
        }
    }

    private class NewPostViewHolder extends RecyclerView.ViewHolder {
        LinearLayout con;

        public NewPostViewHolder(View view) {
            super(view);
            con = (LinearLayout) view.findViewById(R.id.newCon);
            ImageView img = (ImageView) view.findViewById(R.id.pic);
            Glide.with(mContext).load(session.getProfilePicture()).into(img);
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
        public LocationViewHolder(View inflate) {
            super(inflate);
        }
    }

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.share:
                    takeScreenshot();
                    ShareIntent();
                    return true;
                case R.id.send:
                    Toast.makeText(mContext, "This feature will be added in next update", Toast.LENGTH_SHORT).show();
                    // suggest(currentposition);

                    return true;
                default:
            }
            return false;
        }
    }

}