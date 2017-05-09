package in.reweyou.reweyou.fcm;

/**
 * Created by Reweyou on 10/14/2016.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import in.reweyou.reweyou.classes.UserSessionManager;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
    public static final String REGISTRATION_ERROR = "RegistrationError";
    public static final String URL_UPDATE_TOKEN = "https://www.reweyou.in/reweyou/firebasetoken.php";
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "sendRegistrationToServer: " + refreshedToken);

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(REGISTRATION_SUCCESS);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
            UserSessionManager session = new UserSessionManager(getApplicationContext());
            // final ProgressDialog loading = ProgressDialog.show(this, "Authenticating", "Please wait", false, false);
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    URL_UPDATE_TOKEN, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    //if the server response is success
                    if(response.equalsIgnoreCase("success")){
                        //dismissing the progressbar
                        //     loading.show();
                        Log.e(TAG, "Token Sent: " + token);
                        //Starting a new activity
                    }else{
                        //Displaying a toast if the otp entered is wrong
                       // Toast.makeText(getApplicationContext(),"Wrong OTP Please Try Again",Toast.LENGTH_LONG).show();

                    }
                }
            },new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //   Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                //    Toast.makeText(getApplicationContext(),"Something went wrong, Try again",Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", token);

                    Log.e(TAG, "Posting params: " + params.toString());
                    return params;
                }

            };

            // Adding request to request queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(strReq);
        }




    private void storeRegIdInPref(String token) {
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }
}