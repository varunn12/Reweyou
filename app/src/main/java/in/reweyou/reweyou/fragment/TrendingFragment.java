package in.reweyou.reweyou.fragment;

import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import in.reweyou.reweyou.classes.DividerItemDecoration;
import in.reweyou.reweyou.classes.HidingScrollListener;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.classes.RequestHandler;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.adapter.CityAdapter;
import in.reweyou.reweyou.model.MpModel;


public class TrendingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    UserSessionManager session;

    SwipeRefreshLayout swipeLayout;
    private RecyclerView recyclerView;
    private List<MpModel> messagelist;
    private CityAdapter adapter;
    private ProgressBar progressBar;
    private String tag, location, formattedDate;
    private TextView datepick;
    private int mYear, mMonth, mDay, mHour, mMinute;

    public TrendingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View layout=inflater.inflate(R.layout.fragment_second, container, false);
        session = new UserSessionManager(getActivity());
        recyclerView=(RecyclerView)layout.findViewById(R.id.recycler_view);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.line) );
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //Progress bar
        progressBar = (ProgressBar)layout. findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        swipeLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        //  SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        //formattedDate = df.format(c.getTime());
        formattedDate="2016";

        datepick=(TextView)layout.findViewById(R.id.date);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");
        datepick.setTypeface(font);
        datepick.setOnClickListener(this);


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
        location = user.get(UserSessionManager.KEY_LOCATION);
        Messages();
        adapter=new CityAdapter(getActivity(),messagelist);
        recyclerView.setAdapter(adapter);

    }

    private void Messages() {

        tag="General";
        new JSONTask().execute(tag, location,formattedDate);

    }

    @Override
    public void onRefresh() {
        Messages();
    }

    @Override
    public void onClick(View v) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        int realmonth=monthOfYear+1;


                        String selected =  dayOfMonth + "-" + realmonth + "-"
                                + year;
                        DateFormat inputFormatter1 = new SimpleDateFormat("dd-MM-yyyy");
                        Date date1 = null;
                        try {
                            date1 = inputFormatter1.parse(selected);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        DateFormat outputFormatter1 = new SimpleDateFormat("dd-MMM-yyyy");
                        formattedDate = outputFormatter1.format(date1);
                        tag="General";
                        new JSONTask().execute(tag, location,formattedDate);

                        datepick.setText(formattedDate);
                        //  txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
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
            data.put("location",params[1]);
            data.put("date",params[2]);
            //  tag="All";
            try {
                URL url = new URL("https://www.reweyou.in/reweyou/trending.php");
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
            CityAdapter adapter = new CityAdapter(getActivity(),result);
            recyclerView.setAdapter(adapter);
            swipeLayout.setRefreshing(false);
            //need to set data to the list
        }
    }
}

