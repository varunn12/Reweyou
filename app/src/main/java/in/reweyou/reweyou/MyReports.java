package in.reweyou.reweyou;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class MyReports extends FragmentActivity {
    private static final int REQUEST_CODE = 0;
    Fragment fragment;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);
    }

}