package in.reweyou.reweyou;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class EditActivity extends AppCompatActivity {

    private String groupid;
    private String groupdescription;
    private String grouprules;
    private String groupimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        try {
            getSupportActionBar().setTitle("Edit Info");
            groupid = getIntent().getStringExtra("groupid");
            groupdescription = getIntent().getStringExtra("description");
            grouprules = getIntent().getStringExtra("rules");
            groupimage = getIntent().getStringExtra("image");
        } catch (Exception e) {
            e.printStackTrace();
        }


        EditText groupdes = (EditText) findViewById(R.id.groupdescription);
        EditText grouprul = (EditText) findViewById(R.id.grouprules);
        ImageView image = (ImageView) findViewById(R.id.groupimage);
        LinearLayout container = (LinearLayout) findViewById(R.id.container);

        groupdes.setText(groupdescription);
        grouprul.setText(grouprules);
        Glide.with(this).load(groupimage).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
