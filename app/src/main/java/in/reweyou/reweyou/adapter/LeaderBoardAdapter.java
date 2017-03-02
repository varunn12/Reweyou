package in.reweyou.reweyou.adapter;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.model.LeaderboardModel;


public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder> {
    private List<LeaderboardModel> mpModelList;
    private Context mContext;

    public LeaderBoardAdapter(Context context, List<LeaderboardModel> mpsList) {
        this.mpModelList = mpsList;
        this.mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_leaderboard_row, viewGroup, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(LeaderBoardAdapter.ViewHolder viewHolder, final int position) {
        String rank = String.valueOf(position+1);
        viewHolder.rank.setText("#" + rank);
        viewHolder.userName.setText(mpModelList.get(position).getName());

        viewHolder.reviews.setText(mpModelList.get(position).getTotal_points());
        Glide.with(mContext).load(mpModelList.get(position).getProfilePic()).placeholder(R.drawable.download).error(R.drawable.download).dontAnimate().into(viewHolder.image);
    }


    @Override
    public int getItemCount() {
        return (null != mpModelList ? mpModelList.size() : 0);
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        protected TextView userName;
        protected TextView reviews;
        protected TextView rank;
        protected ImageView image;
        private CardView cv;

        public ViewHolder(View view) {
            super(view);
            this.userName = (TextView) view.findViewById(R.id.Who);
            this.rank = (TextView) view.findViewById(R.id.rank);
            this.reviews=(TextView)view.findViewById(R.id.Continue);
            this.image=(ImageView)view.findViewById(R.id.image);
            this.cv = (CardView) view.findViewById(R.id.cv);
            /*cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("myData", mpModelList.get(getAdapterPosition()).getNumber());
                    Intent in = new Intent(mContext, UserProfile.class);
                    in.putExtras(bundle);
                    mContext.startActivity(in);
                }
            });*/




        }

    }
}
