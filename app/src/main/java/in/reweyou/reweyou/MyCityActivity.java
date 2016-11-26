package in.reweyou.reweyou;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import in.reweyou.reweyou.fragment.SecondFragment;

public class MyCityActivity extends AppCompatActivity {
    private Toolbar toolbar;
    //private static final int REQUEST_CODE = 0;
    //PermissionsChecker checker;
    //protected TextView headline;
    //UserSessionManager session;
    // static final String[] PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE};


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My City");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
       /* session= new UserSessionManager(getApplicationContext());
        String city= session.getLoginLocation();
        String fontPath = "fonts/Roboto-Medium.ttf";*/
        /*Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        headline = (TextView) findViewById(R.id.headline);
        headline.setText(city);
        headline.setTypeface(tf);*/

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SecondFragment frag = new SecondFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", 9);
        frag.setArguments(bundle);
        fragmentTransaction.add(R.id.myfragment, frag, "MyCITY");
        fragmentTransaction.commit();


    }

}