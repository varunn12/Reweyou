package in.reweyou.reweyou;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import in.reweyou.reweyou.classes.AppLocationService;
import in.reweyou.reweyou.classes.UserSessionManager;

public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "SplashScreen";
    SharedPreferences sharedPreferences;
    UserSessionManager session;
    AppLocationService appLocationService;
    Location location;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("ShaPreferences", Context.MODE_PRIVATE);
        session=new UserSessionManager(SplashScreen.this);
        appLocationService = new AppLocationService(SplashScreen.this);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean firstTime = sharedPreferences.getBoolean("first", true);


       /* Intent intent = new Intent(SplashScreen.this, TutorialScreen.class);
        startActivity(intent);
        finish();*/

        if (firstTime) {
            editor.putBoolean("first", false);
            editor.apply();

            Intent intent = new Intent(SplashScreen.this, TutorialScreen.class);
            startActivity(intent);
            finish();
        } else {
            if(!session.checkLoginSplash()) {

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
}