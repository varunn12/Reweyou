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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import in.reweyou.reweyou.classes.HidingScrollListener;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.classes.RequestHandler;
import in.reweyou.reweyou.adapter.VideoAdapter;
import in.reweyou.reweyou.model.MpModel;


public class VideoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swipeLayout;
    private RecyclerView recyclerView;
    private List<MpModel> messagelist;
    private VideoAdapter adapter;
    private ProgressBar progressBar;
    private Spinner staticSpinner;
    private String tag;
    private Button button8;

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View layout=inflater.inflate(R.layout.fragment_video, container, false);
        recyclerView=(RecyclerView)layout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Progress bar
        progressBar = (ProgressBar)layout. findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        swipeLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        staticSpinner = (Spinner)layout.findViewById(R.id.static_spinner);
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
    private void hideViews() {
        staticSpinner.animate().translationY(-staticSpinner.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        button8.animate().translationY(-button8.getHeight()).setInterpolator(new AccelerateInterpolator(2));

    }

    private void showViews() {
        staticSpinner.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        button8.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        Messages();
        adapter=new VideoAdapter(getActivity(),messagelist);
        recyclerView.setAdapter(adapter);
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(getActivity(), R.array.brew_array,
                        R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);
        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position > 0) {
                    // get spinner value
                    tag = (String) parent.getItemAtPosition(position);
                    new JSONTask().execute(tag);
                    //Toast.makeText(getActivity(), tag, Toast.LENGTH_LONG).show();
                    // Messages();
                } else {
                    // show toast select gender
                    new JSONTask().execute("General");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void Messages() {
        if(staticSpinner.getSelectedItem()==null)
        {
            tag="General";
            new JSONTask().execute(tag);
        }
        else {
            tag = staticSpinner.getSelectedItem().toString();
            new JSONTask().execute(tag);
        }
    }

    @Override
    public void onRefresh() {
        Messages();
    }



    public class JSONTask extends AsyncTask<String, String, List<MpModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected List<MpModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            RequestHandler rh= new RequestHandler();
            HashMap<String, String> data = new HashMap<String,String>();
            data.put("tag",params[0]);
            //  tag="All";
            try {
                URL url = new URL("https://www.reweyou.in/videolist.php");
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

                List<MpModel> messagelist = new ArrayList<>();

                Gson gson = new Gson();
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    MpModel mpModel = gson.fromJson(finalObject.toString(), MpModel.class);
                    messagelist.add(mpModel);
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
        protected void onPostExecute(List<MpModel> result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            VideoAdapter adapter = new VideoAdapter(getActivity(),result);
            recyclerView.setAdapter(adapter);
            swipeLayout.setRefreshing(false);
            //need to set data to the list
        }
    }
}

