package in.reweyou.reweyou.fragment;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import in.reweyou.reweyou.adapter.FriendsAdapter;
import in.reweyou.reweyou.classes.HidingScrollListener;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.classes.RequestHandler;
import in.reweyou.reweyou.adapter.MessageAdapter;
import in.reweyou.reweyou.classes.UserSessionManager;
import in.reweyou.reweyou.model.MpModel;


public class FriendsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swipeLayout;
    private RecyclerView recyclerView;
    ArrayList<String> messagelist = new ArrayList<>();
    private FriendsAdapter adapter;
    private ProgressBar progressBar;
    String phoneNumber;
    ArrayList<String> aa = new ArrayList<>();
    ArrayList<String> conname=new ArrayList<>();

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_friends, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        swipeLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getNumber(getActivity().getContentResolver());
        Messages();
        adapter = new FriendsAdapter(getActivity(), messagelist);
        recyclerView.setAdapter(adapter);

    }

    private void Messages() {
        new JSONTask().execute("9711188949");
    }

    @Override
    public void onRefresh() {
        Messages();
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
                }

                return messagelist;

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

            ArrayList<String> tempToDelete = new ArrayList<>();

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
            for(int i=0;i<tempToDelete.size(); i++)
            {
                getContactName(getActivity(),tempToDelete.get(i));
            }

            //aa.removeAll(tempToDelete);
            FriendsAdapter adapter = new FriendsAdapter(getActivity(), conname);
            progressBar.setVisibility(View.GONE);
            recyclerView.setAdapter(adapter);
            swipeLayout.setRefreshing(false);
            //need to set data to the list
        }
    }

    public void getNumber(ContentResolver cr) {
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            System.out.println(".................." + phoneNumber);
            name = phoneNumber.substring(3);
            name = name.replace(" ", "");
            aa.add(name);
        }
        phones.close();// close cursor
        //display contact numbers in the list
    }

    public void getContactName(Context context, String phoneNumber) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = context.getContentResolver().query(uri,
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID},null, null, null);
        List<String> listIds = new ArrayList<String>(); //Arraylist to hold the unique ids
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                if(!listIds.contains(contactId))
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
                        System.out.println("ID : " + newId + " Name : " + contactName + " Number : " + phoneNumber);
                        conname.add(contactName);
                    }
                    cursorDetails.close();
                }
            }
            cursor.close();
        }

    }

}

