package in.reweyou.reweyou;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.klinker.android.sliding.MultiShrinkScroller;
import com.klinker.android.sliding.SlidingActivity;

import org.json.JSONObject;

public class CreateActivity extends SlidingActivity {

    private static final String TAG = CreateActivity.class.getName();
    private ProgressBar linkpd;
    private CardView cd;
    private TextView headlinelink;
    private TextView descriptionlink;
    private RelativeLayout rl;
    private ImageView camerabtn;
    private ImageView linkbtn;
    private TextView linklink;
    private TextView create;
    private EditText editText;

    @Override
    protected void configureScroller(MultiShrinkScroller scroller) {
        super.configureScroller(scroller);
        scroller.setIntermediateHeaderHeightRatio(0);

    }

    @Override
    public void init(Bundle savedInstanceState) {
        disableHeader();
        enableFullscreen();

        setContent(R.layout.content_create);

        linkpd = (ProgressBar) findViewById(R.id.linkpd);
        cd = (CardView) findViewById(R.id.cd);

        editText = (EditText) findViewById(R.id.groupname);
        headlinelink = (TextView) findViewById(R.id.headlinelink);
        descriptionlink = (TextView) findViewById(R.id.descriptionlink);
        linklink = (TextView) findViewById(R.id.linklink);
        rl = (RelativeLayout) findViewById(R.id.rl);
        linkbtn = (ImageView) findViewById(R.id.link);
        camerabtn = (ImageView) findViewById(R.id.camera);
        linkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editHeadline();
            }
        });
        create = (TextView) findViewById(R.id.create);
        create.setEnabled(false);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPost();
            }
        });
        initTextWatchers();

    }

    private void uploadPost() {

    }

    private void initTextWatchers() {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText.getText().toString().trim().length() > 0) {
                    updateCreateTextUI(true);
                } else updateCreateTextUI(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateCreateTextUI(boolean b) {
        if (b) {
            create.setEnabled(true);
            create.setTextColor(this.getResources().getColor(R.color.main_background_pink));
            create.setBackground(this.getResources().getDrawable(R.drawable.border_pink));
        } else {
            create.setEnabled(false);
            create.setTextColor(this.getResources().getColor(R.color.grey_create));
            create.setBackground(this.getResources().getDrawable(R.drawable.border_grey));
        }
    }

    private void editHeadline() {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_insert_link, null);
        //  number=session.getMobileNumber();
        //Initizliaing confirm button fo dialog box and edittext of dialog box
        final Button buttonconfirm = (Button) confirmDialog.findViewById(R.id.buttonConfirm);
        final EditText link = (EditText) confirmDialog.findViewById(R.id.editTextlink);
        link.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    buttonconfirm.setBackground(CreateActivity.this.getResources().getDrawable(R.drawable.border_pink));
                    buttonconfirm.setTextColor(CreateActivity.this.getResources().getColor(R.color.main_background_pink));
                } else {
                    buttonconfirm.setBackground(CreateActivity.this.getResources().getDrawable(R.drawable.border_grey));
                    buttonconfirm.setTextColor(Color.parseColor("#9e9e9e"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setView(confirmDialog);

        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.show();

        //On the click of the confirm button from alert dialog
        buttonconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (link.getText().toString().trim().length() > 0) {
                    alertDialog.dismiss();

                    CreateActivity.this.onLinkPasted(link.getText().toString());


                } else alertDialog.dismiss();
            }
        });

    }

    private void onLinkPasted(String s) {
        cd.setVisibility(View.VISIBLE);
        rl.setVisibility(View.GONE);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                linkpd.setVisibility(View.VISIBLE);

            }
        });


        AndroidNetworking.post("https://damp-beyond-15607.herokuapp.com/previewlink.php")
                .addBodyParameter("url", s)
                .setTag("agr")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d(TAG, "onResponse: " + response);

                            linkpd.setVisibility(View.INVISIBLE);
                            JSONObject jsonObject = new JSONObject(response);
                            String reallink = jsonObject.getString("reallink");
                            String link = jsonObject.getString("link");
                            Log.d(TAG, "onResponse: " + response + "   " + link);
                            String title = jsonObject.getString("title");
                            String description = jsonObject.getString("description");

                            if (title != null)
                                headlinelink.setText(title);
                            if (description != null)
                                descriptionlink.setText(description);
                            if (link != null)
                                linklink.setText(reallink);

                        } catch (Exception e) {
                            rl.setVisibility(View.VISIBLE);
                            editText.setHint("Describe this link...");
                            cd.setVisibility(View.GONE);
                            Toast.makeText(CreateActivity.this, "Error in fetching data from link", Toast.LENGTH_SHORT).show();

                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        cd.setVisibility(View.GONE);
                        rl.setVisibility(View.VISIBLE);


                        Toast.makeText(CreateActivity.this, "Error in fetching data from link", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
