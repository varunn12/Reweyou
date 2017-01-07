package in.reweyou.reweyou.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.SinglePostActivity;
import in.reweyou.reweyou.UserChat;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.UserChatModel;

/**
 * Created by master on 28/12/16.
 */

public class UserChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = UserChatAdapter.class.getSimpleName();
    private final String POST_SHARE_MESSAGE = "Have a look at this post";
    private final int VIEW_TYPE_RECEIVER = 2;
    private final int VIEW_TYPE_SENDER = 3;
    private final int VIEW_TYPE_DATE = 5;
    private final List<Object> list;
    private final UserSessionManager session;
    private final UserChat mContext;

    public UserChatAdapter(List<Object> list, UserSessionManager session, UserChat userChat) {
        this.list = list;
        this.session = session;
        this.mContext = userChat;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENDER)
            return new ViewHolder2(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_chat_self, parent, false));
        else if (viewType == VIEW_TYPE_RECEIVER)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_chat_receiver, parent, false));
        else if (viewType == VIEW_TYPE_DATE)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_chat_date, parent, false));
        else return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder2) {
            if (list.get(position) instanceof UserChatModel) {

                if (((UserChatModel) list.get(position)).getPostid().isEmpty())
                    ((ViewHolder2) holder).textView.setText(((UserChatModel) list.get(position)).getMessage());
                else {
                    SpannableString content = new SpannableString(POST_SHARE_MESSAGE);
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    ((ViewHolder2) holder).textView.setText(content);
                }

                if (!((UserChatModel) list.get(position)).getFailed()) {
                    ((ViewHolder2) holder).fail.setVisibility(View.INVISIBLE);

                } else ((ViewHolder2) holder).fail.setVisibility(View.VISIBLE);


                if (!((UserChatModel) list.get(position)).getSending())
                    ((ViewHolder2) holder).textView.setTextColor(Color.parseColor("#05A8AA"));
                else {
                    ((ViewHolder2) holder).textView.setTextColor(Color.parseColor("#5405A8AA"));
                }
            }
        } else if (holder instanceof ViewHolder) {
            if (list.get(position) instanceof UserChatModel) {
                if (((UserChatModel) list.get(position)).getPostid().isEmpty())
                    ((ViewHolder) holder).textView.setText(((UserChatModel) list.get(position)).getMessage());
                else {
                    SpannableString content = new SpannableString(POST_SHARE_MESSAGE);
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    ((ViewHolder) holder).textView.setText(content);
                }
            } else if (list.get(position) instanceof String)
                ((ViewHolder) holder).textView.setText(((String) list.get(position)));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<
            Object> payloads) {
        if (payloads.isEmpty())
            super.onBindViewHolder(holder, position, payloads);
        else if (payloads.contains("true")) {
            if (holder instanceof ViewHolder2)
                ((ViewHolder2) holder).textView.setTextColor(Color.parseColor("#05A8AA"));
            else if (holder instanceof ViewHolder)
                ((ViewHolder) holder).textView.setTextColor(Color.parseColor("#05A8AA"));


        } else if (payloads.contains("false")) {
            if (holder instanceof ViewHolder2)
                ((ViewHolder2) holder).fail.setVisibility(View.VISIBLE);
            else if (holder instanceof ViewHolder)
                ((ViewHolder) holder).textView.setTextColor(Color.parseColor("#05A8AA"));

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void add(UserChatModel userChatModel) {
        list.add(userChatModel);
        Log.d(TAG, "add: called" + userChatModel.getSender());
        notifyItemInserted(list.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof UserChatModel) {
            if (((UserChatModel) list.get(position)).getSender().equals(session.getMobileNumber()))
                return VIEW_TYPE_SENDER;
            else
                return VIEW_TYPE_RECEIVER;
        } else if (list.get(position) instanceof String) {
            return VIEW_TYPE_DATE;
        } else return super.getItemViewType(position);
    }

    public void changestateofsendingmessage(boolean b) {
        if (b) {
            notifyItemChanged(list.size() - 1, "true");
            ((UserChatModel) list.get(list.size() - 1)).setFailed(false);
            ((UserChatModel) list.get(list.size() - 1)).setSending(false);

        } else {
            notifyItemChanged(list.size() - 1, "false");
            ((UserChatModel) list.get(list.size() - 1)).setSending(false);
            ((UserChatModel) list.get(list.size() - 1)).setFailed(true);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!((UserChatModel) list.get(getAdapterPosition())).getPostid().isEmpty()) {

                        Intent i = new Intent(mContext, SinglePostActivity.class);
                        i.putExtra("postid", ((UserChatModel) list.get(getAdapterPosition())).getPostid());
                        i.putExtra("fromchat", true);

                        mContext.startActivity(i);
                    }
                }
            });

        }
    }

    private class ViewHolder2 extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView fail;

        ViewHolder2(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            fail = (ImageView) itemView.findViewById(R.id.fail);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!((UserChatModel) list.get(getAdapterPosition())).getPostid().isEmpty()) {
                        Intent i = new Intent(mContext, SinglePostActivity.class);
                        i.putExtra("postid", ((UserChatModel) list.get(getAdapterPosition())).getPostid());
                        i.putExtra("fromchat", true);
                        mContext.startActivity(i);
                    }
                }
            });

        }
    }
}
