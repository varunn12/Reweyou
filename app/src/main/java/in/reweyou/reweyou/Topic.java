package in.reweyou.reweyou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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

public class Topic extends AppCompatActivity implements View.OnClickListener {

    Button save;
    UserSessionManager session;
    List<String> categoriesList = new ArrayList<>();
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        session = new UserSessionManager(getApplicationContext());


        jsonRequest();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Feed Settings");

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);
    }

    private void jsonRequest() {
        final String url = "https://www.reweyou.in/reweyou/topiclist.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response", response.toString());

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
                Log.d("Error.Response", error.getMessage());
            }
        });

        queue.add(jsonArrayRequest);
    }

    private void updateUI() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TopicsAdapter(categoriesList, Topic.this));
    }


    @Override
    public void onClick(View v) {
        Intent feed = new Intent(Topic.this, Feed.class);
        startActivity(feed);
    }


}
