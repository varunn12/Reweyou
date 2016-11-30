package in.reweyou.reweyou.adapter;


import android.content.Context;
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
import in.reweyou.reweyou.model.LikesModel;


public class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.ViewHolder> {
    ImageLoader imageLoader = ImageLoader.getInstance();
    private List<LikesModel> likeslList;
    private Context mContext;
    private DisplayImageOptions options;

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

        public ViewHolder(View view) {
            super(view);

            this.Who = (TextView) view.findViewById(R.id.Who);

            this.image = (ImageView) view.findViewById(R.id.image);

            //userName.setTypeface(font);
        }

    }

}
