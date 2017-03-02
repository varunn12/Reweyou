package in.reweyou.reweyou.adapter;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyou.FullImage;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.ReviewActivity;
import in.reweyou.reweyou.VideoDisplay;
import in.reweyou.reweyou.model.IssueModel;


public class IssueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final String TAG = IssueAdapter.class.getSimpleName();
    private final Activity mContext;

    private List<IssueModel> messagelist;


    public IssueAdapter(Activity mContext) {
        this.mContext = mContext;
        this.messagelist = new ArrayList<>();
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

    private class IssueViewHolder extends RecyclerView.ViewHolder {
        private TextView headline;
        private TextView description;
        private TextView rating;
        private TextView review;
        private TextView user;
        private TextView tag;
        private ImageView imageView;
        private CardView cv;

        private IssueViewHolder(View inflate) {
            super(inflate);
            headline = (TextView) inflate.findViewById(R.id.headline);
            description = (TextView) inflate.findViewById(R.id.description);
            rating = (TextView) inflate.findViewById(R.id.rating);
            review = (TextView) inflate.findViewById(R.id.review);
            user = (TextView) inflate.findViewById(R.id.user);
            tag = (TextView) inflate.findViewById(R.id.tag);
            imageView = (ImageView) inflate.findViewById(R.id.image);
            cv = (CardView) inflate.findViewById(R.id.cv);


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