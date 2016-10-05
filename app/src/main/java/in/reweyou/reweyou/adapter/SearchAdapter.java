package in.reweyou.reweyou.adapter;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import in.reweyou.reweyou.Details;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.model.MpDetail;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private List<MpDetail> mpModelList;
    private Context mContext;

    public SearchAdapter(Context context, List<MpDetail> mpsList) {
        this.mpModelList = mpsList;
        this.mContext = context;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card, viewGroup, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolder viewHolder, final int position) {


        viewHolder.mpName.setText(mpModelList.get(position).getName());
        viewHolder.mpName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = mpModelList.get(position).getId();
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                Log.d("id", id);
                Intent in=new Intent(mContext,Details.class);
                in.putExtras(bundle);
                mContext.startActivity(in);
            }
        });
        viewHolder.tvPlace.setText(mpModelList.get(position).getPlace());
        viewHolder.tvState.setText(mpModelList.get(position).getState());
        viewHolder.tvParty.setText(mpModelList.get(position).getParty());


        // Then later, when you want to display image
        ImageLoader.getInstance().displayImage(mpModelList.get(position).getImage(), viewHolder.ivIcon);
        viewHolder.ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = mpModelList.get(position).getId();
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                Log.d("id", id);
                Intent in=new Intent(mContext,Details.class);
                in.putExtras(bundle);
                mContext.startActivity(in);
            }
        });
    }


    @Override
    public int getItemCount() {
        return (null != mpModelList ? mpModelList.size() : 0);
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        protected ImageView ivIcon;
        protected TextView mpName;
        protected TextView tvParty;
        protected TextView tvPlace;
        protected TextView tvState;
        protected CardView cv;

        public ViewHolder(View view) {
            super(view);
            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fontawesome-webfont.ttf");

            this.cv = (CardView) itemView.findViewById(R.id.cv);
            this.mpName = (TextView) view.findViewById(R.id.Who);
            this.tvParty = (TextView) view.findViewById(R.id.Post);
            this.tvPlace = (TextView) view.findViewById(R.id.place);
            this.tvState = (TextView) view.findViewById(R.id.tvState);
            this.ivIcon = (ImageView) view.findViewById(R.id.image);
            //mpName.setTypeface(font);
        }

    }
}
