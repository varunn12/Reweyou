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
    }

    public MessageAdapter() {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i != 10) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_messageadapter, viewGroup, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_messageadapter_loading, viewGroup, false);
            return new ViewHolder1(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder2, final int position) {

        switch (viewHolder2.getItemViewType()) {
            case 10:
                Log.d("dsd", "here");
                break;

            default:
                final ViewHolder viewHolder = (ViewHolder) viewHolder2;
                Spannable spannable = new SpannableString(messagelist.get(position).getHeadline());
                Util.linkifyUrl(spannable, new CustomTabsOnClickListener(activity, mCustomTabActivityHelper));
                viewHolder.headline.setText(spannable);
                viewHolder.headline.setMovementMethod(LinkMovementMethod.getInstance());


                if (messagelist.get(position).getHead() == null || messagelist.get(position).getHead().isEmpty())
                    viewHolder.head.setVisibility(View.GONE);
                else {
                    viewHolder.head.setVisibility(View.VISIBLE);
                    viewHolder.head.setText(messagelist.get(position).getHead());
                }

                if (messagelist.get(position).getHeadline() == null || messagelist.get(position).getHeadline().isEmpty())
                    viewHolder.headline.setVisibility(View.GONE);
                else {
                    viewHolder.headline.setVisibility(View.VISIBLE);
                    viewHolder.headline.setText(messagelist.get(position).getHeadline());
                }

                if (messagelist.get(position).getDate().isEmpty() || messagelist.get(position).getDate() == null)
                    viewHolder.date.setVisibility(View.GONE);
                else {
                    viewHolder.date.setVisibility(View.VISIBLE);
                    viewHolder.date.setText(messagelist.get(position).getDate());
                }


                Glide.with(mContext).load(messagelist.get(position).getProfilepic()).placeholder(R.drawable.download).error(R.drawable.download).fallback(R.drawable.download).dontAnimate().into(viewHolder.profilepic);


                if (messagelist.get(position).getImage() == null || messagelist.get(position).getImage().isEmpty()) {
                    viewHolder.image.setVisibility(View.GONE);
                } else {
                    viewHolder.image.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(messagelist.get(position).getImage()).placeholder(R.drawable.irongrip).diskCacheStrategy(DiskCacheStrategy.SOURCE).fallback(R.drawable.ic_reload).error(R.drawable.ic_error).dontAnimate().into(viewHolder.image);
                }

                if (messagelist.get(position).getLocation() == null || messagelist.get(position).getLocation().isEmpty())
                    viewHolder.place.setVisibility(View.GONE);
                else {
                    viewHolder.place.setVisibility(View.VISIBLE);
                    viewHolder.place.setText(messagelist.get(position).getLocation());
                }

                if (messagelist.get(position).getName() == null || messagelist.get(position).getName().isEmpty())
                    viewHolder.from.setVisibility(View.GONE);
                else {
                    viewHolder.from.setVisibility(View.VISIBLE);
                    viewHolder.from.setText(messagelist.get(position).getName());
                }

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

                if (messagelist.get(position).getReviews().isEmpty() || messagelist.get(position).getReviews() == null)
                    viewHolder.reviews.setText("0 likes");
                else
                    viewHolder.reviews.setText(String.valueOf(Integer.parseInt(messagelist.get(position).getReviews())) + " likes");

                if (messagelist.get(position).getCategory().isEmpty() || messagelist.get(position).getCategory() == null)
                    viewHolder.source.setVisibility(View.GONE);
                else {
                    viewHolder.source.setVisibility(View.VISIBLE);
                    viewHolder.source.setText('#' + messagelist.get(position).getCategory());

                }

                final int total = Integer.parseInt(messagelist.get(position).getReviews());

                viewHolder.linearLayout.setTag(1);
                viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isInternetPresent = cd.isConnectingToInternet();
                        if (isInternetPresent) {


                            final int status = (Integer) view.getTag();
                            if (status == 1) {
                                upvote(position);
                                viewHolder.reviews.setText(String.valueOf(total + 1) + " likes");
                                viewHolder.upicon.setImageResource(R.drawable.ic_thumb_up_primary_16px);
                                viewHolder.upvote.setTextColor(ContextCompat.getColor(mContext, R.color.rank));
                                view.setTag(0);
                                //pause
                            } else {
                                upvote(position);
                                viewHolder.reviews.setText(String.valueOf(total) + " likes");
                                viewHolder.upicon.setImageResource(R.drawable.ic_thumb_up_black_16px);
                                viewHolder.upvote.setTextColor(ContextCompat.getColor(mContext, R.color.likeText));
                                view.setTag(1);
                            }
                        } else {
                            Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                break;

        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("pppp", String.valueOf(position));
        if (position % 10 == 0 && position > 0 && loadingView == true) {
            Log.d("item", String.valueOf(10) + "          " + position);
            return 10;
        } else {
            Log.d("item", String.valueOf(super.getItemViewType(position)));
            return super.getItemViewType(position);
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

    @Override
    public int getItemCount() {
        return (null != messagelist ? messagelist.size() : 0);
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
        //   builder.setTitle(messagelist.get(position).getHeadline());
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
                Map<String, String> map = new HashMap<>();
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


    class ViewHolder extends RecyclerView.ViewHolder {
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

        public ViewHolder(View view) {
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

    private class ViewHolder1 extends RecyclerView.ViewHolder {
        public ViewHolder1(View view) {
            super(view);
        }
    }
}