package in.reweyou.reweyou;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.HttpService;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.customView.CustomDialogClass;
import in.reweyou.reweyou.fcm.Config;
import in.reweyou.reweyou.fcm.MyFirebaseInstanceIDService;

import static in.reweyou.reweyou.utils.Constants.URL_VERIFY_OTP;

public class Signup extends AppCompatActivity implements View.OnClickListener {


    public static final String KEY_USERNAME = "username";
    public static final String KEY_NUMBER = "number";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_OTP = "otp";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_DEVICE_ID = "deviceid";
    static final String[] PERMISSIONS = new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS};
    private static final int REQUEST_CODE = 0;
    private static final String REGISTER_URL = "https://www.reweyou.in/reweyou/signupnew.php";
    private static final int PERMISSION_ALL = 1;
    private static final String TAG = Signup.class.getSimpleName();
    private static final String PACKAGE_URL_SCHEME = "package:";
    UserSessionManager session;
    PermissionsChecker checker;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
        @Override
        protected String doInBackground(Void... params) {
            AdvertisingIdClient.Info idInfo = null;
            try {
                idInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            advertId = null;
            try {
                advertId = idInfo.getId();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            return advertId;
        }

        @Override
        protected void onPostExecute(String advertId) {
            // Toast.makeText(getApplicationContext(), advertId, Toast.LENGTH_SHORT).show();
            remarket();
        }

    };
    private EditText editTextUsername, editTextNumber, editTextConfirmOtp, editLocation;
    private AppCompatButton buttonConfirm;
    private Button buttonRegister;
    private TextView Read;
    private String username, number, place, token, advertId;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private boolean active;
    private ProgressDialog pd;
    private CustomDialogClass customDialogClass;
    private BroadcastReceiver showVerifyDialogReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showVerifiyingDialog();
        }
    };
    private BroadcastReceiver dismissVerifyDialogReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dismissVerifiyingDialog();
        }
    };
    private AlertDialog alertDialog;
    private BroadcastReceiver dismissOtpDialogReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dismissotpDialog();
        }
    };

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        session = new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(Signup.this);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextNumber = (EditText) findViewById(R.id.editTextNumber);
        editLocation = (EditText) findViewById(R.id.editLocation);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        Read = (TextView) findViewById(R.id.Read);


        Read.setPaintFlags(Read.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        task.execute();

        Read.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
        checker = new PermissionsChecker(this);
      /*  if (checker.lacksPermissions(PERMISSIONS)) {
            //   Snackbar.make(mToolbar, R.string.no_permissions, Snackbar.LENGTH_INDEFINITE).show();
            Toast.makeText(this, R.string.sms_permissions, Toast.LENGTH_LONG).show();
        } else {

        }
*/
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent) {
                //If the broadcast has received with success
                //that means device is registered successfully
                if (intent.getAction().equals(MyFirebaseInstanceIDService.REGISTRATION_SUCCESS)) {
                    //Getting the registration token from the intent
                    token = intent.getStringExtra("token");
                    //Displaying the token as toast
                    //      Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();
                    //if the intent is not with success then displaying error messages
                }
            }
        };
        //  Log.e("Token",token);
        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                //     Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled on this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
                //If play service is not supported
                //Displaying an error message
            } else {
                //   Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            //Starting intent to register device
            Intent itent = new Intent(this, MyFirebaseInstanceIDService.class);
            startService(itent);
            FirebaseMessaging.getInstance().subscribeToTopic("news");
        }


    }

    private void registerUser() {
        username = editTextUsername.getText().toString().trim();
        number = editTextNumber.getText().toString().trim();
        place = editLocation.getText().toString().trim();
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        token = pref.getString("regId", "0");

        final String deviceid = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Log.d("deviceid", deviceid);


        if (editTextUsername.getText().toString().trim().equals("")) {
            Toast.makeText(Signup.this, "username cannot be empty", Toast.LENGTH_LONG).show();
        } else if (editTextNumber.getText().toString().trim().equals("")) {
            Toast.makeText(Signup.this, "mobile number cannot be empty", Toast.LENGTH_LONG).show();
        } else if (editTextNumber.getText().toString().trim().length() != 10) {
            Toast.makeText(Signup.this, "mobile number must be of 10 digits", Toast.LENGTH_LONG).show();
        } else if (editLocation.getText().toString().trim().equals("")) {
            Toast.makeText(Signup.this, "location cannot be empty", Toast.LENGTH_LONG).show();
        } else {
            final ProgressDialog loading = ProgressDialog.show(Signup.this, "Signing in", "Please wait", false, false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            if (response.trim().equals("success")) {
                                loading.dismiss();
                                try {
                                    //Asking user to enter otp again
                                    session.setMobileNumber(number);
                                    session.setUsername(username);
                                    session.setLoginLocation(place);
                                    confirmOtp(number);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                loading.dismiss();
                                Toast.makeText(Signup.this, response, Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loading.dismiss();
                            Toast.makeText(Signup.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(KEY_USERNAME, username);
                    params.put(KEY_NUMBER, number);
                    params.put(KEY_LOCATION, place);
                    params.put(KEY_TOKEN, token);
                    params.put(KEY_DEVICE_ID, deviceid);

                    Log.e("Check", "Posting params: " + token);
                    Log.e("Check", "Posting params: " + place);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }

    private void openProfile() {
        //Intent intent = new Intent(this, IconTabs.class);
        //intent.putExtra(KEY_USERNAME, username);
        //startActivity(intent);

        session.createUserRegisterSession(username, number, place);

        // Starting TokenTest
        Intent i = new Intent(this, WelcomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(i);
        finish(); // Call once you redirect to another activity
    }

    @Override
    public void onClick(View v) {
        if (v == buttonRegister) {
            isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                startPermissionsActivity();
                // registerUser();

            } else {
                Toast.makeText(this, "You are not connected to Internet", Toast.LENGTH_LONG).show();
            }
        }
        if (v == Read) {
            isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                Uri uri = Uri.parse("https://www.reweyou.in/reweyou/termsConditions.php"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } else {
                Toast.makeText(this, "You are not connected to Internet", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                Signup.this);

        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        Signup.this.startActivity(intent);
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

    //This method would confirm the otp
    public void confirmOtp(String number) throws JSONException {


        Intent i = new Intent(getApplicationContext(), HttpService.class);
        startService(i);


        LayoutInflater li = LayoutInflater.from(Signup.this);
        View confirmDialog = li.inflate(R.layout.dialog_confirm, null);

        TextView editNum = (TextView) confirmDialog.findViewById(R.id.editNumber);
        editNum.setText("+91-" + number);
        final EditText otpField = (EditText) confirmDialog.findViewById(R.id.editTextOtp);
        Button confirm = (Button) confirmDialog.findViewById(R.id.buttonConfirm);


        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(Signup.this);
        alert.setView(confirmDialog);

        //Creating an alert dialog
        alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Displaying the alert dialog
        alertDialog.show();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (otpField.getText().toString().trim().length() > 0) {
                    alertDialog.dismiss();
                    verifyOtp(otpField.getText().toString());
                } else Toast.makeText(Signup.this, "OTP can't be empty", Toast.LENGTH_SHORT).show();
            }
        });


       /* customDialogClass = new CustomDialogClass(Signup.this, number);
        customDialogClass.show();*/

    }

    public void verifyOtp(final String otp) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_VERIFY_OTP, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("resp", response);

                try {
                    JSONArray jsonArray = new JSONArray(response);

                    JSONObject responseObject = jsonArray.getJSONObject(0);
                    JSONObject jsonObject = responseObject.getJSONObject("profile");
                    session.setAuthToken(jsonObject.getString("token"));
                    session.setUsername(jsonObject.getString("name"));
                    session.setMobileNumber(jsonObject.getString("number"));
                    session.setLoginLocation(jsonObject.getString("location"));
                    session.setProfilePicture(jsonObject.getString("profilepic"));
                    session.setDeviceid(jsonObject.getString("deviceid"));

                    if (responseObject.has("likes")) {
                        JSONArray jsonArray1 = responseObject.getJSONArray("likes");
                        List<String> likesList = new ArrayList<>();
                        for (int i = 0; i < jsonArray1.length(); i++) {
                            JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                            Log.d("jsonO", String.valueOf(jsonObject1));
                            likesList.add(jsonObject1.getString("postid"));

                        }
                        session.setLikesList(likesList);
                        Log.d("jsonlist", String.valueOf(likesList));
                    }
                    openProfile();


                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(getApplicationContext(), "Wrong OTP Please Try Again", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Something went wrong, Try again", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("otp", otp);
                params.put("number", session.getMobileNumber());
                return params;
            }

        };

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }

    @Override
    protected void onResume() {
        super.onResume();

      /*  if (checker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();

        }*/
        Log.w("Signup", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(MyFirebaseInstanceIDService.REGISTRATION_SUCCESS));
    }

    private void startPermissionsActivity() {
        // PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else registerUser();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {

                String permission = permissions[0];
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission

                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(Signup.this, permission);
                    if (!showRationale) {
                        showPermissionDeniedDialog();
                    } else
                        showPermissionRequiredDialog(permission);


                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    registerUser();
                }
            }
        }
    }

    private void showPermissionRequiredDialog(final String permission) {
        AlertDialogBox alertDialogBox = new AlertDialogBox(Signup.this, "Permission Required", getResources().getString(R.string.permission_required), "grant", "deny") {
            @Override
            public void onNegativeButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                registerUser();
            }

            @Override
            public void onPositiveButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                String[] p = {permission};
                ActivityCompat.requestPermissions(Signup.this, p, PERMISSION_ALL);

            }
        };
        alertDialogBox.setCancellable(true);
        alertDialogBox.show();
    }

    private void showPermissionDeniedDialog() {
        AlertDialogBox alertDialogBox = new AlertDialogBox(Signup.this, "Permission Denied", getResources().getString(R.string.permission_denied), "settings", "okay") {
            @Override
            public void onNegativeButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                registerUser();
            }

            @Override
            public void onPositiveButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                startAppSettings();

            }
        };
        alertDialogBox.setCancellable(true);
        alertDialogBox.show();
    }

    private void startAppSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }

    private void remarket() {

        String uri = String.format("https://www.googleadservices.com/pagead/conversion/870227648/?rdid=%1$s&lat=0&bundleid=in.reweyou.reweyou&idtype=advertisingid&remarketing_only=1&appversion=1.3.3.0&usage_tracking_enabled=1",
                advertId);

// prepare the Request
        StringRequest getRequest = new StringRequest(Request.Method.GET, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // display response
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("volley Error .................");
                    }
                }
        );

        // add it to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(getRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
        LocalBroadcastManager.getInstance(this).registerReceiver(showVerifyDialogReciever, new IntentFilter("verifyshow"));
        LocalBroadcastManager.getInstance(this).registerReceiver(dismissVerifyDialogReciever, new IntentFilter("verifydismiss"));
        LocalBroadcastManager.getInstance(this).registerReceiver(dismissOtpDialogReciever, new IntentFilter("verifyotp"));
    }

    @Override
    protected void onStop() {
        active = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(showVerifyDialogReciever);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dismissVerifyDialogReciever);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dismissOtpDialogReciever);

        super.onStop();
    }

    public void showVerifiyingDialog() {
        if (active) {
            if (customDialogClass != null)
                customDialogClass.dismiss();
            pd = new ProgressDialog(Signup.this);
            pd.setMessage("Verifying");
            pd.setCancelable(false);
            pd.show();
        }
    }

    public void dismissVerifiyingDialog() {
        if (pd != null) {

            pd.dismiss();
        }
    }

    public void dismissotpDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

}
