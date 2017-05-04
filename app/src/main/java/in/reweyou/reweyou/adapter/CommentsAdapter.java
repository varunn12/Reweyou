package in.reweyou.reweyou.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyou.CommentActivity;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.model.CommentModel;
import in.reweyou.reweyou.model.ReplyCommentModel;

/**
 * Created by master on 1/5/17.
 */

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEWTYPE_COMMENT = 1;
    private static final int VIEWTYPE_REPLY = 2;
    private final Context context;
    List<Object> messagelist;

    public CommentsAdapter(Context context) {
        this.context = context;
        this.messagelist = new ArrayList<>();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEWTYPE_COMMENT)
            return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_main, parent, false));
        else if (viewType == VIEWTYPE_REPLY)
            return new ReplyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_reply, parent, false));
        else return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEWTYPE_COMMENT) {
            CommentModel commentModel = (CommentModel) messagelist.get(position);
            CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            commentViewHolder.username.setText(commentModel.getName());
            commentViewHolder.comment.setText(commentModel.getComment());

        } else if (getItemViewType(position) == VIEWTYPE_REPLY) {
            ReplyCommentModel replyCommentModel = (ReplyCommentModel) messagelist.get(position);
            ReplyViewHolder replyViewHolder = (ReplyViewHolder) holder;
            replyViewHolder.username.setText(replyCommentModel.getName());
            replyViewHolder.comment.setText(replyCommentModel.getReply());


        }


    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messagelist.get(position) instanceof CommentModel)
            return VIEWTYPE_COMMENT;
        else if (messagelist.get(position) instanceof ReplyCommentModel)
            return VIEWTYPE_REPLY;
        else
            return super.getItemViewType(position);
    }

    public void add(List<Object> list) {
        messagelist.clear();
        messagelist.addAll(list);
        notifyDataSetChanged();
    }

    private class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView reply;
        private ImageView image;
        private TextView username, comment;


        public CommentViewHolder(View inflate) {
            super(inflate);

            image = (ImageView) inflate.findViewById(R.id.image);
            username = (TextView) inflate.findViewById(R.id.username);
            comment = (TextView) inflate.findViewById(R.id.comment);
            reply = (TextView) inflate.findViewById(R.id.reply);

            reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CommentActivity) context).passClicktoEditText(((CommentModel) messagelist.get(getAdapterPosition())).getName());
                }
            });


        }
    }

    private class ReplyViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView username, comment, reply;


        public ReplyViewHolder(View inflate) {
            super(inflate);

            image = (ImageView) inflate.findViewById(R.id.image);
            username = (TextView) inflate.findViewById(R.id.username);
            comment = (TextView) inflate.findViewById(R.id.comment);


        }
    }
}
