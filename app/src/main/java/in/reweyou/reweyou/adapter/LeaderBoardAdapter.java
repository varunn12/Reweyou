package in.reweyou.reweyou.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.UserProfile;
import in.reweyou.reweyou.model.LeaderboardModel;


public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder> {
    ImageLoader imageLoader = ImageLoader.getInstance();
    private List<LeaderboardModel> mpModelList;
    private Context mContext;
    private DisplayImageOptions options;

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

        viewHolder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("myData", mpModelList.get(position).getNumber());
                Intent in = new Intent(mContext, UserProfile.class);
                in.putExtras(bundle);
                mContext.startActivity(in);
            }
        });


        viewHolder.reviews.setText(mpModelList.get(position).getTotal_points());
        Glide.with(mContext).load(mpModelList.get(position).getProfilePic()).error(R.drawable.download).into(viewHolder.image);
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

        public ViewHolder(View view) {
            super(view);
            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fontawesome-webfont.ttf");
            String fontPath = "fonts/Roboto-Regular.ttf";
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), fontPath);
            this.userName = (TextView) view.findViewById(R.id.Who);
            this.rank = (TextView) view.findViewById(R.id.rank);
            this.userName.setTypeface(tf);
            this.reviews=(TextView)view.findViewById(R.id.Continue);
            this.image=(ImageView)view.findViewById(R.id.image);
            //userName.setTypeface(font);
        }

    }
}
