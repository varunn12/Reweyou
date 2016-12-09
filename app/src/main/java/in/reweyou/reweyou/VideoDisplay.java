package in.reweyou.reweyou;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.EMVideoView;

import in.reweyou.reweyou.classes.ConnectionDetector;

public class VideoDisplay extends AppCompatActivity implements OnPreparedListener {

    private EMVideoView emVideoView;
    private TextView headline;
    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_display);

        headline = (TextView) findViewById(R.id.headline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final ConnectionDetector connectionDetector = new ConnectionDetector(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Intent i = getIntent();
        String url = i.getStringExtra("myData");
        String tag = i.getStringExtra("tag");
        String headline = i.getStringExtra("headline");

        if (headline != null) {
            if (!headline.isEmpty()) {
                this.headline.setVisibility(View.VISIBLE);
                this.headline.setText(headline);
            }
        }


        getSupportActionBar().setTitle("#" + tag);

        Log.d("ewdwefdwef", url);

        emVideoView = (EMVideoView) findViewById(R.id.video_view);
        emVideoView.setOnPreparedListener(this);
        Log.d("ewdwefdweswqdqwf", String.valueOf(emVideoView.getBufferPercentage()));
        emVideoView.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError() {
                emVideoView.stopPlayback();
                if (!connectionDetector.isConnectingToInternet()) {
                    AlertDialogBox alertDialogBox = new AlertDialogBox(VideoDisplay.this, "Error", "Network error! cannot play video.", "okay", null) {
                        @Override
                        public void onNegativeButtonClick(DialogInterface dialog) {

                        }

                        @Override
                        public void onPositiveButtonClick(DialogInterface dialog) {
                            dialog.dismiss();
                            finish();
                        }

                    };
                    alertDialogBox.setCancellable(false);
                    alertDialogBox.show();
                } else {
                    AlertDialogBox alertDialogBox = new AlertDialogBox(VideoDisplay.this, "Error", "Can't play video.", "okay", null) {
                        @Override
                        public void onNegativeButtonClick(DialogInterface dialog) {

                        }

                        @Override
                        public void onPositiveButtonClick(DialogInterface dialog) {
                            dialog.dismiss();
                            finish();
                        }
                    };
                    alertDialogBox.setCancellable(false);
                    alertDialogBox.show();
                }
                Log.w("error", "error");

                return false;
            }
        });
        emVideoView.getVideoControls().setCanHide(false);
        //For now we just picked an arbitrary item to play.  More can be found at
        //https://archive.org/details/more_animation
        emVideoView.setVideoURI(Uri.parse(url));

    }

    @Override
    public void onPrepared() {
        //Starts the video playback as soon as it is ready
        emVideoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        emVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
