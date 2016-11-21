package in.reweyou.reweyou;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class Raw extends AppCompatActivity {


    private LinearLayout logo;
    private RelativeLayout preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Upload a Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        logo = (LinearLayout) findViewById(R.id.logo);
        preview = (RelativeLayout) findViewById(R.id.previewLayout);

        if (preview.getVisibility() == View.VISIBLE) {
            logo.setVisibility(View.GONE);
        } else logo.setVisibility(View.VISIBLE);

    }

}
