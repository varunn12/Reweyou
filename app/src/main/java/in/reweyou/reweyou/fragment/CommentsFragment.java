package in.reweyou.reweyou.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import in.reweyou.reweyou.PermissionsActivity;
import in.reweyou.reweyou.PermissionsChecker;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.UILApplication;
import in.reweyou.reweyou.UpdateImage;
import in.reweyou.reweyou.adapter.CommentsAdapter;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.RequestHandler;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.CommentsModel;

/**
 * Created by master on 20/11/16.
 */

public class CommentsFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String UPLOAD_URL = "https://www.reweyou.in/reweyou/post_comments_new.php";
    public static final String KEY_TEXT = "comments";
    public static final String KEY_NAME = "name";
    public static final String KEY_TIME = "time";
    public static final String KEY_ID = "postid";
    public static final String KEY_NUMBER = "number";
    static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int DISABLE = 0;
    private static final int ENABLE = 1;
    private static final int REQUEST_CODE = 0;
    int SELECT_FILE = 1;
    private LinearLayout commentContainer;
    private ConnectionDetector checknet;
    private PermissionsChecker checker;
    private UserSessionManager session;
    private String name;
    private String number;
    private String result;
    private String i;
    private LinearLayout Empty;
    private EditText editText;
    private ImageView button;
    private ImageView imagebutton;
    private ImageView image;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeLayout;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_comments, container, false);
        commentContainer = (LinearLayout) layout.findViewById(R.id.comment);
        checknet = new ConnectionDetector(mContext);
        checker = new PermissionsChecker(mContext);
        session = new UserSessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        name = user.get(UserSessionManager.KEY_NAME);
        number = user.get(UserSessionManager.KEY_NUMBER);
        Intent in = getActivity().getIntent();

        Empty = (LinearLayout) layout.findViewById(R.id.empty);


        editText = (EditText) layout.findViewById(R.id.Who);
        editText.setText("");
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    button.setEnabled(false);
                    button.setImageResource(R.drawable.button_send_disable);

                } else {
                    button.setEnabled(true);
                    button.setImageResource(R.drawable.button_send_comments);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        button = (ImageView) layout.findViewById(R.id.btn_send);

        imagebutton = (ImageView) layout.findViewById(R.id.btn_image);


        imagebutton.setOnClickListener(this);
        button.setOnClickListener(this);

        setEnabledBottomBarViews(DISABLE);
        image = (ImageView) layout.findViewById(R.id.image);

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        progressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);

        swipeLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        if (getArguments() != null) {
            i = getArguments().getString("myData");
            new JSONTask(false).execute(i);
        }

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    private void setEnabledBottomBarViews(int j) {
        if (j == DISABLE) {
            commentContainer.setVisibility(View.INVISIBLE);

        } else {
            commentContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        new JSONTask(true).execute(i);
    }

    @Override
    public void onClick(View v) {
        if (v == button) {
            uploadText();
        } else {
            editText.clearFocus();
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if (checker.lacksPermissions(PERMISSIONS)) {
                        startPermissionsActivity();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        // 2. pick image only
                        intent.setType("image/*");
                        // 3. start activity
                        startActivityForResult(intent, SELECT_FILE);
                        UILApplication.getInstance().trackEvent("Gallery", "Gallery", "For Pics");
                    }
                }
            });

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
                    loading = ProgressDialog.show(mContext, "Please wait...", "uploading", false, false);
                    loading.setCancelable(false);
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    loading.dismiss();
                    if (s.trim().equals("Successfully Uploaded")) {
                        onRefresh();
                        editText.setText("");
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    } else {
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        Snackbar.make(getActivity().findViewById(R.id.main_content), "Some error occurred while uploading", Snackbar.LENGTH_LONG).setDuration(Snackbar.LENGTH_INDEFINITE)
                                .setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (checknet.isConnectingToInternet()) {
                                            uploadText();
                                        } else {
                                            Snackbar.make(getActivity().findViewById(R.id.main_content), "No Internet Connectivity", Snackbar.LENGTH_LONG).setDuration(Snackbar.LENGTH_INDEFINITE)
                                                    .setAction("RETRY", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if (checknet.isConnectingToInternet()) {
                                                                uploadText();
                                                            }
                                                        }
                                                    }).show();
                                        }
                                    }
                                }).show();
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
                    param.put(KEY_ID, i);
                    result = rh.sendPostRequest(UPLOAD_URL, param);
                    return result;
                }
            }
            UploadText u = new UploadText();
            u.execute();
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && reqCode == SELECT_FILE && data != null) {
            Uri uriFromPath = data.getData();
            String show = uriFromPath.toString();
            Intent intent = new Intent(mContext, UpdateImage.class);
            intent.putExtra("path", show);
            intent.putExtra("postid", i);
            startActivity(intent);
        } else {
            Toast.makeText(mContext, "There is some error!", Toast.LENGTH_LONG).show();
        }
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(getActivity(), REQUEST_CODE, PERMISSIONS);
    }

    public void setData(String i) {
        this.i = i;

        new JSONTask(false).execute(i);

    }

    /* Initializing collapsing toolbar
    * Will show and hide the toolbar title on scroll
    */

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    public class JSONTask extends AsyncTask<String, String, List<CommentsModel>> {

        private boolean b = false;

        public JSONTask(boolean b) {
            this.b = b;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!b)
                progressBar.setVisibility(View.VISIBLE);
            Empty.setVisibility(View.GONE);
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
        protected void onPostExecute(final List<CommentsModel> result) {
            super.onPostExecute(result);
            if (!b)
                progressBar.setVisibility(View.GONE);

            if (result != null) {
                setEnabledBottomBarViews(ENABLE);
                button.setEnabled(false);
                if (result.isEmpty()) {
                    Empty.setVisibility(View.VISIBLE);

                } else {
                    CommentsAdapter adapter = new CommentsAdapter(mContext, result);
                    recyclerView.setAdapter(adapter);
                    swipeLayout.setRefreshing(false);
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(result.size());

                        }
                    });
                }
            } else {
                setEnabledBottomBarViews(DISABLE);
                Snackbar.make(getActivity().findViewById(R.id.main_content), "No Internet Connectivity", Snackbar.LENGTH_LONG).setDuration(Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                new JSONTask(false).execute(i);

                            }
                        }).show();
            }


        }
    }
}
