package in.reweyou.reweyou;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import in.reweyou.reweyou.adapter.UserChatThreadAdapter;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.ContactListModel;
import in.reweyou.reweyou.model.UserChatThreadModel;

public class Contacts extends AppCompatActivity {

    private String TAG = Contacts.class.getSimpleName();
    private List<String> matchContactList = new ArrayList<>();
    private List<ContactListModel> contactList = new ArrayList<>();
    private List<ContactListModel> finalMatchContactList = new ArrayList<>();
    private Gson gson = new Gson();
    private List<UserChatThreadModel> chatThreadList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Inbox");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(Contacts.this));


        session = new UserSessionManager(Contacts.this);
        //getContactsfromDevice();
        FetchContacts fetchContacts = new FetchContacts();
        fetchContacts.execute();
    }

    private void getContacts() {

        final JSONObject jsonObject = new JSONObject();
        try {
            for (int i = 0; i < contactList.size(); i++)
                jsonObject.put(String.valueOf(i), contactList.get(i).getNumber());

            jsonObject.put("username", session.getMobileNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post("https://www.reweyou.in/reweyou/contact.php")
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject jsonObject1 = response.getJSONObject(0);


                            if (jsonObject1.has("chatroom")) {
                                JSONArray jsonArray_chatroom = jsonObject1.getJSONArray("chatroom");
                                for (int i = 0; i < jsonArray_chatroom.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray_chatroom.getJSONObject(i);
                                    UserChatThreadModel userChatThreadModel = gson.fromJson(jsonObject2.toString(), UserChatThreadModel.class);

                                    if (session.getMobileNumber().equals(userChatThreadModel.getSender())) {
                                        for (int j = 0; j < contactList.size(); j++) {
                                            if (contactList.get(j).getNumber().equals(userChatThreadModel.getReceiver())) {
                                                userChatThreadModel.setPic(contactList.get(j).getPic());
                                                userChatThreadModel.setshowNumber(userChatThreadModel.getReceiver());
                                                userChatThreadModel.setname(userChatThreadModel.getReceiver_name());
                                                break;
                                            }
                                        }
                                    } else {
                                        for (int j = 0; j < contactList.size(); j++) {
                                            if (contactList.get(j).getNumber().equals(userChatThreadModel.getSender())) {
                                                userChatThreadModel.setPic(contactList.get(j).getPic());
                                                userChatThreadModel.setshowNumber(userChatThreadModel.getSender());
                                                userChatThreadModel.setname(userChatThreadModel.getReceiver_name());


                                                break;
                                            }
                                        }
                                    }
                                    chatThreadList.add(userChatThreadModel);
                                }
                            }


                            if (jsonObject1.has("contact")) {
                                JSONObject jsonObject_contact = jsonObject1.getJSONObject("contact");

                                for (Iterator<String> iter = jsonObject_contact.keys(); iter.hasNext(); ) {
                                    String key = iter.next();
                                    matchContactList.add(jsonObject_contact.getString(key));
                                }
                            }

                            for (int i = 0; i < matchContactList.size(); i++) {
                                for (int j = 0; j < contactList.size(); j++)
                                    if (contactList.get(j).getNumber().equals(matchContactList.get(i))) {
                                        finalMatchContactList.add(contactList.get(j));
                                        break;
                                    }
                            }

                            UserChatThreadAdapter userChatThreadAdapter = new UserChatThreadAdapter(Contacts.this, chatThreadList, finalMatchContactList, session);
                            recyclerView.setAdapter(userChatThreadAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        try {
                            Log.d(TAG, "onError: " + anError.getErrorCode());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }


    public void getContactsfromDevice() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String phoneNumberPic = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
            String phoneNumberName = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


            if (phoneNumber.length() >= 10) {

                String number = phoneNumber.replaceAll("\\D+", "");
                try {
                    contactList.add(new ContactListModel(phoneNumberName, number.substring(number.length() - 10), phoneNumberPic));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        phones.close();
        // getContacts();
    }


    private class FetchContacts extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                getContactsfromDevice();
                return 1;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer rescode) {
            switch (rescode) {
                case 1:
                    getContacts();
                    break;
                case -1:
                    Log.w(TAG, "onPostExecute: contacts fetch error");
                    break;
            }
        }
    }
}
