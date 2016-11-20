package in.reweyou.reweyou;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.TouchImageView;

public class FullImage extends AppCompatActivity {
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
private String i;
    private ProgressBar progressBar;
    private DisplayImageOptions options;
    private TouchImageView imageView;
    private String text;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        initToolbar();

        Bundle bundle = getIntent().getExtras();
        i = bundle.getString("myData");
        imageView = (TouchImageView) findViewById(R.id.image);
         // Do it on Application start
        cd = new ConnectionDetector(FullImage.this);

         progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.irongrip)
                .showImageForEmptyUri(R.drawable.ic_reload)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        // Then later, when you want to display image

        isInternetPresent = cd.isConnectingToInternet();
        if(isInternetPresent) {
            showimage(i);
        }
        else
        {
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
        Glide.with(FullImage.this).load(i).into(imageView);
    }


}
