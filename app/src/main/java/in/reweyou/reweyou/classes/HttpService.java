package in.reweyou.reweyou.classes;

/**
 * Created by Reweyou on 1/23/2016.
 */

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.reweyou.reweyou.WelcomeActivity;

import static in.reweyou.reweyou.utils.Constants.URL_VERIFY_OTP;

/**
 * Created by Ravi on 04/04/15.
 */
public class HttpService extends Service {
    private static String TAG = HttpService.class.getSimpleName();

    private final BroadcastReceiver mybroadcast = new SmsReceiver();


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate: called");

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mybroadcast, filter);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy: called");

        try {
            unregisterReceiver(mybroadcast);

        } catch (Exception e) {
            Log.d(TAG, "onDestroy: register exception");
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * Posting the OTP to server and activating the user
     *
     * @param
     */
    public void verifyOtp(final String otp) {

        unregisterReceiver(mybroadcast);
        sendOTPDismissVerifyDialog();


        Log.d("verifff", "called");

        sendMessageShowVerifyDialog();

        final UserSessionManager session = new UserSessionManager(getApplicationContext());
        final String number = session.getMobileNumber();
        // final ProgressDialog loading = ProgressDialog.show(this, "Authenticating", "Please wait", false, false);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_VERIFY_OTP, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("resp", response);
                sendMessageDismissVerifyDialog();
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
                    // session.setAuthToken(jsonObject.getString("token"));

                    if (responseObject.has("likes")) {
                        JSONArray jsonArray1 = responseObject.getJSONArray("likes");
                        List<String> likesList = new ArrayList<>();
                        String[] array = new String[jsonArray1.length()];
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
                    sendMessageDismissVerifyDialog();


                    Toast.makeText(getApplicationContext(), "Wrong OTP Please Try Again", Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override

            public void onErrorResponse(VolleyError error) {
                sendMessageDismissVerifyDialog();

                //   Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Something went wrong, Try again", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("otp", otp);
                params.put("number", number);

                Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }

        };

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }

    private void openProfile() {

        UserSessionManager session = new UserSessionManager(getApplicationContext());
        String place = session.getLoginLocation();
        String number = session.getMobileNumber();
        String name = session.getUsername();
        session.createUserRegisterSession(name, number, place);

        // Starting TokenTest
        Intent i = new Intent(this, WelcomeActivity.class);

        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i); // Call once you redirect to another activity
        stopSelf();

        Log.d("called", "serviice");
    }


    private void sendMessageShowVerifyDialog() {
        Intent intent = new Intent("verifyshow");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendMessageDismissVerifyDialog() {
        Intent intent = new Intent("verifydismiss");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendOTPDismissVerifyDialog() {
        Intent intent = new Intent("verifyotp");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}