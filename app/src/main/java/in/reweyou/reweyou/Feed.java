package in.reweyou.reweyou;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;

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
import static in.reweyou.reweyou.classes.UploadOptions.PERMISSION_ALL_IMAGE;
import static in.reweyou.reweyou.classes.UploadOptions.PERMISSION_ALL_SHARE;
import static in.reweyou.reweyou.classes.UploadOptions.PERMISSION_ALL_VIDEO;
import static in.reweyou.reweyou.classes.UploadOptions.PERMISSION_ALL_VIDEO_CAPTURE;
import static in.reweyou.reweyou.utils.Constants.AUTH_ERROR;

public class Feed extends AppCompatActivity {
    public static final int REQ_CODE_NOTI_COUNT = 45;
    private static final String PACKAGE_URL_SCHEME = "package:";
    private static final String TAG = Feed.class.getSimpleName();
    private final int REQ_CODE_PROFILE = 56;

    UserSessionManager session;
    Uri uri;
    PermissionsChecker checker;
    ConnectionDetector cd;
    boolean doubleBackToExitPressedOnce = false;
    private TabLayout tabLayout;
    private DrawerLayout drawerLayout;

    private Toolbar mToolbar;
    private FloatingActionButton floatingActionButton;
    private ImageView image;
    private TextView tv;
    private ProgressBar pd;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private BroadcastReceiver netChangeReceiver;
    private IntentFilter netChangeIntentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    private FirebaseAnalytics mFirebaseAnalytics;
    private BroadcastReceiver addnotireceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                if (session.getDeviceid() != null) {
                    makeNotificationsRequest();
                }

                Log.w(TAG, "onReceive: feed noti change request called");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private android.app.AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        session = new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(Feed.this);
        checker = new PermissionsChecker(this);

        initViews();


        /*method to check for users who were using old versions of the app*/

        //device id must be null for the users using old version of app

        if (session.getDeviceid() == null) {
            checkforolduserstatus();
        } else {                              //for new users we didnt need this check
            pagerAdapter = new PagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(pagerAdapter);
            viewPager.setCurrentItem(1);
            makeNotificationsRequest();


            if (!session.getFirstLoad1())
                showwhatsnewdialog();

        }


        netChangeReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: onNetChange");
                if (!netChangeReceiver.isInitialStickyBroadcast())
                    if (viewPager != null) {
                        if (pagerAdapter != null) {
                            Fragment page = pagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
                            if (page != null) {
                                Log.d(TAG, "onReceive: onRefresh of fragment " + viewPager.getCurrentItem() + " is called");


                                ((SecondFragment) page).onNetChange();
                            }
                        }
                    }
            }
        };

    }

    private void showwhatsnewdialog() {
        LayoutInflater li = LayoutInflater.from(Feed.this);
        View confirmDialog = li.inflate(R.layout.dialog_whatsnew, null);

        //  TextView editNum = (TextView) confirmDialog.findViewById(R.id.editNumber);
        //final EditText otpField = (EditText) confirmDialog.findViewById(R.id.editTextOtp);
        Button confirm = (Button) confirmDialog.findViewById(R.id.buttonConfirm);


        //Creating an alertdialog builder
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(Feed.this);
        alert.setView(confirmDialog);

        //Creating an alert dialog
        alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        //Displaying the alert dialog
        alertDialog.show();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.setFirstLoad1();
                alertDialog.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (session.getDeviceid() != null) {
            makeNotificationsRequest();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(addnotireceiver, new IntentFilter(Constants.SEND_NOTI_CHANGE_REQUEST));
        registerNetChangeReceiver();
    }

    private void registerNetChangeReceiver() {
        registerReceiver(netChangeReceiver, netChangeIntentFilter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(netChangeReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(addnotireceiver);

        super.onStop();
    }

    private void initViews() {
        initToolbar();
        initFAB();
        initViewPagerAndTabs();
        initNavigationDrawer();
    }

    private void initFAB() {
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Feed.this, PostReport.class));
            }
        });
    }

    private void showProgressBarforOldUserCheck() {
        pd = (ProgressBar) findViewById(R.id.pd);
        pd.setVisibility(View.VISIBLE);
    }

    private void checkforolduserstatus() {

        showProgressBarforOldUserCheck(); //show progress bar when making request to update old user data
        tabLayout.setVisibility(View.GONE); //hide tabs bar as it empty(no tabs name) because we havent set adapter

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_OLD_USER_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.setVisibility(View.GONE);
                        Log.d("responseolduser", response);
                        if (response != null) {
                            if (!response.isEmpty()) {
                                if (response.equals("error")) {
                                    Log.d("olduser", "error");
                                } else {

                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        String token = (String) jsonArray.get(0);
                                        String profilepic = (String) jsonArray.get(1);

                                        Log.d("token", token);
                                        Log.d("profielpic", profilepic);

                                        session.setAuthToken(token);
                                        session.setDeviceid(Settings.Secure.getString(Feed.this.getContentResolver(), Settings.Secure.ANDROID_ID));
                                        session.setProfilePicture(profilepic);


                                        tabLayout.setVisibility(View.VISIBLE);
                                        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
                                        viewPager.setAdapter(pagerAdapter);
                                        viewPager.setCurrentItem(1);
                                        if (image != null)
                                            Glide.with(Feed.this).load(session.getProfilePicture()).error(R.drawable.download).into(image);
                                        makeNotificationsRequest();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.setVisibility(View.GONE);
                        if (error instanceof NoConnectionError) {
                            showSnackBar("no internet connectivity");
                        } else if (error instanceof TimeoutError) {
                            showSnackBar("poor internet connectivity");
                        } else if (error instanceof NetworkError || error instanceof ParseError || error instanceof ServerError) {
                            showSnackBar("something went wrong");
                        }

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

    private void showSnackBar(String msg) {
        Snackbar.make(findViewById(R.id.coordinatorLayout), msg, Snackbar.LENGTH_LONG).setDuration(Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkforolduserstatus();
                    }
                }).show();
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    private void initViewPagerAndTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Fragment page = pagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
                if (page != null) {
                    Log.d(TAG, "onPageChange: loadFeeds() of fragment " + viewPager.getCurrentItem() + " is called");
                    ((SecondFragment) page).loadFeeds();
                }

                ApplicationClass.getInstance().trackEvent("FEED_TAB", String.valueOf(position), "current selected tab");


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
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
                                    setnotificationNumber(Integer.parseInt(response));
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       /* if (error instanceof NoConnectionError) {
                            showSnackBar1("no internet connectivity");
                        } else if (error instanceof TimeoutError) {
                            showSnackBar1("poor internet connectivity");
                        } else if (error instanceof NetworkError || error instanceof ParseError || error instanceof ServerError) {
                            showSnackBar1("something went wrong");
                        }*/
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

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {

        if (reqCode == REQ_CODE_PROFILE && resCode == RESULT_OK) {
            //called when profilepicture is updated by user
            Glide.with(Feed.this).load(session.getProfilePicture()).error(R.drawable.download).into(image);
        } else {
            int dataType = new HandleActivityResult().handleResult(reqCode, resCode, data);
            switch (dataType) {
                case HANDLE_IMAGE:
                    Uri uri = data.getData();
                    if (uri != null) {
                        Intent i = new Intent(this, PostReport.class);
                        i.putExtra("dataImage", uri.toString());
                        startActivity(i);
                    } else Log.w("uri", "null");


                    break;
                case HANDLE_VIDEO:
                    Uri uri2 = data.getData();
                    if (uri2 != null) {
                        Intent i2 = new Intent(this, PostReport.class);
                        i2.putExtra("dataVideo", uri2.toString());
                        startActivity(i2);
                    } else Log.w("uri", "null");

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setTextColor(Color.BLACK);
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setHintTextColor(getResources().getColor(R.color.black2));
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setHint("Search Reports");
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setTextSize(15.0f);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
                View view = Feed.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                menu.findItem(R.id.action_search).collapseActionView();

                if (!query.isEmpty()) {
                    Intent i = new Intent(Feed.this, SearchResultsActivity.class);
                    i.putExtra("query", query);
                    i.putExtra("position", 12);
                    startActivity(i);
                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });
        setNotificationIcon(menu);

        return true;
    }

    private void setNotificationIcon(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_notification);
        MenuItemCompat.setActionView(item, R.layout.notification_icon_layout);
        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);
        tv = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);
        ImageView im = (ImageView) notifCount.findViewById(R.id.im);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notifications = new Intent(Feed.this, Notifications.class);
                startActivityForResult(notifications, REQ_CODE_NOTI_COUNT);
                overridePendingTransition(0, 0);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_search:
                return true;
            case R.id.action_message:
                Intent s = new Intent(Feed.this, Contacts.class);
                startActivity(s);
                overridePendingTransition(0, 0);
                return true;

            default:
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


    private void setnotificationNumber(int i) {
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
            finish();
        }
        if (!doubleBackToExitPressedOnce)
            Toast.makeText(this, "Press again to exit.", Toast.LENGTH_SHORT).show();

        this.doubleBackToExitPressedOnce = true;

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
                        session.logoutUser1();
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL_IMAGE:

                String permission = permissions[0];
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission

                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(Feed.this, permission);
                    if (!showRationale) {
                        showPermissionDeniedDialog();
                    } else
                        showPermissionRequiredDialog(permission);


                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    UploadOptions uploadOptions = new UploadOptions(Feed.this);
                    uploadOptions.showImageOptions();
                }

                break;
            case PERMISSION_ALL_VIDEO_CAPTURE:
                boolean temp = false;
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            temp = true;
                            break;
                        }
                    }
                    if (temp)
                        Toast.makeText(Feed.this, "Please allow all permissions", Toast.LENGTH_SHORT).show();
                    else {
                        UploadOptions uploadOptions = new UploadOptions(Feed.this);
                        uploadOptions.captureVideo();

                    }
                } else
                    Toast.makeText(Feed.this, "Please allow all permissions", Toast.LENGTH_SHORT).show();
                break;
            case PERMISSION_ALL_VIDEO:
                String permission2 = permissions[0];
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission

                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(Feed.this, permission2);
                    if (!showRationale) {
                        showPermissionDeniedDialog();
                    } else
                        showPermissionRequiredDialog(permission2);


                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    UploadOptions uploadOptions = new UploadOptions(Feed.this);
                    uploadOptions.showVideogallery();
                }
                break;
            case PERMISSION_ALL_SHARE:

                String permission4 = permissions[0];
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission

                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(Feed.this, permission4);
                    if (!showRationale) {
                        showPermissionDeniedDialog();
                    } else
                        showPermissionRequiredDialog(permission4);


                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                break;

        }
    }

    private void showPermissionRequiredDialog(final String permission) {
        AlertDialogBox alertDialogBox = new AlertDialogBox(Feed.this, "Permission Required", getResources().getString(R.string.permission_required_image), "grant", "deny") {
            @Override
            public void onNegativeButtonClick(DialogInterface dialog) {
                dialog.dismiss();
            }

            @Override
            public void onPositiveButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                String[] p = {permission};
                ActivityCompat.requestPermissions(Feed.this, p, PERMISSION_ALL_IMAGE);

            }
        };
        alertDialogBox.setCancellable(true);
        alertDialogBox.show();
    }

    private void showPermissionDeniedDialog() {
        AlertDialogBox alertDialogBox = new AlertDialogBox(Feed.this, "Permission Denied", getResources().getString(R.string.permission_denied_image), "settings", "okay") {
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

    public void initNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.myreports:
                        drawerLayout.closeDrawers();
                        Intent reports = new Intent(Feed.this, Topic.class);
                        startActivity(reports);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.invite:
                        drawerLayout.closeDrawers();
                        Intent inv = new Intent(Feed.this, Invite.class);
                        startActivity(inv);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.New:
                        drawerLayout.closeDrawers();
                        Intent New = new Intent(Feed.this, WhatsNew.class);
                        startActivity(New);
                        overridePendingTransition(0, 0);
                        break;

                    case R.id.leaderboard:
                        drawerLayout.closeDrawers();
                        Intent leader = new Intent(Feed.this, Leaderboard.class);
                        startActivity(leader);
                        overridePendingTransition(0, 0);
                        break;
                   /* case R.id.invite:
                        drawerLayout.closeDrawers();
                        Intent i = new Intent(Feed.this, Invite.class);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                        break;*/
                    case R.id.nav_rate:
                        drawerLayout.closeDrawers();
                        openplaystore();
                        break;

                }
                return true;
            }
        });
        View header = navigationView.getHeaderView(0);
        TextView tv_name = (TextView) header.findViewById(R.id.tv_name);
        image = (ImageView) header.findViewById(R.id.image);
        ImageView profileSetting = (ImageView) header.findViewById(R.id.profile_settings);
        profileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(Feed.this, MyProfile.class);
                startActivityForResult(profile, REQ_CODE_PROFILE);
            }
        });
        tv_name.setText(session.getUsername());
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

    private void openplaystore() {
        Uri uri = Uri.parse("market://details?id=" + Feed.this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + Feed.this.getPackageName())));
        }
    }


    private class PagerAdapter extends FragmentStatePagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        private String[] tabs = getResources().getStringArray(R.array.tabs);

        private PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            SecondFragment fragment = new SecondFragment();
            Bundle bundle = new Bundle();
            if (position == 2) {
                bundle.putInt("position", Constants.POSITION_FEED_TAB_MY_CITY);
                bundle.putString("place", session.getLoginLocation());
            } else if (position == 1)
                bundle.putInt("position", Constants.POSITION_FEED_TAB_MAIN_FEED);
            else bundle.putInt("position", position);


            fragment.setArguments(bundle);
            return fragment;
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

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }
    }


}
