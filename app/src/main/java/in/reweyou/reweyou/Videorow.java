package in.reweyou.reweyou;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * Created by Reweyou on 2/1/2016.
 */
public class Videorow extends Activity {

    // Declare variables
    ProgressDialog pDialog;
    VideoView videoview;
    ProgressBar progressbar;
    protected TextView video;
    protected TextView Headline;
    protected TextView place;
    protected TextView Date;
    protected TextView tag;
    protected Button share;
    protected TextView tv;
    protected TextView From;
    private String url;
    private String date;
    private String from;
    private String location;
    private String category;
    private String reviews;
    private String headline;
    // Insert your Video URL
    String VideoURL = "https://www.reweyou.in/uploads/VID1454333655915_1517396294.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the layout from video_main.xml
        setContentView(R.layout.videorow);
        // Find your VideoView in your video_main.xml layout
        videoview = (VideoView) findViewById(R.id.video);
        progressbar = (ProgressBar) findViewById(R.id.progressBar2);
        // Execute StreamVideo AsyncTask
        Intent in=getIntent();
        Bundle bundle = getIntent().getExtras();
        url = bundle.getString("myData");
        Headline = (TextView) findViewById(R.id.Who);
        place = (TextView) findViewById(R.id.place);
        Date = (TextView) findViewById(R.id.date);
        tag = (TextView) findViewById(R.id.tag);
        share = (Button)findViewById(R.id.share);
        tv = (TextView) findViewById(R.id.tv);
        From=(TextView) findViewById(R.id.from);

        headline =bundle.getString("headline");
        reviews=bundle.getString("reviews");
        category=bundle.getString("tag");
        location=bundle.getString("place");
        date=bundle.getString("date");
        from=bundle.getString("from");

        Headline.setText(headline);
        place.setText(location);
        Date.setText(date);
        tag.setText(category);
        share.setVisibility(View.GONE);
        From.setText(from);


        // Create a progressbar
      /*  pDialog = new ProgressDialog(Videorow.this);
        // Set progressbar title
        pDialog.setTitle("Android Video Streaming Tutorial");
        // Set progressbar message
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbar
        pDialog.show();
*/              progressbar.setVisibility(View.VISIBLE);
        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    Videorow.this);
            mediacontroller.setAnchorView(videoview);
            // Get the URL from String VideoURL
            Uri video = Uri.parse(url);
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                // pDialog.dismiss();
                progressbar.setVisibility(View.GONE);
                videoview.start();
            }
        });

    }

}