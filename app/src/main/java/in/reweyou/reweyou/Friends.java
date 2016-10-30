package in.reweyou.reweyou;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.reweyou.reweyou.adapter.FriendsAdapter;
import in.reweyou.reweyou.classes.RequestHandler;
import in.reweyou.reweyou.classes.UserSessionManager;

public class Friends extends AppCompatActivity {
    static final String[] PERMISSIONS = new String[]{Manifest.permission.READ_CONTACTS};
    private static final int REQUEST_CODE = 0;
    ArrayList<String> messagelist = new ArrayList<>();
    PermissionsChecker checker;
    ArrayList<String> aa = new ArrayList<>();
    ArrayList<String> conname = new ArrayList<>();
    private RecyclerView recyclerView;
    private FriendsAdapter adapter;
    private ProgressBar progressBar;
    private HashMap<String, String> contactsHashMaps = new HashMap<>();
    private List<String> contactNumber = new ArrayList<>();
    private List<String> matchedNumber = new ArrayList<>();
    private List<String> matchedName = new ArrayList<>();
    private UserSessionManager session;

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        session = new UserSessionManager(getApplicationContext());

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Contacts");
        checker = new PermissionsChecker(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(Friends.this));
        if (checker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        } else {
            getNumber(this.getContentResolver());

            //Progress bar
            progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
            new JSONTask().execute("9711188949");
            //adapter = new FriendsAdapter(Friends.this, messagelist);
            //recyclerView.setAdapter(adapter);

        }
    }

    public void getNumber(ContentResolver cr) {
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (phones.moveToNext()) {

            //String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            //contactsHashMaps.put("phone",phoneNumber);
            String number = phoneNumber.replaceAll("\\D", "");

            contactNumber.add(number);

           /* System.out.println(".................."+phoneNumber);
            name = phoneNumber.substring(3);
            name=name.replace(" ","");
            aa.add(name);*/
        }

        Log.d("co", String.valueOf(contactNumber));
        phones.close();
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    public void getContactNaame(Context context, String phoneNumber) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = context.getContentResolver().query(uri,
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
        List<String> listIds = new ArrayList<String>(); //Arraylist to hold the unique ids
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                if (!listIds.contains(contactId))
                    listIds.add(contactId); //adding unique id to arraylist
            }
            //pass unique ids to get contact names
            for (int i = 0; i < listIds.size(); i++) {
                String newId = listIds.get(i);
                Cursor cursorDetails = context.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{newId}, null);
                if (cursorDetails != null) {
                    if (cursorDetails.moveToFirst()) {
                        String contactName = cursorDetails.getString(cursorDetails.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
                        // System.out.println("ID : " + newId + " Name : " + contactName + " Number : " + phoneNumber);
                        conname.add(contactName);
                    }
                    cursorDetails.close();
                }
            }
            cursor.close();
        }


    }

    public class JSONTask extends AsyncTask<String, String, List<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected List<String> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            RequestHandler rh = new RequestHandler();
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("query", params[0]);
            try {
                URL url = new URL("https://www.reweyou.in/reweyou/friends.php");
                connection = (HttpURLConnection) url.openConnection();

                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(rh.getPostDataString(data));
                wr.flush();


                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finalJson = buffer.toString();

                JSONArray parentArray = new JSONArray(finalJson);
                StringBuffer finalBufferedData = new StringBuffer();

                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    messagelist.add(finalObject.getString("number"));

                    for (int j = 0; j < contactNumber.size(); j++) {
                        if (PhoneNumberUtils.compare(finalObject.getString("number"), contactNumber.get(j))) {
                            Log.d("matchedNumber", contactNumber.get(j));

                            if (!matchedNumber.contains(contactNumber.get(j))) {
                                if (!PhoneNumberUtils.compare(contactNumber.get(j), session.getMobileNumber())) {
                                    matchedNumber.add(contactNumber.get(j));
                                    Log.d("namee", getContactName(Friends.this, contactNumber.get(j)));
                                    matchedName.add(getContactName(Friends.this, contactNumber.get(j)));
                                }

                            }

                        }
                    }
                }
                return matchedName;

                //return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);

            /*ArrayList<String> tempToDelete = new ArrayList<>();

            for (int i = 0; i < result.size(); i++) {
                for (int j = 0; j < aa.size(); j++) {
                    if (result.get(i).equals(aa.get(j))) {
                        tempToDelete.add(aa.get(j));
                        break;
                    }
                    //    if (PhoneNumberUtils.compare(result.get(i), aa.get(j))) {
                    //      //they are the same do whatever you want!
                    //    tempToDelete.add(aa.get(j));
                    //  break;
                    //}

                }
            }
            for (int i = 0; i < tempToDelete.size(); i++) {
                getContactName(Friends.this, tempToDelete.get(i));
            }*/

            //aa.removeAll(tempToDelete);
            progressBar.setVisibility(View.GONE);
            FriendsAdapter adapter = new FriendsAdapter(Friends.this, result);
            recyclerView.setAdapter(adapter);

            //need to set data to the list
        }
    }

}