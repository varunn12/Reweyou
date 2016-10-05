package in.reweyou.reweyou;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.fragment.AccountFragment;

public class MyCityActivity extends FragmentActivity {
    private static final int REQUEST_CODE = 0;
    Fragment fragment;
    PermissionsChecker checker;
    protected TextView headline;
    UserSessionManager session;
    static final String[] PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE};


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycity);
        session= new UserSessionManager(getApplicationContext());
        String city= session.getLoginLocation();
        String fontPath = "fonts/Roboto-Medium.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        headline = (TextView) findViewById(R.id.headline);
        headline.setText(city);
        headline.setTypeface(tf);
    }

}