package in.reweyou.reweyou;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import in.reweyou.reweyou.adapter.CommentsAdapter;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.fragment.CommentsFragment;
import in.reweyou.reweyou.model.CommentsModel;

import static in.reweyou.reweyou.classes.UploadOptions.PERMISSION_ALL_IMAGE;

public class Comments1 extends AppCompatActivity {
    public static final String UPLOAD_URL = "https://www.reweyou.in/reweyou/post_comments_new.php";
    public static final String KEY_TEXT = "comments";
    public static final String KEY_NAME = "name";
    public static final String KEY_TIME = "time";
    public static final String KEY_ID = "postid";
    public static final String KEY_NUMBER = "number";
    static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE = 0;
    private static final int DISABLE = 0;
    private static final int ENABLE = 1;
    private static final String PACKAGE_URL_SCHEME = "package:";

    int SELECT_FILE = 1;
    SwipeRefreshLayout swipeLayout;
    UserSessionManager session;
    ImageLoader imageLoader = ImageLoader.getInstance();
    PermissionsChecker checker;
    private RecyclerView recyclerView;
    private ImageView button;
    private ImageView imagebutton;
    private EditText editText;
    private List<CommentsModel> mpModelList;
    private CommentsAdapter adapter;
    private TextView headline;
    private ImageView image;
    private Toolbar toolbar;
    private String name;
    private String result;
    private String i;
    private String number, head, url;
    private ProgressBar progressBar;
    private LinearLayout Empty;
    private ConnectionDetector checknet;
    private LinearLayout commentContainer;
    private CommentsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_test2);
        //initCollapsingToolbar();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reactions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(0, 0);
            }
        });
        Bundle bundle = getIntent().getExtras();
        i = bundle.getString("myData");


        fragment = (CommentsFragment) getSupportFragmentManager().findFragmentById(R.id.frag);
        fragment.setData(i);

    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        Log.d("result", "" + reqCode + "    " + resCode + "     " + data);

        if (resCode == Activity.RESULT_OK && reqCode == SELECT_FILE && data != null) {

            Uri uriFromPath = data.getData();
            String path = uriFromPath.toString();
            /*Intent intent = new Intent(Comments1.this, UpdateImage.class);
            intent.putExtra("path", show);
            intent.putExtra("postid", i);
            startActivity(intent);*/
            if (fragment != null) {
                fragment.setpreviewImage(path);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL_IMAGE:

                String permission = permissions[0];
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission

                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(Comments1.this, permission);
                    if (!showRationale) {
                        showPermissionDeniedDialog();
                    } else
                        showPermissionRequiredDialog(permission);


                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    if (fragment != null) {
                        fragment.onImageButtonClick();
                    }
                }

                break;


        }
    }

    private void showPermissionRequiredDialog(final String permission) {
        AlertDialogBox alertDialogBox = new AlertDialogBox(Comments1.this, "Permission Required", getResources().getString(R.string.permission_required_image), "grant", "deny") {
            @Override
            public void onNegativeButtonClick(DialogInterface dialog) {
                dialog.dismiss();
            }

            @Override
            public void onPositiveButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                String[] p = {permission};
                ActivityCompat.requestPermissions(Comments1.this, p, PERMISSION_ALL_IMAGE);

            }
        };
        alertDialogBox.setCancellable(true);
        alertDialogBox.show();
    }

    private void showPermissionDeniedDialog() {
        AlertDialogBox alertDialogBox = new AlertDialogBox(Comments1.this, "Permission Denied", getResources().getString(R.string.permission_denied_image), "settings", "okay") {
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
}