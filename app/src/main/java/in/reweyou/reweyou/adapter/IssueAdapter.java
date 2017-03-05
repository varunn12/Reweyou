package in.reweyou.reweyou.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
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
    private final Activity mContext;
    private final UserSessionManager session;
    private final IssueFragment fragment;

    private List<IssueModel> messagelist;


    public IssueAdapter(Activity mContext, IssueFragment issueFragment) {
        this.mContext = mContext;
        this.messagelist = new ArrayList<>();
        session = new UserSessionManager(mContext);
        this.fragment = issueFragment;
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
        } else issueViewHolder.editpost.setVisibility(View.INVISIBLE);


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

    private class IssueViewHolder extends RecyclerView.ViewHolder {
        private TextView headline;
        private TextView description;
        private TextView rating;
        private TextView review;
        private TextView user;
        private TextView tag;
        private TextView editpost;
        private ImageView imageView;
        private CardView cv;

        private IssueViewHolder(View inflate) {
            super(inflate);
            headline = (TextView) inflate.findViewById(R.id.headline);
            description = (TextView) inflate.findViewById(R.id.description);
            rating = (TextView) inflate.findViewById(R.id.rating);
            editpost = (TextView) inflate.findViewById(R.id.editpost);
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