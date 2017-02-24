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
        issueViewHolder.review.setText(messagelist.get(position).getReviews() + "  RATING");
        issueViewHolder.user.setText(messagelist.get(position).getCreated_by());
        issueViewHolder.tag.setText("#" + messagelist.get(position).getCategory());
    }


    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public void add(List<IssueModel> list) {
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

        private IssueViewHolder(View inflate) {
            super(inflate);
            headline = (TextView) inflate.findViewById(R.id.headline);
            description = (TextView) inflate.findViewById(R.id.description);
            rating = (TextView) inflate.findViewById(R.id.rating);
            review = (TextView) inflate.findViewById(R.id.review);
            user = (TextView) inflate.findViewById(R.id.user);
            tag = (TextView) inflate.findViewById(R.id.tag);

        }


    }


}