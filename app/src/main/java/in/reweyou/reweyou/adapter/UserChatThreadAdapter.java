package in.reweyou.reweyou.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyou.Contacts;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.UserChat;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.ContactListModel;
import in.reweyou.reweyou.model.UserChatThreadModel;

/**
 * Created by master on 1/1/17.
 */

public class UserChatThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = UserChatThreadAdapter.class.getSimpleName();
    private final UserSessionManager session;
    private final int VIEWTYPE_SEPARATOR = 3;
    private final int VIEWTYPE_ACTIVE_CHATS = 6;
    private final int VIEWTYPE_CHAT_THREAD = 2;
    private final int VIEWTYPE_CONTACT_GROUP = 4;
    private final int LIST_CHAT_THREAD_LIST_SIZE;
    private List<Object> list = new ArrayList<>();
    private Context context;

    public UserChatThreadAdapter(Contacts contacts, List<UserChatThreadModel> chatThreadList, List<ContactListModel> matchContactList, UserSessionManager session) {
        list.add(VIEWTYPE_ACTIVE_CHATS);
        LIST_CHAT_THREAD_LIST_SIZE = chatThreadList.size() + 1;
        list.addAll(chatThreadList);
        list.add(VIEWTYPE_SEPARATOR);
        list.addAll(matchContactList);

        this.session = session;

        this.context = contacts;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEWTYPE_CHAT_THREAD)
            return new ViewHolderActiveChat(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacts_chat_threads, parent, false));
        else if (viewType == VIEWTYPE_SEPARATOR)
            return new ViewHolderSeparator(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_separator, parent, false));
        else if (viewType == VIEWTYPE_CONTACT_GROUP)
            return new UserChatThreadAdapter.ViewHolderContact(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacts_groups, parent, false));
        else if (viewType == VIEWTYPE_ACTIVE_CHATS)
            return new ViewHolderSeparator(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_active_chat_separator, parent, false));
        else return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolderActiveChat) {
            if (session.getMobileNumber().equals(((UserChatThreadModel) list.get(position)).getSender()))
                ((ViewHolderActiveChat) holder).textView.setText(((UserChatThreadModel) list.get(position)).getReceiver_name());
            else
                ((ViewHolderActiveChat) holder).textView.setText(((UserChatThreadModel) list.get(position)).getSender_name());
            Glide.with(context).load(((UserChatThreadModel) list.get(position)).getPic()).error(R.drawable.download).into(((ViewHolderActiveChat) holder).pic);

            ((ViewHolderActiveChat) holder).lastMessage.setText(((UserChatThreadModel) list.get(position)).getLast_message());
        } else if (holder instanceof ViewHolderContact) {
            ((ViewHolderContact) holder).username.setText(((ContactListModel) list.get(position)).getName());
            ((ViewHolderContact) holder).number.setText("+91-" + ((ContactListModel) list.get(position)).getNumber());
            Glide.with(context).load(((ContactListModel) list.get(position)).getPic()).error(R.drawable.download).into(((ViewHolderContact) holder).pic);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEWTYPE_ACTIVE_CHATS;
        else if (position < LIST_CHAT_THREAD_LIST_SIZE)
            return VIEWTYPE_CHAT_THREAD;
        else if (position == LIST_CHAT_THREAD_LIST_SIZE)
            return VIEWTYPE_SEPARATOR;
        else if (position > LIST_CHAT_THREAD_LIST_SIZE)
            return VIEWTYPE_CONTACT_GROUP;
        else
            return super.getItemViewType(position);
    }

    private class ViewHolderActiveChat extends RecyclerView.ViewHolder {
        private final ImageView pic;
        private TextView textView;
        private TextView lastMessage;
        private RelativeLayout container;

        ViewHolderActiveChat(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.Who);
            lastMessage = (TextView) itemView.findViewById(R.id.lastmessage);
            pic = (ImageView) itemView.findViewById(R.id.pic);
            container = (RelativeLayout) itemView.findViewById(R.id.container);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, UserChat.class);
                    i.putExtra("chatroomid", ((UserChatThreadModel) list.get(getAdapterPosition())).getChatroom_id());
                    i.putExtra("othernumber", ((UserChatThreadModel) list.get(getAdapterPosition())).getshowNumber());
                    i.putExtra("othername", ((UserChatThreadModel) list.get(getAdapterPosition())).getname());
                    context.startActivity(i);
                }
            });

        }
    }

    private class ViewHolderContact extends RecyclerView.ViewHolder {
        private TextView username;
        private TextView number;
        private ImageView pic;

        ViewHolderContact(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            number = (TextView) itemView.findViewById(R.id.number);
            pic = (ImageView) itemView.findViewById(R.id.pic);
        }
    }

    private class ViewHolderSeparator extends RecyclerView.ViewHolder {


        ViewHolderSeparator(View itemView) {
            super(itemView);

        }
    }
}
