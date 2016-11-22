package in.reweyou.reweyou;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import in.reweyou.reweyou.adapter.CommentsAdapter;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.fragment.CommentsFragment;
import in.reweyou.reweyou.model.CommentsModel;

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
            }
        });
        Bundle bundle = getIntent().getExtras();
        i = bundle.getString("myData");


        CommentsFragment fragment = (CommentsFragment) getSupportFragmentManager().findFragmentById(R.id.frag);
        fragment.setData(i, head, url);

    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && reqCode == SELECT_FILE && data != null) {
           /* Uri uriFromPath = data.getData();
            String show = uriFromPath.toString();
            Intent intent = new Intent(this, UpdateImage.class);
            intent.putExtra("path", show);
            intent.putExtra("postid", i);
            startActivity(intent);*/
        } else {
            Toast.makeText(this, "There is some error!", Toast.LENGTH_LONG).show();
        }
    }
}