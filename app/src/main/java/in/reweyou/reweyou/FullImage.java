package in.reweyou.reweyou;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.w3c.dom.Text;

import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.TouchImageView;

public class FullImage extends AppCompatActivity {
private String i;
    private ProgressBar progressBar;
    private DisplayImageOptions options;
    private TouchImageView imageView;
    private String text;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        Intent in=getIntent();
        Bundle bundle = getIntent().getExtras();
        i = bundle.getString("myData");
        text=bundle.getString("headline");
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
        textview.setText(text);
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
        ImageLoader.getInstance().displayImage(i, imageView,options);
    }


}
