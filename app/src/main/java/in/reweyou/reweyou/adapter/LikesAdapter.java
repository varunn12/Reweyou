package in.reweyou.reweyou.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.UserProfile;
import in.reweyou.reweyou.model.LikesModel;


public class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.ViewHolder> {
    private List<LikesModel> likeslList;
    private Context mContext;

    public LikesAdapter(Context context, List<LikesModel> mpsList) {
        this.likeslList = mpsList;
        this.mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_likes_row, viewGroup, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(LikesAdapter.ViewHolder viewHolder, final int position) {

        viewHolder.Who.setText(likeslList.get(position).getReviewer_name());
        Glide.with(mContext).load(likeslList.get(position).getProfilepic()).error(R.drawable.download).into(viewHolder.image);
    }


    @Override
    public int getItemCount() {
        return likeslList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView Who;

        protected ImageView image;
        private RelativeLayout parent;

        public ViewHolder(View view) {
            super(view);

            this.Who = (TextView) view.findViewById(R.id.Who);

            this.image = (ImageView) view.findViewById(R.id.image);
            this.parent = (RelativeLayout) view.findViewById(R.id.parent);
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("myData", likeslList.get(getAdapterPosition()).getReviewer());
                    Intent in = new Intent(mContext, UserProfile.class);
                    in.putExtras(bundle);
                    mContext.startActivity(in);
                }
            });



            //userName.setTypeface(font);
        }

    }

}
