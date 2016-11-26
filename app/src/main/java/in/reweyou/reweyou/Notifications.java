package in.reweyou.reweyou;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import in.reweyou.reweyou.adapter.NotificationAdapter;
import in.reweyou.reweyou.classes.UserSessionManager;

import static in.reweyou.reweyou.utils.Constants.MY_PROFILE_URL_FOLLOW;

public class Notifications extends AppCompatActivity {


    UserSessionManager session;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        initToolbar();

        initRecyclerView();

        makeRequest();
    }

    private void makeRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MY_PROFILE_URL_FOLLOW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("resppnse", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
               /* map.put("number", number);
                map.put("user", i);
                map.put("unread", "reading");*/
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Notifications.this);
        requestQueue.add(stringRequest);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(Notifications.this));
        NotificationAdapter notificationAdapter = new NotificationAdapter();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Notifications");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

}


