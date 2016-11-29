package in.reweyou.reweyou.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.UserProfile;
import in.reweyou.reweyou.model.ReadersModel;


public class ReadersAdapter extends RecyclerView.Adapter<ReadersAdapter.ViewHolder> {
    private List<ReadersModel> mpModelList;
    private Context mContext;
    private DisplayImageOptions options;

    public ReadersAdapter(Context context, List<ReadersModel> mpsList) {
        this.mpModelList = mpsList;
        this.mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_readers_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReadersAdapter.ViewHolder viewHolder, final int position) {

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

        //  Glide.with(mContext).load(mpModelList.get(position).)viewHolder.image
    }


    @Override
    public int getItemCount() {
        return (null != mpModelList ? mpModelList.size() : 0);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView userName;
        protected ImageView image;

        public ViewHolder(View view) {
            super(view);

            this.userName = (TextView) view.findViewById(R.id.Who);
            this.image = (ImageView) view.findViewById(R.id.image);
        }

    }
}
