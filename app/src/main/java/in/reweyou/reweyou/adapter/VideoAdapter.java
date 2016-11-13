package in.reweyou.reweyou.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.reweyou.reweyou.Comments;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.Videorow;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.MpModel;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    public static final String VIEW_URL = "https://www.reweyou.in/videoview.php";
    public static final String REVIEW_URL = "https://www.reweyou.in/videoreview.php";
    ProgressDialog pDialog;
    Uri uri;
    UserSessionManager session;
    ImageLoader imageLoader = ImageLoader.getInstance();
    private List<MpModel> messagelist;
    private Context mContext;
    private Bitmap bm;
    private String id;
    private String number;
    private String username;
    private DisplayImageOptions options;

    public VideoAdapter(Context context, List<MpModel> mlist) {
        this.mContext = context;
        this.messagelist = mlist;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.irongrip)
                .showImageForEmptyUri(R.drawable.ic_reload)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(10))
                .build();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vidrow, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(VideoAdapter.ViewHolder viewHolder, final int position) {

        viewHolder.headline.setText(messagelist.get(position).getHeadline());
        final int total = Integer.parseInt(messagelist.get(position).getPostviews());
        final ViewHolder viewHolderFinal = viewHolder;
        viewHolder.headline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolderFinal.cv.setDrawingCacheEnabled(true);
                viewHolderFinal.cv.buildDrawingCache();
                bm = viewHolderFinal.cv.getDrawingCache();
                takeScreenshot();
                ShareIntent();
            }
            });


        viewHolder.place.setText(messagelist.get(position).getLocation());
        viewHolder.tag.setText(messagelist.get(position).getCategory());
        viewHolder.tag.setVisibility(View.GONE);
        viewHolder.date.setText(messagelist.get(position).getDate());
        viewHolder.from.setText(messagelist.get(position).getName());
        viewHolder.reviews.setText(messagelist.get(position).getReviews()+ " views");
        imageLoader.displayImage(messagelist.get(position).getImage(), viewHolder.image, options);
        Typeface font = Typeface.createFromAsset( mContext.getAssets(), "fontawesome-webfont.ttf" );
        viewHolder.vidicon.setTypeface(font);

        final Bundle bundle = new Bundle();
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                views(position);
                viewHolderFinal.reviews.setText(String.valueOf(total + 1) + " views");
                bundle.putString("myData", messagelist.get(position).getVideo());
                bundle.putString("date",messagelist.get(position).getDate());
                bundle.putString("headline",messagelist.get(position).getHeadline());
                bundle.putString("from",messagelist.get(position).getName());
                bundle.putString("tag",messagelist.get(position).getCategory());
                bundle.putString("place",messagelist.get(position).getLocation());
                bundle.putString("reviews",messagelist.get(position).getReviews());
                Intent in = new Intent(mContext, Videorow.class);
                in.putExtras(bundle);
                mContext.startActivity(in);
            }
        });
        viewHolder.vidicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                views(position);
                viewHolderFinal.reviews.setText(String.valueOf(total + 1) + " views");
                bundle.putString("myData", messagelist.get(position).getVideo());
                bundle.putString("date", messagelist.get(position).getDate());
                bundle.putString("headline", messagelist.get(position).getHeadline());
                bundle.putString("from", messagelist.get(position).getName());
                bundle.putString("tag", messagelist.get(position).getCategory());
                bundle.putString("place", messagelist.get(position).getLocation());
                bundle.putString("reviews", messagelist.get(position).getReviews());
                Intent in = new Intent(mContext, Videorow.class);
                in.putExtras(bundle);
                mContext.startActivity(in);
            }
        });
        viewHolder.share.setVisibility(View.GONE);
        viewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        viewHolder.app.setText(messagelist.get(position).getComments() + " reactions");
        viewHolder.app.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("myData", messagelist.get(position).getPostId());
                Intent in = new Intent(mContext, Comments.class);
                in.putExtras(bundle);
                mContext.startActivity(in);
            }
        });
        viewHolder.app.setVisibility(View.GONE);
        viewHolder.reviews.setText(messagelist.get(position).getPostviews()+ " views");
        viewHolder.reviews.setTag(1);

  /*      viewHolder.reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int status = (Integer) view.getTag();
                if (status == 1) {
                    user(position);

                    viewHolderFinal.reviews.setText(String.valueOf(total + 1) + " reviews");
                    viewHolderFinal.reviews.setBackgroundResource(R.color.colorPrimary);
                    //   viewHolderFinal.myreviews.setText("");
                    view.setTag(0);
                    //pause
                } else {
                    user(position);

                    viewHolderFinal.reviews.setText(String.valueOf(total)+ " reviews");
                    viewHolderFinal.reviews.setBackgroundResource(R.color.random);
                    //  viewHolderFinal.myreviews.setText("");
                    view.setTag(1);
                }
            }
        });
*/
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        try {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Reweyou/Screenshot");
            // This location works best if you want the created images to be shared
            // between applications and persist after your app has been uninstalled.
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
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


    private void ShareIntent()
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Download Reweyou App to watch this video. https://goo.gl/o5Kyqc");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/jpeg");
        //intent.setPackage("com.whatsapp");
        mContext.startActivity(Intent.createChooser(intent, "Share using"));
    }

    @Override
    public int getItemCount() {
        return (null != messagelist ? messagelist.size() : 0);
    }

    private void user(int position) {

        session = new UserSessionManager(mContext);

        HashMap<String, String> user = session.getUserDetails();

        username = user.get(UserSessionManager.KEY_NAME);
        number=session.getMobileNumber();
        id=messagelist.get(position).getPostId();
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
                map.put("name",username);
                map.put("id", id);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    private void views(int position) {
        id=messagelist.get(position).getPostId();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VIEW_URL,
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
                map.put("id", id);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView headline;
        protected TextView place;
        protected TextView vidicon;
        protected TextView date;
        protected TextView tag;
        protected TextView share;
        protected TextView tv;
        protected TextView from;
        protected ImageView image;
        protected TextView reviews;
        protected RelativeLayout relative;
        protected CardView cv;
        protected TextView app;

        public ViewHolder(View view) {
            super(view);
            String fontPath = "fonts/Roboto-Regular.ttf";
            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fontawesome-webfont.ttf");
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), fontPath);
            this.cv = (CardView) itemView.findViewById(R.id.cv);
            this.headline = (TextView) view.findViewById(R.id.Who);
            this.headline.setTypeface(tf);
            this.place = (TextView) view.findViewById(R.id.place);
            this.place.setTypeface(tf);
            this.date = (TextView) view.findViewById(R.id.date);
            this.vidicon = (TextView) view.findViewById(R.id.vidicon);
            this.image = (ImageView) view.findViewById(R.id.image);
            this.tag = (TextView) view.findViewById(R.id.tag);
            this.share = (TextView) view.findViewById(R.id.share);
            this.share.setTypeface(font);
            this.reviews = (TextView) view.findViewById(R.id.reviews);
            this.tv = (TextView) view.findViewById(R.id.tv);
            this.from = (TextView) view.findViewById(R.id.from);
            this.from.setTypeface(tf);
            this.relative = (RelativeLayout) view.findViewById(R.id.Relative);
            this.app = (TextView) view.findViewById(R.id.app);
            this.app.setTypeface(tf);
        }
    }
}