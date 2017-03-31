package in.reweyou.reweyou.fcm;

/**
 * Created by Reweyou on 10/14/2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.ReviewActivityQR;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private static final String NOTI_TYPE_CHAT = "chat";

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
           /* NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();*/
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Log.d(TAG, "handleDataMessage: " + payload.toString());

               /* if (UserChat.userChatActivityOpen && payload.getString("chatroom_id").equals(UserChat.chatroomid)) {
                    Intent intent = new Intent(Constants.ADD_CHAT_MESSAGE_EVENT);

                    intent.putExtra(Constants.ADD_CHAT_MESSAGE_SENDER_NUMBER, payload.getString("sender_name"));
                    intent.putExtra(Constants.ADD_CHAT_MESSAGE_MESSAGE, message);
                    intent.putExtra(Constants.ADD_CHAT_MESSAGE_TIMESTAMP, timestamp);
                    intent.putExtra(Constants.ADD_CHAT_MESSAGE_CHATROOM_ID, payload.getString("chatroom_id"));
                    intent.putExtra("suggestid", payload.getString("suggestid"));

                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                } else {
                    Log.w(TAG, "handleDataMessage: chat activity is background");
                    Intent i = new Intent(this, UserChat.class);
                    i.putExtra(Constants.ADD_CHAT_MESSAGE_SENDER_NAME, title);
                    i.putExtra(Constants.ADD_CHAT_MESSAGE_CHATROOM_ID, payload.getString("chatroom_id"));
                    i.putExtra(Constants.ADD_CHAT_MESSAGE_SENDER_NUMBER, payload.getString("sender_name"));

                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_stat_logo_plain)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)

                            .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                            .setContentTitle(title + " messaged you")
                            .setContentText(message);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.notify(100, mBuilder.build());
                }
*/
            Intent i = new Intent(this, ReviewActivityQR.class);

          /*  i.putExtra("headline", payload.getString("headline"));
            i.putExtra("description", payload.getString("description"));
            i.putExtra("rating", payload.getString("rating"));
            i.putExtra("name", payload.getString("user"));
            i.putExtra("review", payload.getString("reviews"));
            i.putExtra("tag", payload.getString("tag"));
            i.putExtra("image", payload.getString("image"));
            i.putExtra("video", payload.getString("video"));
            i.putExtra("gif", payload.getString("gif"));*/
            i.putExtra("qrdata", "https://www.reweyou.in/qr/topicid=" + payload.getString("topicid"));


            Random random = new Random();

            int m = random.nextInt(9999 - 1000) + 1000;

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_stat_logo_plain)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE)

                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(title)
                    .setContentText(message);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(m, mBuilder.build());
             /*else {
                Intent i = new Intent(Constants.SEND_NOTI_CHANGE_REQUEST);
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);

                if (postid.equals("0")) {
                    Intent resultIntent = new Intent(this, MyProfile.class);
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("postid", postid);
                    Intent resultIntent = new Intent(this, SinglePostActivity.class);
                    resultIntent.putExtras(bundle);
                    resultIntent.setAction(Long.toString(System.currentTimeMillis()));
                    // check for image attachment
                    if (TextUtils.isEmpty(imageUrl)) {
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                    } else {
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                    }
                }

            }*/
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
