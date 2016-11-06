package in.reweyou.reweyou.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.reweyou.reweyou.CategoryActivity;
import in.reweyou.reweyou.Comments;
import in.reweyou.reweyou.FullImage;
import in.reweyou.reweyou.LocationActivity;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.UserProfile;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.CustomTabActivityHelper;
import in.reweyou.reweyou.classes.CustomTabsOnClickListener;
import in.reweyou.reweyou.classes.TouchImageView;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.classes.Util;
import in.reweyou.reweyou.model.MpModel;

import static in.reweyou.reweyou.utils.Constants.EDIT_URL;
import static in.reweyou.reweyou.utils.Constants.REVIEW_URL;
import static in.reweyou.reweyou.utils.Constants.SUGGEST_URL;


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final String TAG = MessageAdapter.class.getSimpleName();

    private static final int TYPE_LOAD_MORE = 0;
    private static final int TYPE_FEEDS = 1;
    private final LayoutInflater layoutInflater;

    Activity activity;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    CustomTabActivityHelper mCustomTabActivityHelper;
    UserSessionManager session;
    Uri uri;
    ImageLoader imageLoader = ImageLoader.getInstance();
    private List<MpModel> messagelist;
    private Context mContext;
    private Bitmap bm;
    private String id, postid;
    private EditText editTextHeadline;
    private AppCompatButton buttonEdit;
    private String number;
    private String username;
    private boolean loadingView = false;

    public MessageAdapter(Context context, List<MpModel> mlist) {
        this.mContext = context;
        activity = (Activity) context;
        this.messagelist = mlist;
        cd = new ConnectionDetector(mContext);
        mCustomTabActivityHelper = new CustomTabActivityHelper();
        session = new UserSessionManager(mContext);
        this.layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_LOAD_MORE:
                return createLoadMoreHolder(parent);
            case TYPE_FEEDS:
                return createFeedsHolder(parent);
        }
       return null;
    }

    private FeedsHolder createFeedsHolder(ViewGroup parent) {
        final FeedsHolder holder = new FeedsHolder(layoutInflater.inflate(R.layout.row_messageadapter, parent, false));
        return holder;
    }

    private LoadMoreHolder createLoadMoreHolder(ViewGroup parent){
        final LoadMoreHolder holder = new LoadMoreHolder(layoutInflater.inflate(R.layout.row_messageadapter_loading, parent,false));
        return holder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case TYPE_LOAD_MORE:
                Log.d("dsd", "here");
                break;

            case TYPE_FEEDS:
                bindFeeds(getItem(position), (FeedsHolder)holder, position);
                break;


        }
    }

    private MpModel getItem(int position){
        return messagelist.get(position);
    }

    private void bindFeeds(MpModel item, final FeedsHolder holder, final int position){
        Spannable spannable = new SpannableString(item.getHeadline());
        Util.linkifyUrl(spannable, new CustomTabsOnClickListener(activity, mCustomTabActivityHelper));
        holder.headline.setText(spannable);
        holder.headline.setMovementMethod(LinkMovementMethod.getInstance());


        if (item.getHead() == null || item.getHead().isEmpty())
            holder.head.setVisibility(View.GONE);
        else {
            holder.head.setVisibility(View.VISIBLE);
            holder.head.setText(item.getHead());
        }

        if (item.getHeadline() == null || item.getHeadline().isEmpty())
            holder.headline.setVisibility(View.GONE);
        else {
            holder.headline.setVisibility(View.VISIBLE);
            holder.headline.setText(item.getHeadline());
        }

        if (item.getDate().isEmpty() || item.getDate() == null)
            holder.date.setVisibility(View.GONE);
        else {
            holder.date.setVisibility(View.VISIBLE);
            holder.date.setText(item.getDate());
        }


        Glide.with(mContext).load(item.getProfilepic()).placeholder(R.drawable.download).error(R.drawable.download).fallback(R.drawable.download).dontAnimate().into(holder.profilepic);


        if (item.getImage() == null || item.getImage().isEmpty()) {
            holder.image.setVisibility(View.GONE);
        } else {
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(item.getImage()).placeholder(R.drawable.irongrip).diskCacheStrategy(DiskCacheStrategy.SOURCE).fallback(R.drawable.ic_reload).error(R.drawable.ic_error).dontAnimate().into(holder.image);
        }

        if (item.getLocation() == null || item.getLocation().isEmpty())
            holder.place.setVisibility(View.GONE);
        else {
            holder.place.setVisibility(View.VISIBLE);
            holder.place.setText(item.getLocation());
        }

        if (item.getName() == null || item.getName().isEmpty())
            holder.from.setVisibility(View.GONE);
        else {
            holder.from.setVisibility(View.VISIBLE);
            holder.from.setText(item.getName());
        }

        if (item.getComments() == null || item.getComments().isEmpty())
            holder.app.setText("0 Reactions");
        else {
            holder.app.setText(item.getComments() + " Reactions");
            if (!item.getComments().equals("0")) {
                holder.rv.setVisibility(View.VISIBLE);

                Spannable spannables = new SpannableString(item.getReaction());
                Util.linkifyUrl(spannables, new CustomTabsOnClickListener(activity, mCustomTabActivityHelper));
                holder.userName.setText(spannables);
                holder.userName.setMovementMethod(LinkMovementMethod.getInstance());

                holder.name.setText(item.getFrom());
            } else {
                holder.rv.setVisibility(View.GONE);
            }
        }

        if (item.getReviews().isEmpty() || item.getReviews() == null)
            holder.reviews.setText("0 likes");
        else
            holder.reviews.setText(String.valueOf(Integer.parseInt(item.getReviews())) + " likes");

        if (item.getCategory().isEmpty() || item.getCategory() == null)
            holder.source.setVisibility(View.GONE);
        else {
            holder.source.setVisibility(View.VISIBLE);
            holder.source.setText('#' + item.getCategory());

        }

        final int total = Integer.parseInt(item.getReviews());

        holder.linearLayout.setTag(1);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    final int status = (Integer) view.getTag();
                    if (status == 1) {
                        upvote(position);
                        holder.reviews.setText(String.valueOf(total + 1) + " likes");
                        holder.upicon.setImageResource(R.drawable.ic_thumb_up_primary_16px);
                        holder.upvote.setTextColor(ContextCompat.getColor(mContext, R.color.rank));
                        view.setTag(0);
                        //pause
                    } else {
                        upvote(position);
                        holder.reviews.setText(String.valueOf(total) + " likes");
                        holder.upicon.setImageResource(R.drawable.ic_thumb_up_black_16px);
                        holder.upvote.setTextColor(ContextCompat.getColor(mContext, R.color.likeText));
                        view.setTag(1);
                    }
                } else {
                    Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    @Override
    public int getItemViewType(int position) {
        Log.d("pppp", String.valueOf(position));
        if (position % 10 == 0 && position > 0 && loadingView == true) {
            Log.d("item", String.valueOf(10) + "          " + position);
            return TYPE_LOAD_MORE;
        } else {
            Log.d("item", String.valueOf(super.getItemViewType(position)));
            return TYPE_FEEDS;
        }
    }

    public void add() {
        loadingView = true;
        Log.d("fwdfwfwefwe", "here");
        this.messagelist.add(new MpModel());
        notifyItemInserted(this.messagelist.size() - 1);
    }

    public void remove() {
        loadingView = false;
        messagelist.remove(messagelist.size() - 1);
        notifyItemRemoved(messagelist.size());
    }

    public void loadMore(List<MpModel> messagelist2) {
        this.messagelist.addAll(messagelist2);
        Log.d("sizeof list", String.valueOf(this.messagelist.size()));
        notifyItemRangeInserted(this.messagelist.size() - 10, 10);
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {

            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Reweyou/Screenshot");
            // This location works best if you want the created images to be shared
            // between applications and persist after your app has been uninstalled.

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("Reweyou", "failed to create directory");
                }
            }
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = mediaStorageDir.toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            //   View v1 = getWindow().getDecorView().getRootView();
            // v1.setDrawingCacheEnabled(true);
            //Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            //  v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);
            uri = Uri.fromFile(imageFile);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bm.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            // openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
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

    @Override
    public int getItemCount() {
        return (null != messagelist? messagelist.size() : 0);
    }

    public void showImage(int position) {
        Dialog builder = new Dialog(mContext);
        //  builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //  builder.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });
        //   builder.setTitle(messagelist.getHeadline());
        TouchImageView imageView = new TouchImageView(mContext);
        imageLoader.displayImage(messagelist.get(position).getImage(), imageView);
        //imageView.setImageURI(imageUri);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
        // builder.getWindow().setLayout(1200, 1200);
    }

    private void upvote(int position) {
        HashMap<String, String> user = session.getUserDetails();
        username = user.get(UserSessionManager.KEY_NAME);
        number = session.getMobileNumber();
        id = messagelist.get(position).getPostId();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REVIEW_URL,
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
                map.put("number", number);
                map.put("name", username);
                map.put("id", id);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    //This method would confirm the otp
    public void editHeadline(int position) throws JSONException {
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

    /**
     * Showing popup menu when tapping on 3 dots
     */
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


    class FeedsHolder extends RecyclerView.ViewHolder {
        protected ImageView image, profilepic, overflow;
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

        public FeedsHolder(View view) {
            super(view);

            this.cv = (CardView) itemView.findViewById(R.id.cv);
            this.headline = (TextView) view.findViewById(R.id.Who);
            this.head = (TextView) view.findViewById(R.id.head);
            this.name = (TextView) view.findViewById(R.id.name);
            this.userName = (TextView) view.findViewById(R.id.userName);

            rv = (RelativeLayout) view.findViewById(R.id.rv);
            this.place = (TextView) view.findViewById(R.id.place);
            this.reaction = (ImageView) view.findViewById(R.id.comment);
            this.date = (TextView) view.findViewById(R.id.date);
            this.image = (ImageView) view.findViewById(R.id.image);
            this.overflow = (ImageView) view.findViewById(R.id.overflow);
            this.profilepic = (ImageView) view.findViewById(R.id.profilepic);
            this.tv = (TextView) view.findViewById(R.id.tv);
            this.from = (TextView) view.findViewById(R.id.from);

            this.reviews = (TextView) view.findViewById(R.id.reviews);
            this.app = (TextView) view.findViewById(R.id.app);
            this.upvote = (TextView) view.findViewById(R.id.upvote);
            this.source = (TextView) view.findViewById(R.id.source);

            linearLayout = (LinearLayout) view.findViewById(R.id.like);
            upicon = (ImageView) view.findViewById(R.id.upicon);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        Bundle bundle = new Bundle();
                        bundle.putString("myData", messagelist.get(getAdapterPosition()).getImage());
                        bundle.putString("headline", messagelist.get(getAdapterPosition()).getHeadline());
                        Intent in = new Intent(mContext, FullImage.class);
                        in.putExtras(bundle);
                        mContext.startActivity(in);
                    } else {
                        Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
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
                    Intent in = new Intent(mContext, Comments.class);
                    in.putExtras(bundle);
                    mContext.startActivity(in);
                }
            });
            reaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("myData", messagelist.get(getAdapterPosition()).getPostId());
                    bundle.putString("headline", messagelist.get(getAdapterPosition()).getHeadline());
                    bundle.putString("image", messagelist.get(getAdapterPosition()).getImage());
                    Intent in = new Intent(mContext, Comments.class);
                    in.putExtras(bundle);
                    mContext.startActivity(in);
                }
            });
            place.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    session = new UserSessionManager(mContext);
                    session.setCityLocation(messagelist.get(getAdapterPosition()).getLocation());
                    Intent in = new Intent(mContext, LocationActivity.class);
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
                    Intent in = new Intent(mContext, Comments.class);
                    in.putExtras(bundle);
                    mContext.startActivity(in);
                }
            });


        }
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

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

    private class LoadMoreHolder extends RecyclerView.ViewHolder {
        public LoadMoreHolder(View view) {
            super(view);
        }
    }
}