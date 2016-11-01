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
import android.widget.Toast;

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
import java.util.Map;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.adapter.MessageAdapter;
import in.reweyou.reweyou.classes.DividerItemDecoration;
import in.reweyou.reweyou.classes.HidingScrollListener;
import in.reweyou.reweyou.classes.RequestHandler;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.MpModel;
import in.reweyou.reweyou.utils.MyJSON;


public class SecondFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    SwipeRefreshLayout swipeLayout;
    UserSessionManager session;
    private RecyclerView recyclerView;
    private List<MpModel> messagelist;
    private MessageAdapter adapter;
    private ProgressBar progressBar;
    private Spinner staticSpinner;
    private String tag, location, formattedDate, number;
    private TextView datepick;
    private int mYear, mMonth, mDay;

    public SecondFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_second, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.line));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemViewCacheSize(4);

        session = new UserSessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        number = session.getMobileNumber();
        //Progress bar
        progressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        swipeLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        // staticSpinner = (Spinner)layout.findViewById(R.id.static_spinner);

        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");


        datepick = (TextView) layout.findViewById(R.id.date);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");
        datepick.setTypeface(font);
        formattedDate = df.format(c.getTime());
        datepick.setText(formattedDate);
        datepick.setVisibility(View.GONE);
        formattedDate = "2016";
        datepick.setOnClickListener(this);


        location = user.get(UserSessionManager.KEY_LOCATION);

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Messages();
        adapter = new MessageAdapter(getActivity(), messagelist);
        recyclerView.setAdapter(adapter);

    }

    private void Messages() {

        tag = "General";
        Log.e("D", tag);
        Log.e("D", location);
        Log.e("D", formattedDate);
        // new JSONTask().execute(tag, location,formattedDate,number);

        makeRequest();

    }

    private void makeRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.reweyou.in/reweyou/newsfeed.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);

                        JSONArray parentArray = null;
                        try {
                            parentArray = new JSONArray(response);
                            Log.d("aaa", String.valueOf(parentArray));
                            StringBuffer finalBufferedData = new StringBuffer();

                            List<MpModel> messagelist = new ArrayList<>();

                            Gson gson = new Gson();
                            for (int i = 0; i < parentArray.length(); i++) {
                                JSONObject finalObject = parentArray.getJSONObject(i);
                                MpModel mpModel = gson.fromJson(finalObject.toString(), MpModel.class);
                                messagelist.add(mpModel);
                            }
                            progressBar.setVisibility(View.GONE);
                            MessageAdapter adapter = new MessageAdapter(getActivity(), messagelist);

                            recyclerView.setAdapter(adapter);

                            swipeLayout.setRefreshing(false);

                            MyJSON.saveData(getContext(), response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();

                       /* if (error instanceof AuthFailureError) {
                            Log.d("Response", "a");

                        } else */
                        if (error instanceof NetworkError || error instanceof NoConnectionError || error instanceof TimeoutError) {
                            Log.d("ResponseError", "n");

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "No internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            });
                            String respo = MyJSON.getData(getContext());
                            if (respo != null) {
                                JSONArray parentArray = null;
                                try {
                                    parentArray = new JSONArray(respo);
                                    Log.d("aaa", String.valueOf(parentArray));
                                    StringBuffer finalBufferedData = new StringBuffer();

                                    List<MpModel> messagelist = new ArrayList<>();

                                    Gson gson = new Gson();
                                    for (int i = 0; i < parentArray.length(); i++) {
                                        JSONObject finalObject = parentArray.getJSONObject(i);
                                        MpModel mpModel = gson.fromJson(finalObject.toString(), MpModel.class);
                                        messagelist.add(mpModel);
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    MessageAdapter adapter = new MessageAdapter(getActivity(), messagelist);

                                    recyclerView.setAdapter(adapter);

                                    swipeLayout.setRefreshing(false);

                                    MyJSON.saveData(getContext(), respo);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                        } else if (error instanceof ParseError) {
                            Log.d("Response", "p");


                        } else if (error instanceof ServerError) {
                            Log.d("Response", "s");


                        }

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("tag", tag);
                data.put("location", location);
                data.put("date", formattedDate);
                data.put("number", number);
                return data;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
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
                        int realmonth = monthOfYear + 1;


                        String selected = dayOfMonth + "-" + realmonth + "-"
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
                        Log.e("D", formattedDate);
                        new JSONTask().execute(tag, location, formattedDate, number);

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
            RequestHandler rh = new RequestHandler();
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("tag", params[0]);
            data.put("location", params[1]);
            data.put("date", params[2]);
            data.put("number", params[3]);
            //  tag="All";
            try {
                URL url = new URL("https://www.reweyou.in/reweyou/newsfeed.php");
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
                Log.d("aaa", String.valueOf(parentArray));
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
            MessageAdapter adapter = new MessageAdapter(getActivity(), result);

            recyclerView.setAdapter(adapter);

            swipeLayout.setRefreshing(false);
            //need to set data to the list
        }
    }
}

