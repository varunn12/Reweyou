package in.reweyou.reweyou;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

public class Notification extends BroadcastReceiver {
    private int MID;


    @Override
    public void onReceive(Context context, Intent arg1) {
        showNotification(context);
    }

    private void showNotification(Context context) {
        MID =1;
        long when = System.currentTimeMillis();

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, Feed.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Reweyou")
                        .setAutoCancel(true).setWhen(when)
                        .setContentText("Report news and issues around you. Click, Report and Share.");
        mBuilder.setContentIntent(contentIntent);
      //  mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MID, mBuilder.build());
        MID++;

    }
}
