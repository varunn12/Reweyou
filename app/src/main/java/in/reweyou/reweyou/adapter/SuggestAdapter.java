package in.reweyou.reweyou.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.model.ForumModel;

/**
 * Created by master on 1/5/17.
 */

public class SuggestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    List<ForumModel> messagelist;

    public SuggestAdapter(Context context) {
        this.context = context;
        this.messagelist = new ArrayList<>();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SuggestViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explore_suggest, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SuggestViewHolder forumViewHolder = (SuggestViewHolder) holder;
        Glide.with(context).load(messagelist.get(position).getImage()).into(forumViewHolder.backgroundImage);
        forumViewHolder.groupName.setText(messagelist.get(position).getForum_name());
    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public void add(List<ForumModel> list) {
        messagelist.clear();
        messagelist.addAll(list);
        notifyDataSetChanged();
    }

    private class SuggestViewHolder extends RecyclerView.ViewHolder {
        private ImageView backgroundImage;
        private TextView groupName;
        private TextView members;
        private TextView threads;

        public SuggestViewHolder(View inflate) {
            super(inflate);

            backgroundImage = (ImageView) inflate.findViewById(R.id.img);
            groupName = (TextView) inflate.findViewById(R.id.groupname);
            members = (TextView) inflate.findViewById(R.id.members);
            threads = (TextView) inflate.findViewById(R.id.threads);
        }
    }
}
