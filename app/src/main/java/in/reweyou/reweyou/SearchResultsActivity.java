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
import in.reweyou.reweyou.fragment.SecondFragment;
import in.reweyou.reweyou.model.FeedModel;

public class SearchResultsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<FeedModel> messagelist;
    private String query;
    private Toolbar toolbar;
    private UserSessionManager sessionManager;
    private int position;

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
        if (intent.hasExtra("position"))
            position = intent.getIntExtra("position", -1);



    }


    private void setdata() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SecondFragment frag = new SecondFragment();
        Bundle bundle = new Bundle();

        bundle.putInt("position", position);
        bundle.putString("query", query);


        frag.setArguments(bundle);
        fragmentTransaction.add(R.id.container, frag, "Search");
        fragmentTransaction.commit();

    }

}