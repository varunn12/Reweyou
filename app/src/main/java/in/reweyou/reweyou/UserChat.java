package in.reweyou.reweyou;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import in.reweyou.reweyou.adapter.UserChatAdapter;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.classes.VerticalSpaceItemDecorator;
import in.reweyou.reweyou.model.UserChatModel;
import in.reweyou.reweyou.utils.Constants;

import static in.reweyou.reweyou.utils.Constants.ADD_CHAT_MESSAGE_CHATROOM_ID;
import static in.reweyou.reweyou.utils.Constants.ADD_CHAT_MESSAGE_SENDER_NAME;
import static in.reweyou.reweyou.utils.Constants.ADD_CHAT_MESSAGE_SENDER_NUMBER;
import static in.reweyou.reweyou.utils.Constants.suggestpostid;

public class UserChat extends AppCompatActivity {

    private static final String TAG = UserChat.class.getName();
    public static boolean userChatActivityOpen;
    private RecyclerView recyclerView;
    private UserChatAdapter userChatAdapter;
    private EditText editBox;
    private ImageView sendButton;
    private UserSessionManager session;
    private SimpleDateFormat dfs = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.US);
    private SimpleDateFormat todayDateFormat = new SimpleDateFormat("hh:mm a", Locale.US);
    private SimpleDateFormat previousDateFormat = new SimpleDateFormat("dd MMM hh:mm a", Locale.US);
    private SimpleDateFormat yesterdayDateFormat = new SimpleDateFormat("hh:mm a", Locale.US);
    private String chatroomid = "abc";
    private String othernumber;
    private String othername;
    private BroadcastReceiver addMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String sendernumber = intent.getStringExtra(Constants.ADD_CHAT_MESSAGE_SENDER_NUMBER);
                String message = intent.getStringExtra(Constants.ADD_CHAT_MESSAGE_MESSAGE);
                String timestamp = intent.getStringExtra(Constants.ADD_CHAT_MESSAGE_TIMESTAMP);
                String chatroomid = intent.getStringExtra(ADD_CHAT_MESSAGE_CHATROOM_ID);

                if (chatroomid.equals(UserChat.this.chatroomid)) {
                    UserChatModel userChatModel = new UserChatModel();
                    userChatModel.setMessage(message);
                    userChatModel.setSender(sendernumber);
                    userChatModel.setTime(timestamp);
                    userChatModel.setReceiver(session.getMobileNumber());
                    if (Constants.suggestpostid != null)
                        userChatModel.setPostid(Constants.suggestpostid);

                    addMessage(userChatModel);
                } else
                    Log.w(TAG, "onReceive: message was from anotherchat");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private LinearLayout empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        empty = (LinearLayout) findViewById(R.id.empty);

        try {
            othername = getIntent().getStringExtra(ADD_CHAT_MESSAGE_SENDER_NAME);
            chatroomid = getIntent().getStringExtra(ADD_CHAT_MESSAGE_CHATROOM_ID);
            othernumber = getIntent().getStringExtra(ADD_CHAT_MESSAGE_SENDER_NUMBER);

            getSupportActionBar().setTitle(othername);
        } catch (Exception e) {
            Log.w(TAG, "onCreate: chatroomid is null");
        }
        session = new UserSessionManager(UserChat.this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewUserChat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserChat.this);
        linearLayoutManager.setStackFromEnd(true);

        VerticalSpaceItemDecorator verticalSpaceItemDecorator = new VerticalSpaceItemDecorator(16);
        recyclerView.addItemDecoration(verticalSpaceItemDecorator);

        recyclerView.setLayoutManager(linearLayoutManager);

        if (chatroomid != null)
            getChatList();
        else
            empty.setVisibility(View.VISIBLE);

        editBox = (EditText) findViewById(R.id.editBox);

        sendButton = (ImageView) findViewById(R.id.btn_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editBox.getText().toString().trim().length() > 0) {

                    empty.setVisibility(View.GONE);
                    final UserChatModel userChatModel = new UserChatModel();
                    userChatModel.setMessage(editBox.getText().toString());
                    userChatModel.setSender(session.getMobileNumber());
                    userChatModel.setSending(true);
                    if (userChatAdapter == null) {
                        final List<Object> list = new ArrayList<>();

                        userChatAdapter = new UserChatAdapter(list, session, UserChat.this);
                        recyclerView.setAdapter(userChatAdapter);
                        userChatAdapter.add(userChatModel);
                    } else userChatAdapter.add(userChatModel);
                    sendMessage(editBox.getText().toString());


                    editBox.setText("");
                    /*InputMethodManager imm = (InputMethodManager) UserChat.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editBox.getWindowToken(), 0);*/

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(userChatAdapter.getItemCount() - 1);
                        }
                    });

                } else
                    Toast.makeText(UserChat.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getChatList() {

       /* HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("chatroomid", chatroomid);*/

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("chatroomid", chatroomid);

            final Gson gson = new Gson();
            final List<Object> list = new ArrayList<>();
            AndroidNetworking.post("https://www.reweyou.in/reweyou/message_read.php")
                    .addJSONObjectBody(jsonObject).setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .setAnalyticsListener(new AnalyticsListener() {
                        @Override
                        public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                            Log.d(TAG, "onReceived: isFromCache: " + isFromCache);
                        }
                    })

                    .getAsJSONArray(new JSONArrayRequestListener() {
                        String previousDate;

                        @Override
                        public void onResponse(final JSONArray response) {
                            try {

                                for (int i = 0; i < response.length(); i++) {
                                    UserChatModel userChatModel = gson.fromJson(response.getJSONObject(i).toString(), UserChatModel.class);

                                    int temp = -1;
                                    if (i != 0)
                                        temp = checkDates(userChatModel.getTime(), previousDate);
                                    else
                                        temp = gettimeforfirst(userChatModel.getTime());

                                    switch (temp) {
                                        case 0:
                                            String s = getDateFormat(userChatModel.getTime());
                                            if (s != null) {
                                                list.add("TODAY " + s);
                                                previousDate = userChatModel.getTime();
                                            }
                                            break;
                                        case 1:
                                            String s1 = getDateFormat2(userChatModel.getTime());
                                            if (s1 != null) {
                                                list.add(s1);
                                                previousDate = userChatModel.getTime();
                                            }
                                            break;
                                        case 2:
                                            String s2 = getDateFormat3(userChatModel.getTime());
                                            if (s2 != null) {
                                                list.add("YESTERDAY " + s2);
                                                previousDate = userChatModel.getTime();
                                            }
                                            break;

                                    }
                                    list.add(userChatModel);
                                }


                                if (Constants.suggestpostid != null) {
                                    final UserChatModel userChatModel = new UserChatModel();
                                    userChatModel.setMessage(editBox.getText().toString());
                                    userChatModel.setSender(session.getMobileNumber());
                                    userChatModel.setSending(true);
                                    userChatModel.setPostid(Constants.suggestpostid);
                                    list.add(userChatModel);
                                }

                                userChatAdapter = new UserChatAdapter(list, session, UserChat.this);
                                recyclerView.setAdapter(userChatAdapter);
                                recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                    @Override
                                    public void onGlobalLayout() {
                                        Log.d(TAG, "onGlobalLayout: callll");
                                        recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                        if (Constants.suggestpostid != null) {
                                            sendMessage("Shared a post");
                                        }


                                    }
                                });


                            } catch (
                                    Exception e)

                            {
                                e.printStackTrace();
                            }
                        }

                        private int gettimeforfirst(String time) {
                            try {
                                Date date = dfs.parse(time);
                                Calendar c1 = Calendar.getInstance(); // today
                                Calendar c2 = Calendar.getInstance();
                                c2.setTime(date); // your date

                                if (c1.get(Calendar.DAY_OF_YEAR) != c2.get(Calendar.DAY_OF_YEAR)) {
                                    c1.add(Calendar.DAY_OF_YEAR, -1);
                                    if (c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR))
                                        return 2;
                                    else
                                        return 1;
                                } else
                                    return 0;
                            } catch (ParseException e) {

                                e.printStackTrace();
                                return -1;
                            }


                        }

                        private String getDateFormat(String time) {
                            try {
                                Date date = dfs.parse(time);
                                return todayDateFormat.format(date);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        private String getDateFormat2(String time) {
                            try {
                                Date date = dfs.parse(time);
                                return previousDateFormat.format(date);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        private String getDateFormat3(String time) {
                            try {
                                Date date = dfs.parse(time);
                                return yesterdayDateFormat.format(date);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        private int checkDates(String time, String previousDate) {
                            try {
                                Date date1 = dfs.parse(time);
                                Date date;
                                date = dfs.parse(previousDate);
                                long difference = date1.getTime() - date.getTime();
                                if (difference > 30 * 60 * 1000) {
                                    Calendar c1 = Calendar.getInstance(); // today
                                    Calendar c2 = Calendar.getInstance();
                                    c2.setTime(date1); // your date

                                    if (c1.get(Calendar.DAY_OF_YEAR) != c2.get(Calendar.DAY_OF_YEAR)) {
                                        c1.add(Calendar.DAY_OF_YEAR, -1);
                                        if (c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR))
                                            return 2;
                                        else
                                            return 1;
                                    } else
                                        return 0;
                                } else return -1;


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            return 0;
                        }

                        @Override
                        public void onError(ANError anError) {

                            try {
                                Log.d(TAG, "onError: " + anError.getResponse());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (
                JSONException e)

        {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {


        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sender", session.getMobileNumber());
        hashMap.put("receiver", othernumber);
        hashMap.put("s_name", session.getUsername());
        hashMap.put("r_name", othername);
        hashMap.put("message", message);
        if (Constants.suggestpostid != null)
            hashMap.put("postid", Constants.suggestpostid);

        suggestpostid = null;
        AndroidNetworking.post("https://www.reweyou.in/reweyou/chatroom.php")
                .addBodyParameter(hashMap)
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d(TAG, "onResponse: " + response);
                            if (response.equals("sent")) {

                                userChatAdapter.changestateofsendingmessage(true);
                                // Toast.makeText(UserChat.this, "Message sent successfully", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(UserChat.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                userChatAdapter.changestateofsendingmessage(false);
                            }

                            // getChatList();
                        } catch (Exception e) {
                            // Toast.makeText(UserChat.this, "an error occured", Toast.LENGTH_SHORT).show();
                            userChatAdapter.changestateofsendingmessage(false);

                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        try {

                            //Toast.makeText(UserChat.this, "an error occured", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onError: " + anError.getResponse());
                            userChatAdapter.changestateofsendingmessage(false);

                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                });

    }

    public void addMessage(UserChatModel userChatModel) {
        userChatAdapter.add(userChatModel);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(userChatAdapter.getItemCount() - 1);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        userChatActivityOpen = true;
        LocalBroadcastManager.getInstance(this).registerReceiver(addMessageReceiver,
                new IntentFilter(Constants.ADD_CHAT_MESSAGE_EVENT));
    }

    @Override
    protected void onStop() {
        userChatActivityOpen = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(addMessageReceiver);
        super.onStop();
    }


}
