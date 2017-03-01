package in.reweyou.reweyou.adapter;


import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyou.Feed;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.model.TagsModel;


public class TagsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final String TAG = TagsAdapter.class.getSimpleName();
    private final Activity mContext;

    private List<TagsModel> messagelist;
    private int selectPosition = 0;
    private boolean flagChange;


    public TagsAdapter(Activity mContext) {
        this.mContext = mContext;
        this.messagelist = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new TagsViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tags, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        TagsViewHolder tagsViewHolder = (TagsViewHolder) viewHolder;

        tagsViewHolder.tag.setText(messagelist.get(position).getTags());
        if (position == selectPosition) {
            tagsViewHolder.tag.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            tagsViewHolder.tag.setBackground(mContext.getResources().getDrawable(R.drawable.box_tags_white));
        } else {
            tagsViewHolder.tag.setTextColor(mContext.getResources().getColor(R.color.white));
            tagsViewHolder.tag.setBackground(mContext.getResources().getDrawable(R.drawable.box_tags));

        }



    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        TagsViewHolder tagsViewHolder = (TagsViewHolder) holder;

        if (!payloads.isEmpty()) {

            if (payloads.contains("uncheck")) {
                tagsViewHolder.tag.setTextColor(mContext.getResources().getColor(R.color.white));
                tagsViewHolder.tag.setBackground(mContext.getResources().getDrawable(R.drawable.box_tags));

            } else if (payloads.contains("check")) {

                tagsViewHolder.tag.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                tagsViewHolder.tag.setBackground(mContext.getResources().getDrawable(R.drawable.box_tags_white));

            }
        } else super.onBindViewHolder(holder, position, payloads);
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

            tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() == selectPosition) {

                    } else {


                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                notifyItemChanged(selectPosition, "uncheck");

                            }
                        });

                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                selectPosition = getAdapterPosition();

                                notifyItemChanged(selectPosition, "check");
                            }
                        });

                        ((Feed) mContext).makeIssueRequest(tag.getText().toString());
                    }
                }
            });

        }


    }


}