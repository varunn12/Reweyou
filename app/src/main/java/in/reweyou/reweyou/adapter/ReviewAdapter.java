package in.reweyou.reweyou.adapter;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import in.reweyou.reweyou.LikesActivity;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.customView.CustomSigninDialog;
import in.reweyou.reweyou.model.ReviewModel;


public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final String TAG = ReviewAdapter.class.getSimpleName();
    private final Activity mContext;
    private final UserSessionManager sessionManager;

    private List<ReviewModel> messagelist;


    public ReviewAdapter(Activity mContext) {
        sessionManager = new UserSessionManager(mContext);
        this.mContext = mContext;
        this.messagelist = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new IssueViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_review, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder2, final int position) {

        IssueViewHolder issueViewHolder = (IssueViewHolder) viewHolder2;
        issueViewHolder.description.setText(messagelist.get(position).getDescription());
        issueViewHolder.user.setText(messagelist.get(position).getName());
        issueViewHolder.rate.setText(messagelist.get(position).getRating());
        if (messagelist.get(position).getIs_liked().equals("true")) {
            issueViewHolder.like.setImageResource(R.drawable.ic_thumbs_up_red);
            issueViewHolder.likesnumber.setTextColor(Color.RED);
        } else {
            issueViewHolder.like.setImageResource(R.drawable.ic_thumbs_up);
            issueViewHolder.likesnumber.setTextColor(Color.parseColor("#909090"));
        }

        float rating = Float.parseFloat(messagelist.get(position).getRating());
        if (rating < 2) {
            issueViewHolder.rate.setTextColor(ContextCompat.getColor(mContext, R.color.rating1));
            issueViewHolder.img.setColorFilter(ContextCompat.getColor(mContext, R.color.rating1));
        } else if (rating >= 2 && rating < 3) {
            issueViewHolder.rate.setTextColor(ContextCompat.getColor(mContext, R.color.rating2));
            issueViewHolder.img.setColorFilter(ContextCompat.getColor(mContext, R.color.rating2));

        } else if (rating >= 3 && rating < 4) {
            issueViewHolder.rate.setTextColor(ContextCompat.getColor(mContext, R.color.rating3));
            issueViewHolder.img.setColorFilter(ContextCompat.getColor(mContext, R.color.rating3));

        } else if (rating >= 4 && rating < 5) {
            issueViewHolder.rate.setTextColor(ContextCompat.getColor(mContext, R.color.rating4));
            issueViewHolder.img.setColorFilter(ContextCompat.getColor(mContext, R.color.rating4));

        } else if (rating == 5) {
            issueViewHolder.rate.setTextColor(ContextCompat.getColor(mContext, R.color.rating5));
            issueViewHolder.img.setColorFilter(ContextCompat.getColor(mContext, R.color.rating5));

        }

        issueViewHolder.likesnumber.setText("(" + messagelist.get(position).getLikes() + ")");
        if (messagelist.get(position).getImage() != null) {
            if (!messagelist.get(position).getImage().isEmpty()) {
                issueViewHolder.image.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(messagelist.get(position).getImage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(issueViewHolder.image);
            } else
                issueViewHolder.image.setVisibility(View.GONE);
        } else
            issueViewHolder.image.setVisibility(View.GONE);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        IssueViewHolder issueViewHolder = (IssueViewHolder) holder;

        if (!payloads.isEmpty()) {
            if (payloads.contains("prelike")) {
                ((IssueViewHolder) holder).like.setImageResource(R.drawable.ic_thumbs_up_red);
                issueViewHolder.likesnumber.setTextColor(Color.RED);

                issueViewHolder.likesnumber.setText("(" + (Integer.parseInt(messagelist.get(position).getLikes()) + 1) + ")");
                messagelist.get(position).setLikes(String.valueOf(Integer.parseInt(messagelist.get(position).getLikes()) + 1));

            } else if (payloads.contains("preunlike")) {
                ((IssueViewHolder) holder).like.setImageResource(R.drawable.ic_thumbs_up);
                issueViewHolder.likesnumber.setTextColor(Color.parseColor("#909090"));

                issueViewHolder.likesnumber.setText("(" + (Integer.parseInt(messagelist.get(position).getLikes()) - 1) + ")");
                messagelist.get(position).setLikes(String.valueOf(Integer.parseInt(messagelist.get(position).getLikes()) - 1));

            }
        } else super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public void add(List<ReviewModel> list) {
        messagelist.clear();
        messagelist.addAll(list);
        notifyDataSetChanged();
    }

    private void showlogindialog() {
        CustomSigninDialog customSigninDialog = new CustomSigninDialog(mContext);
        customSigninDialog.show();

    }

    private class IssueViewHolder extends RecyclerView.ViewHolder {

        private TextView description;
        private TextView user;
        private TextView likesnumber;
        private TextView rate;
        private ImageView like, image, img;
        private LinearLayout likebox;
        private RelativeLayout rl;


        private IssueViewHolder(View inflate) {
            super(inflate);
            description = (TextView) inflate.findViewById(R.id.description);
            user = (TextView) inflate.findViewById(R.id.user);
            likesnumber = (TextView) inflate.findViewById(R.id.likesnumber);
            rate = (TextView) inflate.findViewById(R.id.rate);
            rl = (RelativeLayout) inflate.findViewById(R.id.rl);
            like = (ImageView) inflate.findViewById(R.id.like);
            image = (ImageView) inflate.findViewById(R.id.image);
            img = (ImageView) inflate.findViewById(R.id.img);
            likebox = (LinearLayout) inflate.findViewById(R.id.likesbox);
            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, LikesActivity.class);
                    i.putExtra("reviewid", messagelist.get(getAdapterPosition()).getReviewid());
                    i.putExtra("numLikes", messagelist.get(getAdapterPosition()).getLikes());
                    mContext.startActivity(i);
                }
            });
            likebox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (sessionManager.checkLoginSplash()) {
                        if (messagelist.get(getAdapterPosition()).getIs_liked().equals("true")) {
                            messagelist.get(getAdapterPosition()).setIs_liked("false");
                            notifyItemChanged(getAdapterPosition(), "preunlike");
                        } else {
                            messagelist.get(getAdapterPosition()).setIs_liked("true");
                            notifyItemChanged(getAdapterPosition(), "prelike");
                        }
                        updateLike(getAdapterPosition());
                    } else showlogindialog();
                }
            });
        }

        private void updateLike(final int adapterPosition) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("reviewid", messagelist.get(adapterPosition).getReviewid());
            hashMap.put("number", sessionManager.getMobileNumber());
            AndroidNetworking.post("https://reweyou.in/reviews/agree.php")
                    .addBodyParameter(hashMap)
                    .setTag("agree")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d(TAG, "onResponse: like" + response);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            try {

                                Toast.makeText(mContext, "couldnt connect", Toast.LENGTH_SHORT).show();
                                if (messagelist.get(adapterPosition).getIs_liked().equals("false")) {
                                    messagelist.get(getAdapterPosition()).setIs_liked("true");
                                    notifyItemChanged(adapterPosition, "prelike");
                                } else {
                                    messagelist.get(getAdapterPosition()).setIs_liked("false");
                                    notifyItemChanged(adapterPosition, "preunlike");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

        }


    }


}