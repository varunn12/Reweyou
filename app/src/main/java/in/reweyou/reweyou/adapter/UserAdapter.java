package in.reweyou.reweyou.adapter;


import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import in.reweyou.reweyou.Details;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.Signup;
import in.reweyou.reweyou.UserProfile;
import in.reweyou.reweyou.model.UserModel;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<UserModel> mpModelList;
    private Context mContext;
    private DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public UserAdapter(Context context, List<UserModel> mpsList) {
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_row, viewGroup, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(UserAdapter.ViewHolder viewHolder, final int position) {
        String rank = String.valueOf(position+1);
        viewHolder.userName.setText((rank+"       "+mpModelList.get(position).getName()));
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
        final ViewHolder viewHolderFinal = viewHolder;
        imageLoader.displayImage(mpModelList.get(position).getProfilePic(), viewHolder.image, options);
    }


    @Override
    public int getItemCount() {
        return (null != mpModelList ? mpModelList.size() : 0);
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        protected TextView userName;
        protected TextView reviews;
        protected ImageView image;

        public ViewHolder(View view) {
            super(view);
            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fontawesome-webfont.ttf");
            String fontPath = "fonts/Roboto-Regular.ttf";
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), fontPath);
            this.userName = (TextView) view.findViewById(R.id.Who);
            this.userName.setTypeface(tf);
            this.reviews=(TextView)view.findViewById(R.id.Continue);
            this.image=(ImageView)view.findViewById(R.id.image);
            //userName.setTypeface(font);
        }

    }
}
