package in.reweyou.reweyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyou.Details;
import in.reweyou.reweyou.Feed;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.adapter.MessageAdapter;
import in.reweyou.reweyou.adapter.MpAdapter;
import in.reweyou.reweyou.model.MpDetail;
import in.reweyou.reweyou.model.MpModel;


public class OneFragment extends Fragment {


    private RecyclerView recyclerView;
    private List<MpDetail> mpModelList;
    private MpAdapter adapter;
    private ProgressBar progressBar;
    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_one, container, false);
        recyclerView=(RecyclerView)layout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //Progress bar
        //progressBar = (ProgressBar)layout. findViewById(R.id.progress_bar);
        //progressBar.setVisibility(View.VISIBLE);
        return layout;
    }
    @Override
    public void onViewCreated(View layout, Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Bundle bundle = new Bundle();
                bundle.putInt("myData", position);
                Intent in=new Intent(getActivity(),Details.class);
                in.putExtras(bundle);
                startActivity(in);
                }

            @Override
            public void onLongClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putInt("name", position);
                Intent in2=new Intent(getActivity(),Feed.class);
                in2.putExtras(bundle);
                startActivity(in2);
                //Toast.makeText(getActivity(), "onLongClick" + position, Toast.LENGTH_SHORT).show();
            }
        }));

        MpDetails();
        adapter=new MpAdapter(getActivity(),mpModelList);
        recyclerView.setAdapter(adapter);

    }


    private void MpDetails() {
        new JSONTask().execute("https://www.reweyou.in/mplist.php");
    }
    public class JSONTask extends AsyncTask<String, String, List<MpDetail>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected List<MpDetail> doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
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

                List<MpDetail> mpModelList = new ArrayList<>();

                Gson gson = new Gson();
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    MpDetail mpModel = gson.fromJson(finalObject.toString(), MpDetail.class);
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
        protected void onPostExecute(List<MpDetail> result) {
            super.onPostExecute(result);
//            progressBar.setVisibility(View.GONE);
            MpAdapter adapter = new MpAdapter(getActivity(),result);
            recyclerView.setAdapter(adapter);
            //need to set data to the list
        }
    }


    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            }
            );
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child,rv.getChildAdapterPosition(child));
            }
            return false;
        }
        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }
        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    public static interface ClickListener {
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }
}
