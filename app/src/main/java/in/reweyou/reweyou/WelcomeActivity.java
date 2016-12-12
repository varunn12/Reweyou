package in.reweyou.reweyou;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import io.codetail.animation.ViewAnimationUtils;


public class WelcomeActivity extends AppCompatActivity {

    private Animator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        final ImageView myView = (ImageView) findViewById(R.id.img);

        // get the center for the clipping circle

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int cx = (myView.getLeft() + myView.getRight()) / 2;
                int cy = (myView.getTop() + myView.getBottom()) / 2;

                // get the final radius for the clipping circle
                int dx = Math.max(cx, myView.getWidth());
                int dy = Math.max(cy, myView.getHeight());
                float finalRadius = (float) Math.hypot(dx, dy);

                // Android native animator
                animator =
                        ViewAnimationUtils.createCircularReveal(myView, 0, myView.getHeight(), 0, (float) (1.5 * myView.getWidth()));
                animator.setInterpolator(new AccelerateInterpolator());
                animator.setDuration(800);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        myView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(WelcomeActivity.this, Feed.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            }
                        }, 400);

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                        finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                animator.start();
            }
        }, 800);


    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
