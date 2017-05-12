package in.reweyou.reweyou.adapter;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyou.CommentActivity;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.model.ThreadModel;

/**
 * Created by master on 1/5/17.
 */

public class FeeedsAdapter extends RecyclerView.Adapter<FeeedsAdapter.BaseViewHolder> {

    private static final int VIEW_TYPE_IMAGE_1 = 21;
    private static final int VIEW_TYPE_IMAGE_2 = 22;
    private static final int VIEW_TYPE_IMAGE_3 = 23;
    private static final int VIEW_TYPE_IMAGE_4 = 24;
    private static final int VIEW_TYPE_TEXT = 25;
    private static final int VIEW_TYPE_LINK = 26;
    private static final int VIEW_TYPE_YOUTUBE_LINK = 27;

    private final Context mContext;
    List<ThreadModel> messagelist;

    public FeeedsAdapter(Context context) {
        this.mContext = context;
        this.messagelist = new ArrayList<>();

    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_IMAGE_1:
                return new Image1ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feeds_image1, parent, false));
            case VIEW_TYPE_IMAGE_2:
                return new Image2ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feeds_image2, parent, false));
            case VIEW_TYPE_IMAGE_3:
                return new Image3ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feeds_image3, parent, false));
            case VIEW_TYPE_IMAGE_4:
                return new Image4ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feeds_image4, parent, false));
            case VIEW_TYPE_TEXT:
                return new TextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feeds_text, parent, false));
            case VIEW_TYPE_LINK:
                return new LinkViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feeds_link, parent, false));
            default:
                return new TextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feeds_text, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {


        holder.description.setText(messagelist.get(position).getDescription());
        holder.date.setText(messagelist.get(position).getTimestamp());
        holder.username.setText(messagelist.get(position).getName());
        holder.commentnum.setText(messagelist.get(position).getComments());
        Glide.with(mContext).load(messagelist.get(position).getProfilepic()).into(holder.profileimage);


        switch (getItemViewType(position)) {
            case VIEW_TYPE_IMAGE_1:
                Image1ViewHolder image1ViewHolder = (Image1ViewHolder) holder;
                onbindimage1(image1ViewHolder, position);
                return;
            case VIEW_TYPE_IMAGE_2:
                Image2ViewHolder image2ViewHolder = (Image2ViewHolder) holder;
                onbindimage2(image2ViewHolder, position);
                return;
            case VIEW_TYPE_IMAGE_3:
                Image3ViewHolder image3ViewHolder = (Image3ViewHolder) holder;
                onbindimage3(image3ViewHolder, position);
                return;
            case VIEW_TYPE_IMAGE_4:
                Image4ViewHolder image4ViewHolder = (Image4ViewHolder) holder;
                onbindimage4(image4ViewHolder, position);
                return;
            case VIEW_TYPE_TEXT:
                return;
            case VIEW_TYPE_LINK:
                LinkViewHolder linkViewHolder = (LinkViewHolder) holder;
                onbindlink(linkViewHolder, position);
                return;

        }
       /* if (messagelist.get(position).getImage().isEmpty())
            forumViewHolder.image.setVisibility(View.GONE);
        else {
            forumViewHolder.image.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(messagelist.get(position).getImage()).into(forumViewHolder.image);
        }*/
    }

    private void onbindlink(LinkViewHolder linkViewHolder, int position) {
        linkViewHolder.linkheadline.setText(messagelist.get(position).getLinkhead());
        linkViewHolder.linkdescription.setText(messagelist.get(position).getLinkdes());
        linkViewHolder.link.setText(messagelist.get(position).getLink());

    }

    private void onbindimage1(Image1ViewHolder image1ViewHolder, int position) {
        Glide.with(mContext).load(messagelist.get(position).getImage1()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image1ViewHolder.image1);
    }

    private void onbindimage2(Image2ViewHolder image2ViewHolder, int position) {
        Glide.with(mContext).load(messagelist.get(position).getImage1()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image2ViewHolder.image1);
        Glide.with(mContext).load(messagelist.get(position).getImage2()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image2ViewHolder.image2);


    }

    private void onbindimage3(Image3ViewHolder image3ViewHolder, int position) {
        Glide.with(mContext).load(messagelist.get(position).getImage1()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image3ViewHolder.image1);
        Glide.with(mContext).load(messagelist.get(position).getImage2()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image3ViewHolder.image2);
        Glide.with(mContext).load(messagelist.get(position).getImage3()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image3ViewHolder.image3);

    }

    private void onbindimage4(Image4ViewHolder image4ViewHolder, int position) {
        Glide.with(mContext).load(messagelist.get(position).getImage1()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image4ViewHolder.image1);
        Glide.with(mContext).load(messagelist.get(position).getImage2()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image4ViewHolder.image2);
        Glide.with(mContext).load(messagelist.get(position).getImage3()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image4ViewHolder.image3);
        Glide.with(mContext).load(messagelist.get(position).getImage4()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image4ViewHolder.image4);


    }

    private void onbindtext() {

    }

    @Override
    public int getItemViewType(int position) {
        String viewty = messagelist.get(position).getType();
        switch (viewty) {
            case "image1":
                return VIEW_TYPE_IMAGE_1;
            case "image2":
                return VIEW_TYPE_IMAGE_2;
            case "image3":
                return VIEW_TYPE_IMAGE_3;
            case "image4":
                return VIEW_TYPE_IMAGE_4;
            case "text":
                return VIEW_TYPE_TEXT;
            case "link":

                return VIEW_TYPE_LINK;

            default:
                return super.getItemViewType(position);

        }
    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public void add(List<ThreadModel> list) {
        messagelist.clear();
        messagelist.addAll(list);
        notifyDataSetChanged();
    }


    public class BaseViewHolder extends RecyclerView.ViewHolder {
        private ImageView profileimage, liketemp, comment, like1, like2, like3;
        private TextView username, likenum, commentnum, likenumber1, likenumber2, likenumber3;
        private TextView date;
        private TextView description;

        public BaseViewHolder(View inflate) {
            super(inflate);

            profileimage = (ImageView) inflate.findViewById(R.id.profilepic);
            comment = (ImageView) inflate.findViewById(R.id.comment);
            like1 = (ImageView) inflate.findViewById(R.id.i1);
            like2 = (ImageView) inflate.findViewById(R.id.i2);
            like3 = (ImageView) inflate.findViewById(R.id.i3);
            likenumber1 = (TextView) inflate.findViewById(R.id.likenumber1);
            likenumber2 = (TextView) inflate.findViewById(R.id.likenumber2);
            likenumber3 = (TextView) inflate.findViewById(R.id.likenumber3);
            description = (TextView) inflate.findViewById(R.id.description);
            username = (TextView) inflate.findViewById(R.id.usernamee);
            date = (TextView) inflate.findViewById(R.id.date);
            commentnum = (TextView) inflate.findViewById(R.id.commentnumber);

            like1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    like1.animate().alpha(1.0f).setDuration(100).rotationBy(360).scaleX(1.0f).scaleY(1.0f).start();
                    like2.setOnClickListener(null);
                    like3.setOnClickListener(null);

                    likenumber1.setAlpha(1.0f);

                }
            });
            like2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    like2.animate().alpha(1.0f).setDuration(100).rotationBy(360).scaleX(1.0f).scaleY(1.0f).start();
                    like1.setOnClickListener(null);
                    like3.setOnClickListener(null);

                    likenumber2.setAlpha(1.0f);

                }
            });
            like3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    like3.animate().alpha(1.0f).setDuration(100).rotationBy(360).scaleX(1.0f).scaleY(1.0f).start();
                    like1.setOnClickListener(null);
                    like2.setOnClickListener(null);

                    likenumber3.setAlpha(1.0f);

                }
            });

            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, CommentActivity.class);
                    i.putExtra("threadid", messagelist.get(getAdapterPosition()).getThreadid());
                    mContext.startActivity(i);
                }
            });

        }
    }


    private class Image1ViewHolder extends BaseViewHolder {
        private ImageView image1, wallpaper;

        public Image1ViewHolder(View inflate) {
            super(inflate);
            image1 = (ImageView) inflate.findViewById(R.id.image1);
            wallpaper = (ImageView) inflate.findViewById(R.id.wallpaper);
            wallpaper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    RotateAnimation rotate = new RotateAnimation(0, 360,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                            0.5f);

                    rotate.setDuration(700);
                    rotate.setInterpolator(new LinearInterpolator());
                    rotate.setRepeatCount(Animation.INFINITE);
                    rotate.setFillAfter(false);
                    wallpaper.startAnimation(rotate);

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int height = displayMetrics.heightPixels;
                    int width = 2 * displayMetrics.widthPixels;
                    Log.d("ddd", "onClick: " + height + "  " + width);
                    Glide.with(mContext).load(messagelist.get(getAdapterPosition()).getImage1()).asBitmap().override(width, height).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    // you can do something with loaded bitmap here

                                    // .....
                                    new WallpaperAsync(resource, wallpaper).execute();


                                }
                            });


                }
            });

        }
    }

    private class Image2ViewHolder extends BaseViewHolder {
        private ImageView image1, image2;

        public Image2ViewHolder(View inflate) {
            super(inflate);
            image1 = (ImageView) inflate.findViewById(R.id.image1);
            image2 = (ImageView) inflate.findViewById(R.id.image2);

        }
    }

    private class Image3ViewHolder extends BaseViewHolder {
        private ImageView image1, image2, image3;

        public Image3ViewHolder(View inflate) {
            super(inflate);
            image1 = (ImageView) inflate.findViewById(R.id.image1);
            image2 = (ImageView) inflate.findViewById(R.id.image2);
            image3 = (ImageView) inflate.findViewById(R.id.image3);

        }
    }

    private class Image4ViewHolder extends BaseViewHolder {
        private ImageView image1, image2, image3, image4;

        public Image4ViewHolder(View inflate) {
            super(inflate);
            image1 = (ImageView) inflate.findViewById(R.id.image1);
            image2 = (ImageView) inflate.findViewById(R.id.image2);
            image3 = (ImageView) inflate.findViewById(R.id.image3);
            image4 = (ImageView) inflate.findViewById(R.id.image4);

        }
    }

    private class TextViewHolder extends BaseViewHolder {

        public TextViewHolder(View inflate) {
            super(inflate);
        }
    }

    private class LinkViewHolder extends BaseViewHolder {
        private TextView linkheadline, linkdescription, link;
        private RelativeLayout container;

        public LinkViewHolder(View inflate) {
            super(inflate);
            link = (TextView) inflate.findViewById(R.id.linklink);
            linkheadline = (TextView) inflate.findViewById(R.id.headlinelink);
            linkdescription = (TextView) inflate.findViewById(R.id.descriptionlink);
            container = (RelativeLayout) inflate.findViewById(R.id.rlcont);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    private class WallpaperAsync extends AsyncTask<Void, Void, Void> {
        private final Bitmap adapterPosition;
        private final ImageView wallpaper;

        public WallpaperAsync(Bitmap adapterPosition, ImageView wallpaper) {
            this.adapterPosition = adapterPosition;
            this.wallpaper = wallpaper;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            wallpaper.clearAnimation();
            Toast.makeText(mContext, "wallpaper set", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected Void doInBackground(Void... params) {

            WallpaperManager myWallpaperManager
                    = WallpaperManager.getInstance(mContext.getApplicationContext());
            try {
                myWallpaperManager.setBitmap(adapterPosition);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }
}
