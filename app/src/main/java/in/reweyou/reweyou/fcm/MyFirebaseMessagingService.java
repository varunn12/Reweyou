package in.reweyou.reweyou.fcm;

/**
 * Created by Reweyou on 10/14/2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import in.reweyou.reweyou.MyProfile;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.SinglePostActivity;
import in.reweyou.reweyou.UserChat;
import in.reweyou.reweyou.utils.Constants;

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
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
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

            String postid = payload.getString("postid");


            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);

            if (postid.equals(NOTI_TYPE_CHAT)) {

                if (UserChat.userChatActivityOpen) {
                    Intent intent = new Intent(Constants.ADD_CHAT_MESSAGE_EVENT);

                    intent.putExtra(Constants.ADD_CHAT_MESSAGE_SENDER_NUMBER, payload.getString("sender_name"));
                    intent.putExtra(Constants.ADD_CHAT_MESSAGE_MESSAGE, message);
                    intent.putExtra(Constants.ADD_CHAT_MESSAGE_TIMESTAMP, timestamp);
                    intent.putExtra(Constants.ADD_CHAT_MESSAGE_CHATROOM_ID, payload.getString("chatroom_id"));
                    if (payload.has("suggestid"))
                        Constants.suggestpostid = payload.getString("suggestid");




                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                } else {
                    Log.w(TAG, "handleDataMessage: chat activity is background");
                    Intent i = new Intent(this, UserChat.class);

                    i.putExtra(Constants.ADD_CHAT_MESSAGE_SENDER_NAME, title);
                    i.putExtra(Constants.ADD_CHAT_MESSAGE_CHATROOM_ID, payload.getString("chatroom_id"));
                    i.putExtra(Constants.ADD_CHAT_MESSAGE_SENDER_NUMBER, payload.getString("sender_name"));
                    //   i.putExtra(Constants.ADD_CHAT_MESSAGE_SENDER_NUMBER, "9711188949");

                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.logo_plain)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)

                            .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                            .setContentTitle(title + " messaged you")
                            .setContentText(message);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.notify(100, mBuilder.build());
                }

            } else if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
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
                        // image is present, show notification with image
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                    }
                }
            } else {
                // app is in background, show the notification in notification tray
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
                        // image is present, show notification with image
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                    }
                }
            }
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
