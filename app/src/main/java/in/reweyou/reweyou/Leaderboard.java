package in.reweyou.reweyou;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import in.reweyou.reweyou.fragment.AccountFragment;

public class Leaderboard extends FragmentActivity {
    private static final int REQUEST_CODE = 0;
    Fragment fragment;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
    }

}