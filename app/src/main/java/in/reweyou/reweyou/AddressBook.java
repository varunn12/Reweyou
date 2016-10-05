package in.reweyou.reweyou;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class AddressBook extends Activity implements AdapterView.OnItemClickListener {

    String phoneNumber;
    PermissionsChecker checker;
    private static final int REQUEST_CODE = 0;
    ListView lv;
    ArrayList <String> aa= new ArrayList<String>();
    static final String[] PERMISSIONS = new String[]{ Manifest.permission.READ_CONTACTS};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_address_book);
        lv= (ListView) findViewById(R.id.lv);
        checker = new PermissionsChecker(this);
        if (checker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();

        }
        else
        {
            getNumber(this.getContentResolver());
        }


    }

    public void getNumber(ContentResolver cr)
    {
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            System.out.println(".................."+name);
            String number =  phoneNumber.substring(3);
            number=number.replace(" ","");
            String contact=name+ "    " +number;
            aa.add(contact);
        }
        phones.close();// close cursor
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,aa);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        //display contact numbers in the list
    }
    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String val =(String) parent.getItemAtPosition(position);
        val =val.substring(val.length() - 10);
        Bundle bundle = new Bundle();
        bundle.putString("myData", val);
        Intent in = new Intent(AddressBook.this, UserProfile.class);
        in.putExtras(bundle);
        startActivity(in);
    }




}

