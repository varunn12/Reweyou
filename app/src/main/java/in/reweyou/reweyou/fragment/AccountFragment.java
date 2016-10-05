package in.reweyou.reweyou.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import in.reweyou.reweyou.Feed;
import in.reweyou.reweyou.MyProfile;
import in.reweyou.reweyou.MyReports;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.ShowImage;
import in.reweyou.reweyou.classes.AppLocationService;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.LocationAddress;
import in.reweyou.reweyou.classes.RequestHandler;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.adapter.MpAdapter;
import in.reweyou.reweyou.model.MpDetail;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link } interface
 * to handle interaction events.
 * Use the {@link } factory method to
 * create an instance of this fragment.
 */

public class AccountFragment extends Fragment implements View.OnClickListener{
    UserSessionManager session;
    AppLocationService appLocationService;
        // Button Logout
    Button EditInfo;
    private TextView Name, Info, Mobile, Location;
    private EditText editTextHeadline;
    private AppCompatButton buttonEdit;
    private String selectedImagePath;
    private Bitmap bitmap, Correctbmp,btmap;
    private ImageView profilepic;
    private String username;
    private String number;
    private String image;
    private String city;
    int SELECT_FILE = 1;
    public static final String KEY_NUMBER = "number";
    public static final String KEY_IMAGE = "image";
    public static final String UPLOAD_URL = "https://www.reweyou.in/reweyou/profile_picture.php";
    public static final String EDIT_URL = "https://www.reweyou.in/reweyou/profile.php";
    private Button  myreports;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_account, container, false);


        //Progress bar
        profilepic=(ImageView)layout.findViewById(R.id.profilepic);
        cd = new ConnectionDetector(getActivity());


        session = new UserSessionManager(getActivity());
        appLocationService = new AppLocationService(getActivity().getApplicationContext());
         Name = (TextView) layout.findViewById(R.id.Name);
        Info = (TextView)layout.findViewById(R.id.Info);
        Location = (TextView) layout.findViewById(R.id.Location);
         Mobile = (TextView)layout.findViewById(R.id.Mobile);
        EditInfo = (Button)layout.findViewById(R.id.EditInfo);
        myreports=(Button)layout.findViewById(R.id.MyReports);

        EditInfo.setOnClickListener(this);
        myreports.setOnClickListener(this);
        Info.setOnClickListener(this);


        HashMap<String, String> user = session.getUserDetails();
        // get name
        username = user.get(UserSessionManager.KEY_NAME);
        // get email
        number = user.get(UserSessionManager.KEY_NUMBER);
        city = user.get(UserSessionManager.KEY_LOCATION);
        //String password = user.get(UserSessionManager.KEY_PASSWORD);
        // Show user data on activity
        Name.setText(username);
        Location.setText(city);
        Mobile.setText(number);
        //Password.setText(Html.fromHtml("Password: <b>" + password + "</b>"));

        if(session.getProfilePicture()!=null)
        {
            String pic = session.getProfilePicture();
            btmap=decodeBase64(pic);
            profilepic.setImageBitmap(btmap);
            EditInfo.setText("Change Pic");
        }
        else
        {
            //
        }
        return layout;
    }

    @Override
    public void onClick(View v ) {
        switch (v.getId()){
            case R.id.EditInfo:
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // 2. pick image only
            intent.setType("image/*");
            // 3. start activity
            startActivityForResult(intent, SELECT_FILE);
        break;
            case R.id.MyReports:
                isInternetPresent = cd.isConnectingToInternet();
                if(isInternetPresent) {
                    Intent reports = new Intent(getActivity(), MyReports.class);
                    startActivity(reports);
                }
                else
            {
                Toast.makeText(getActivity(),"You are not connected to Internet",Toast.LENGTH_SHORT).show();
            }
                break;
            case R.id.Info:
                isInternetPresent = cd.isConnectingToInternet();
                if(isInternetPresent) {
                    try {
                        editHeadline();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getActivity(),"You are not connected to Internet",Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && reqCode==SELECT_FILE && data != null) {
            Uri uriFromPath = data.getData();
            selectedImagePath = getAbsolutePath(uriFromPath);
            setPic(selectedImagePath,profilepic);
            image=encodeTobase64(Correctbmp);
           uploadImage();
        }
    }
    public static String encodeTobase64(Bitmap image)
    {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

      //  Log.d("Image Log:", imageEncoded);
        return imageEncoded;

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
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

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
        }
        else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            angle = 180;
        }
        else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            angle = 270;
        }

        Matrix mat = new Matrix();
        mat.postRotate(angle);

        Correctbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
        destination.setImageBitmap(Correctbmp);
    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }
    @Override
    public void onViewCreated(View layout, Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

    }

    private File savebitmap() {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;

        File file = new File("ProfilePic" + ".jpg");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, "ProfilePic" + ".jpg");
            Log.e("file exist", "" + file + ",Bitmap= " + "ProfilePic");
        }
        try {
            // make a new bitmap from your file
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file", "" + file);
        return file;
    }

    public void uploadImage() {
        class UploadImage extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(), "Please wait...", "Adding your picture", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.trim().equals("Successfully Uploaded")) {
                    session.setProfilePicture(image);
                } else {
                    Toast.makeText(getActivity(), "Couldn't set", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                HashMap<String, String> param = new HashMap<String, String>();
                param.put(KEY_NUMBER, number);
                param.put(KEY_IMAGE, image);


                String result = rh.sendPostRequest(UPLOAD_URL, param);
                return result;
            }
        }
        UploadImage u = new UploadImage();
        u.execute();
    }



    //This method would confirm the otp
    public void editHeadline() throws JSONException {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(getActivity());
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_edit, null);
        //  number=session.getMobileNumber();
        //Initizliaing confirm button fo dialog box and edittext of dialog box
        buttonEdit = (AppCompatButton) confirmDialog.findViewById(R.id.buttonConfirm);
        editTextHeadline = (EditText) confirmDialog.findViewById(R.id.editTextOtp);
        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

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
                final ProgressDialog loading = ProgressDialog.show(getActivity(), "Updating", "Please wait", false, false);
                //Getting the user entered otp from edittext
                final String headline = editTextHeadline.getText().toString().trim();
                //Creating an string request
                StringRequest stringRequest = new StringRequest(Request.Method.POST, EDIT_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //if the server response is success
                                if (response.equalsIgnoreCase("success")) {
                                    //dismissing the progressbar
                                    loading.dismiss();

                                    //Starting a new activity
                                } else {
                                    //Displaying a toast if the otp entered is wrong
                                    loading.dismiss();
                                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();

                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                alertDialog.dismiss();
                                loading.dismiss();
                                Toast.makeText(getActivity(), "Try again later", Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        //Adding the parameters otp and username
                        params.put("number", number);
                        params.put("info", headline);
                        // params.put("number",number);
                        return params;
                    }
                };
                //Adding the request to the queue
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                requestQueue.add(stringRequest);
            }
        });
    }

}
