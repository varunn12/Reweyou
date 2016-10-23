package in.reweyou.reweyou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nineoldandroids.view.ViewHelper;

import in.reweyou.reweyou.fragment.WelcomeScreenFragment;


public class WelcomeScreen extends AppCompatActivity {

    static final int TOTAL_PAGES = 3;
    private static final String TAG = WelcomeScreen.class.getSimpleName();
    private static final int FIRST_PAGE = 0;

    ViewPager pager;
    PagerAdapter pagerAdapter;
    LinearLayout pageIndicator;
    Button btnSkip;
    Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.welcome_layout);

        initbtnSkip();
        initbtnDone();

        initViewPager();

    }

    private void initViewPager() {
        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlideAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(true, new CrossfadePageTransformer());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                /*if (position == TOTAL_PAGES - 2 && positionOffset > 0) {
                    if (isOpaque) {
                        pager.setBackgroundColor(Color.TRANSPARENT);
                        isOpaque = false;
                    }
                } else {
                    if (!isOpaque) {
                        pager.setBackgroundColor(getResources().getColor(R.color.primary_material_light));
                        isOpaque = true;
                    }
                }*/
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
                if (position == 2) {
                    btnSkip.setVisibility(View.GONE);
                    btnDone.setVisibility(View.VISIBLE);
                } else {
                    btnSkip.setVisibility(View.VISIBLE);
                    btnDone.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        buildCircles();


    }

    private void initbtnDone() {

        btnDone = Button.class.cast(findViewById(R.id.done));
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTutorial();
            }
        });
    }

    private void initbtnSkip() {
        btnSkip = Button.class.cast(findViewById(R.id.btn_skip));
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTutorial();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pager != null) {
            pager.clearOnPageChangeListeners();
        }
    }

    private void buildCircles() {
        pageIndicator = (LinearLayout) findViewById(R.id.circles);
        setIndicator(FIRST_PAGE);
    }

    private void setIndicator(int index) {

        for (int i = 0; i < TOTAL_PAGES; i++) {
            ImageView circle = (ImageView) pageIndicator.getChildAt(i);
            if (i == index) {
                //  circle.setColorFilter(getResources().getColor(R.color.whiteDot));
                circle.setImageResource(R.drawable.dot_white);
            } else {
                //  circle.setColorFilter(getResources().getColor(R.color.greyDot));
                circle.setImageResource(R.drawable.dot_grey);

            }

        }
    }

    private void endTutorial() {

        Intent intent = new Intent(WelcomeScreen.this, Signup.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlideAdapter extends FragmentStatePagerAdapter {

        public ScreenSlideAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            WelcomeScreenFragment welcomeScreenFragment = null;
            switch (position) {
                case 0:
                    welcomeScreenFragment = WelcomeScreenFragment.newInstance(R.layout.fragment_screen1);
                    break;
                case 1:
                    welcomeScreenFragment = WelcomeScreenFragment.newInstance(R.layout.fragment_screen2);
                    break;
                case 2:
                    welcomeScreenFragment = WelcomeScreenFragment.newInstance(R.layout.fragment_screen3);
                    break;

            }

            return welcomeScreenFragment;
        }

        @Override
        public int getCount() {
            return TOTAL_PAGES;
        }
    }

    public class CrossfadePageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();

            View backgroundView = page.findViewById(R.id.welcome_fragment);
            View text_head = page.findViewById(R.id.screen_heading);
            View text_content = page.findViewById(R.id.screen_desc);

            if (0 <= position && position < 1) {
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }
            if (-1 < position && position < 0) {
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }

            if (position <= -1.0f || position >= 1.0f) {

            } else if (position == 0.0f) {
            } else {
                if (backgroundView != null) {
                    ViewHelper.setAlpha(backgroundView, 1.0f - Math.abs(position));

                }

                if (text_head != null) {
                    ViewHelper.setTranslationX(text_head, pageWidth * position);
                    ViewHelper.setAlpha(text_head, 1.0f - Math.abs(position));
                }

                if (text_content != null) {
                    ViewHelper.setTranslationX(text_content, pageWidth * position);
                    ViewHelper.setAlpha(text_content, 1.0f - Math.abs(position));
                }
            }
        }
    }
}