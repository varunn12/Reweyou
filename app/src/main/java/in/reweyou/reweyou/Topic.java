package in.reweyou.reweyou;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyou.adapter.TopicsAdapter;
import in.reweyou.reweyou.classes.UserSessionManager;

public class Topic extends AppCompatActivity {

    Button save;
    UserSessionManager session;
    List<String> categoriesList = new ArrayList<>();
    private Toolbar toolbar;
    private ProgressBar pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        session = new UserSessionManager(getApplicationContext());
        pd = (ProgressBar) findViewById(R.id.pd);

        jsonRequest();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Feed Settings");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void jsonRequest() {
        pd.setVisibility(View.VISIBLE);
        final String url = "https://www.reweyou.in/reweyou/topiclist.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response", response.toString());
                pd.setVisibility(View.GONE);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        categoriesList.add(jsonObject.getString("topics"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("Categories", String.valueOf(categoriesList));
                updateUI();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.setVisibility(View.GONE);
                Log.d("Error.Response", error.getMessage());
                if (error instanceof NoConnectionError) {
                    showSnackBar("no internet connectivity");
                } else if (error instanceof TimeoutError) {
                    showSnackBar("poor internet connectivity");
                } else if (error instanceof NetworkError || error instanceof ParseError || error instanceof ServerError) {
                    showSnackBar("something went wrong");
                }
            }
        });

        queue.add(jsonArrayRequest);
    }

    private void showSnackBar(String msg) {
        Snackbar.make(findViewById(R.id.coordinatorLayout), msg, Snackbar.LENGTH_LONG).setDuration(Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        jsonRequest();
                    }
                }).show();
    }

    private void updateUI() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TopicsAdapter(categoriesList, Topic.this));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
