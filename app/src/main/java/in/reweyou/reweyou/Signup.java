package in.reweyou.reweyou;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.HttpService;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.fcm.MyFirebaseInstanceIDService;

public class Signup extends AppCompatActivity implements View.OnClickListener {
    public static final String URL_VERIFY_OTP = "https://www.reweyou.in/reweyou/verify_otp.php";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_NUMBER = "number";
    public static final String KEY_LOCATION="location";
    public static final String KEY_OTP = "otp";
    public static final String KEY_TOKEN = "token";
    static final String[] PERMISSIONS = new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS};
    private static final int REQUEST_CODE = 0;
    private static final String REGISTER_URL = "https://www.reweyou.in/reweyou/signup.php";
    UserSessionManager session;
    PermissionsChecker checker;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private EditText editTextUsername, editTextNumber,editTextConfirmOtp, editLocation;
    private AppCompatButton buttonConfirm;
    private Button buttonRegister;
    private Location location;
    private TextView Read;
    private String otp;
    private RequestQueue requestQueue;
    private String username, number, place, token;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextInputLayout inputLayoutName;
    private TextInputLayout inputLayoutNumber;
    private TextInputLayout inputLayoutCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_username);
        inputLayoutNumber = (TextInputLayout) findViewById(R.id.input_layout_number);
        inputLayoutCity = (TextInputLayout) findViewById(R.id.input_layout_city);


        session = new UserSessionManager(getApplicationContext());
        requestQueue = Volley.newRequestQueue(this);
        cd = new ConnectionDetector(Signup.this);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextNumber= (EditText) findViewById(R.id.editTextNumber);
        editLocation=(EditText)findViewById(R.id.editLocation);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        Read = (TextView)findViewById(R.id.Read);


        Read.setPaintFlags(Read.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        Read.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
        checker = new PermissionsChecker(this);
        if (checker.lacksPermissions(PERMISSIONS)) {
            //   Snackbar.make(mToolbar, R.string.no_permissions, Snackbar.LENGTH_INDEFINITE).show();
            Toast.makeText(this,R.string.sms_permissions,Toast.LENGTH_LONG).show();
        }
        else {

        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent) {
                //If the broadcast has received with success
                //that means device is registered successfully
                if(intent.getAction().equals(MyFirebaseInstanceIDService.REGISTRATION_SUCCESS)){
                    //Getting the registration token from the intent
                    token = intent.getStringExtra("token");
                    //Displaying the token as toast
                    Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();
                    //if the intent is not with success then displaying error messages
                }
            }
        };
      //  Log.e("Token",token);
        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            //Starting intent to register device
            Intent itent = new Intent(this, MyFirebaseInstanceIDService.class);
            startService(itent);
            Toast.makeText(this,"Request Sent",Toast.LENGTH_SHORT).show();
        }



    }

    private void registerUser() {
        username = editTextUsername.getText().toString().trim();
        number = editTextNumber.getText().toString().trim();
        place=editLocation.getText().toString().trim();
        token=session.getFirebaseToken();

        if (editTextUsername.getText().toString().trim().equals("")) {
            editTextUsername.setError("Required!");
            // editTextUsername.setHint("Enter Email");
        }
        else if (editTextNumber.getText().toString().trim().equals("")) {
            editTextNumber.setError("Required!");

            //editTextPassword.setHint("Enter password");
        }
        else if (editLocation.getText().toString().trim().equals("")) {
            editLocation.setError("Required!");
            //editTextPassword.setHint("Enter password");
        }
        else {
            final ProgressDialog loading = ProgressDialog.show(Signup.this, "Authenticating", "Please wait", false, false);
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
                                    confirmOtp();
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
                    Log.e("Check", "Posting params: " + token);
                    Log.e("Check", "Posting params: " + location);
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
        Intent i = new Intent(this, Welcome.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish(); // Call once you redirect to another activity
    }


    @Override
    public void onClick(View v) {
        if(v == buttonRegister){
            isInternetPresent = cd.isConnectingToInternet();
            if(isInternetPresent) {
                if (checker.lacksPermissions(PERMISSIONS)) {
                    //   Snackbar.make(mToolbar, R.string.no_permissions, Snackbar.LENGTH_INDEFINITE).show();
                    // Toast.makeText(this,R.string.sms_permissions,Toast.LENGTH_LONG).show();
                }
                else {
                    registerUser();
                }
            }
            else
            {
                Toast.makeText(this, "You are not connected to Internet", Toast.LENGTH_LONG).show();
            }
        }
        if(v==Read){
            isInternetPresent = cd.isConnectingToInternet();
            if(isInternetPresent) {
                Uri uri = Uri.parse("https://www.reweyou.in/reweyou/termsConditions.php"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
            else
            {
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
    public void confirmOtp() throws JSONException {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_confirm, null);

        //Initizliaing confirm button fo dialog box and edittext of dialog box
        buttonConfirm = (AppCompatButton) confirmDialog.findViewById(R.id.buttonConfirm);
        editTextConfirmOtp = (EditText) confirmDialog.findViewById(R.id.editTextOtp);
        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();

        //Displaying the alert dialog
        alertDialog.show();

        //On the click of the confirm button from alert dialog
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp();
            }
        });
    }
    private void verifyOtp() {
        otp = editTextConfirmOtp.getText().toString().trim();
        if (!otp.isEmpty()) {
            Intent grabIntent = new Intent(getApplicationContext(), HttpService.class);
            grabIntent.putExtra("otp", otp);
            startService(grabIntent);
        } else {
            Toast.makeText(getApplicationContext(), "Please enter the OTP", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (checker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
            Log.w("Signup", "onResume");
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(MyFirebaseInstanceIDService.REGISTRATION_SUCCESS));


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

}
