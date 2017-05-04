package in.reweyou.reweyou.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyou.CommentActivity;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.model.IssueModel;

/**
 * Created by master on 1/5/17.
 */

public class FeeedsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    List<IssueModel> messagelist;

    public FeeedsAdapter(Context context) {
        this.context = context;
        this.messagelist = new ArrayList<>();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ForumViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feeds, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ForumViewHolder forumViewHolder = (ForumViewHolder) holder;

        forumViewHolder.description.setText(messagelist.get(position).getDescription());
        forumViewHolder.date.setText(messagelist.get(position).getCreated_on());
        forumViewHolder.username.setText(messagelist.get(position).getName());
        if (messagelist.get(position).getImage().isEmpty())
            forumViewHolder.image.setVisibility(View.GONE);
        else {
            forumViewHolder.image.setVisibility(View.VISIBLE);
            Glide.with(context).load(messagelist.get(position).getImage()).into(forumViewHolder.image);
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

    private class ForumViewHolder extends RecyclerView.ViewHolder {
        private ImageView image, like, liketemp, comment;
        private TextView username;
        private TextView date;
        private TextView description;

        public ForumViewHolder(View inflate) {
            super(inflate);

            image = (ImageView) inflate.findViewById(R.id.image);
            comment = (ImageView) inflate.findViewById(R.id.comment);
            like = (ImageView) inflate.findViewById(R.id.i1);
            liketemp = (ImageView) inflate.findViewById(R.id.itemp);
            description = (TextView) inflate.findViewById(R.id.description);
            username = (TextView) inflate.findViewById(R.id.usernamee);
            date = (TextView) inflate.findViewById(R.id.date);

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    like.setColorFilter(Color.parseColor("#D75A4A"));
                    liketemp.animate().translationYBy(-100).alpha(0).rotationBy(150).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
                }
            });

            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, CommentActivity.class));
                }
            });

        }
    }
}
