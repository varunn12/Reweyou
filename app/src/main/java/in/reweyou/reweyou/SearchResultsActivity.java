package in.reweyou.reweyou;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.reweyou.reweyou.adapter.CityAdapter;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.fragment.SecondFragment;
import in.reweyou.reweyou.model.MpModel;
import in.reweyou.reweyou.utils.Constants;

public class SearchResultsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<MpModel> messagelist;
    private CityAdapter adapter;
    private String query;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        sessionManager = new UserSessionManager(this);
        handleIntent(getIntent());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(query);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
/*

        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(ContextCompat.getDrawable(SearchResultsActivity.this, R.drawable.line) );
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchResultsActivity.this));
*/

        //Progress bar
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
       /* getSupportActionBar().setTitle(query);
        adapter=new CityAdapter(SearchResultsActivity.this,messagelist);
        recyclerView.setAdapter(adapter);*/

        //makeRequest();
        setdata();
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(SearchResultsActivity.this, Feed.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            // new JSONTask().execute(query);
        }
    }

    private void makeRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SEARCH_QUERY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("responsesearch", response);
                        if (response != null) {
                            if (!response.isEmpty()) {
                                setdata();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                //   map.put("number", sessionManager.getMobileNumber());
                map.put("query", query);

               /* map.put("token", session.getKeyAuthToken());
                map.put("deviceid", session.getDeviceid());*/

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(SearchResultsActivity.this);
        requestQueue.add(stringRequest);
    }

    private void setdata() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SecondFragment frag = new SecondFragment();
        Bundle bundle = new Bundle();

        bundle.putInt("position", 12);
        bundle.putString("query", query);


        frag.setArguments(bundle);
        fragmentTransaction.add(R.id.container, frag, "Search");
        fragmentTransaction.commit();

    }


/*
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
            data.put("query", params[0]);
            try {
                URL url = new URL("https://www.reweyou.in/reweyou/searchresults.php");
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
            CityAdapter adapter = new CityAdapter(SearchResultsActivity.this, result);
            recyclerView.setAdapter(adapter);
            //need to set data to the list
        }
    }
*/

    private void showSnackBar(String msg) {
        Snackbar.make(findViewById(R.id.coordinatorLayout), msg, Snackbar.LENGTH_LONG).setDuration(Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeRequest();
                    }
                }).show();
    }

/*
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
    }*/
}