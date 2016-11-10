package in.reweyou.reweyou.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import in.reweyou.reweyou.FullImage;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.UserProfile;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.CustomTabActivityHelper;
import in.reweyou.reweyou.classes.CustomTabsOnClickListener;
import in.reweyou.reweyou.classes.TouchImageView;
import in.reweyou.reweyou.classes.Util;
import in.reweyou.reweyou.model.CommentsModel;


public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    Boolean isInternetPresent = false;
    Activity activity;
    CustomTabActivityHelper mCustomTabActivityHelper;
    ConnectionDetector cd;
    ImageLoader imageLoader = ImageLoader.getInstance();
    private List<CommentsModel> mpModelList;
    private Context mContext;
    private DisplayImageOptions options;

    public CommentsAdapter(Context context, List<CommentsModel> mpsList) {
        activity = (Activity) context;
        mCustomTabActivityHelper = new CustomTabActivityHelper();

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

       /* if (position % 2 == 0) {
            viewHolder.cv.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.randoms));
        } else {
            // set the other color

        }
*/
        Spannable spannable = new SpannableString(mpModelList.get(position).getComments());
        Util.linkifyUrl(spannable, new CustomTabsOnClickListener(activity, mCustomTabActivityHelper));
        viewHolder.userName.setText(spannable);
        viewHolder.userName.setMovementMethod(LinkMovementMethod.getInstance());

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
                } else {
                    Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewHolder.time.setText(mpModelList.get(position).getTime().substring(0, 12));

        Glide.with(mContext).load(mpModelList.get(position).getProfilepic()).placeholder(R.drawable.download).error(R.drawable.download).fallback(R.drawable.download).dontAnimate().into(viewHolder.profilepic);

        imageLoader.displayImage(mpModelList.get(position).getR_Image(), viewHolder.image, options);

        if (mpModelList.get(position).getR_Image().isEmpty() || mpModelList.get(position).getR_Image() == null) {
            viewHolder.image.setVisibility(View.GONE);
        } else {
            viewHolder.image.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(mpModelList.get(position).getR_Image()).into(viewHolder.image);

        }
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


    class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView userName;
        protected CardView cv;
        protected TextView name;
        protected TextView time;
        protected TouchImageView image;
        protected ImageView profilepic;

        public ViewHolder(View view) {
            super(view);

            this.userName = (TextView) view.findViewById(R.id.userName);
            this.name = (TextView) view.findViewById(R.id.name);
            this.time = (TextView) view.findViewById(R.id.time);
            this.cv = (CardView) view.findViewById(R.id.cv);
            this.image = (TouchImageView) view.findViewById(R.id.image);
            this.profilepic = (ImageView) view.findViewById(R.id.profilepic);
            //userName.setTypeface(font);
        }

    }

}
