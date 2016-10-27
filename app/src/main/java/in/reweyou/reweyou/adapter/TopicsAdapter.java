package in.reweyou.reweyou.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.classes.UserSessionManager;

/**
 * Created by master on 27/10/16.
 */

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.ViewHolder> {
    private static String URL_FOLLOW = "https://www.reweyou.in/reweyou/topic.php";
    private final List<String> categoriesList;
    private final UserSessionManager session;
    private final String number;
    private final Context context;


    public TopicsAdapter(List<String> categoriesList, Context context) {
        this.categoriesList = categoriesList;
        this.context = context;
        session = new UserSessionManager(context);
        number = session.getMobileNumber();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_topics, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.checkBox.setText(categoriesList.get(position));
        holder.checkBox.setChecked(session.getFromSP(holder.checkBox.getText().toString()));
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    session.saveInSp(checkBox.getText().toString(), isChecked);
                    if (isChecked) {
                        delete(checkBox.getText().toString());
                    } else {
                        insert(checkBox.getText().toString());
                    }
                    Log.d("isChecked", String.valueOf(isChecked));
                }
            });
        }

        private void insert(final String i) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_FOLLOW,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.trim().equals("success")) {
                                //button.setText("Reviewed");
                                Toast.makeText(context, "Unfollowed", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Try Again", Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("number", number);
                    map.put("topic", i);
                    map.put("unread", "reading");
                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        }

        private void delete(final String i) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_FOLLOW,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.trim().equals("success")) {
                                //button.setText("Reviewed");
                                Toast.makeText(context, "Followed", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Try Again", Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("number", number);
                    map.put("topic", i);
                    map.put("unread", "delete");
                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        }
    }
}
