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
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.reweyou.reweyou.adapter.TopicsAdapter;
import in.reweyou.reweyou.classes.UserSessionManager;

public class Topic extends AppCompatActivity implements View.OnClickListener {
    private static String URL_FOLLOW = "https://www.reweyou.in/reweyou/topic.php";
    CheckBox National, Technology, International, Sports, SocialMedia, Business, Opinion;
    Button save;
    UserSessionManager session;
    boolean n, i, s, b, t, sm, o;
    List<String> categoriesList = new ArrayList<>();
    private String number;
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

        number = session.getMobileNumber();
/*

        National = (CheckBox) findViewById(R.id.National);
        National.setChecked(session.getFromSP("n"));
        National.setOnCheckedChangeListener(this);
        n = session.getFromSP("n");

        International = (CheckBox) findViewById(R.id.International);
        International.setChecked(session.getFromSP("i"));
        International.setOnCheckedChangeListener(this);
        i = session.getFromSP("i");

        Sports = (CheckBox) findViewById(R.id.Sports);
        Sports.setChecked(session.getFromSP("s"));
        Sports.setOnCheckedChangeListener(this);
        s = session.getFromSP("s");

        Business = (CheckBox) findViewById(R.id.Business);
        Business.setChecked(session.getFromSP("b"));
        Business.setOnCheckedChangeListener(this);
        b = session.getFromSP("b");

        SocialMedia = (CheckBox) findViewById(R.id.SocialMedia);
        SocialMedia.setChecked(session.getFromSP("sm"));
        SocialMedia.setOnCheckedChangeListener(this);
        sm = session.getFromSP("sm");

        Opinion = (CheckBox) findViewById(R.id.Opinion);
        Opinion.setChecked(session.getFromSP("o"));
        Opinion.setOnCheckedChangeListener(this);
        o = session.getFromSP("o");

        Technology = (CheckBox) findViewById(R.id.Technology);
        Technology.setChecked(session.getFromSP("t"));
        Technology.setOnCheckedChangeListener(this);
        t = session.getFromSP("t");
*/

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);
    }

    private void jsonRequest() {
        final String url = "https://www.reweyou.in/reweyou/topiclist.php";
        RequestQueue queue = Volley.newRequestQueue(this); // this = context
/*// prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        );*/

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

// add it to the RequestQueue
        queue.add(jsonArrayRequest);
    }

    private void updateUI() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TopicsAdapter(categoriesList, Topic.this));
    }

  /*  @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.National:
                session.saveInSp("n", isChecked);
                if (National.isChecked()) {
                    delete("National");
                } else {
                    insert("National");
                }
                break;
            case R.id.International:
                session.saveInSp("i", isChecked);
                if (International.isChecked()) {
                    delete("International");
                } else {
                    insert("International");
                }
                break;

            case R.id.Sports:
                session.saveInSp("s", isChecked);
                if (Sports.isChecked()) {
                    delete("Sports");
                } else {
                    insert("Sports");
                }
                break;

            case R.id.Business:
                session.saveInSp("b", isChecked);
                if (Business.isChecked()) {
                    delete("Business");
                } else {
                    insert("Business");
                }
                break;
            case R.id.Technology:
                session.saveInSp("t", isChecked);
                if (Technology.isChecked()) {
                    delete("Technology");
                } else {
                    insert("Technology");
                }
                break;

            case R.id.Opinion:
                session.saveInSp("o", isChecked);
                if (Opinion.isChecked()) {
                    delete("Opinion");
                } else {
                    insert("Opinion");
                }
                break;

            case R.id.SocialMedia:
                session.saveInSp("sm", isChecked);
                if (SocialMedia.isChecked()) {
                    delete("Social Media");
                } else {
                    insert("Social Media");
                }
                break;
        }
    }
*/

    @Override
    public void onClick(View v) {
        Intent feed = new Intent(Topic.this, Feed.class);
        startActivity(feed);
    }

    private void insert(final String i) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_FOLLOW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("success")) {
                            //button.setText("Reviewed");
                            Toast.makeText(Topic.this, "Unfollowed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Topic.this, "Try Again", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Topic.this, error.toString(), Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(Topic.this);
        requestQueue.add(stringRequest);
    }

    private void delete(final String i) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_FOLLOW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("success")) {
                            //button.setText("Reviewed");
                            Toast.makeText(Topic.this, "Followed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Topic.this, "Try Again", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Topic.this, error.toString(), Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(Topic.this);
        requestQueue.add(stringRequest);
    }
}
