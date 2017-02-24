package in.reweyou.reweyou.adapter;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.model.TagsModel;


public class TagsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final String TAG = TagsAdapter.class.getSimpleName();
    private final Activity mContext;

    private List<TagsModel> messagelist;


    public TagsAdapter(Activity mContext) {
        this.mContext = mContext;
        this.messagelist = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new TagsViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tags, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder2, final int position) {

        TagsViewHolder tagsViewHolder = (TagsViewHolder) viewHolder2;

        tagsViewHolder.tag.setText(messagelist.get(position).getTags());
    }


    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public void add(List<TagsModel> list) {
        messagelist.addAll(list);
        notifyDataSetChanged();
    }

    private class TagsViewHolder extends RecyclerView.ViewHolder {

        private TextView tag;

        private TagsViewHolder(View inflate) {
            super(inflate);

            tag = (TextView) inflate.findViewById(R.id.tag);

        }


    }


}