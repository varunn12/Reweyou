package in.reweyou.reweyou.gcm;

/**
 * Created by Reweyou on 5/8/2016.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.Random;

import in.reweyou.reweyou.MyProfile;
import in.reweyou.reweyou.Notifications;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.SinglePostAcitivty;

//Class is extending GcmListenerService
public class GCMPushReceiverService extends GcmListenerService {

    private int m;
    //This method will be called on every new message received
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //Getting the message from the bundle
        String Requestcode = String.valueOf(1);
        String profilecode=String.valueOf(2);
        String message = data.getString("message");
        String code=data.getString("title");
        String postid=data.getString("subtitle");
        Random random = new Random();
         m = random.nextInt(9999 - 1000) + 1000;
        //Displaying a notiffication with the message
        if(code.equals(Requestcode)) {
            sendNotificationComments(message, postid);
        }
        else if(code.equals(profilecode))
        {
            sendNotificationFollow(message, postid);
        }
        else
        {
            sendNotification(message, postid);
        }
    }

    //This method is generating a notification and displaying the notification
    private void sendNotification(String message, String postid) {
        Bundle bundle = new Bundle();
        bundle.putString("postid", postid);
        Intent intent = new Intent(this, SinglePostAcitivty.class);
        intent.putExtras(bundle);
        intent.setAction(Long.toString(System.currentTimeMillis()));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        RemoteViews mContentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this);
        Notification notification = mBuilder.setSmallIcon(R.drawable.logo_plain).setTicker("Reweyou").setWhen(0)
                .setAutoCancel(true)
                .setContentTitle("Reweyou")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentText(message).build();


        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(m, notification); //0 = ID of notification
    }
    private void sendNotificationComments(String message, String postid) {
        Intent intent = new Intent(this, Notifications.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        RemoteViews mContentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_plain)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH);

        mContentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        mContentView.setTextViewText(R.id.title, message);
        mContentView.setInt(R.id.title, "setTextColor",
                android.graphics.Color.BLACK);

        noBuilder.setContent(mContentView);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }

    private void sendNotificationFollow(String message, String postid) {
        Intent intent = new Intent(this, MyProfile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        RemoteViews mContentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_plain)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH);

        mContentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        mContentView.setTextViewText(R.id.title, message);
        mContentView.setInt(R.id.title, "setTextColor",
                android.graphics.Color.BLACK);

        noBuilder.setContent(mContentView);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, noBuilder.build()); //0 = ID of notification
    }

}
