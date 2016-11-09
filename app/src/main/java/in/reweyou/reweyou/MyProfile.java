package in.reweyou.reweyou;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import java.util.Map;

import in.reweyou.reweyou.adapter.MessageAdapter;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.DividerItemDecoration;
import in.reweyou.reweyou.classes.RequestHandler;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.MpModel;
import in.reweyou.reweyou.utils.Constants;

import static in.reweyou.reweyou.utils.Constants.MY_PROFILE_EDIT_URL;
import static in.reweyou.reweyou.utils.Constants.MY_PROFILE_UPLOAD_URL;
import static in.reweyou.reweyou.utils.Constants.MY_PROFILE_URL_FOLLOW;

public class MyProfile extends AppCompatActivity implements View.OnClickListener {


    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    UserSessionManager session;
    ImageLoader imageLoader = ImageLoader.getInstance();
    ArrayList<String> profilelist = new ArrayList<>();
    int SELECT_FILE = 1;
    private String i, tag, number, user, result, image, selectedImagePath;
    private TextView Name, Reports, Info, Readers, Location, Mobile;
    private Bitmap bitmap, Correctbmp, btmap;
    private ImageView profilepic;
    private EditText editTextHeadline, editLocation;
    private AppCompatButton buttonEdit;
    private int length;
    private Button button;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private DisplayImageOptions option;
    private LinearLayout Empty;

    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        //  Log.d("Image Log:", imageEncoded);
        return imageEncoded;

    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //initCollapsingToolbar();
        session = new UserSessionManager(MyProfile.this);
        i = session.getMobileNumber();
        cd = new ConnectionDetector(MyProfile.this);
        session = new UserSessionManager(getApplicationContext());
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);


        Name = (TextView) findViewById(R.id.Name);
        Reports = (TextView) findViewById(R.id.Reports);
        Info = (TextView) findViewById(R.id.Info);
        Readers = (TextView) findViewById(R.id.Readers);
        Location = (TextView) findViewById(R.id.Location);
        Mobile = (TextView) findViewById(R.id.Mobile);
        Empty = (LinearLayout) findViewById(R.id.empty);


        button = (Button) findViewById(R.id.button);
        profilepic = (ImageView) findViewById(R.id.profilepic);

        String fontPath = "fonts/Roboto-Medium.ttf";
        String thinpath = "fonts/Roboto-Regular.ttf";
       /* Typeface font = Typeface.createFromAsset(this.getAssets(), "fontawesome-webfont.ttf");
        Typeface tf = Typeface.createFromAsset(this.getAssets(), fontPath);
        Typeface thin = Typeface.createFromAsset(this.getAssets(), thinpath);
*/
       /* Name.setTypeface(tf);
        Reports.setTypeface(thin);
        Info.setTypeface(thin);
        Readers.setTypeface(thin);
        Location.setTypeface(thin);
        Mobile.setTypeface(thin);*/

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.line));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        option = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.irongrip)
                .displayer(new RoundedBitmapDisplayer(1000))
                .showImageForEmptyUri(R.drawable.download)
                .showImageOnFail(R.drawable.download)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        //Progress bar
        tag = "Random";
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            new JSONTask().execute(i);
            new JSONTasks().execute(tag, i);
        } else {
            Toast.makeText(this, "You are not connected to Internet", Toast.LENGTH_LONG).show();
        }

        profilepic.setOnClickListener(this);
        button.setOnClickListener(this);
        Readers.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profilepic:
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // 2. pick image only
                intent.setType("image/*");
                // 3. start activity
                startActivityForResult(intent, SELECT_FILE);
                break;
            case R.id.button:
                try {
                    editHeadline();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.Readers:
                Bundle bundle = new Bundle();
                bundle.putString("myData", user);
                Intent in = new Intent(this, Readers.class);
                in.putExtras(bundle);
                startActivity(in);
                break;
        }
    }

    private void button(final String i) {
        final String number = session.getMobileNumber();
        // final ProgressDialog loading = ProgressDialog.show(this, "Authenticating", "Please wait", false, false);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Constants.MY_PROFILE_URL_VERIFY_FOLLOW, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //if the server response is success
                if (response.equalsIgnoreCase("success")) {
                    //dismissing the progressbar
                    //     loading.dismiss();

                    //Starting a new activity
                    button.setVisibility(View.VISIBLE);
                    button.setBackgroundColor(ContextCompat.getColor(MyProfile.this, R.color.red));
                    button.setTextColor(ContextCompat.getColor(MyProfile.this, R.color.transparent_bg));
                    button.setText("Unread");
                    button.setTag(1);
                } else {
                    //Displaying a toast if the otp entered is wrong
                    button.setVisibility(View.VISIBLE);
                    button.setBackgroundColor(ContextCompat.getColor(MyProfile.this, R.color.feedbackground));
                    button.setTextColor(ContextCompat.getColor(MyProfile.this, R.color.black));
                    button.setText("Read");
                    button.setTag(0);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Something went wrong, Try again", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user", i);
                params.put("number", number);

                return params;
            }
        };
        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }


    private void reading(final String i) {
        HashMap<String, String> user = session.getUserDetails();
        final String number = session.getMobileNumber();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, MY_PROFILE_URL_FOLLOW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("success")) {
                            //button.setText("Reviewed");
                            button.setBackgroundColor(ContextCompat.getColor(MyProfile.this, R.color.red));
                            button.setTextColor(ContextCompat.getColor(MyProfile.this, R.color.transparent_bg));
                            button.setText("Unread");
                            button.setTag(1);
                        } else {
                            Toast.makeText(MyProfile.this, "Try Again", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MyProfile.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("number", number);
                map.put("user", i);
                map.put("unread", "reading");
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MyProfile.this);
        requestQueue.add(stringRequest);
    }

    private void read(final String i) {
        HashMap<String, String> user = session.getUserDetails();
        number = session.getMobileNumber();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, MY_PROFILE_URL_FOLLOW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("success")) {
                            //button.setText("Reviewed");
                            button.setBackgroundColor(ContextCompat.getColor(MyProfile.this, R.color.feedbackground));
                            button.setTextColor(ContextCompat.getColor(MyProfile.this, R.color.black));
                            button.setText("Read");
                            button.setTag(0);
                        } else {
                            Toast.makeText(MyProfile.this, "Try Again", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MyProfile.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("number", number);
                map.put("user", i);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MyProfile.this);
        requestQueue.add(stringRequest);
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

    private void setPic(String imagePath, ImageView destination) {
        int targetW = 200;
        int targetH = 200;
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        int angle = 0;

        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            angle = 90;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            angle = 180;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            angle = 270;
        }

        Matrix mat = new Matrix();
        mat.postRotate(angle);

        Correctbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
        destination.setImageBitmap(Correctbmp);
    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void uploadImage() {
        class UploadImage extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MyProfile.this, "Please wait...", "Adding your picture", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.trim().equals("Successfully Uploaded")) {
                    session.setProfilePicture(image);
                    Toast.makeText(MyProfile.this, "Profile Picture updated", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MyProfile.this, "Couldn't set", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                HashMap<String, String> param = new HashMap<String, String>();
                param.put("number", i);
                param.put("image", image);


                String result = rh.sendPostRequest(MY_PROFILE_UPLOAD_URL, param);
                return result;
            }
        }
        UploadImage u = new UploadImage();
        u.execute();
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && reqCode == SELECT_FILE && data != null) {
            Uri uriFromPath = data.getData();
            selectedImagePath = getAbsolutePath(uriFromPath);
            setPic(selectedImagePath, profilepic);
            image = encodeTobase64(Correctbmp);
            uploadImage();
        }
    }

    //This method would confirm the otp
    public void editHeadline() throws JSONException {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(MyProfile.this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_profile, null);
        //  number=session.getMobileNumber();
        //Initizliaing confirm button fo dialog box and edittext of dialog box
        buttonEdit = (AppCompatButton) confirmDialog.findViewById(R.id.buttonConfirm);
        editTextHeadline = (EditText) confirmDialog.findViewById(R.id.editTextOtp);
        editLocation = (EditText) confirmDialog.findViewById(R.id.editTextLocation);

        if (Info.getText().toString().equals(getResources().getString(R.string.emptyStatus)))
            editTextHeadline.setHint(R.string.emptyStatusHint);
        else editTextHeadline.setText(Info.getText());

        editLocation.setText(Location.getText());
        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(MyProfile.this);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();

        //Displaying the alert dialog
        alertDialog.show();

        //On the click of the confirm button from alert dialog
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Hiding the alert dialog
                alertDialog.dismiss();
                //Displaying a progressbar
                if (!editTextHeadline.getText().toString().equals(Info.getText().toString())) {
                    final ProgressDialog loading = ProgressDialog.show(MyProfile.this, "Updating", "Please wait", false, false);
                    //Getting the user entered otp from edittext
                    final String headline = editTextHeadline.getText().toString().trim();
                    final String location = editLocation.getText().toString().trim();
                    //Creating an string request
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, MY_PROFILE_EDIT_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //if the server response is success
                                    if (response.equalsIgnoreCase("success")) {
                                        //dismissing the progressbar
                                        loading.dismiss();

                                        if (!headline.isEmpty()) {
                                            Info.setText(headline);
                                            Info.setTextColor(getResources().getColor(android.R.color.white));
                                        } else {
                                            Info.setText(getResources().getString(R.string.emptyStatus));
                                            Info.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                                            Info.setPaintFlags(Info.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                                        }
                                        Toast.makeText(MyProfile.this, "Profile Updated!", Toast.LENGTH_LONG).show();
                                        //Starting a new activity
                                    } else {
                                        //Displaying a toast if the otp entered is wrong
                                        loading.dismiss();
                                        Toast.makeText(MyProfile.this, "Something went wrong!", Toast.LENGTH_LONG).show();

                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    alertDialog.dismiss();
                                    loading.dismiss();
                                    Toast.makeText(MyProfile.this, "Try again later", Toast.LENGTH_LONG).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            //Adding the parameters otp and username
                            params.put("number", i);
                            params.put("info", headline);
                            params.put("location", location);
                            // params.put("number",number);
                            return params;
                        }
                    };
                    //Adding the request to the queue
                    RequestQueue requestQueue = Volley.newRequestQueue(MyProfile.this);
                    requestQueue.add(stringRequest);
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        // Retrieve the SearchView and plug it into SearchManager
        // Associate searchable configuration with the SearchView
       /* SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchable

        (
                searchManager.getSearchableInfo(getComponentName()));
*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_settings:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...

                try {
                    editHeadline();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public class JSONTask extends AsyncTask<String, String, List<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected List<String> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            RequestHandler rh = new RequestHandler();
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("number", params[0]);
            try {
                URL url = new URL("https://www.reweyou.in/reweyou/user_list.php");
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

                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    profilelist.add(finalObject.getString("name"));
                    profilelist.add(finalObject.getString("total_reviews"));
                    profilelist.add(finalObject.getString("profilepic"));
                    profilelist.add(finalObject.getString("info"));
                    profilelist.add(finalObject.getString("number"));
                    profilelist.add(finalObject.getString("location"));
                    profilelist.add(finalObject.getString("readers"));
                    //  profilelist.add(finalObject.getString("readers"));
                }

                return profilelist;

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
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);
            Name.setText(result.get(0));
            Reports.setText(result.get(1));
            if (!result.get(3).isEmpty()) {
                Info.setText(result.get(3));
                Info.setPaintFlags(Info.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            } else {
                Info.setText(getResources().getString(R.string.emptyStatus));
                Info.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                Info.setPaintFlags(Info.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            }

            Info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        editHeadline();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            // imageLoader.displayImage(result.get(2), profilepic, option);
            Glide.with(MyProfile.this).load(result.get(2)).error(R.drawable.download).into(profilepic);
            user = result.get(4);
            Mobile.setText(result.get(4));
            Location.setText(result.get(5));
            Readers.setText(result.get(6));

            //    progressBar.setVisibility(View.GONE);
            //need to set data to the list
        }
    }

    public class JSONTasks extends AsyncTask<String, String, List<MpModel>> {

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
            data.put("number", params[1]);
            //  tag="All";
            try {
                URL url = new URL("https://www.reweyou.in/reweyou/myreports.php");
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
                length = parentArray.length();
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

            if (result.isEmpty()) {
                Empty.setVisibility(View.VISIBLE);
            }
            MessageAdapter adapter = new MessageAdapter(MyProfile.this, result);
            // total.setText("You have reported "+ String.valueOf(length)+ " stories.");
            recyclerView.setAdapter(adapter);
            //need to set data to the list
        }
    }
}
