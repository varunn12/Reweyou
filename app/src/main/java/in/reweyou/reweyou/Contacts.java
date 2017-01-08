package in.reweyou.reweyou;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import in.reweyou.reweyou.utils.Constants;

public class Contacts extends AppCompatActivity {

    private static final String PACKAGE_URL_SCHEME = "package:";
    private final int PERMISSION_REQUEST_CODE = 5;
    private final String[] PERMISSIONS_CONTACT_READ = new String[]{Manifest.permission.READ_CONTACTS};
    private String TAG = Contacts.class.getSimpleName();
    private List<String> matchContactList = new ArrayList<>();
    private List<String> tempContactList = new ArrayList<>();
    private List<ContactListModel> contactList = new ArrayList<>();
    private List<ContactListModel> finalMatchContactList = new ArrayList<>();
    private Gson gson = new Gson();
    private List<UserChatThreadModel> chatThreadList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserSessionManager session;
    private FetchContacts fetchContacts;

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


        if (!hasPermissions(this, PERMISSIONS_CONTACT_READ)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_CONTACT_READ, PERMISSION_REQUEST_CODE);
        } else {
            permissionGranted();
        }
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
                                                tempContactList.add(userChatThreadModel.getReceiver());
                                                break;
                                            }
                                        }
                                    } else {
                                        for (int j = 0; j < contactList.size(); j++) {
                                            if (contactList.get(j).getNumber().equals(userChatThreadModel.getSender())) {
                                                userChatThreadModel.setPic(contactList.get(j).getPic());
                                                userChatThreadModel.setshowNumber(userChatThreadModel.getSender());
                                                userChatThreadModel.setname(userChatThreadModel.getSender_name());

                                                tempContactList.add(userChatThreadModel.getSender());

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
                                tempContactList.add(session.getMobileNumber());
                                for (int i = 0; i < tempContactList.size(); i++) {
                                    for (int j = 0; j < matchContactList.size(); j++) {
                                        if (matchContactList.get(j).equals(tempContactList.get(i))) {
                                            matchContactList.remove(tempContactList.get(i));
                                            Log.d(TAG, "onResponse: temp" + tempContactList.get(i));
                                        }
                                    }
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

                            Toast.makeText(Contacts.this, "Couldn't get data", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
        Constants.suggestpostid = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {

                String permission = permissions[0];
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission

                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(Contacts.this, permission);
                    if (!showRationale) {
                        showPermissionDeniedDialog();
                    } else
                        showPermissionRequiredDialog(permission);
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // registerUser();
                    permissionGranted();
                }
                break;
            }
        }
    }

    private void showPermissionRequiredDialog(final String permission) {
        AlertDialogBox alertDialogBox = new AlertDialogBox(Contacts.this, "Permission Required", getResources().getString(R.string.permission_required_contacts), "grant", null) {
            @Override
            public void onNegativeButtonClick(DialogInterface dialog) {
                // dialog.dismiss();
                //registerUser();
                // permissionGranted();
            }

            @Override
            public void onPositiveButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                String[] p = {permission};
                ActivityCompat.requestPermissions(Contacts.this, p, PERMISSION_REQUEST_CODE);

            }
        };
        alertDialogBox.setCancellable(false);
        alertDialogBox.show();
    }

    private void showPermissionDeniedDialog() {
        AlertDialogBox alertDialogBox = new AlertDialogBox(Contacts.this, "Permission Denied", getResources().getString(R.string.permission_denied_contacts), "settings", null) {
            @Override
            public void onNegativeButtonClick(DialogInterface dialog) {

            }

            @Override
            public void onPositiveButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                startAppSettings();

            }
        };
        alertDialogBox.setCancellable(false);
        alertDialogBox.show();
    }

    private void startAppSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

    private void permissionGranted() {
        fetchContacts = new FetchContacts();
        fetchContacts.execute();
    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if (fetchContacts != null)
            fetchContacts.cancel(true);
        super.onDestroy();
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

            if (!isCancelled()) {
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
}
