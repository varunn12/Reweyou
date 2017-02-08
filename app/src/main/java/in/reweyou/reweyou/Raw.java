package in.reweyou.reweyou;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.fragment.DumFrag;
import in.reweyou.reweyou.model.FeedModel;


public class Raw extends AppCompatActivity {

    public static List<FeedModel> list;
    private ViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raw);

        UserSessionManager sessionManager = new UserSessionManager(this);

        list = sessionManager.getSaveNewsReportsinCache();


        vp = (ViewPager) findViewById(R.id.vp);
        vp.setAdapter(new Pagedapet(getSupportFragmentManager()));
    }

    private class Pagedapet extends FragmentStatePagerAdapter {

        public Pagedapet(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            DumFrag fragment = new DumFrag();
            Bundle bundle = new Bundle();
            bundle.putInt("a", position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return 10;
        }
    }

}
