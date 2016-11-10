package in.reweyou.reweyou;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import in.reweyou.reweyou.adapter.CommentsAdapter;
import in.reweyou.reweyou.classes.DividerItemDecoration;
import in.reweyou.reweyou.classes.RequestHandler;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.CommentsModel;

public class Comments extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    public static final String UPLOAD_URL = "https://www.reweyou.in/reweyou/post_comments_new.php";
    public static final String KEY_TEXT = "comments";
    public static final String KEY_NAME = "name";
    public static final String KEY_TIME = "time";
    public static final String KEY_ID = "postid";
    public static final String KEY_NUMBER = "number";
    static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE = 0;
    int SELECT_FILE = 1;
    SwipeRefreshLayout swipeLayout;
    UserSessionManager session;
    ImageLoader imageLoader = ImageLoader.getInstance();
    PermissionsChecker checker;
    private RecyclerView recyclerView;
    private ImageView button;
    private ImageView imagebutton;
    private EditText editText;
    private List<CommentsModel> mpModelList;
    private CommentsAdapter adapter;
    private TextView headline;
    private ImageView image;
    private Toolbar toolbar;
    private String name;
    private String result;
    private String i;
    private String number, head, url;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_test);
        //initCollapsingToolbar();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reactions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        checker = new PermissionsChecker(this);
        /*Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
        Typeface tf= Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
*/
        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        name = user.get(UserSessionManager.KEY_NAME);
        number= user.get(UserSessionManager.KEY_NUMBER);
        Intent in=getIntent();
        Bundle bundle = getIntent().getExtras();
        i = bundle.getString("myData");
        head=bundle.getString("headline");
        url=bundle.getString("image");


        editText = (EditText)findViewById(R.id.Who);
        button = (ImageView) findViewById(R.id.btn_send);
        imagebutton = (ImageView) findViewById(R.id.btn_image);


        imagebutton.setOnClickListener(this);
        button.setOnClickListener(this);
/*
        headline=(TextView)findViewById(R.id.headline);
        headline.setText(head);*/
      //  headline.setVisibility(View.GONE);

        image=(ImageView)findViewById(R.id.image);
        // imageLoader.displayImage(url,image);
        //  Glide.with(Comments.this).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).skipMemoryCache(true).into(image);
     //   image.setVisibility(View.GONE);
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);


        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(ContextCompat.getDrawable(Comments.this, R.drawable.line) );
      /*  recyclerView.addItemDecoration(dividerItemDecoration);*/
        recyclerView.setLayoutManager(new LinearLayoutManager(Comments.this));
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);

        swipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        progressBar.setVisibility(View.VISIBLE);
        new JSONTask().execute(i);

    }

    @Override
    public void onRefresh() {
        new JSONTask().execute(i);
    }

    @Override
    public void onClick(View v) {
        if (v == button) {
            uploadText();
        }
        else {
            if (checker.lacksPermissions(PERMISSIONS)) {
                startPermissionsActivity();
            }
            else {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // 2. pick image only
                intent.setType("image/*");
                // 3. start activity
                startActivityForResult(intent, SELECT_FILE);
                UILApplication.getInstance().trackEvent("Gallery", "Gallery", "For Pics");
            }
        }
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
                    loading = ProgressDialog.show(Comments.this, "Please wait...", "uploading", false, false);
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    loading.dismiss();
                    if (s.trim().equals("Successfully Uploaded")) {
                        onRefresh();
                        editText.setText("");
                    } else {
                        Toast.makeText(Comments.this, "Swipe down to refresh.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                protected String doInBackground(Void... params) {
                    RequestHandler rh = new RequestHandler();
                    HashMap<String, String> param = new HashMap<String, String>();
                    param.put(KEY_TEXT, text);
                    param.put(KEY_NUMBER, number);
                    param.put(KEY_NAME, name);
                    param.put(KEY_TIME, timeStamp);
                    param.put(KEY_ID,i);
                    result = rh.sendPostRequest(UPLOAD_URL, param);
                    return result;
                }
            }
            UploadText u = new UploadText();
            u.execute();
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && reqCode==SELECT_FILE && data != null) {
            Uri uriFromPath = data.getData();
            String show = uriFromPath.toString();
            Intent intent = new Intent(this, UpdateImage.class);
            intent.putExtra("path", show);
            intent.putExtra("postid",i);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this,"There is some error!",Toast.LENGTH_LONG).show();
        }
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    /* Initializing collapsing toolbar
    * Will show and hide the toolbar title on scroll
    */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    public class JSONTask extends AsyncTask<String, String, List<CommentsModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected List<CommentsModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            RequestHandler rh = new RequestHandler();
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("postid", params[0]);
            try {
                URL url = new URL("https://www.reweyou.in/reweyou/comments_list.php");
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

                List<CommentsModel> mpModelList = new ArrayList<>();

                Gson gson = new Gson();
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    CommentsModel mpModel = gson.fromJson(finalObject.toString(), CommentsModel.class);
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
        protected void onPostExecute(List<CommentsModel> result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            CommentsAdapter adapter = new CommentsAdapter(Comments.this, result);
            recyclerView.setAdapter(adapter);
            swipeLayout.setRefreshing(false);
            //need to set data to the list
        }
    }

}
