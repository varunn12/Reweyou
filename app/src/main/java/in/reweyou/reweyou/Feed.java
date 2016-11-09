package in.reweyou.reweyou;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.fragment.SecondFragment;

public class Feed extends AppCompatActivity implements View.OnClickListener {
    static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE = 0;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1, REQUEST_VIDEO = 3;
    Button camera, gallery, notify, text;
    UserSessionManager session;
    Uri uri;
    PermissionsChecker checker;
    ImageLoader imageLoader = ImageLoader.getInstance();
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    boolean doubleBackToExitPressedOnce = false;
    private TabLayout tabLayout;
    private DisplayImageOptions options;
    private DrawerLayout drawerLayout;
    private String mCurrentPhotoPath;
    private String videoFilePath;
    private Toolbar mToolbar;
    private FloatingActionButton floatingActionButton;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        // Session class instance

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder getImageFrom = new AlertDialog.Builder(Feed.this);
                getImageFrom.setTitle("Select Image from:");
                final CharSequence[] opsChars = {getResources().getString(R.string.takepic), getResources().getString(R.string.opengallery)};
                getImageFrom.setItems(opsChars, new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if (checker.lacksPermissions(PERMISSIONS)) {
                                startPermissionsActivity();
                            } else {

                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                File photoFile = null;
                                photoFile = getOutputMediaFile();
                                uri = Uri.fromFile(photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                                UILApplication.getInstance().trackEvent("Image", "Camera", "For Pics");
                            }

                        } else if (which == 1) {
                            if (checker.lacksPermissions(PERMISSIONS)) {
                                startPermissionsActivity();
                            } else {
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                // 2. pick image only
                                intent.setType("image/*");
                                // 3. start activity
                                startActivityForResult(intent, SELECT_FILE);
                                UILApplication.getInstance().trackEvent("Gallery", "Gallery", "For Pics");
                            }
                        }
                        dialog.dismiss();
                    }
                });
                getImageFrom.show();
            }
        });
        session = new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(Feed.this);
        checker = new PermissionsChecker(this);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.irongrip)
                .showImageForEmptyUri(R.drawable.download)
                .showImageOnFail(R.drawable.download)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        initToolbar();
        initViewPagerAndTabs();
        initNavigationDrawer();

        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        camera = (Button) findViewById(R.id.camera);
        gallery = (Button) findViewById(R.id.gallery);
        notify = (Button) findViewById(R.id.notify);
        text = (Button) findViewById(R.id.text);
        camera.setOnClickListener(this);
        gallery.setOnClickListener(this);
        notify.setOnClickListener(this);
        text.setOnClickListener(this);
        camera.setTypeface(font);
        gallery.setTypeface(font);
        notify.setTypeface(font);
        text.setTypeface(font);
        // Create default options which will be used for every
//  displayImage(...) call if no options will be passed to this method
        // Do it on Application start

    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //setTitle("Trending");
        //  mToolbar.setLogo(R.drawable.logo_plain);
    }

    private void initViewPagerAndTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
/*

        pagerAdapter.addFragment(getFragment(0), getString(R.string.tab_1));
        pagerAdapter.addFragment(getFragment(1), getString(R.string.tab_4));
        pagerAdapter.addFragment(getFragment(2), getString(R.string.tab_5));
*/

        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        //setupTabIcons();
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < 3; i++) {
                    if (i == position) {
                        if (position == 0) {
                            String title = "Trending";
                            // getSupportActionBar().setTitle(title);
                            //   tabLayout.getTabAt(i).setIcon(R.drawable.ic_newspaper_white);
                            // mToolbar.setLogo(R.drawable.ic_newspaper_white);
                        } else if (position == 1) {
                            String title1 = "Home";
                            //  getSupportActionBar().setTitle(title1);
                            //  tabLayout.getTabAt(i).setIcon(R.drawable.ic_map_marker_radius_white);
                            //  mToolbar.setLogo(R.drawable.ic_map_marker_radius_white);
                        } else if (position == 2) {
                            String title2 = "My City";
                            // getSupportActionBar().setTitle(title2);
                            //  tabLayout.getTabAt(i).setIcon(R.drawable.ic_account_location_white);
                        } else {
                            String title2 = "Trending";
                            // getSupportActionBar().setTitle(title2);
                            // tabLayout.getTabAt(i).setIcon(R.drawable.ic_account_location_white);
                        }
                    } else {
                        if (i == 0) {
                            //  tabLayout.getTabAt(i).setIcon(R.drawable.ic_newspaper_black);
                        } else if (i == 1) {
                            //      tabLayout.getTabAt(i).setIcon(R.drawable.ic_map_marker_radius_black);
                        } else if (i == 2) {
                            //   tabLayout.getTabAt(i).setIcon(R.drawable.ic_map_marker_radius_black);
                        } else {
                            // tabLayout.getTabAt(i).setIcon(R.drawable.ic_account_location);
                        }
                    }
                }
            }

            @Override
            public void onPageScrolled(int position, float offset, int offsetPixel) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private Fragment getFragment(int i) {
        SecondFragment fragment = new SecondFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", i);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void setupTabIcons() {
        int[] tabIcons = {
                R.drawable.ic_newspaper_white,
                R.drawable.ic_map_marker_radius_black,
                R.drawable.ic_magnify,
                R.drawable.ic_error


        };

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
//        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    private boolean isDeviceSupportCamera() {
        return getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera:
                if (checker.lacksPermissions(PERMISSIONS)) {
                    startPermissionsActivity();
                } else {
          /*      Intent cameraIntent = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                 startActivityForResult(cameraIntent, REQUEST_CAMERA);
            */
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File photoFile = null;
                    photoFile = getOutputMediaFile();
                    uri = Uri.fromFile(photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                    UILApplication.getInstance().trackEvent("Image", "Camera", "For Pics");
                }
                break;

            case R.id.gallery:
                if (checker.lacksPermissions(PERMISSIONS)) {
                    startPermissionsActivity();
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    // 2. pick image only
                    intent.setType("image/*");
                    // 3. start activity
                    startActivityForResult(intent, SELECT_FILE);
                    UILApplication.getInstance().trackEvent("Gallery", "Gallery", "For Pics");
                }
                break;
            case R.id.notify:
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Intent notifications = new Intent(Feed.this, Notifications.class);
                    startActivity(notifications);
                } else {
                    Toast.makeText(this, "You are not connected to Internet", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.text:
                Intent profile = new Intent(Feed.this, MyProfile.class);
                startActivity(profile);
                break;

            case R.id.fab:
                Toast.makeText(Feed.this, "hey", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Reweyou");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Reweyou", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
        mCurrentPhotoPath = "file:" + mediaFile.getAbsolutePath();
        return mediaFile;
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && reqCode == SELECT_FILE && data != null) {
            Uri uriFromPath = data.getData();
            String show = uriFromPath.toString();
            Intent intent = new Intent(this, ShowImage.class);
            intent.putExtra("path", show);
            startActivity(intent);
        }
        if (resCode == Activity.RESULT_OK && reqCode == REQUEST_CAMERA) {
            String show = uri.toString();
            Intent intent = new Intent(Feed.this, CameraActivity.class);
            Log.d("URI", show);
            Log.d("Intent", mCurrentPhotoPath);
            intent.putExtra("path", mCurrentPhotoPath);
            startActivity(intent);
        }
        if (reqCode == REQUEST_VIDEO
                && resCode == RESULT_OK) {

            if (data != null && data.getStringExtra("videopath") != null)
                videoFilePath = data.getStringExtra("videopath");
            Intent intent = new Intent(Feed.this, VideoUpload.class);
            intent.putExtra("path", videoFilePath);
            startActivity(intent);
        }
        if (reqCode == REQUEST_CODE && resCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        // Retrieve the SearchView and plug it into SearchManager
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_search:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            case R.id.action_notification:
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Intent notifications = new Intent(Feed.this, Notifications.class);
                    startActivity(notifications);
                } else {
                    Toast.makeText(this, "You are not connected to Internet", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {"support@reweyou.in"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Reweyou");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "I need some help regarding");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Feed.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            moveTaskToBack(true);
        }

        this.doubleBackToExitPressedOnce = true;
        //Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 3000);
    }

    public void alertMessage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Feed.this);
        builder.setMessage(R.string.confirm_logout)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        session.logoutUser();
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog object and return it
        builder.show();
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    public void initNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        Menu menu = navigationView.getMenu();
        //    MenuItem title= menu.findItem(R.id.menu_title);
        //  SpannableString s = new SpannableString(title.getTitle());
        //s.setSpan(new TextAppearanceSpan(this, R.style.Menutitle), 0, s.length(), 0);
        // title.setTitle(s);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.mycity:
                        Intent news = new Intent(Feed.this, MyCityActivity.class);
                        startActivity(news);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.myreports:
                        Intent reports = new Intent(Feed.this, Topic.class);
                        startActivity(reports);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.notifications:
                        Intent notif = new Intent(Feed.this, Notifications.class);
                        startActivity(notif);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.New:
                        Intent New = new Intent(Feed.this, NewActivity.class);
                        startActivity(New);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.logout:
                        alertMessage();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.leaderboard:
                        Intent leader = new Intent(Feed.this, Leaderboard.class);
                        startActivity(leader);
                        drawerLayout.closeDrawers();
                }
                return true;
            }
        });
        View header = navigationView.getHeaderView(0);
        TextView tv_email = (TextView) header.findViewById(R.id.tv_email);
        ImageView image = (ImageView) header.findViewById(R.id.image);
        ImageView profileSetting = (ImageView) header.findViewById(R.id.profile_settings);
        profileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(Feed.this, MyProfile.class);
                startActivity(profile);
            }
        });
        tv_email.setText(session.getUsername());
        String url = "https://www.reweyou.in/uploads/profilepic/" + session.getMobileNumber() + ".jpg";
        // imageLoader.displayImage(url, image, options);
        Glide.with(Feed.this).load(url).error(R.drawable.download).into(image);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        private String[] tabs = getResources().getStringArray(R.array.tabs);

        private PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

       /* public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }*/

        @Override
        public Fragment getItem(int position) {
            SecondFragment fragment = new SecondFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            fragment.setArguments(bundle);
            Log.d("getItem", "" + position);
            return fragment;
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
