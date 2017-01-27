package in.reweyou.reweyou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.fragment.BaseFragment;
import in.reweyou.reweyou.model.FeedModel;
import in.reweyou.reweyou.utils.ReportLoadingConstant;

public class SearchResultsActivity extends AppCompatActivity {

    private String query;
    private Toolbar toolbar;
    private UserSessionManager sessionManager;
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        sessionManager = new UserSessionManager(this);
        handleIntent(getIntent());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(query);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (query != null)
            setdata();
    }


    private void handleIntent(Intent intent) {

        if (intent.hasExtra("query"))
            query = intent.getStringExtra("query");
        if (intent.hasExtra("number"))
            number = intent.getStringExtra("number");


    }


    private void setdata() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        BaseFragment frag = new BaseFragment();
        Bundle bundle = new Bundle();

        bundle.putInt(BaseFragment.TAG_FRAGMENT_CATEGORY, ReportLoadingConstant.FRAGMENT_CATEGORY_REPORTER_PROFILE);
        bundle.putString(BaseFragment.TAG_REPORTER_NUMBER, number);


        frag.setArguments(bundle);
        fragmentTransaction.add(R.id.container, frag, "Search");
        fragmentTransaction.commit();

    }

}