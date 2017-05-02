package in.reweyou.reweyou;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;

import in.reweyou.reweyou.fragment.CreateFragment;
import in.reweyou.reweyou.fragment.ExploreFragment;
import in.reweyou.reweyou.fragment.ForumFragment;
import in.reweyou.reweyou.utils.Utils;

public class ForumMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView back = (ImageView) findViewById(R.id.backgroundimageview);


        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab tabCall1 = tabLayout.getTabAt(0);
        tabCall1.setIcon(R.drawable.tab1_selector);
        TabLayout.Tab tabCall2 = tabLayout.getTabAt(1);
        tabCall2.setIcon(R.drawable.tab2_selector);
        TabLayout.Tab tabCall3 = tabLayout.getTabAt(2);
        tabCall3.setIcon(R.drawable.tab3_selector);
        TabLayout.Tab tabCall4 = tabLayout.getTabAt(3);
        tabCall4.setIcon(R.drawable.tab4_selector);

        switch (Utils.backgroundCode) {
            case 0:
                break;
            case 1:
                back.setColorFilter(ForumMainActivity.this.getResources().getColor(R.color.main_background_blue_alpha));
                tabLayout.setSelectedTabIndicatorColor(ForumMainActivity.this.getResources().getColor(R.color.main_background_blue));
                break;
            case 2:
                back.setColorFilter(ForumMainActivity.this.getResources().getColor(R.color.main_background_green_alpha));
                tabLayout.setSelectedTabIndicatorColor(ForumMainActivity.this.getResources().getColor(R.color.main_background_green));
                break;
            case 3:
                back.setColorFilter(ForumMainActivity.this.getResources().getColor(R.color.main_background_pink_alpha));
                tabLayout.setSelectedTabIndicatorColor(ForumMainActivity.this.getResources().getColor(R.color.main_background_pink));
                break;
        }

    }

    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forum, menu);
        return true;
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        private String[] tabs = getResources().getStringArray(R.array.tabs);

        private PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {


            if (position == 1)
                return new ExploreFragment();
            else if (position == 2)
                return new CreateFragment();
            else
                return new ForumFragment();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }


        @Override
        public int getCount() {
            return tabs.length;
        }


    }
}
