package com.example.sbandroidsampleapptutorial.fcm;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

public class MyFirebaseInstanceIDService {

//    private static final String TAG = "MyFirebaseIIDService";
//
//    /**
//     * Called if InstanceID token is updated. This may occur if the security of
//     * the previous token had been compromised. Note that this is called when the InstanceID token
//     * is initially generated so this is where you would retrieve the token.
//     */
//    @Override
//    public void onTokenRefresh() {
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.d(TAG, "Refreshed token: " + refreshedToken);
//
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // Instance ID token to your app server.
//        sendRegistrationToServer(refreshedToken);
//    }
//
//    /**
//     * Persist token to third-party servers.
//     *
//     * Modify this method to associate the user's FCM InstanceID token with any server-side account
//     * maintained by your application.
//     *
//     * @param token The new token.
//     */
//    private void sendRegistrationToServer(String token){
//        // Register a registration token to SendBird server.
//        SendBird.registerPushTokenForCurrentUser(token, new SendBird.RegisterPushTokenWithStatusHandler() {
//            @Override
//            public void onRegistered(SendBird.PushTokenRegistrationStatus ptrs, SendBirdException e) {
//                if (e != null) {
//                    Toast.makeText(MyFirebaseInstanceIDService.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (ptrs == SendBird.PushTokenRegistrationStatus.PENDING) {
//                    // A token registration is pending.
//                    // Retry the registration after a connection has been successfully established.
//                }
//            }
//        });
//    }
}
