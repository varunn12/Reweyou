package in.reweyou.reweyou.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.adapter.MessageAdapter;
import in.reweyou.reweyou.classes.DividerItemDecoration;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.MpModel;
import in.reweyou.reweyou.utils.MyJSON;


public class SecondFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    SwipeRefreshLayout swipeLayout;
    UserSessionManager session;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private RecyclerView recyclerView;
    private List<MpModel> messagelist = new ArrayList<>();
    private ProgressBar progressBar;
    private Spinner staticSpinner;
    private String tag, location, formattedDate, number;
    private TextView datepick;
    private int mYear, mMonth, mDay;
    private boolean loading = true;
    private SimpleDateFormat df;
    private MessageAdapter adapter;
    private Calendar c;

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
        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(defaultItemAnimator);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            adapter.add();
                            //Do pagination.. i.e. fetch new data

                            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.reweyou.in/reweyou/timefeed.php",
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.d("Response", response);

                                            List<MpModel> list = new ArrayList<MpModel>();
                                            JSONArray parentArray = null;
                                            try {
                                                parentArray = new JSONArray(response);
                                                adapter.remove();

                                                Gson gson = new Gson();
                                                for (int i = 0; i < parentArray.length(); i++) {
                                                    JSONObject finalObject = parentArray.getJSONObject(i);
                                                    MpModel mpModel = gson.fromJson(finalObject.toString(), MpModel.class);
                                                    list.add(mpModel);
                                                    if (i == parentArray.length() - 1) {
                                                        formattedDate = mpModel.getDate1();
                                                        Log.d("last", mpModel.getCategory());
                                                    }
                                                }

                                                adapter.loadMore(list);
                                                loading = true;

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            //  adapter.remove();
                                            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> data = new HashMap<>();
                                    data.put("tag", tag);
                                    data.put("location", location);
                                    data.put("date", formattedDate);
                                    Log.d("ddd", formattedDate);
                                    data.put("number", number);
                                    return data;
                                }
                            };

                            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                            requestQueue.add(stringRequest);


                        }
                    }
                }
            }
        });

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

        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");


        datepick = (TextView) layout.findViewById(R.id.date);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");
        datepick.setTypeface(font);
        formattedDate = df.format(c.getTime());
        datepick.setText(formattedDate);
        datepick.setVisibility(View.GONE);
        //  formattedDate = "2016";
        // formattedDate =c.getTime().toString();
        datepick.setOnClickListener(this);
        location = user.get(UserSessionManager.KEY_LOCATION);

        Messages();
        return layout;
    }


    private void Messages() {
        formattedDate = df.format(c.getTime());

        tag = "General";
        Log.e("D", tag);
        Log.e("D", location);
        Log.e("D", formattedDate);
        // new JSONTask().execute(tag, location,formattedDate,number);

        makeRequest();

    }

    private void makeRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.reweyou.in/reweyou/timefeed.php",
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
                            Log.d("size", String.valueOf(parentArray.length()));
                            for (int i = 0; i < parentArray.length(); i++) {
                                JSONObject finalObject = parentArray.getJSONObject(i);
                                MpModel mpModel = gson.fromJson(finalObject.toString(), MpModel.class);
                                messagelist.add(mpModel);

                                if (i == parentArray.length() - 1) {
                                    formattedDate = mpModel.getDate1();
                                    Log.d("last", mpModel.getCategory());
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                            adapter = new MessageAdapter(getActivity(), messagelist);
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
                            Log.d("ResponseError", error.toString());

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
                Log.d("ddd", formattedDate);
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
/*
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
        datePickerDialog.show();*/
    }

}

