package in.reweyou.reweyou.adapter;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import in.reweyou.reweyou.classes.TouchImageView;
import in.reweyou.reweyou.model.UserModel;


public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ViewHolder> {
    private List<UserModel> mpModelList;
    private Context mContext;
    private DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public ActionAdapter(Context context, List<UserModel> mpsList) {
        this.mpModelList = mpsList;
        this.mContext = context;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.action_row, viewGroup, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ActionAdapter.ViewHolder viewHolder, final int position) {

        viewHolder.userName.setText(mpModelList.get(position).getAction());
        viewHolder.name.setText(mpModelList.get(position).getName() + " -");
        viewHolder.time.setText(mpModelList.get(position).getTime().substring(0,12));

        imageLoader.displayImage(mpModelList.get(position).getImage(), viewHolder.image, options);
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("myData", mpModelList.get(position).getImage());
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
        protected TextView name;
        protected TextView time;
        protected TouchImageView image;
        public ViewHolder(View view) {
            super(view);
            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fontawesome-webfont.ttf");

            this.userName = (TextView) view.findViewById(R.id.userName);
            this.name = (TextView) view.findViewById(R.id.name);
            this.time=(TextView)view.findViewById(R.id.time);
            this.image=(TouchImageView)view.findViewById(R.id.image);

            //userName.setTypeface(font);
        }

    }
}
