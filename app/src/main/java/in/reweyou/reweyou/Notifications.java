package in.reweyou.reweyou;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.reweyou.reweyou.adapter.NotificationAdapter;
import in.reweyou.reweyou.classes.DividerItemDecoration;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.NotificationCommentsModel;
import in.reweyou.reweyou.model.NotificationLikesModel;
import in.reweyou.reweyou.utils.Constants;

import static in.reweyou.reweyou.utils.Constants.URL_NOTI_READ_STATUS;

public class Notifications extends AppCompatActivity {


    UserSessionManager session;
    private Toolbar toolbar;
    private List<Object> list;
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private boolean dataLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        list = new ArrayList<>();
        session = new UserSessionManager(Notifications.this);
        initToolbar();

        initRecyclerView();

        makeRequest();
    }

    private void makeRequest() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Loading! Please wait...");
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.MY_NOTIFICATIONS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d("response", response);
                        if (response.trim().equals(Constants.AUTH_ERROR)) {
                            session.logoutUser();
                        } else {

                            try {
                                JSONArray jsonArray = new JSONArray(response);

                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                if (jsonObject.has("likes")) {
                                    JSONArray jsonArray1 = jsonObject.getJSONArray("likes");
                                    for (int i = 0; i < jsonArray1.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                        Gson gson = new Gson();
                                        NotificationLikesModel notificationLikesModel = gson.fromJson(jsonObject1.toString(), NotificationLikesModel.class);
                                        list.add(notificationLikesModel);
                                    }


                                }

                                if (jsonObject.has("comments")) {
                                    JSONArray jsonArray1 = jsonObject.getJSONArray("comments");
                                    for (int i = 0; i < jsonArray1.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                        Gson gson = new Gson();
                                        NotificationCommentsModel notificationCommentsModel = gson.fromJson(jsonObject1.toString(), NotificationCommentsModel.class);
                                        list.add(notificationCommentsModel);
                                    }

                                }

                                for (int i = 0; i < list.size(); i++)
                                    Log.d("list", String.valueOf(list.get(i)));


                                Collections.sort(list, new Comparator<Object>() {
                                    public int compare(Object o1, Object o2) {
                                        if (o1 instanceof NotificationCommentsModel && o2 instanceof NotificationCommentsModel)
                                            return ((NotificationCommentsModel) o1).getTime().compareTo(((NotificationCommentsModel) o2).getTime());
                                        else if (o1 instanceof NotificationCommentsModel && o2 instanceof NotificationLikesModel)
                                            return ((NotificationCommentsModel) o1).getTime().compareTo(((NotificationLikesModel) o2).getTime());
                                        else if (o1 instanceof NotificationLikesModel && o2 instanceof NotificationCommentsModel)
                                            return ((NotificationLikesModel) o1).getTime().compareTo(((NotificationCommentsModel) o2).getTime());
                                        else if (o1 instanceof NotificationLikesModel && o2 instanceof NotificationLikesModel)
                                            return ((NotificationLikesModel) o1).getTime().compareTo(((NotificationLikesModel) o2).getTime());
                                        else return 0;
                                    }
                                });

                                Collections.reverse(list);


                                for (int i = 0; i < list.size(); i++)
                                    Log.d("list", String.valueOf(list.get(i)));


                                notificationAdapter.add(list);
                                recyclerView.setAdapter(notificationAdapter);
                                dataLoaded = true;

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        Log.d("Error.Response", error.getMessage());
                        if (error instanceof NoConnectionError) {
                            showSnackBar("no internet connectivity");
                        } else if (error instanceof TimeoutError) {
                            showSnackBar("poor internet connectivity");
                        } else if (error instanceof NetworkError || error instanceof ParseError || error instanceof ServerError) {
                            showSnackBar("something went wrong");
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("number", session.getMobileNumber());
              /*  map.put("token", session.getKeyAuthToken());
                map.put("deviceid", session.getDeviceid());*/
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Notifications.this);
        requestQueue.add(stringRequest);
    }

    private void showSnackBar(String msg) {
        Snackbar.make(findViewById(R.id.coordinatorLayout), msg, Snackbar.LENGTH_LONG).setDuration(Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeRequest();
                    }
                }).show();
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(Notifications.this));
        notificationAdapter = new NotificationAdapter(Notifications.this);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(ContextCompat.getDrawable(Notifications.this, R.drawable.line2));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Notifications");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void makeRequestforallReadchange() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Please wait...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_NOTI_READ_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d("ResponseLike", response);
                        if (response.equals("all")) {
                            if (notificationAdapter != null)
                                notificationAdapter.notifyDataSetChanged();
                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        Log.d("Error.Response", error.getMessage());
                        if (error instanceof NoConnectionError) {
                            showToast("no internet connectivity");
                        } else if (error instanceof TimeoutError) {
                            showToast("poor internet connectivity");
                        } else if (error instanceof NetworkError || error instanceof ParseError || error instanceof ServerError) {
                            showToast("something went wrong");
                        }


                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();

                data.put("mark", "all");
                data.put("number", session.getMobileNumber());
                return data;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(Notifications.this);
        requestQueue.add(stringRequest);
    }

    private void showToast(String s) {
        Toast.makeText(Notifications.this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_noti, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_done:
                if (dataLoaded) {
                    if (list != null) {
                        makeRequestforallReadchange();
                    }
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);

    }
}


