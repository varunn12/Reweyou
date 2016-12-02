package in.reweyou.reweyou;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import in.reweyou.reweyou.classes.ConnectionDetector;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class FullImage extends AppCompatActivity {
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private String i;
    private ProgressBar progressBar;
    private DisplayImageOptions options;
    private ImageViewTouch imageView;
    private String text;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        initToolbar();

        Bundle bundle = getIntent().getExtras();
        i = bundle.getString("myData");
        imageView = (ImageViewTouch) findViewById(R.id.image);
        // Do it on Application start
        cd = new ConnectionDetector(FullImage.this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        // Then later, when you want to display image

        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            showimage(i);
        } else {
            Toast.makeText(this, "You are not connected to Internet", Toast.LENGTH_LONG).show();
        }


    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showimage(String i) {
        progressBar.setVisibility(View.GONE);
        Glide.with(FullImage.this).load(i).diskCacheStrategy(DiskCacheStrategy.SOURCE).fitCenter().error(R.drawable.ic_error).into(imageView);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
