package com.example.sbandroidsampleapptutorial.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.sbandroidsampleapptutorial.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sendbird.android.SendBird;

import static android.provider.Settings.System.getString;

public class PushUtils {

    public static void registerPushTokenForCurrentUser(final Context context, final SendBird.RegisterPushTokenWithStatusHandler handler) {
//        SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(), handler);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("pushutils-----", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d("pushUtils___token: ", token);
                        Log.d("pushUtils___sendBird: ", token);
                        SendBird.registerPushTokenForCurrentUser(token, handler);
                        // Log and toast

                        Toast.makeText(context, "pushUtils___token: " + token, Toast.LENGTH_SHORT).show();
                    }
                });
//        SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getInstanceId(), handler);
    }

    public static void unregisterPushTokenForCurrentUser(final Context context, SendBird.UnregisterPushTokenHandler handler) {
        SendBird.unregisterPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(), handler);
    }

}
