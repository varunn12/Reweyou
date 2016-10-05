package in.reweyou.reweyou;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyou.model.MpDetail;

public class Details extends AppCompatActivity {

    private TextView tvData;
    private ListView lvmp;
    private ProgressDialog dialog;
    private int i;
    private Toolbar toolbar;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Elected Representatives");



        dialog= new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading, Please wait...");

        // Create default options which will be used for every
//  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config); // Do it on Application start



        lvmp = (ListView) findViewById(R.id.lvmp);


        new JSONTask().execute("https://www.reweyou.in/reweyou/mplist.php");
        Intent in=getIntent();
        Bundle bundle = getIntent().getExtras();
       // i = bundle.getInt("myData");
        i= Integer.parseInt(bundle.getString("id"));
        i=i-1;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, Feed.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(Details.this, Feed.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }



    public class JSONTask extends AsyncTask<String, String, List<MpDetail>> {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<MpDetail> doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
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

                List<MpDetail> MpModelList = new ArrayList<>();

                Gson gson = new Gson();
                //i=12;
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    MpDetail mpModel = gson.fromJson(finalObject.toString(),MpDetail.class);
 /*                    mpModel.setName(finalObject.getString("name"));
                    mpModel.setPlace(finalObject.getString("place"));
                    mpModel.setParty(finalObject.getString("party"));
                    mpModel.setPositions(finalObject.getString("positions"));
                    mpModel.setImage(finalObject.getString("image"));
                    mpModel.setEmail_id(finalObject.getString("email_id"));

                    String name = finalObject.getString("name");
                    String place = finalObject.getString("place");
                    */
                    MpModelList.add(mpModel);

                return MpModelList;

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
        protected void onPostExecute(List<MpDetail> result) {
            super.onPostExecute(result);
            //need to set data to the list
            dialog.dismiss();
            MpAdapter adapter = new MpAdapter(getApplicationContext(), R.layout.mprow, result);
            lvmp.setAdapter(adapter);

        }
    }

    public class MpAdapter extends ArrayAdapter {
        private List<MpDetail> MpModelList;
        private int resource;
        private LayoutInflater inflater;

        public MpAdapter(Context context, int resource, List<MpDetail> objects) {
            super(context, resource, objects);
            MpModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.image);
                holder.mpName = (TextView) convertView.findViewById(R.id.Who);
                holder.tvParty = (TextView) convertView.findViewById(R.id.Post);
                holder.tvPlace = (TextView) convertView.findViewById(R.id.place);
                holder.tvState = (TextView) convertView.findViewById(R.id.tvState);
                holder.tvType = (TextView) convertView.findViewById(R.id.tvType);
                holder.tvEmail = (TextView) convertView.findViewById(R.id.tvEmail);
                holder.tvCast = (TextView) convertView.findViewById(R.id.tvCast);
                holder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
                holder.tvContact = (TextView) convertView.findViewById(R.id.tvContact);
                holder.tvOffice = (TextView) convertView.findViewById(R.id.tvOffice);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

            // Then later, when you want to display image
            ImageLoader.getInstance().displayImage(MpModelList.get(position).getImage(), holder.ivIcon, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                }
            }); // Default options will be used

            holder.mpName.setText(MpModelList.get(position).getName());
            holder.tvParty.setText(MpModelList.get(position).getParty());
            holder.tvType.setText(MpModelList.get(position).getType());
            holder.tvPlace.setText(MpModelList.get(position).getPlace());
            holder.tvState.setText(MpModelList.get(position).getState());
            holder.tvEmail.setText(MpModelList.get(position).getEmail_id());
            holder.tvCast.setText(MpModelList.get(position).getPositions());
            holder.tvContact.setText(MpModelList.get(position).getContact());
            holder.tvOffice.setText(MpModelList.get(position).getOffice());
           // holder.tvAddress.setText(MpModelList.get(position).getAddress());
            return convertView;
        }

        class ViewHolder {
            private ImageView ivIcon;
            private TextView mpName;
            private TextView tvParty;
            private TextView tvPlace;
            private TextView tvState;
            private TextView tvEmail;
            private TextView tvType;
            private TextView tvCast;
            private TextView tvStory;
            private TextView tvOffice;
            private TextView tvContact;
            private TextView tvAddress;

        }
    }
}


