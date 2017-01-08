package in.reweyou.reweyou.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.reweyou.reweyou.Notifications;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.SinglePostActivity;
import in.reweyou.reweyou.customView.CircularImageView;
import in.reweyou.reweyou.model.NotificationCommentsModel;
import in.reweyou.reweyou.model.NotificationLikesModel;

import static in.reweyou.reweyou.utils.Constants.URL_NOTI_READ_STATUS;

/**
 * Created by master on 25/11/16.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Notifications context;
    private List<Object> list;

    public NotificationAdapter(Notifications notifications) {
        this.context = notifications;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notify_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (list.get(position) instanceof NotificationLikesModel) {
            holder.who.setText(((NotificationLikesModel) list.get(position)).getReviewer_name());
            holder.Continue.setText(" likes your report.");
            holder.time.setText(((NotificationLikesModel) list.get(position)).getFormattedTime());
            Glide.with(context).load(((NotificationLikesModel) list.get(position)).getProfilepic()).error(R.drawable.download).into(holder.image);
            if (((NotificationLikesModel) list.get(position)).getReadstatus().equals("false"))
                holder.rv.setBackgroundResource(R.drawable.noti_back);
            else
                holder.rv.setBackgroundResource(R.drawable.noti_back_tint);


        } else if (list.get(position) instanceof NotificationCommentsModel) {
            holder.who.setText(((NotificationCommentsModel) list.get(position)).getReviewer_name());
            holder.Continue.setText(" reacted on your report.");
            holder.time.setText(((NotificationCommentsModel) list.get(position)).getFormattedTime());
            Glide.with(context).load(((NotificationCommentsModel) list.get(position)).getProfilepic()).error(R.drawable.download).into(holder.image);
            if (((NotificationCommentsModel) list.get(position)).getReadstatus().equals("false"))
                holder.rv.setBackgroundResource(R.drawable.noti_back);
            else holder.rv.setBackgroundResource(R.drawable.noti_back_tint);

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void add(List<Object> list) {
        this.list = list;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView who;
        private TextView time;
        private TextView Continue;
        private CircularImageView image;
        private RelativeLayout rv;

        public ViewHolder(View itemView) {
            super(itemView);

            who = (TextView) itemView.findViewById(R.id.Who);
            time = (TextView) itemView.findViewById(R.id.time);
            Continue = (TextView) itemView.findViewById(R.id.Continue);
            image = (CircularImageView) itemView.findViewById(R.id.image);
            rv = (RelativeLayout) itemView.findViewById(R.id.rv);


            rv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("clicked", "clidked");
                    makeRequestforReadchange(getAdapterPosition());


                    Bundle bundle = new Bundle();
                    if (list.get(getAdapterPosition()) instanceof NotificationLikesModel)
                        bundle.putString("postid", ((NotificationLikesModel) list.get(getAdapterPosition())).getPostid());
                    else if (list.get(getAdapterPosition()) instanceof NotificationCommentsModel)
                        bundle.putString("postid", ((NotificationCommentsModel) list.get(getAdapterPosition())).getPostid());
                    Intent resultIntent = new Intent(context, SinglePostActivity.class);
                    resultIntent.putExtra("fromnoti", true);
                    resultIntent.putExtras(bundle);
                    context.startActivity(resultIntent);


                }
            });
        }

        private void makeRequestforReadchange(final int adapterPosition) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_NOTI_READ_STATUS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("ResponseLike", response);
                            if (response.equals("true ")) {


                                if (list.get(adapterPosition) instanceof NotificationLikesModel) {
                                    ((NotificationLikesModel) list.get(getAdapterPosition())).setReadstatus("true");
                                    notifyItemChanged(getAdapterPosition());
                                } else if (list.get(adapterPosition) instanceof NotificationCommentsModel) {
                                    ((NotificationCommentsModel) list.get(getAdapterPosition())).setReadstatus("true");
                                    notifyItemChanged(getAdapterPosition());
                                }
                            }

                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                        Log.d("ResponseLike", error.getMessage());


                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> data = new HashMap<>();

                    if (list.get(getAdapterPosition()) instanceof NotificationLikesModel) {
                        data.put("notid", ((NotificationLikesModel) list.get(getAdapterPosition())).getNotid());
                        data.put("type", "like");
                        Log.d("reach", ((NotificationLikesModel) list.get(getAdapterPosition())).getNotid());
                    } else if (list.get(getAdapterPosition()) instanceof NotificationCommentsModel) {
                        data.put("notid", ((NotificationCommentsModel) list.get(getAdapterPosition())).getNotid());
                        data.put("type", "comment");
                        Log.d("reach", ((NotificationCommentsModel) list.get(getAdapterPosition())).getNotid());

                    }
                    return data;
                }
            };


            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        }
    }
}
