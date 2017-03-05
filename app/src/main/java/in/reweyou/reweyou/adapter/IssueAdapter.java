package in.reweyou.reweyou.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import in.reweyou.reweyou.FullImage;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.ReviewActivity;
import in.reweyou.reweyou.VideoDisplay;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.fragment.IssueFragment;
import in.reweyou.reweyou.model.IssueModel;


public class IssueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final String TAG = IssueAdapter.class.getSimpleName();
    private static final int PERMISSION_STORAGE_REQUEST_CODE = 12;
    private final Activity mContext;
    private final UserSessionManager session;
    private final IssueFragment fragment;
    private List<IssueModel> messagelist;
    private Uri uri;
    private int positioon;


    public IssueAdapter(Activity mContext, IssueFragment issueFragment) {
        this.mContext = mContext;
        this.messagelist = new ArrayList<>();
        session = new UserSessionManager(mContext);
        this.fragment = issueFragment;
    }

    private static Bitmap drawToBitmap(Context context, final int layoutResId, final int width, final int height) {

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(layoutResId, null);
        layout.setDrawingCacheEnabled(true);
        layout.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST));
        layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());
        final Bitmap bmp = Bitmap.createBitmap(layout.getMeasuredWidth(), layout.getMeasuredHeight(), Bitmap.Config.ARGB_4444);
        final Canvas canvas = new Canvas(bmp);
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        canvas.drawBitmap(layout.getDrawingCache(), 0, 0, p);
        return bmp;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new IssueViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_feed_adapter_reading_no_readers, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder2, final int position) {

        IssueViewHolder issueViewHolder = (IssueViewHolder) viewHolder2;
        issueViewHolder.headline.setText(messagelist.get(position).getHeadline());
        issueViewHolder.description.setText(messagelist.get(position).getDescription());
        issueViewHolder.rating.setText(messagelist.get(position).getRating());
        issueViewHolder.review.setText(messagelist.get(position).getReviews());
        issueViewHolder.user.setText("By- " + messagelist.get(position).getName());
        issueViewHolder.tag.setText("#" + messagelist.get(position).getCategory());
        if (!messagelist.get(position).getGif().isEmpty())
            Glide.with(mContext).load(messagelist.get(position).getGif()).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(issueViewHolder.imageView);
        else if (!messagelist.get(position).getImage().isEmpty())
            Glide.with(mContext).load(messagelist.get(position).getImage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(issueViewHolder.imageView);
        else {
            issueViewHolder.imageView.setVisibility(View.GONE);
        }

        if (session.getMobileNumber().equals(messagelist.get(position).getCreated_by())) {
            issueViewHolder.editpost.setVisibility(View.VISIBLE);
        } else issueViewHolder.editpost.setVisibility(View.GONE);


    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public void add(List<IssueModel> list) {
        messagelist.clear();
        messagelist.addAll(list);
        notifyDataSetChanged();
    }

    private void editHeadline(final int position) {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(mContext);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_edit_new, null);
        //  number=session.getMobileNumber();
        //Initizliaing confirm button fo dialog box and edittext of dialog box
        Button buttonEdit = (Button) confirmDialog.findViewById(R.id.buttonConfirm);
        final EditText editTextHeadline = (EditText) confirmDialog.findViewById(R.id.headline);
        editTextHeadline.setText(messagelist.get(position).getHeadline());

        editTextHeadline.setSelection(editTextHeadline.getText().length());
        final EditText editTextDes = (EditText) confirmDialog.findViewById(R.id.des);
        editTextDes.setText(messagelist.get(position).getDescription());
        editTextDes.setSelection(editTextHeadline.getText().length());


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


                if (!editTextHeadline.getText().toString().equals(messagelist.get(position).getHeadline()) || !editTextDes.getText().toString().trim().equals(messagelist.get(position).getDescription())) {
                    alertDialog.dismiss();
                    final String headline = editTextHeadline.getText().toString().trim();
                    final String des = editTextDes.getText().toString().trim();
                    Toast.makeText(mContext, "Updating...", Toast.LENGTH_SHORT);
                    //Creating an string request
               /* StringRequest stringRequest = new StringRequest(Request.Method.POST, EDIT_URL,
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
                requestQueue.add(stringRequest);*/

                    Toast.makeText(mContext, "Updating...", Toast.LENGTH_SHORT).show();

                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("topicid", messagelist.get(position).getTopicid());
                    hashMap.put("headline", headline);
                    hashMap.put("description", des);
                    hashMap.put("number", session.getMobileNumber());
                    hashMap.put("deviceid", session.getDeviceid());
                    hashMap.put("token", session.getKeyAuthToken());

                    AndroidNetworking.post("https://www.reweyou.in/reviews/edit_topic.php")
                            .addBodyParameter(hashMap)
                            .setTag("agredwe")
                            .setPriority(Priority.HIGH)
                            .build()
                            .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        Toast.makeText(mContext, "Your post updated", Toast.LENGTH_SHORT).show();

                                        fragment.loadreportsafteredit();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onError(ANError anError) {
                                    Toast.makeText(mContext, "Couldnt update", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else alertDialog.dismiss();
            }
        });

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

    public float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    private Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), (int) (v.getHeight() - pxFromDp(mContext, 6)), Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(b);
        v.draw(c);

        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final Bitmap b2 = drawToBitmap(mContext, R.layout.share_reweyou_tag, v.getWidth(), metrics.heightPixels);
        return combineImages(b, b2);
    }


    private Bitmap combineImages(Bitmap c, Bitmap s) {
        Bitmap cs = null;

        int width, height = 0;

        width = c.getWidth();
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

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (mContext.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Storage Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Storage Permission is revoked");
                ActivityCompat.requestPermissions(mContext, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE_REQUEST_CODE);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Storage Permission is auto granted for sdk<23");
            return true;
        }
    }

    private class IssueViewHolder extends RecyclerView.ViewHolder {
        private ImageView share;
        private TextView headline;
        private TextView description;
        private TextView rating;
        private TextView review;
        private TextView user;
        private TextView tag;
        private ImageView editpost;
        private ImageView imageView;
        private CardView cv;

        private IssueViewHolder(View inflate) {
            super(inflate);
            headline = (TextView) inflate.findViewById(R.id.headline);
            description = (TextView) inflate.findViewById(R.id.description);
            rating = (TextView) inflate.findViewById(R.id.rating);
            editpost = (ImageView) inflate.findViewById(R.id.editpost);
            share = (ImageView) inflate.findViewById(R.id.sharepost);
            review = (TextView) inflate.findViewById(R.id.review);
            user = (TextView) inflate.findViewById(R.id.user);
            tag = (TextView) inflate.findViewById(R.id.tag);
            imageView = (ImageView) inflate.findViewById(R.id.image);
            cv = (CardView) inflate.findViewById(R.id.cv);

            editpost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editHeadline(getAdapterPosition());
                }
            });


            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (isStoragePermissionGranted()) {
                            takeScreenshot(cv);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, ReviewActivity.class);
                    i.putExtra("headline", messagelist.get(getAdapterPosition()).getHeadline());
                    i.putExtra("description", messagelist.get(getAdapterPosition()).getDescription());
                    i.putExtra("rating", messagelist.get(getAdapterPosition()).getRating());
                    i.putExtra("user", messagelist.get(getAdapterPosition()).getCreated_by());
                    i.putExtra("review", messagelist.get(getAdapterPosition()).getReviews());
                    i.putExtra("tag", messagelist.get(getAdapterPosition()).getCategory());
                    i.putExtra("image", messagelist.get(getAdapterPosition()).getImage());
                    i.putExtra("name", messagelist.get(getAdapterPosition()).getName());
                    i.putExtra("video", messagelist.get(getAdapterPosition()).getVideo());
                    i.putExtra("gif", messagelist.get(getAdapterPosition()).getGif());
                    i.putExtra("topicid", messagelist.get(getAdapterPosition()).getTopicid());
                    Log.d(TAG, "onClick:swsq " + messagelist.get(getAdapterPosition()).getStatus());
                    Log.d(TAG, "onClick:swsqii" + messagelist.get(getAdapterPosition()).getImage());
                    i.putExtra("status", messagelist.get(getAdapterPosition()).getStatus());
                    mContext.startActivity(i);
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!messagelist.get(getAdapterPosition()).getVideo().isEmpty()) {
                        Intent in = new Intent(mContext, VideoDisplay.class);
                        in.putExtra("myData", messagelist.get(getAdapterPosition()).getVideo());
                        in.putExtra("tag", messagelist.get(getAdapterPosition()).getCategory());
                        in.putExtra("headline", messagelist.get(getAdapterPosition()).getHeadline());
                        if (messagelist.get(getAdapterPosition()).getHeadline() != null)
                            in.putExtra("description", messagelist.get(getAdapterPosition()).getHeadline());
                        mContext.startActivity(in);

                    } else if (!messagelist.get(getAdapterPosition()).getImage().isEmpty()) {

                        Bundle bundle = new Bundle();
                        bundle.putString("myData", messagelist.get((getAdapterPosition())).getImage());
                        bundle.putString("tag", messagelist.get((getAdapterPosition())).getCategory());
                        bundle.putString("headline", messagelist.get((getAdapterPosition())).getHeadline());
                        Intent in = new Intent(mContext, FullImage.class);
                        in.putExtras(bundle);
                        mContext.startActivity(in);
                    }
                }
            });
        }


    }


}