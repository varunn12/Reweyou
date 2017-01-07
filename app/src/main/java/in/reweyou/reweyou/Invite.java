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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import in.reweyou.reweyou.adapter.InviteAdapter;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.ContactListModel;

public class Invite extends AppCompatActivity {

    private static final String TAG = Invite.class.getSimpleName();
    private ArrayList<ContactListModel> contactList = new ArrayList<>();
    private UserSessionManager session;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Invite");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        session = new UserSessionManager(Invite.this);

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(Invite.this));

        FetchContacts fetchContacts = new FetchContacts();
        fetchContacts.execute();
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

                    ArrayList<ContactListModel> matchContactList = new ArrayList<>();
                    ArrayList<ContactListModel> nonmatchContactList = new ArrayList<>();

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject jsonObject1 = response.getJSONObject(0);


                            if (jsonObject1.has("contact")) {
                                JSONObject jsonObject_contact = jsonObject1.getJSONObject("contact");

                                for (Iterator<String> iter = jsonObject_contact.keys(); iter.hasNext(); ) {
                                    String key = iter.next();
                                    for (int j = 0; j < contactList.size(); j++) {
                                        if (contactList.get(j).getNumber().equals(jsonObject_contact.getString(key))) {
                                            matchContactList.add(contactList.get(j));

                                            break;
                                        }
                                        if (j == contactList.size() - 1) {
                                            nonmatchContactList.add(contactList.get(j));
                                        }
                                    }
                                }

                            }

                            Log.d(TAG, "onResponse: smatfh" + matchContactList + nonmatchContactList);

                            InviteAdapter inviteAdapter = new InviteAdapter(Invite.this, matchContactList, nonmatchContactList, session);
                            recyclerView.setAdapter(inviteAdapter);
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
