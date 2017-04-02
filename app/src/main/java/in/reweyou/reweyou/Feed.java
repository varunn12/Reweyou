package in.reweyou.reweyou;

import android.animation.ValueAnimator;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gnzlt.AndroidVisionQRReader.QRActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.reflect.TypeToken;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import in.reweyou.reweyou.adapter.TagsAdapter;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.HandleActivityResult;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.customView.CustomSigninDialog;
import in.reweyou.reweyou.fragment.IssueFragment;
import in.reweyou.reweyou.model.TagsModel;
import in.reweyou.reweyou.utils.Constants;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;

import static in.reweyou.reweyou.classes.HandleActivityResult.HANDLE_IMAGE;
import static in.reweyou.reweyou.classes.HandleActivityResult.HANDLE_VIDEO;

public class Feed extends AppCompatActivity {
    public static final int REQ_CODE_NOTI_COUNT = 45;
    private static final String PACKAGE_URL_SCHEME = "package:";
    private static final String TAG = Feed.class.getSimpleName();
    private static final int PERMISSION_STORAGE_REQUEST_CODE = 12;
    private static final int QR_REQUEST = 63;
    private static final int CUSTOM_RESULT_SCAN_ERROR = 23;
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
    private ValueAnimator valueAnimator;
    private boolean animRunning;
    private ValueAnimator valueAnimator1;
    private android.app.AlertDialog alertDialog1;
    private CustomSigninDialog customSigninDialog;
    private TextView signinbutton;
    private RecyclerView recycelrview;
    private TagsAdapter tagsAdapter;
    private String currentTab = "All";
    private AVLoadingIndicatorView loadingcircular;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        loadingcircular = (AVLoadingIndicatorView) findViewById(R.id.avi);
        loadingcircular.show();
        session = new UserSessionManager(this);
        cd = new ConnectionDetector(Feed.this);
        checker = new PermissionsChecker(this);

        initViews();

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);

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
        // registerNetChangeReceiver();
    }

    /* private void registerNetChangeReceiver() {
         registerReceiver(netChangeReceiver, netChangeIntentFilter);
     }
 */
    @Override
    protected void onStop() {
        // unregisterReceiver(netChangeReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(addnotireceiver);

        super.onStop();
    }

    private void initViews() {
        initToolbar();
        initFAB();
        initViewPagerAndTabs();
        initNavigationDrawer();
        initSigninDialog();

        recycelrview = (RecyclerView) findViewById(R.id.tagsrecyclerview);
        recycelrview.setItemAnimator(new ScaleInAnimator());


        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        recycelrview.setLayoutManager(staggeredGridLayoutManager);
        tagsAdapter = new TagsAdapter(this);
        recycelrview.setAdapter(tagsAdapter);

        makeTagsRequest();
    }

    public void makeTagsRequest() {

        AndroidNetworking.get("https://reweyou.in/reviews/tags.php")
                .setTag("t")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsParsed(new TypeToken<List<TagsModel>>() {
                }, new ParsedRequestListener<List<TagsModel>>() {

                    @Override
                    public void onResponse(List<TagsModel> list) {

                        loadingcircular.smoothToHide();
                        TagsModel tagsModel = new TagsModel();
                        tagsModel.setId("default");
                        tagsModel.setTags("All");

                        list.add(0, tagsModel);
                        //tagsAdapter.add(list);

                        for (int i = 0; i < list.size(); i++)
                            tagsAdapter.addsingle(list.get(i), i);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                makeIssueRequest("All");

                            }
                        }, 400);
                    }

                    @Override
                    public void onError(final ANError anError) {
                        Log.e(TAG, "run: error: " + anError.getErrorDetail());

                        /*new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipe.setRefreshing(false);
                                if (isAdded() && !anError.getErrorDetail().equals("requestCancelled"))
                                    Toast.makeText(mContext, "Couldn't connect", Toast.LENGTH_SHORT).show();


                            }
                        }, 1200);*/


                    }
                });
    }

    private void initSigninDialog() {
       /* LayoutInflater li = LayoutInflater.from(Feed.this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_signin, null);
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Feed.this);
        // Include dialog.xml file
        dialog.setView(confirmDialog);
        alertDialog1 = dialog.create();

        // Set dialog title
        alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog1.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button button = (Button) confirmDialog.findViewById(R.id.buttonConfirm);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog1.dismiss();
                startActivity(new Intent(Feed.this, Signup.class));
            }
        });*/

        customSigninDialog = new CustomSigninDialog(this);


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


    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

       /* TextView mTitle = (TextView) mToolbar.findViewById(R.id.title);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/Pacifico.ttf");*/
       /* mTitle.setTypeface(face);*/

        ImageView inbox = (ImageView) mToolbar.findViewById(R.id.action_menu_inbox);
        inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.checkLoginSplash()) {
                    Intent s = new Intent(Feed.this, MyProfile.class);
                    startActivity(s);
                    overridePendingTransition(0, 0);
                } else showSignupDialog();
            }
        });
        ImageView qr = (ImageView) mToolbar.findViewById(R.id.action_qr);
        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent qrScanIntent = new Intent(Feed.this, QRActivity.class);
                startActivityForResult(qrScanIntent, QR_REQUEST);

            }
        });
/*
        ImageView noti = (ImageView) mToolbar.findViewById(R.id.action_menu_not);
        noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.checkLoginSplash()) {
                    Intent notifications = new Intent(Feed.this, Notifications.class);
                    startActivity(notifications);
                    overridePendingTransition(0, 0);
                } else showSignupDialog();
            }
        });

        tv = (TextView) mToolbar.findViewById(R.id.actionbar_notifcation_textview);*/


        mToolbar.findViewById(R.id.sssss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void showSignupDialog() {
        customSigninDialog.show();
    }

    private void initViewPagerAndTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
/*
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public boolean pagechanged;
            public int mPosition = -1;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                mPosition = position;
                pagechanged = true;
                Log.d(TAG, "onPageSelected: called");

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    Log.d(TAG, "onPageScrollStateChanged: " + state);
                    if (pagechanged) {
                        pagechanged = false;
                        Fragment page = pagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
                        if (page != null) {
                            Log.d(TAG, "onPageChange: loadFeeds() of fragment " + viewPager.getCurrentItem() + " is called");
                            ((BaseFragment) page).loadfeeds();
                        }

                        ApplicationClass.getInstance().trackEvent("FEED_TAB", String.valueOf(mPosition), "current selected tab");
                    }

                }
            }
        });
*/
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void makeNotificationsRequest() {
       /* StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.MY_TOTAL_NOTIFICATIONS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("responsetotallikes", response);
                        if (response != null) {
                            if (!response.isEmpty()) {
                                if (response.equals(AUTH_ERROR)) {
                                    session.logoutUser();

                                } else {
                                    setnotificationNumber(Integer.parseInt(response));

                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       *//* if (error instanceof NoConnectionError) {
                            showSnackBar1("no internet connectivity");
                        } else if (error instanceof TimeoutError) {
                            showSnackBar1("poor internet connectivity");
                        } else if (error instanceof NetworkError || error instanceof ParseError || error instanceof ServerError) {
                            showSnackBar1("something went wrong");
                        }*//*
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
*/

    }


   /* @Override
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
    }*/

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {

        if (reqCode == QR_REQUEST) {
            Log.d(TAG, "onActivityResult: " + resCode);
            if (resCode == RESULT_OK) {
                String qrData = data.getStringExtra(QRActivity.EXTRA_QR_RESULT);
                if (qrData.contains("https://www.reweyou.in/qr/")) {
                    // do something with the QR data String
                    Intent i = new Intent(Feed.this, ReviewActivityQR.class);
                    i.putExtra("qrdata", qrData);
                    startActivity(i);
                } else {
                    Toast.makeText(Feed.this, "Invalid QR code", Toast.LENGTH_SHORT).show();

                }


            } else if (resCode == CUSTOM_RESULT_SCAN_ERROR) {
                Log.d(TAG, "onActivityResult: scanerror");
                Toast.makeText(Feed.this, "Scan Error. Please try again", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "onActivityResult: backpress");

            }
        }

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

        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {

            if (doubleBackToExitPressedOnce) {

                finishAffinity();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: " + requestCode);
        switch (requestCode) {

            case PERMISSION_STORAGE_REQUEST_CODE:

                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Permission denied by user", Toast.LENGTH_SHORT).show();

                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                break;

        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Storage Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Storage Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE_REQUEST_CODE);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Storage Permission is auto granted for sdk<23");
            return true;
        }
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

                        Intent reports = new Intent(Feed.this, YourReview.class);
                        startActivity(reports);
                        break;
                  /*  case R.id.leaderboard:
                        drawerLayout.closeDrawers();
                        if(session.checkLoginSplash())
                        {

                            Intent profile = new Intent(Feed.this, MyProfile.class);
                            startActivityForResult(profile, REQ_CODE_PROFILE);
                        }else {
                            Toast.makeText(Feed.this,"Sign in to view your profile",Toast.LENGTH_SHORT).show();
                        }
                        overridePendingTransition(0, 0);
                        break;*/
                    case R.id.invite:
                        drawerLayout.closeDrawers();
                        Intent inv = new Intent(Feed.this, Invite.class);
                        startActivity(inv);
                        break;

                    case R.id.qrreport:
                        drawerLayout.closeDrawers();
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                Intent qrScanIntent = new Intent(Feed.this, QRActivity.class);
                                startActivityForResult(qrScanIntent, QR_REQUEST);
                            }
                        });

                        break;
                   /* case R.id.New:
                        drawerLayout.closeDrawers();
                        Intent New = new Intent(Feed.this, WhatsNew.class);
                        startActivity(New);
                        overridePendingTransition(0, 0);
                        break;*/

                   /* case R.id.leaderboard:
                        drawerLayout.closeDrawers();
                        Intent leader = new Intent(Feed.this, Leaderboard.class);
                        startActivity(leader);
                        overridePendingTransition(0, 0);
                        break;*/
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
        signinbutton = (TextView) header.findViewById(R.id.signin);
        ImageView profileSetting = (ImageView) header.findViewById(R.id.profile_settings);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);


        if (session.checkLoginSplash()) {
            signinbutton.setVisibility(View.GONE);

            profileSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.closeDrawers();
                    Intent profile = new Intent(Feed.this, MyProfile.class);
                    startActivityForResult(profile, REQ_CODE_PROFILE);
                }
            });
            tv_name.setText(session.getUsername());
            Glide.with(Feed.this).load(session.getProfilePicture()).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.download).into(image);
        } else {
            signinbutton.setVisibility(View.VISIBLE);
            signinbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Feed.this, Signup.class));
                }
            });
        }
      /*  ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {

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
        actionBarDrawerToggle.syncState();*/
    }

    private void openplaystore() {
        Uri uri = Uri.parse("market://details?id=" + Feed.this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |

                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + Feed.this.getPackageName())));
        }
    }


    public void makeIssueRequest(String s) {
        currentTab = s;
        Fragment page = pagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
        ((IssueFragment) page).loadReportsfromServer(currentTab);

    }


    private class PagerAdapter extends FragmentStatePagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        private String[] tabs = getResources().getStringArray(R.array.tabs);

        private PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            /*SecondFragment fragment = new SecondFragment();
            Bundle bundle = new Bundle();
            if (position == 2) {
                bundle.putInt("position", Constants.POSITION_FEED_TAB_MY_CITY);
                bundle.putString("place", session.getLoginLocation());
            } else if (position == 1)
                bundle.putInt("position", Constants.POSITION_FEED_TAB_MAIN_FEED);
            else bundle.putInt("position", position);

*/
            IssueFragment fragment = new IssueFragment();
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
