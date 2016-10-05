package in.reweyou.reweyou;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import in.reweyou.reweyou.classes.AppLocationService;
import in.reweyou.reweyou.classes.UserSessionManager;

public class SplashScreen extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    UserSessionManager session;
    private String mCurrentPhotoPath;
    int REQUEST_CAMERA = 0;
    AppLocationService appLocationService;
    Location location;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("ShaPreferences", Context.MODE_PRIVATE);
        session=new UserSessionManager(SplashScreen.this);
        appLocationService = new AppLocationService(SplashScreen.this);

   /*     Intent myIntent = new Intent(SplashScreen.this, Notification.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                SplashScreen.this, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar firingCal = Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();

        firingCal.set(Calendar.HOUR, 3); // At the hour you wanna fire
        firingCal.set(Calendar.MINUTE, 14); // Particular minute
        firingCal.set(Calendar.SECOND, 0); // particular second

        long intendedTime = firingCal.getTimeInMillis();
        long currentTime = currentCal.getTimeInMillis();

        if (intendedTime >= currentTime) // you can add buffer time too here to ignore some small differences in milliseconds
        {
            //set from today
            alarmManager.setRepeating(AlarmManager.RTC,
                    intendedTime, AlarmManager.INTERVAL_DAY,
                    pendingIntent);

        } else {
            //set from next day
            // you might consider using calendar.add() for adding one day to the current day
            firingCal.add(Calendar.DAY_OF_MONTH, 1);
            intendedTime = firingCal.getTimeInMillis();

            alarmManager.setRepeating(AlarmManager.RTC,
                    intendedTime, AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }

*/
      // location = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
        //if (location!= null) {
//Log.d("locattion", String.valueOf(location));
        //}
        //else {
               // showSettingsAlert();
          // Toast.makeText(SplashScreen.this,"Enable Location Provider!",Toast.LENGTH_SHORT);
            //}
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean firstTime = sharedPreferences.getBoolean("first", true);

        if (firstTime) {
            editor.putBoolean("first", false);
            editor.commit();
            Intent intent = new Intent(SplashScreen.this, WelcomeScreen.class);
            startActivity(intent);
            finish();
        } else {
            if(!session.checkLogin()) {
                Intent intent = new Intent(SplashScreen.this, Feed.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Intent intent = new Intent(SplashScreen.this, Signup.class);
                startActivity(intent);
                finish();
            }
        }
    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                SplashScreen.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                       SplashScreen.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
    public boolean isAlaramSet()
    {
        if(PendingIntent.getBroadcast(SplashScreen.this, 0,
                new Intent(SplashScreen.this, Notification.class),
                PendingIntent.FLAG_NO_CREATE) != null)
        {
            Log.d("myTag", "Alarm dont already active");
            return false;
        }
        else
        {
            Log.d("myTag", "Alarm is already active");

            return true;}
    }
}