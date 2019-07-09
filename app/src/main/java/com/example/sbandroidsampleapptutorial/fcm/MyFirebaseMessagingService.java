package com.example.sbandroidsampleapptutorial.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.sbandroidsampleapptutorial.R;
import com.example.sbandroidsampleapptutorial.groupchannel.GroupChannelActivity;
import com.example.sbandroidsampleapptutorial.utils.PreferenceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.shadow.com.google.gson.JsonElement;
import com.sendbird.android.shadow.com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "MyFirebaseMsgService";
    private String fcmToken = "";
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
//    @Override
//    public void onNewToken() {
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.d(TAG, "Refreshed token: " + refreshedToken);
//
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // Instance ID token to your app server.
//        sendRegistrationToServer(refreshedToken);
//    }



    @Override
    public void onNewToken(String token) {
        // ...
        fcmToken = token;
        Log.d("Token: ", token);
        Log.d("log", "--------------------");

        // Register a registration token to SendBird server.
        SendBird.registerPushTokenForCurrentUser(token, new SendBird.RegisterPushTokenWithStatusHandler() {
            @Override
            public void onRegistered(SendBird.PushTokenRegistrationStatus ptrs, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(MyFirebaseMessagingService.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ptrs == SendBird.PushTokenRegistrationStatus.PENDING) {
                    // A token registration is pending.
                    // Retry the registration after a connection has been successfully established.
                    Toast.makeText(MyFirebaseMessagingService.this, "Connection required to register push token.", Toast.LENGTH_SHORT).show();
                    Log.d("log", "onNewToken_______registerPushTokenForCurrentUser");

                }
            }
        });
    }




    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
//    private void sendRegistrationToServer(String token){
//        // Register a registration token to SendBird server.
//        SendBird.registerPushTokenForCurrentUser(token, new SendBird.RegisterPushTokenWithStatusHandler() {
//            @Override
//            public void onRegistered(SendBird.PushTokenRegistrationStatus ptrs, SendBirdException e) {
//                if (e != null) {
//                    Toast.makeText(MyFirebaseMessagingService.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (ptrs == SendBird.PushTokenRegistrationStatus.PENDING) {
//                    // A token registration is pending.
//                    // Retry the registration after a connection has been successfully established.
//                    Toast.makeText(MyFirebaseMessagingService.this, "Connection required to register push token.", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
//    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("log", "onMessageReceived_______" + remoteMessage.toString());
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        String channelUrl = null;
        try {
//            String message = remoteMessage.getData().get("message");
//            JsonElement payload = new JsonParser().parse(remoteMessage.getData().get("sendbird"));

            JSONObject sendBird = new JSONObject(remoteMessage.getData().get("sendbird"));
            JSONObject channel = (JSONObject) sendBird.get("channel");
            channelUrl = (String) channel.get("channel_url");
            // logic
//            if (channelUrl != ) {
//
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "previous stuff worked ");


        String message = remoteMessage.getData().get("message");
//        JsonElement payload = new JsonParser().parse(remoteMessage.getData().get("sendbird"));
         sendNotification(this, message, channelUrl);
    }

    private void sendNotification(Context context, String message, String channelUrl) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        // Your own way to show notifications to users.
        final String CHANNEL_ID = "CHANNEL_ID";
        if (Build.VERSION.SDK_INT >= 26) {  // Build.VERSION_CODES.O
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        Intent intent = new Intent(context, GroupChannelActivity.class);
        intent.putExtra("groupChannelUrl", channelUrl);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.img_notification)
                .setColor(Color.parseColor("#7469C4"))  // small icon background color
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.img_notification_large))
                .setContentTitle(this.getResources().getString(R.string.app_name))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        if (PreferenceUtils.getNotificationsShowPreviews()) {
            notificationBuilder.setContentText(message);
        } else {
            notificationBuilder.setContentText("Somebody sent you a message.");
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }





}
