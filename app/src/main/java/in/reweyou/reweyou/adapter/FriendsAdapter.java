package in.reweyou.reweyou.adapter;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.reweyou.reweyou.R;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    private List<String> messagelist;
    private Context mContext;
    private String number;

    public FriendsAdapter(Context context, List<String> mlist) {
        this.mContext = context;
        this.messagelist = mlist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_leaderboard_row, viewGroup, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FriendsAdapter.ViewHolder viewHolder, final int position) {

       viewHolder.number.setText(messagelist.get(position));
        viewHolder.Continue.setText("");
    }

    @Override
    public int getItemCount() {
        return (null != messagelist ? messagelist.size() : 0);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView number;
        protected TextView Continue;

        public ViewHolder(View view) {
            super(view);
            String fontPath = "fonts/Roboto-Regular.ttf";
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), fontPath);
            this.number = (TextView) view.findViewById(R.id.Who);
            this.number.setTypeface(tf);
            this.Continue = (TextView)view.findViewById(R.id.Continue);
        }
    }

}