package in.reweyou.reweyou.adapter;


import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyou.MyProfile;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.model.LikesModel;


public class LikesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final String TAG = LikesAdapter.class.getSimpleName();
    private final Activity mContext;

    private List<LikesModel> messagelist;
    private int selectPosition = 0;
    private boolean flagChange;


    public LikesAdapter(Activity mContext) {
        this.mContext = mContext;
        this.messagelist = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new LikesViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_likes, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        LikesViewHolder likesViewHolder = (LikesViewHolder) viewHolder;
        likesViewHolder.name.setText(messagelist.get(position).getName());
        Glide.with(mContext).load(messagelist.get(position).getProfilepic()).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.download).into(likesViewHolder.profilepic);


    }


    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public void add(List<LikesModel> list) {
        messagelist.addAll(list);
        notifyDataSetChanged();
    }


    private class LikesViewHolder extends RecyclerView.ViewHolder {

        private ImageView profilepic;
        private TextView name;
        private LinearLayout con;

        private LikesViewHolder(View inflate) {
            super(inflate);
            profilepic = (ImageView) inflate.findViewById(R.id.profilepic);
            name = (TextView) inflate.findViewById(R.id.name);
            con = (LinearLayout) inflate.findViewById(R.id.ll);
            con.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MyProfile.class);
                    intent.putExtra("number", messagelist.get(getAdapterPosition()).getCreated_by());
                    mContext.startActivity(intent);
                }
            });


        }


    }


}