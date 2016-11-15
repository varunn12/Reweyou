package in.reweyou.reweyou;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        Intent in=getIntent();
        Bundle bundle = getIntent().getExtras();
        i = bundle.getString("myData");
        if (in.hasExtra("headline")) {
            text = bundle.getString("headline");
        }
        imageView = (TouchImageView) findViewById(R.id.image);
        TextView textview = (TextView)findViewById(R.id.text);
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
        if (text != null)
        textview.setText(text);
        else textview.setVisibility(View.GONE);
        isInternetPresent = cd.isConnectingToInternet();
        if(isInternetPresent) {
            showimage(i);
        }
        else
        {
            Toast.makeText(this, "You are not connected to Internet", Toast.LENGTH_LONG).show();
        }


    }

    private void showimage(String i) {
        progressBar.setVisibility(View.GONE);
        Glide.with(FullImage.this).load(i).override(300, 300).into(imageView);
    }


}
