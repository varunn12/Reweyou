package in.reweyou.reweyou;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.HandleActivityResult;
import in.reweyou.reweyou.classes.UploadOptions;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.fragment.SecondFragment;
import in.reweyou.reweyou.utils.Constants;

import static in.reweyou.reweyou.classes.HandleActivityResult.HANDLE_IMAGE;
import static in.reweyou.reweyou.classes.HandleActivityResult.HANDLE_VIDEO;
import static in.reweyou.reweyou.utils.Constants.AUTH_ERROR;

public class Feed extends AppCompatActivity implements View.OnClickListener {
    static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE = 0;
    private final int REQ_CODE_PROFILE = 56;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1, REQUEST_VIDEO = 3;
    UserSessionManager session;
    Uri uri;
    PermissionsChecker checker;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    boolean doubleBackToExitPressedOnce = false;
    private TabLayout tabLayout;
    private DrawerLayout drawerLayout;
    private String mCurrentPhotoPath;
    private String videoFilePath;
    private Toolbar mToolbar;
    private FloatingActionButton floatingActionButton;
    private ImageView image;
    private TextView tv;
    private ProgressBar pd;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        pd = (ProgressBar) findViewById(R.id.pd);
        pd.setVisibility(View.VISIBLE);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Feed.this, PostReport.class));
               /* AlertDialog.Builder getImageFrom = new AlertDialog.Builder(Feed.this);
                getImageFrom.setTitle("Select Image from:");
                final CharSequence[] opsChars = {getResources().getString(R.string.takepic), getResources().getString(R.string.opengallery)};
                getImageFrom.setItems(opsChars, new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if (checker.lacksPermissions(PERMISSION)) {
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
                            if (checker.lacksPermissions(PERMISSION)) {
                                startPermissionsActivity();
                            } else {
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                // 2. pick image only
                                intent.setType("image*//*");
                                // 3. start activity
                                startActivityForResult(intent, SELECT_FILE);
                                UILApplication.getInstance().trackEvent("Gallery", "Gallery", "For Pics");
                            }
                        }
                        dialog.show();
                    }
                });
                getImageFrom.show();*/
            }
        });
        session = new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(Feed.this);
        checker = new PermissionsChecker(this);

        initToolbar();
        initViewPagerAndTabs();
        initNavigationDrawer();

        if (session.getDeviceid() == null)
            checkforolduserstatus();
        else {
            pd.setVisibility(View.GONE);
            viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
            makeNotificationsRequest();
        }


    }

    private void checkforolduserstatus() {

        tabLayout.setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_OLD_USER_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.setVisibility(View.GONE);

                        Log.d("responseolduser", response);
                        if (response != null) {
                            if (!response.isEmpty()) {
                                if (response.equals("error")) {
                                    Toast.makeText(Feed.this, "wrong", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d("readched", "22323");
                                    session.setAuthToken(response);
                                    session.setDeviceid(Settings.Secure.getString(Feed.this.getContentResolver(), Settings.Secure.ANDROID_ID));

                                    Log.d("readched oid", Settings.Secure.getString(Feed.this.getContentResolver(), Settings.Secure.ANDROID_ID));

                                    tabLayout.setVisibility(View.VISIBLE);

                                    // viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
                                    // makeNotificationsRequest();
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.setVisibility(View.GONE);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("number", session.getMobileNumber());
                map.put("deviceid", Settings.Secure.getString(Feed.this.getContentResolver(), Settings.Secure.ANDROID_ID));

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Feed.this);
        requestQueue.add(stringRequest);


    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    private void initViewPagerAndTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        // viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }


    private boolean isDeviceSupportCamera() {
        return getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

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
      /*  if (resCode == Activity.RESULT_OK && reqCode == SELECT_FILE && data != null) {
            Uri uriFromPath = data.getData();
            String show = uriFromPath.toString();
            Intent intent = new Intent(this, PostReport.class);
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
        }*/

        Log.d("reacheddewdd", "wwqd   hereeeeeee");


        if (reqCode == REQ_CODE_PROFILE && resCode == RESULT_OK) {

            Log.d("reached", "ewjdkejdu   hereeeeeee");
            Glide.with(Feed.this).load(session.getProfilePicture()).error(R.drawable.download).into(image);

        } else {

            int dataType = new HandleActivityResult().handleResult(reqCode, resCode, data);
            switch (dataType) {
                case HANDLE_IMAGE:
                    Intent i = new Intent(this, PostReport.class);
                    i.putExtra("dataImage", data.getData().toString());
                    startActivity(i);
                    break;
                case HANDLE_VIDEO:
                    Intent i2 = new Intent(this, PostReport.class);
                    UploadOptions uploadOptions = new UploadOptions(Feed.this);
                    i2.putExtra("dataVideo", uploadOptions.getAbsolutePath(data.getData()));
                    startActivity(i2);
                default:
                    return;
            }
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


        MenuItem item = menu.findItem(R.id.action_notification);
        MenuItemCompat.setActionView(item, R.layout.notification_icon_layout);
        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);

        tv = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);
        tv.setText("1");

        ImageView im = (ImageView) notifCount.findViewById(R.id.im);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Intent notifications = new Intent(Feed.this, Notifications.class);
                    startActivity(notifications);
                } else {
                    Toast.makeText(Feed.this, "You are not connected to Internet", Toast.LENGTH_LONG).show();
                }
            }
        });

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

            /*case R.id.action_notification:
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Intent notifications = new Intent(Feed.this, Notifications.class);
                    startActivity(notifications);
                } else {
                    Toast.makeText(this, "You are not connected to Internet", Toast.LENGTH_LONG).show();
                }
                return true;*/
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

    private void makeNotificationsRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.MY_TOTAL_NOTIFICATIONS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("responsetotallikes", response);
                        if (response != null) {
                            if (!response.isEmpty()) {
                                if (response.equals(AUTH_ERROR)) {
                                    session.logoutUser();

                                } else
                                    setnotificationnNumber(Integer.parseInt(response));
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("number", session.getMobileNumber());
                map.put("token", session.getKeyAuthToken());
                map.put("deviceid", session.getDeviceid());

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Feed.this);
        requestQueue.add(stringRequest);
    }

    private void setnotificationnNumber(int i) {
        if (tv != null) {
            if (i == 0) {
                tv.setVisibility(View.INVISIBLE);
            } else {
                tv.setVisibility(View.VISIBLE);
                tv.setText("" + (i));
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            moveTaskToBack(true);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit.", Toast.LENGTH_SHORT).show();

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
                        session.logoutUser1();
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
                        Intent New = new Intent(Feed.this, WhatsNew.class);
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
        image = (ImageView) header.findViewById(R.id.image);
        ImageView profileSetting = (ImageView) header.findViewById(R.id.profile_settings);
        profileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(Feed.this, MyProfile.class);

                startActivityForResult(profile, REQ_CODE_PROFILE);
            }
        });
        tv_email.setText(session.getUsername());
        Glide.with(Feed.this).load(session.getProfilePicture()).error(R.drawable.download).into(image);
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
