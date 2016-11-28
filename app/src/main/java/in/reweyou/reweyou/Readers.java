package in.reweyou.reweyou;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.reweyou.reweyou.adapter.ReadersAdapter;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.DividerItemDecoration;
import in.reweyou.reweyou.classes.RequestHandler;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.LeaderboardModel;

public class Readers extends AppCompatActivity {
UserSessionManager session;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private ProgressBar progressBar;
    private ReadersAdapter adapter;
    private RecyclerView recyclerView;
    private String i;
    private List<LeaderboardModel> messagelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readers);
        session = new UserSessionManager(Readers.this);
        Intent in=getIntent();
        cd = new ConnectionDetector(Readers.this);
        Bundle bundle = getIntent().getExtras();
        i = bundle.getString("myData");
        Log.e("Intent",i);

        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.line));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        isInternetPresent = cd.isConnectingToInternet();
        if(isInternetPresent) {
            new JSONTask().execute(i);
            adapter=new ReadersAdapter(Readers.this,messagelist);
            recyclerView.setAdapter(adapter);
        }
        else {
            Toast.makeText(this, "You are not connected to Internet", Toast.LENGTH_LONG).show();
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
            RequestHandler rh= new RequestHandler();
            HashMap<String, String> data = new HashMap<String,String>();
            //  data.put("tag",params[0]);
             data.put("number",params[0]);
            //  tag="All";
            try {
                URL url = new URL("https://www.reweyou.in/reweyou/readers.php");
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

                List<LeaderboardModel> messagelist = new ArrayList<>();

                Gson gson = new Gson();
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    LeaderboardModel LeaderboardModel = gson.fromJson(finalObject.toString(), LeaderboardModel.class);
                    messagelist.add(LeaderboardModel);
                }

                return messagelist;
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
           // progressBar.setVisibility(View.GONE);
            ReadersAdapter adapter = new ReadersAdapter(Readers.this,result);
            recyclerView.setAdapter(adapter);
            //need to set data to the list
        }
    }

}
