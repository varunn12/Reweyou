package in.reweyou.reweyou.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.UserProfile;
import in.reweyou.reweyou.model.LeaderboardModel;


public class ReadersAdapter extends RecyclerView.Adapter<ReadersAdapter.ViewHolder> {
    ImageLoader imageLoader = ImageLoader.getInstance();
    private List<LeaderboardModel> mpModelList;
    private Context mContext;
    private DisplayImageOptions options;

    public ReadersAdapter(Context context, List<LeaderboardModel> mpsList) {
        this.mpModelList = mpsList;
        this.mContext = context;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.irongrip)
                .displayer(new RoundedBitmapDisplayer(1000))
                .showImageForEmptyUri(R.drawable.cup)
                .showImageOnFail(R.drawable.cup)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_leaderboard_row, viewGroup, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReadersAdapter.ViewHolder viewHolder, final int position) {
        String rank = String.valueOf(position+1);
        if(mpModelList.get(position).getFollower_name()==null)
        {

        }
        if(mpModelList.get(position).getFollower_name().equals("")) {
         viewHolder.cv.setVisibility(View.GONE);
        }
        viewHolder.userName.setText((mpModelList.get(position).getFollower_name()));
        viewHolder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("myData", mpModelList.get(position).getFollower_number());
                Intent in = new Intent(mContext, UserProfile.class);
                in.putExtras(bundle);
                mContext.startActivity(in);
            }
        });


        viewHolder.reviews.setText(mpModelList.get(position).getTotal_points());
        final ViewHolder viewHolderFinal = viewHolder;
        viewHolder.image.setVisibility(View.GONE);
    }


    @Override
    public int getItemCount() {
        return (null != mpModelList ? mpModelList.size() : 0);
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        protected TextView userName;
        protected TextView reviews;
        protected ImageView image;
        protected CardView cv;
        public ViewHolder(View view) {
            super(view);
            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fontawesome-webfont.ttf");
            String fontPath = "fonts/Roboto-Medium.ttf";
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), fontPath);
            this.userName = (TextView) view.findViewById(R.id.Who);
            this.userName.setTypeface(tf);
            this.reviews=(TextView)view.findViewById(R.id.Continue);
            this.image=(ImageView)view.findViewById(R.id.image);
            this.cv=(CardView)view.findViewById(R.id.cv);
            //userName.setTypeface(font);
        }

    }
}
