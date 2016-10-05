package in.reweyou.reweyou.adapter;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

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
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.TouchImageView;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.UserProfile;
import in.reweyou.reweyou.model.MpModel;

import static android.text.format.DateUtils.getRelativeTimeSpanString;


public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {
    private List<MpModel> messagelist;
    private Context mContext;
    private Bitmap bm;
    private String id, postid;
    int currentposition;
    Date dates;
    private EditText editTextHeadline;
    private AppCompatButton buttonEdit;
    private String number;
    private String username;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private DisplayImageOptions options, option;
    UserSessionManager session;
    public static final String REVIEW_URL = "https://www.reweyou.in/reweyou/reviews.php";
    public static final String VIEW_URL = "https://www.reweyou.in/reweyou/postviews.php";
    public static final String SUGGEST_URL = "https://www.reweyou.in/reweyou/suggest.php";
    public static final String EDIT_URL = "https://www.reweyou.in/reweyou/editheadline.php";
    Uri uri;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public CityAdapter(Context context, List<MpModel> mlist) {
        this.mContext = context;
        this.messagelist = mlist;
        cd = new ConnectionDetector(mContext);
        session = new UserSessionManager(mContext);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.irongrip)
                .showImageForEmptyUri(R.drawable.ic_reload)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        option = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.irongrip)
                .displayer(new RoundedBitmapDisplayer(1000))
                .showImageForEmptyUri(R.drawable.download)
                .showImageOnFail(R.drawable.download)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final CityAdapter.ViewHolder viewHolder, final int position) {

        //viewHolder.headline.setText(messagelist.get(position).getHeadline());
        final Bundle bundle = new Bundle();
        final ViewHolder viewHolderFinal = viewHolder;
        final int total = Integer.parseInt(messagelist.get(position).getReviews());
        viewHolder.headline.setText(messagelist.get(position).getHeadline());
        if(messagelist.get(position).getHead()==null)
        {
            viewHolder.head.setVisibility(View.GONE);
        }
        else
        {
            if(messagelist.get(position).getHead().equals("")){
                viewHolder.head.setVisibility(View.GONE);
                viewHolder.headline.setVisibility(View.VISIBLE);
                viewHolder.headline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if(session.getMobileNumber().equals(messagelist.get(position).getNumber())) {
                                editHeadline(position);
                            }
                            else
                            {
                                //Toast.makeText(mContext,"Illegal, It isn't your post",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
            else {
                viewHolder.head.setVisibility(View.VISIBLE);
                viewHolder.head.setText(messagelist.get(position).getHead());
                viewHolder.headline.setVisibility(View.GONE);
                viewHolder.head.setTag(1);
                viewHolder.head.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final int status = (Integer) view.getTag();
                        if (status == 1) {
                            viewHolderFinal.headline.setVisibility(View.VISIBLE);
                            viewHolderFinal.head.setTag(0);
                            viewHolderFinal.headline.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        if (session.getMobileNumber().equals(messagelist.get(position).getNumber())) {
                                            editHeadline(position);
                                        } else {
                                            //Toast.makeText(mContext,"Illegal, It isn't your post",Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
                        else {
                            viewHolderFinal.headline.setVisibility(View.GONE);
                            viewHolderFinal.head.setTag(1);
                        }
                    }
                });

            }
        }
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    //      Toast.makeText(mContext,messagelist.get(position).getPostId(),Toast.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("myData", messagelist.get(position).getImage());
                    bundle.putString("headline", messagelist.get(position).getHeadline());
                    Intent in = new Intent(mContext, FullImage.class);
                    in.putExtras(bundle);
                    mContext.startActivity(in);
                } else {
                    Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        String stroydates = messagelist.get(position).getDate();
        viewHolder.date.setText(stroydates.substring(0, 12));
   /*     if(stroydates != null && !stroydates .isEmpty()) {
            SimpleDateFormat dfs = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.US);
            try {
                dates = dfs.parse(stroydates);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long epochs = dates.getTime();
            Log.e("Time", String.valueOf(epochs));
            CharSequence timePassedString = getRelativeTimeSpanString(epochs, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            viewHolder.date.setText(timePassedString);
        }
        else
        {
            viewHolder.date.setText(stroydates);
        }
*/
        //    imageLoader.clearMemoryCache();
        //  imageLoader.clearDiskCache();

        imageLoader.displayImage(messagelist.get(position).getProfilepic(), viewHolder.profilepic, option);


        if (messagelist.get(position).getImage() == null) {
            viewHolder.image.setVisibility(View.GONE);
        } else {
            imageLoader.displayImage(messagelist.get(position).getImage(), viewHolder.image, options);
        }


        viewHolder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolderFinal.cv.setDrawingCacheEnabled(true);
                viewHolderFinal.cv.buildDrawingCache();
                bm = viewHolderFinal.cv.getDrawingCache();
                currentposition=position;
                showPopupMenu(viewHolderFinal.overflow);
            }
        });

        viewHolder.place.setText(messagelist.get(position).getLocation());

        viewHolder.from.setText(messagelist.get(position).getName());
        viewHolder.profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Bundle bundle = new Bundle();
                    bundle.putString("myData", messagelist.get(position).getNumber());
                    Intent in = new Intent(mContext, UserProfile.class);
                    in.putExtras(bundle);
                    mContext.startActivity(in);
                }
                else
                {
                    Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewHolder.from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Bundle bundle = new Bundle();
                    bundle.putString("myData", messagelist.get(position).getNumber());
                    Intent in = new Intent(mContext, UserProfile.class);
                    in.putExtras(bundle);
                    mContext.startActivity(in);
                }
                else
                {
                    Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // viewHolder.tv.setVisibility(View.GONE);
        viewHolder.app.setText(messagelist.get(position).getComments() + " Reactions");
        viewHolder.app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("myData", messagelist.get(position).getPostId());
                bundle.putString("headline", messagelist.get(position).getHeadline());
                bundle.putString("image", messagelist.get(position).getImage());
                Intent in = new Intent(mContext, Comments.class);
                in.putExtras(bundle);
                mContext.startActivity(in);
            }
        });
        viewHolder.reacticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("myData", messagelist.get(position).getPostId());
                bundle.putString("headline", messagelist.get(position).getHeadline());
                bundle.putString("image", messagelist.get(position).getImage());
                Intent in = new Intent(mContext, Comments.class);
                in.putExtras(bundle);
                mContext.startActivity(in);
            }
        });
        viewHolder.place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session = new UserSessionManager(mContext);
                session.setCityLocation(messagelist.get(position).getLocation());
                Intent in = new Intent(mContext, LocationActivity.class);
                mContext.startActivity(in);
            }
        });
        viewHolder.reviews.setText(String.valueOf(total) + " likes");
        viewHolder.reviews.setTag(1);

        viewHolderFinal.upicon.setTextColor(ContextCompat.getColor(mContext, R.color.ReviewColor));
        viewHolderFinal.upvote.setText("Like");
        viewHolderFinal.upvote.setTextColor(ContextCompat.getColor(mContext, R.color.ReviewColor));
        viewHolder.upicon.setTag(1);
        viewHolder.upicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {


                    final int status = (Integer) view.getTag();
                    if (status == 1) {
                        upvote(position);
                        viewHolderFinal.reviews.setText(String.valueOf(total + 1) + " likes");
                        viewHolderFinal.upicon.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                        viewHolderFinal.upvote.setText("Liked");
                        viewHolderFinal.upvote.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                        //   viewHolderFinal.myreviews.setText("");
                        view.setTag(0);
                        //pause
                    } else {
                        upvote(position);
                        viewHolderFinal.reviews.setText(String.valueOf(total) + " likes");
                        viewHolderFinal.upicon.setTextColor(ContextCompat.getColor(mContext, R.color.ReviewColor));
                        viewHolderFinal.upvote.setText("Like");
                        viewHolderFinal.upvote.setTextColor(ContextCompat.getColor(mContext, R.color.ReviewColor));
                        //  viewHolderFinal.myreviews.setText("");
                        view.setTag(1);
                    }
                } else {
                    Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewHolder.source.setText('#' + messagelist.get(position).getCategory());
        viewHolder.source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session = new UserSessionManager(mContext);
                session.setCategory(messagelist.get(position).getCategory());
                Intent in = new Intent(mContext, CategoryActivity.class);
                mContext.startActivity(in);
            }
        });
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
        return (null != messagelist ? messagelist.size() : 0);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image, profilepic, overflow;
        protected TextView headline, upvote, head;
        protected TextView place;
        protected TextView icon;
        protected TextView reacticon;
        protected TextView date;
        protected TextView tv;
        protected TextView from;
        protected RelativeLayout relative;
        protected CardView cv;
        protected TextView reviews, source;
        protected TextView app, upicon;

        public ViewHolder(View view) {
            super(view);
            String fontPath = "fonts/Roboto-Medium.ttf";
            String thinpath = "fonts/Roboto-Regular.ttf";
            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fontawesome-webfont.ttf");
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), fontPath);
            Typeface thin = Typeface.createFromAsset(mContext.getAssets(), thinpath);
            this.cv = (CardView) itemView.findViewById(R.id.cv);
            this.headline = (TextView) view.findViewById(R.id.Who);
            this.head=(TextView)view.findViewById(R.id.head);
            if(this.head ==null)
            {

            }
            else
            {
                this.head.setTypeface(tf);
            }

            this.headline.setTypeface(thin);
            this.place = (TextView) view.findViewById(R.id.place);
            this.place.setTypeface(tf);
            this.icon = (TextView) view.findViewById(R.id.locationicon);
            this.icon.setTypeface(font);
            this.upicon = (TextView) view.findViewById(R.id.upicon);
            this.upicon.setTypeface(font);
            this.reacticon = (TextView) view.findViewById(R.id.reacticon);
            this.reacticon.setTypeface(font);
            this.date = (TextView) view.findViewById(R.id.date);
            this.date.setTypeface(thin);
            this.image = (ImageView) view.findViewById(R.id.image);
            this.overflow = (ImageView) view.findViewById(R.id.overflow);
            this.profilepic = (ImageView) view.findViewById(R.id.profilepic);
            this.tv = (TextView) view.findViewById(R.id.tv);
            this.from = (TextView) view.findViewById(R.id.from);
            this.from.setTypeface(tf);
            this.relative = (RelativeLayout) view.findViewById(R.id.Relative);
            this.reviews = (TextView) view.findViewById(R.id.reviews);
            this.app = (TextView) view.findViewById(R.id.app);
            this.app.setTypeface(tf);
            this.upvote = (TextView) view.findViewById(R.id.upvote);
            this.source = (TextView) view.findViewById(R.id.source);
            this.source.setTypeface(tf);
        }
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

    private void suggest(int position) {
        id = messagelist.get(position).getPostId();
        username = session.getUsername();
        number=session.getMobileNumber();
        Log.e("id",id);
        Log.e("username",username);
        Log.e("number",number);
        if(messagelist.get(position).getReaders().equals("100")){
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
}