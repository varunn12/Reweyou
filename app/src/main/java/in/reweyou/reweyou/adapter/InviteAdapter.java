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

import in.reweyou.reweyou.Invite;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.ContactListModel;

/**
 * Created by master on 1/1/17.
 */

public class InviteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = InviteAdapter.class.getSimpleName();
    private final UserSessionManager session;
    private final int VIEWTYPE_INVITE_CONTACTS_SEP = 3;
    private final int VIEWTYPE_ACTIVE_USERS_SEP = 6;
    private final int VIEWTYPE_CHAT_THREAD = 2;
    private final int VIEWTYPE_CONTACT_GROUP = 4;
    private final int LIST_CHAT_THREAD_LIST_SIZE;
    private List<Object> list = new ArrayList<>();
    private Context context;


    public InviteAdapter(Invite invite, ArrayList<ContactListModel> matchContactList, ArrayList<ContactListModel> nonmatchContactList, UserSessionManager session) {
        list.add(VIEWTYPE_ACTIVE_USERS_SEP);
        LIST_CHAT_THREAD_LIST_SIZE = matchContactList.size() + 1;
        list.addAll(matchContactList);
        list.add(VIEWTYPE_INVITE_CONTACTS_SEP);
        list.addAll(nonmatchContactList);

        this.session = session;
        this.context = invite;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEWTYPE_CHAT_THREAD)
            return new ViewHolderActiveUser(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacts_groups_active, parent, false));
        else if (viewType == VIEWTYPE_INVITE_CONTACTS_SEP)
            return new ViewHolderSeparator(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_invite_user, parent, false));
        else if (viewType == VIEWTYPE_CONTACT_GROUP)
            return new ViewHolderInviteContact(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacts_groups, parent, false));
        else if (viewType == VIEWTYPE_ACTIVE_USERS_SEP)
            return new ViewHolderSeparator(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_active_user, parent, false));
        else return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolderInviteContact) {
            ((ViewHolderInviteContact) holder).username.setText(((ContactListModel) list.get(position)).getName());
            ((ViewHolderInviteContact) holder).number.setText("+91-" + ((ContactListModel) list.get(position)).getNumber());
            Glide.with(context).load(((ContactListModel) list.get(position)).getPic()).error(R.drawable.download).into(((ViewHolderInviteContact) holder).pic);
        } else if (holder instanceof ViewHolderActiveUser) {
            ((ViewHolderActiveUser) holder).username.setText(((ContactListModel) list.get(position)).getName());
            ((ViewHolderActiveUser) holder).number.setText("+91-" + ((ContactListModel) list.get(position)).getNumber());
            Glide.with(context).load(((ContactListModel) list.get(position)).getPic()).error(R.drawable.download).into(((ViewHolderActiveUser) holder).pic);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEWTYPE_ACTIVE_USERS_SEP;
        else if (position < LIST_CHAT_THREAD_LIST_SIZE)
            return VIEWTYPE_CHAT_THREAD;
        else if (position == LIST_CHAT_THREAD_LIST_SIZE)
            return VIEWTYPE_INVITE_CONTACTS_SEP;
        else if (position > LIST_CHAT_THREAD_LIST_SIZE)
            return VIEWTYPE_CONTACT_GROUP;
        else
            return super.getItemViewType(position);
    }

    private class ViewHolderActiveUser extends RecyclerView.ViewHolder {
        private TextView username;
        private TextView number;
        private ImageView pic;
        private RelativeLayout container;

        ViewHolderActiveUser(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            number = (TextView) itemView.findViewById(R.id.number);
            pic = (ImageView) itemView.findViewById(R.id.pic);
            container = (RelativeLayout) itemView.findViewById(R.id.container);


        }
    }

    private class ViewHolderInviteContact extends RecyclerView.ViewHolder {
        private TextView username;
        private TextView number;
        private ImageView pic;
        private RelativeLayout container;

        ViewHolderInviteContact(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            number = (TextView) itemView.findViewById(R.id.number);
            pic = (ImageView) itemView.findViewById(R.id.pic);
            container = (RelativeLayout) itemView.findViewById(R.id.container);

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_SUBJECT, "Reweyou");
                        String sAux = "";
                        if (session.checkLoginSplash())
                            sAux = session.getUsername() + " has invite you to join Reweyou.\n\n";
                        else
                            sAux = sAux + "Download app: https://play.google.com/store/apps/details?id=in.reweyou.reweyou";
                        i.putExtra(Intent.EXTRA_TEXT, sAux);
                        context.startActivity(Intent.createChooser(i, "choose one"));
                    } catch (Exception e) {
                        //e.toString();
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private class ViewHolderSeparator extends RecyclerView.ViewHolder {


        ViewHolderSeparator(View itemView) {
            super(itemView);

        }
    }
}
