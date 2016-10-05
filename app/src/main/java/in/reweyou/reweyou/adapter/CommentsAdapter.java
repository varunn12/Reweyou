package in.reweyou.reweyou.adapter;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import in.reweyou.reweyou.Details;
import in.reweyou.reweyou.FullImage;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.Signup;
import in.reweyou.reweyou.UserProfile;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.RealPathUtil;
import in.reweyou.reweyou.classes.TouchImageView;
import in.reweyou.reweyou.model.CommentsModel;


public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private List<CommentsModel> mpModelList;
    private Context mContext;
    private DisplayImageOptions options;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public CommentsAdapter(Context context, List<CommentsModel> mpsList) {
        this.mpModelList = mpsList;
        this.mContext = context;
        cd = new ConnectionDetector(mContext);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.action_row, viewGroup, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CommentsAdapter.ViewHolder viewHolder, final int position) {

        if(position % 2 == 0) {
            viewHolder.cv.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.randoms));
        } else {
            // set the other color

        }
        viewHolder.userName.setText(mpModelList.get(position).getComments());
        viewHolder.name.setText(mpModelList.get(position).getReviewer_Name());
        viewHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Bundle bundle = new Bundle();
                    bundle.putString("myData", mpModelList.get(position).getReviewer());
                    Intent in = new Intent(mContext, UserProfile.class);
                    in.putExtras(bundle);
                    mContext.startActivity(in);
                }
                else
                {
                    Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewHolder.time.setText(mpModelList.get(position).getTime().substring(0,12));



        imageLoader.displayImage(mpModelList.get(position).getR_Image(), viewHolder.image, options);
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("myData", mpModelList.get(position).getR_Image());
                Intent in = new Intent(mContext, FullImage.class);
                in.putExtras(bundle);
                mContext.startActivity(in);
                //    showImage(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return (null != mpModelList ? mpModelList.size() : 0);
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        protected TextView userName;
        protected CardView cv;
        protected TextView name;
        protected TextView time;
        protected TouchImageView image;
        public ViewHolder(View view) {
            super(view);
            String fontPath = "fonts/Roboto-Medium.ttf";
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), fontPath);
            String thinpath="fonts/Roboto-Regular.ttf";
            Typeface thin = Typeface.createFromAsset(mContext.getAssets(), thinpath);
            this.userName = (TextView) view.findViewById(R.id.userName);
            this.userName.setTypeface(thin);
            this.name = (TextView) view.findViewById(R.id.name);
            this.name.setTypeface(tf);
            this.time=(TextView)view.findViewById(R.id.time);
            this.time.setTypeface(thin);
            this.cv=(CardView) view.findViewById(R.id.cv);
            this.image=(TouchImageView)view.findViewById(R.id.image);
            //userName.setTypeface(font);
        }

    }

}
