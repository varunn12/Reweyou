package in.reweyou.reweyou.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.reweyou.reweyou.FragmentCommunicator;
import in.reweyou.reweyou.FragmentCommunicator2;
import in.reweyou.reweyou.PermissionsChecker;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.SinglePostActivity;
import in.reweyou.reweyou.UILApplication;
import in.reweyou.reweyou.adapter.CommentsAdapter;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.CommentsModel;
import in.reweyou.reweyou.utils.Constants;

import static in.reweyou.reweyou.classes.UploadOptions.PERMISSION_ALL_IMAGE;

/**
 * Created by master on 20/11/16.
 */

public class CommentsFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, FragmentCommunicator, FragmentCommunicator2 {
    public static final String KEY_TEXT = "comments";
    public static final String KEY_NAME = "name";
    public static final String KEY_ID = "postid";
    public static final String KEY_NUMBER = "number";
    public static final String KEY_IMAGE = "image";
    static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE = 0;
    private final String[] PERMISSION_IMAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    int SELECT_FILE = 1;
    private PermissionsChecker checker;
    private UserSessionManager session;
    private String name;
    private String number;
    private String result;
    private String i;
    private LinearLayout EmptyCommentsContaier;
    private EditText editText;
    private ImageView sendButton;
    private ImageView imagebutton;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeLayout;
    private Context mContext;
    private ImageView previewImageView;
    private String selectedImagePath;
    private TextView previewTextViewDelete;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mContext instanceof SinglePostActivity) {
            ((SinglePostActivity) mContext).fragmentCommunicator = this;
            ((SinglePostActivity) mContext).fragmentCommunicator2 = this;
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_comments, container, false);

        checker = new PermissionsChecker(mContext);
        session = new UserSessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        name = user.get(UserSessionManager.KEY_NAME);
        number = user.get(UserSessionManager.KEY_NUMBER);

        EmptyCommentsContaier = (LinearLayout) layout.findViewById(R.id.empty);
        previewImageView = (ImageView) layout.findViewById(R.id.previewImageView);
        previewTextViewDelete = (TextView) layout.findViewById(R.id.previewImageViewDelete);

        previewTextViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewImageView.setVisibility(View.GONE);
                previewTextViewDelete.setVisibility(View.GONE);
                selectedImagePath = null;
            }
        });
        editText = (EditText) layout.findViewById(R.id.Who);
        editText.requestFocus();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    sendButton.setEnabled(false);
                    sendButton.setImageResource(R.drawable.button_send_disable);

                } else {
                    sendButton.setEnabled(true);
                    sendButton.setImageResource(R.drawable.button_send_comments);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        sendButton = (ImageView) layout.findViewById(R.id.btn_send);
        imagebutton = (ImageView) layout.findViewById(R.id.btn_image);

        imagebutton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        sendButton.setEnabled(false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        progressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        swipeLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        if (getArguments() != null) {
            i = getArguments().getString("myData");
            getCommentsRequest(i);
        }

        return layout;
    }


    @Override
    public void onRefresh() {
        getCommentsRequest(i);
    }

    @Override
    public void onClick(View v) {
        if (v == sendButton) {
            if (selectedImagePath == null)
                makeRequest(null);
            else uploadSelectedImage();
        } else {

            onImageButtonClick();


        }
    }

    public void onImageButtonClick() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (!hasPermissions(mContext, PERMISSION_IMAGE)) {
                    ActivityCompat.requestPermissions((Activity) mContext, PERMISSION_IMAGE, PERMISSION_ALL_IMAGE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    getActivity().startActivityForResult(intent, SELECT_FILE);
                    UILApplication.getInstance().trackEvent("Gallery", "Gallery", "For Pics");
                }
            }
        });
    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    private void makeRequest(final String encodedImage) {
        final String text = editText.getText().toString().trim();
        final ProgressDialog loading = ProgressDialog.show(mContext, "Uploading", "Please wait...");
        loading.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_COMMENTS_UPLOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);
                        loading.dismiss();
                        if (response != null) {
                            if (response.trim().equals("Successfully Uploaded")) {
                                onRefresh();
                                editText.setText("");
                                selectedImagePath = null;

                                previewImageView.setVisibility(View.GONE);
                                previewTextViewDelete.setVisibility(View.GONE);
                                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                            } else if (response.trim().equals(Constants.AUTH_ERROR)) {
                                session.logoutUser();
                            } else {
                                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                Toast.makeText(mContext, "Something went wrong! Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                            showToast("no internet connectivity");
                        } else if (error instanceof TimeoutError) {
                            showToast("poor internet connectivity");
                        } else if (error instanceof NetworkError || error instanceof ParseError || error instanceof ServerError) {
                            showToast("something went wrong");
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put(KEY_TEXT, text);
                param.put(KEY_NUMBER, number);
                param.put(KEY_NAME, name);
                param.put(KEY_ID, i);
                param.put("token", session.getKeyAuthToken());
                param.put("deviceid", session.getDeviceid());
                if (encodedImage != null)
                    param.put(KEY_IMAGE, encodedImage);

                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);


    }


    public void setData(String i) {
        this.i = i;

        getCommentsRequest(i);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    public void setpreviewImage(String path) {
        if (path != null) {
            selectedImagePath = path;
        }
        previewImageView.setVisibility(View.VISIBLE);
        previewTextViewDelete.setVisibility(View.VISIBLE);
        Glide.with(getActivity()).load(path).into(previewImageView);
    }

    private void uploadSelectedImage() {
        if (selectedImagePath != null) {
            Glide
                    .with(this)
                    .load(selectedImagePath)
                    .asBitmap()
                    .toBytes(Bitmap.CompressFormat.JPEG, 90)
                    .fitCenter()
                    .atMost()
                    .override(800, 800)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(new SimpleTarget<byte[]>() {
                        @Override
                        public void onLoadStarted(Drawable ignore) {
                            // started async load
                        }

                        @Override
                        public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> ignore) {
                            String encodedImage = Base64.encodeToString(resource, Base64.DEFAULT);
                            makeRequest(encodedImage);
                        }

                        @Override
                        public void onLoadFailed(Exception ex, Drawable ignore) {
                            Log.d("ex", ex.getMessage());
                        }
                    });
        } else Log.w("uploadSelectedImage", "selected path is null");
    }

    private void getCommentsRequest(final String id) {

        progressBar.setVisibility(View.VISIBLE);
        EmptyCommentsContaier.setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_COMMENTS_FETCH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);
                        progressBar.setVisibility(View.GONE);
                        sendButton.setEnabled(true);
                        if (response != null) {
                            JSONArray parentArray = null;
                            try {
                                parentArray = new JSONArray(response);
                                final List<CommentsModel> mpModelList = new ArrayList<>();

                                Gson gson = new Gson();
                                for (int i = 0; i < parentArray.length(); i++) {
                                    JSONObject finalObject = parentArray.getJSONObject(i);
                                    CommentsModel mpModel = gson.fromJson(finalObject.toString(), CommentsModel.class);
                                    mpModelList.add(mpModel);
                                }

                                if (parentArray.length() == 0) {
                                    EmptyCommentsContaier.setVisibility(View.VISIBLE);

                                } else {
                                    CommentsAdapter adapter = new CommentsAdapter(mContext, mpModelList);
                                    recyclerView.setAdapter(adapter);
                                    swipeLayout.setRefreshing(false);
                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            recyclerView.smoothScrollToPosition(mpModelList.size());

                                        }
                                    });
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        if (error instanceof NoConnectionError) {
                            showSnackbar("no internet connectivity");
                        } else if (error instanceof TimeoutError) {
                            showSnackbar("poor internet connectivity");
                        } else if (error instanceof NetworkError || error instanceof ParseError || error instanceof ServerError) {
                            showSnackbar("something went wrong");
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("postid", id);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);


    }

    private void showSnackbar(final String s) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(((Activity) mContext).findViewById(R.id.main_content), s, Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCommentsRequest(i);
                    }
                }).show();
            }
        });
    }

    private void showToast(final String s) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void passDataToFragment() {

        onImageButtonClick();

    }

    @Override
    public void passDataToFragment2(String path) {
        setpreviewImage(path);

    }
}
