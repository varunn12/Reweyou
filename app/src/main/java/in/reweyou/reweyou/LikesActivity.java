package in.reweyou.reweyou;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.reweyou.reweyou.adapter.LikesAdapter;
import in.reweyou.reweyou.model.LikesModel;
import in.reweyou.reweyou.utils.Constants;

public class LikesActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    private String postid;
    private List<LikesModel> list;
    private ProgressBar pd;
    private TextView tv;
    private TextView toolbartext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.activity_post_report_raw);
        pd = (ProgressBar) findViewById(R.id.pd);
        tv = (TextView) findViewById(R.id.tv);
        toolbartext = (TextView) findViewById(R.id.tool);
        postid = getIntent().getStringExtra("postid");

        recyclerview = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        makeRequest();
    }

    private void makeRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.FEED_LIKES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);
                        if (response != null) {
                            pd.setVisibility(View.GONE);
                            try {

                                list = new ArrayList<>();
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Gson gson = new Gson();
                                    LikesModel likesModel = gson.fromJson(jsonObject.toString(), LikesModel.class);
                                    list.add(likesModel);
                                }
                                toolbartext.setText("Likes " + "(" + jsonArray.length() + ")");

                                recyclerview.setAdapter(new LikesAdapter(LikesActivity.this, list));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("called", "called");
                        tv.setVisibility(View.VISIBLE);
                        pd.setVisibility(View.GONE);

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("postid", postid);


                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(LikesActivity.this);
        requestQueue.add(stringRequest);
    }

}
