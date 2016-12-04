package in.reweyou.reweyou;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import in.reweyou.reweyou.fragment.CommentsFragment;
import in.reweyou.reweyou.fragment.SecondFragment;

import static in.reweyou.reweyou.classes.UploadOptions.PERMISSION_ALL_IMAGE;

public class SinglePostActivity extends AppCompatActivity {

    private static final String PACKAGE_URL_SCHEME = "package:";
    public ViewPager viewPager;
    public FragmentCommunicator fragmentCommunicator;
    public FragmentCommunicator2 fragmentCommunicator2;
    int SELECT_FILE = 1;
    private Toolbar mToolbar;
    private TabLayout tabLayout;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post_acitivty);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        query = bundle.getString("postid");

        initToolbar();
        initViewPagerAndTabs();


    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Reweyou");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SinglePostActivity.this, Feed.class));
                finish();
            }
        });
    }

    private void initViewPagerAndTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                hideSoftKeyboard();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void changetab() {
        viewPager.setCurrentItem(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_post, menu);
        // Retrieve the SearchView and plug it into SearchManager
        // Associate searchable configuration with the Sea

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_home:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                startActivity(new Intent(SinglePostActivity.this, Feed.class));
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && reqCode == SELECT_FILE && data != null) {
            Uri uriFromPath = data.getData();
            String path = uriFromPath.toString();
            /*Intent intent = new Intent(Comments1.this, UpdateImage.class);
            intent.putExtra("path", show);
            intent.putExtra("postid", i);
            startActivity(intent);*/
            if (fragmentCommunicator2 != null)
                fragmentCommunicator2.passDataToFragment2(path);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL_IMAGE:

                String permission = permissions[0];
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission

                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(SinglePostActivity.this, permission);
                    if (!showRationale) {
                        showPermissionDeniedDialog();
                    } else
                        showPermissionRequiredDialog(permission);


                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    if (fragmentCommunicator != null)
                        fragmentCommunicator.passDataToFragment();
                }

                break;


        }
    }

    private void showPermissionRequiredDialog(final String permission) {
        AlertDialogBox alertDialogBox = new AlertDialogBox(SinglePostActivity.this, "Permission Required", getResources().getString(R.string.permission_required_image), "grant", "deny") {
            @Override
            public void onNegativeButtonClick(DialogInterface dialog) {
                dialog.dismiss();
            }

            @Override
            public void onPositiveButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                String[] p = {permission};
                ActivityCompat.requestPermissions(SinglePostActivity.this, p, PERMISSION_ALL_IMAGE);

            }
        };
        alertDialogBox.setCancellable(true);
        alertDialogBox.show();
    }

    private void showPermissionDeniedDialog() {
        AlertDialogBox alertDialogBox = new AlertDialogBox(SinglePostActivity.this, "Permission Denied", getResources().getString(R.string.permission_denied_image), "settings", "okay") {
            @Override
            public void onNegativeButtonClick(DialogInterface dialog) {
                dialog.dismiss();

            }

            @Override
            public void onPositiveButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                startAppSettings();

            }
        };
        alertDialogBox.setCancellable(true);
        alertDialogBox.show();
    }

    private void startAppSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        private String[] tabs = getResources().getStringArray(R.array.tabs_single_activity);

        private PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    SecondFragment fragment2 = new SecondFragment();
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("query", query);
                    bundle2.putInt("position", 15);
                    fragment2.setArguments(bundle2);
                    Log.d("getItem", "" + position);
                    return fragment2;
                case 1:
                    CommentsFragment fragment = new CommentsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("myData", query);
                    fragment.setArguments(bundle);
                    Log.d("getItem", "" + position);
                    return fragment;
                default:
                    return null;
            }

        }


        @Override
        public int getCount() {
            return tabs.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }
    }

}
