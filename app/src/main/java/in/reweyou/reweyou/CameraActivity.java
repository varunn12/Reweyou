package in.reweyou.reweyou;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import in.reweyou.reweyou.classes.AppLocationService;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.LocationAddress;
import in.reweyou.reweyou.classes.RequestHandler;
import in.reweyou.reweyou.classes.UserSessionManager;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button;
    private EditText editText, editTag, head;
    private ImageView imageview;
    private Bitmap bitmap;
    private Bitmap Correctbmp;
    private Spinner staticSpinner;
    Location location;
    private String place, address, mycity, tag, type, result;
    String selectedImagePath;
    UserSessionManager session;
    private String name;
    int backpress;
    private static final int REQUEST_CODE = 0;

    PermissionsChecker checker;
    static final String[] PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

    AppLocationService appLocationService;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    public static final String KEY_LOCATION="location";
    public static final String KEY_ADDRESS="address";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_TIME = "time";
    public static final String KEY_NAME = "name";
    public static final String KEY_TAG="tag";
    public static final String KEY_TEXT = "headline";
    public static final String UPLOAD_URL = "https://www.reweyou.in/reweyou/upload_report.php";
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        checker = new PermissionsChecker(this);
        Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
        session = new UserSessionManager(CameraActivity.this);
        mycity=session.getLoginLocation();
        appLocationService = new AppLocationService(
              CameraActivity.this);
        cd = new ConnectionDetector(CameraActivity.this);
        imageview = (ImageView)findViewById(R.id.ImageShow);
        editText = (EditText)findViewById(R.id.Who);
        editTag=(EditText)findViewById(R.id.EditTag);
        head=(EditText)findViewById(R.id.head);
        button=(Button)findViewById(R.id.btn_send);
        button.setTypeface(font);
        button.setOnClickListener(this);
        HashMap<String, String> user = session.getUserDetails();
        name = user.get(UserSessionManager.KEY_NAME);
        number = user.get(UserSessionManager.KEY_NUMBER);
        String show = getIntent().getStringExtra("path");
        Log.d("TAG", show);
       selectedImagePath = getRealPathFromURI(show);
       setPic(selectedImagePath, imageview);

        staticSpinner = (Spinner) findViewById(R.id.static_spinner);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.brew_array,
                        R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);
        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position > 0) {
                    // get spinner value
                    type = (String) parent.getItemAtPosition(position);
                    //Toast.makeText(getActivity(), tag, Toast.LENGTH_LONG).show();
                    // Messages();
                } else {
                    // show toast select gender
                  //  Toast.makeText(CameraActivity.this,"Select a category",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
               // Toast.makeText(CameraActivity.this,"Select a category",Toast.LENGTH_SHORT).show();
                // TODO Auto-generated method stub
            }
        });

    }

    private void setPic(String imagePath, ImageView destination) {
        int targetW = 400;
        int targetH = 400;
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

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public void uploadImage(){
        tag=editTag.getText().toString().trim();
        final String text = editText.getText().toString().trim();
        final String heads=head.getText().toString().trim();
        final String image = getStringImage(Correctbmp);
        String format = "dd-MMM-yyyy hh:mm:ss a";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        final String timeStamp = sdf.format(new Date());

        class UploadImage extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(CameraActivity.this,"Please wait...","uploading",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.trim().equals("Successfully Uploaded")) {
                    if (selectedImagePath != null)
                    {
                        deleteFile(new File(selectedImagePath));
                }
                    openProfile();
                } else {
                    Toast.makeText(CameraActivity.this, "Check details and try again.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                HashMap<String,String> param = new HashMap<String,String>();
                param.put(KEY_TEXT,text);
                param.put(KEY_IMAGE,image);
                param.put(KEY_LOCATION, place);
                param.put(KEY_NAME, name);
                param.put(KEY_TAG, tag);
                param.put("type",type);
                param.put("head",heads);
                param.put(KEY_TIME, timeStamp);
                param.put(KEY_ADDRESS,address);
                param.put("number",number);
                result = rh.sendPostRequest(UPLOAD_URL, param);
                return result;
            }
        }
        UploadImage u = new UploadImage();
        u.execute();
    }
    private void deleteFile(File delFile) {
        if (delFile == null) {
            return;
        }
        final File file = new File(delFile.getAbsolutePath());
        delFile = null;
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (file.exists()) {
                    file.delete();
                }
            }
        }.start();
    }
    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onClick(View v) {
        isInternetPresent = cd.isConnectingToInternet();
        if(isInternetPresent) {
            if(isLocationEnabled(this)) {
                location = appLocationService
                        .getLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    if (place != null) {

                            uploadImage();

                    } else {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LocationAddress locationAddress = new LocationAddress(CameraActivity.this);
                        locationAddress.getAddressFromLocation(latitude, longitude,
                                getApplicationContext(), new GeocoderHandler());
                        // Toast.makeText(CameraActivity.this,"Detecting current location...We need your current location for authenticity.",Toast.LENGTH_LONG).show();
                        button.setBackgroundResource(R.color.colorPrimary);
                        button.setText(R.string.send);
                    }
                } else {
                    location = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        if (place != null) {
                            uploadImage();
                        } else {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            LocationAddress locationAddress = new LocationAddress(CameraActivity.this);
                            locationAddress.getAddressFromLocation(latitude, longitude,
                                    getApplicationContext(), new GeocoderHandler());
                            //     Toast.makeText(CameraActivity.this,"Detecting current location...We need your current location for authenticity.",Toast.LENGTH_LONG).show();
                            button.setBackgroundResource(R.color.colorPrimary);
                            button.setText(R.string.send);
                        }
                    } else {
                        Toast.makeText(this, "Fetching Reporting Location", Toast.LENGTH_LONG).show();
                    }
                }
            }
            else
            {
                showSettingsAlert();
            }

        }
        else
        {
            Toast.makeText(CameraActivity.this,"You are not connected to Internet",Toast.LENGTH_SHORT).show();
        }
    }
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                CameraActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        CameraActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }


    private void openProfile() {
        // Starting TokenTest
        Intent i = new Intent(this, Feed.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish(); // Call once you redirect to another activity
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            String fulladdress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    fulladdress=bundle.getString("add");
                    break;
                default:
                    locationAddress = mycity;
                    fulladdress=mycity;
            }

            place = locationAddress;
            address=fulladdress;
        }
    }

    public void onBackPressed(){
        backpress = (backpress + 1);
        Toast.makeText(CameraActivity.this, " Press Back again to Exit ", Toast.LENGTH_SHORT).show();

        if (backpress>1) {
            Intent i=new Intent(CameraActivity.this,Feed.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (checker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
    }
    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
}
