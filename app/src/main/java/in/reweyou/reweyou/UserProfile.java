package in.reweyou.reweyou;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.RequestHandler;
import in.reweyou.reweyou.classes.UserSessionManager;

import static in.reweyou.reweyou.utils.Constants.USER_PROFILE_URL_FOLLOW;
import static in.reweyou.reweyou.utils.Constants.USER_PROFILE_URL_VERIFY_FOLLOW;

public class UserProfile extends AppCompatActivity implements View.OnClickListener {
    public static String userprofilenumber;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    UserSessionManager session;
    ArrayList<String> profilelist = new ArrayList<>();
    private String i, tag, number, user, result;
    private TextView Name, Reports, Info, Readers;
    private ImageView profilepic;
    private LinearLayout click;
    private int length;
    private Button button;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView viewreport;
    private boolean dataloaded;
    private String name;
    private String usernumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.activity_user_profile);


        //initCollapsingToolbar();
        Bundle bundle = getIntent().getExtras();
        i = bundle.getString("myData");
        cd = new ConnectionDetector(UserProfile.this);
        session = new UserSessionManager(getApplicationContext());

        viewreport = (TextView) findViewById(R.id.viewreport);
        Name = (TextView) findViewById(R.id.Name);
        Reports = (TextView) findViewById(R.id.Reports);
        Info = (TextView) findViewById(R.id.Info);
        Readers = (TextView) findViewById(R.id.Readers);
        Readers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("myData", user);
                Intent in = new Intent(UserProfile.this, Readers.class);
                in.putExtras(bundle);
                startActivity(in);
            }
        });

        button = (Button) findViewById(R.id.button);
        profilepic = (ImageView) findViewById(R.id.profilepic);

        //Progress bar
        button.setOnClickListener(this);

        new JSONTask().execute(i);
        // new JSONTasks().execute(tag, i);
        button(i);

        viewreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataloaded) {
                    Intent i = new Intent(UserProfile.this, SearchResultsActivity.class);
                    i.putExtra("position", 29);
                    userprofilenumber = usernumber;
                    i.putExtra("query", name);
                    startActivity(i);
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                if (dataloaded)
                    reading(i);
                break;

        }
    }

    private void button(final String i) {
        final String number = session.getMobileNumber();
        // final ProgressDialog loading = ProgressDialog.show(this, "Authenticating", "Please wait", false, false);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                USER_PROFILE_URL_VERIFY_FOLLOW, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //if the server response is success
                if (response.equalsIgnoreCase("success")) {
                    //dismissing the progressbar
                    //     loading.show();

                    //Starting a new activity
                    button.setVisibility(View.VISIBLE);
                    button.setTextColor(ContextCompat.getColor(UserProfile.this, android.R.color.white));
                    button.setBackgroundResource(R.drawable.button_background_rectangular_red);
                    button.setText("Unread");
                    button.setTag(1);
                } else {
                    //Displaying a toast if the otp entered is wrong
                    button.setVisibility(View.VISIBLE);
                    button.setTextColor(ContextCompat.getColor(UserProfile.this, R.color.colorPrimaryDark));
                    button.setBackgroundResource(R.drawable.button_background_rectangular);
                    button.setText("Read");
                    button.setTag(0);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Couldn't fetch data", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user", i);
                params.put("number", number);

                return params;
            }
        };
        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }

    private void reading(final String i) {
        HashMap<String, String> user = session.getUserDetails();
        final String number = session.getMobileNumber();
        final String name = session.getUsername();
        final ProgressDialog loading = ProgressDialog.show(UserProfile.this, "Updating", "Please wait", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, USER_PROFILE_URL_FOLLOW,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("userw", response);
                        loading.dismiss();

                        if (response.trim().equals("unread")) {
                            button.setTextColor(ContextCompat.getColor(UserProfile.this, R.color.colorPrimaryDark));
                            button.setBackgroundResource(R.drawable.button_background_rectangular);
                            button.setText("Read");
                            Readers.setText("" + (Integer.parseInt(Readers.getText().toString()) - 1));


                        } else if (response.trim().equals("read")) {
                            button.setTextColor(ContextCompat.getColor(UserProfile.this, android.R.color.white));
                            button.setBackgroundResource(R.drawable.button_background_rectangular_red);
                            button.setText("Unread");
                            Readers.setText("" + (Integer.parseInt(Readers.getText().toString()) + 1));

                        } else
                            Toast.makeText(UserProfile.this, "Something went wrong!", Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(UserProfile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("number", number);
                map.put("user", i);
                map.put("name", name);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(UserProfile.this);
        requestQueue.add(stringRequest);
    }


    public class JSONTask extends AsyncTask<String, String, List<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected List<String> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            RequestHandler rh = new RequestHandler();
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("number", params[0]);
            try {
                URL url = new URL("https://www.reweyou.in/reweyou/user_list.php");
                connection = (HttpURLConnection) url.openConnection();

                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(rh.getPostDataString(data));
                wr.flush();


                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finalJson = buffer.toString();

                JSONArray parentArray = new JSONArray(finalJson);
                StringBuffer finalBufferedData = new StringBuffer();

                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    profilelist.add(finalObject.getString("name"));
                    name = finalObject.getString("name");
                    usernumber = finalObject.getString("number");
                    profilelist.add(finalObject.getString("total_reviews"));
                    profilelist.add(finalObject.getString("profilepic"));
                    profilelist.add(finalObject.getString("info"));
                    profilelist.add(finalObject.getString("number"));
                    profilelist.add(finalObject.getString("readers"));
                }

                dataloaded = true;
                return profilelist;

                //return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);
            try {
                if (result != null) {
                    if (!result.isEmpty()) {
                        Name.setText(result.get(0));
                        Reports.setText(result.get(1));
                        Info.setText(result.get(3));

                        Glide.with(UserProfile.this).load(result.get(2)).dontAnimate().error(R.drawable.download).into(profilepic);
                        user = result.get(4);
                        Readers.setText(result.get(5));
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Couldn't fetch data", Toast.LENGTH_LONG).show();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //    progressBar.setVisibility(View.GONE);
            //need to set data to the list
        }
    }


}
