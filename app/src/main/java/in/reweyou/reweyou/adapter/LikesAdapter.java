package in.reweyou.reweyou.adapter;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import in.reweyou.reweyou.Comments;
import in.reweyou.reweyou.FullImage;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.model.CommentsModel;


public class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.ViewHolder> {
    private List<CommentsModel> CommentsModelList;
    private Context mContext;
    private DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public LikesAdapter(Context context, List<CommentsModel> mpsList) {
        this.CommentsModelList = mpsList;
        this.mContext = context;


        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.irongrip)
                .displayer(new RoundedBitmapDisplayer(1000))
                .showImageForEmptyUri(R.drawable.ic_reload)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notify, viewGroup, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(LikesAdapter.ViewHolder viewHolder, final int position) {

        viewHolder.Who.setText(CommentsModelList.get(position).getReviewer_Name());
        viewHolder.time.setText(CommentsModelList.get(position).getTime().substring(0,12));
        viewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("myData", CommentsModelList.get(position).getPostid());
                bundle.putString("image", CommentsModelList.get(position).getImage());
                bundle.putString("headline", CommentsModelList.get(position).getHeadline());
                Intent in = new Intent(mContext, Comments.class);
                in.putExtras(bundle);
                mContext.startActivity(in);
            }
        });

        final ViewHolder viewHolderFinal = viewHolder;
        imageLoader.displayImage(CommentsModelList.get(position).getImage(), viewHolder.image, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                viewHolderFinal.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                viewHolderFinal.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                viewHolderFinal.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                viewHolderFinal.progressBar.setVisibility(View.GONE);
            }
        });

        imageLoader.displayImage(CommentsModelList.get(position).getImage(), viewHolder.image, options);
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("myData", CommentsModelList.get(position).getImage());
                Intent in = new Intent(mContext, FullImage.class);
                in.putExtras(bundle);
                mContext.startActivity(in);
                //    showImage(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return (null != CommentsModelList ? CommentsModelList.size() : 0);
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        protected TextView Who;
        protected TextView time;
        protected CardView cv;
        protected ImageView image;
        protected ProgressBar progressBar;
        public ViewHolder(View view) {
            super(view);
            String fontPath = "fonts/Roboto-Regular.ttf";
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), fontPath);
            String thinpath="fonts/Roboto-Regular.ttf";
            Typeface thin = Typeface.createFromAsset(mContext.getAssets(), thinpath);
            this.Who = (TextView) view.findViewById(R.id.Who);
            this.Who.setTypeface(tf);
            this.image=(ImageView)view.findViewById(R.id.image);
            this.progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
            this.cv=(CardView)view.findViewById(R.id.cv);
            this.time=(TextView)view.findViewById(R.id.time);
            //userName.setTypeface(font);
        }

    }

}
