package in.reweyou.reweyou.classes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.reweyou.reweyou.Signup;

/**
 * Created by Reweyou on 12/17/2015.
 */
public class UserSessionManager {

    public static final String KEY_NAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PIC = "pic";
    public static final String KEY_NUMBER = "number";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_MOBILE_NUMBER = "mobilenumber";
    public static final String KEY_LOGIN_LOCATION = "loginlocation";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_CITY_LOCATION = "citylocation";
    public static final String KEY_LOGIN_FULLNAME = "fullname";
    // Sharedpref file name
    private static final String PREFER_NAME = "ReweyouPref";
    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    private static final String KEY_AUTH_TOKEN = "authtoken";
    private static final String KEY_DEVICE_ID = "deviceid";
    // Shared Preferences reference
    SharedPreferences pref;
    // Editor reference for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    private List<String> likesList;


    // Constructor
    public UserSessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public String getCityLocation() {
        return pref.getString(KEY_CITY_LOCATION, null);
    }

    public void setCityLocation(String location) {
        editor.putString(KEY_CITY_LOCATION, location);
        editor.commit();
    }

    public String getCategory() {
        return pref.getString(KEY_CATEGORY, null);
    }

    public void setCategory(String location) {
        editor.putString(KEY_CATEGORY, location);
        editor.commit();
    }

    public String getProfilePicture() {
        return pref.getString(KEY_PIC, null);
    }

    public void setProfilePicture(String image) {
        editor.putString(KEY_PIC, image);
        editor.commit();
    }

    public String getDeviceid() {
        return pref.getString(KEY_DEVICE_ID, null);
    }

    public void setDeviceid(String deviceid) {
        editor.putString(KEY_DEVICE_ID, deviceid);
        editor.commit();

    }

    public String getMobileNumber() {
        return pref.getString(KEY_MOBILE_NUMBER, "0");
    }

    public void setMobileNumber(String number) {
        editor.putString(KEY_MOBILE_NUMBER, number);
        editor.commit();
    }

    public String getUsername() {
        return pref.getString(KEY_LOGIN_FULLNAME, null);
    }

    public void setUsername(String fullname) {
        editor.putString(KEY_LOGIN_FULLNAME, fullname);
        editor.commit();
    }

    public String getLoginLocation() {
        return pref.getString(KEY_LOGIN_LOCATION, "Unknown");
    }

    public void setLoginLocation(String location) {
        editor.putString(KEY_LOGIN_LOCATION, location);
        editor.commit();
    }

    //Create login session and Register
    public void createUserRegisterSession(String username, String number, String place) {
        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, username);

        //Storing number
        editor.putString(KEY_NUMBER, number);


        editor.putString(KEY_LOCATION, place);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else do anything
     */

    public boolean checkLoginSplash() {
        // Check login status
        return !this.isUserLoggedIn();
    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {

        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<String, String>();

        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, "Add Email"));

        //user number
        user.put(KEY_LOCATION, pref.getString(KEY_LOCATION, null));

        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

        user.put(KEY_NUMBER, pref.getString(KEY_NUMBER, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {

        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, Signup.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    // Check for login
    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    public boolean getFromSP(String key) {
        return pref.getBoolean(key, true);
    }

    public void saveInSp(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void setAuthToken(String token) {
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.commit();
    }

    public String getKeyAuthToken() {
        return pref.getString(KEY_AUTH_TOKEN, "default");
    }

    public List<String> getLikesList() {

        List<String> templist = new ArrayList<>();
        templist.add("1");
        Set<String> foo = new HashSet<String>(templist);

        List<String> list = new ArrayList<String>(pref.getStringSet("likesList", foo));

        return list;
    }

    public void setLikesList(List<String> likesList) {
        Set<String> list = new HashSet<String>(likesList);
        editor.putStringSet("likesList", list);
        editor.commit();
    }

    public void addlike(String id) {
        Set<String> set = new HashSet<String>(pref.getStringSet("likesList", new HashSet<String>()));
        set.add(id);
        Log.d("add set", String.valueOf(set));
        editor.putStringSet("likesList", set);
        editor.apply();

    }

    public void removelike(String id) {
        Set<String> set = new HashSet<String>(pref.getStringSet("likesList", new HashSet<String>()));
        set.remove(id);
        Log.d("remove set", String.valueOf(set));
        editor.putStringSet("likesList", set);
        editor.apply();
    }
}