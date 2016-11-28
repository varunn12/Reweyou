package in.reweyou.reweyou;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import in.reweyou.reweyou.adapter.ActionAdapter;
import in.reweyou.reweyou.classes.RequestHandler;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.LeaderboardModel;

public class Action extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    public static final String UPLOAD_URL = "https://www.reweyou.in/reweyou/actions.php";
    public static final String KEY_TEXT = "headline";
    public static final String KEY_NAME = "name";
    public static final String KEY_TIME = "time";
    SwipeRefreshLayout swipeLayout;
    UserSessionManager session;
    private RecyclerView recyclerView;
    private Button button;
    private EditText editText;
    private List<LeaderboardModel> mpModelList;
    private ActionAdapter adapter;
    private Toolbar toolbar;
    private String name;
    private String result;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Actions");
        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        name = user.get(UserSessionManager.KEY_NAME);

        editText = (EditText)findViewById(R.id.Who);
        button=(Button)findViewById(R.id.btn_send);
        button.setTypeface(font);
        button.setOnClickListener(this);

        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(Action.this));
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);

        swipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        progressBar.setVisibility(View.VISIBLE);
        new JSONTask().execute();

    }

    @Override
    public void onRefresh() {
        new JSONTask().execute();
    }

    @Override
    public void onClick(View v) {
        uploadText();
    }

    public void uploadText() {
        final String text = editText.getText().toString().trim();
        String format = "dd-MMM-yyyy hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        final String timeStamp = sdf.format(new Date());
        if (editText.getText().toString().trim().equals("")) {
            editText.setError("Required!");

            // editTextUsername.setHint("Enter Email");
        } else {
            class UploadText extends AsyncTask<Void, Void, String> {
                ProgressDialog loading;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    loading = ProgressDialog.show(Action.this, "Please wait...", "uploading", false, false);
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    loading.dismiss();
                    if (s.trim().equals("Successfully Uploaded")) {
                        onRefresh();
                        editText.setText("");
                    } else {
                        Toast.makeText(Action.this, "Try Again", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                protected String doInBackground(Void... params) {
                    RequestHandler rh = new RequestHandler();
                    HashMap<String, String> param = new HashMap<String, String>();
                    param.put(KEY_TEXT, text);
                    param.put(KEY_NAME, name);
                    param.put(KEY_TIME, timeStamp);
                    result = rh.sendPostRequest(UPLOAD_URL, param);
                    return result;
                }
            }
            UploadText u = new UploadText();
            u.execute();
        }
    }

    public class JSONTask extends AsyncTask<String, String, List<LeaderboardModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected List<LeaderboardModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            RequestHandler rh = new RequestHandler();
            HashMap<String, String> data = new HashMap<String, String>();
            // data.put("query",params[0]);
            try {
                URL url = new URL("https://www.reweyou.in/reweyou/actions_list.php");
                connection = (HttpURLConnection) url.openConnection();

                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                //   OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                // wr.write(rh.getPostDataString(data));
                //wr.flush();

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

                List<LeaderboardModel> mpModelList = new ArrayList<>();

                Gson gson = new Gson();
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    LeaderboardModel mpModel = gson.fromJson(finalObject.toString(), LeaderboardModel.class);
                    mpModelList.add(mpModel);
                }

                return mpModelList;

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
        protected void onPostExecute(List<LeaderboardModel> result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            ActionAdapter adapter = new ActionAdapter(Action.this, result);
            recyclerView.setAdapter(adapter);
            swipeLayout.setRefreshing(false);
            //need to set data to the list
        }
    }
}
