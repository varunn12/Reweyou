package in.reweyou.reweyou.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.adapter.LeaderBoardAdapter;
import in.reweyou.reweyou.classes.HidingScrollListener;
import in.reweyou.reweyou.classes.RequestHandler;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.LeaderboardModel;


public class LeaderboardFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    UserSessionManager session;

    SwipeRefreshLayout swipeLayout;
    private RecyclerView recyclerView;
    private List<LeaderboardModel> messagelist;
    private TextView total;
    private LeaderBoardAdapter adapter;
    private ProgressBar progressBar;
    private String tag;
    private Button button8;
    private String number;
    private int length;
    public LeaderboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View layout=inflater.inflate(R.layout.fragment_reports, container, false);
        session = new UserSessionManager(getActivity().getApplicationContext());
        recyclerView=(RecyclerView)layout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        total=(TextView)layout.findViewById(R.id.Total);
        total.setText("Rank is based on total reviews on your post");
        //Progress bar
        tag="Random";
        progressBar = (ProgressBar)layout. findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        swipeLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);



        recyclerView.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                //  hideViews();
            }

            @Override
            public void onShow() {
                //showViews();
            }
        });
        return layout;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        HashMap<String, String> user = session.getUserDetails();
        number = user.get(UserSessionManager.KEY_NUMBER);
        Messages();
        adapter = new LeaderBoardAdapter(getActivity(), messagelist);
        recyclerView.setAdapter(adapter);

    }

    private void Messages() {

        new JSONTask().execute(tag,number);
    }

    @Override
    public void onRefresh() {
        Messages();
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
           // data.put("number",params[1]);
            //  tag="All";
            try {
                URL url = new URL("https://www.reweyou.in/reweyou/leaderboard.php");
                connection = (HttpURLConnection) url.openConnection();

                connection.setDoOutput(true);
                connection.setRequestMethod("POST");


               // OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
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
                length = parentArray.length();
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
            progressBar.setVisibility(View.GONE);
            LeaderBoardAdapter adapter = new LeaderBoardAdapter(getActivity(), result);
            recyclerView.setAdapter(adapter);
            swipeLayout.setRefreshing(false);
            //need to set data to the list
        }
    }
}

