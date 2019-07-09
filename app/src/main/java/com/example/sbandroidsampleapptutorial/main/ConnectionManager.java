package com.example.sbandroidsampleapptutorial.main;

import com.example.sbandroidsampleapptutorial.utils.PreferenceUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

public class ConnectionManager {


    public static void login(String userId, final SendBird.ConnectHandler handler){
        SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (handler != null){
                    handler.onConnected(user, e);
                }
//                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.class, new OnSuccessListener<InstanceIdResult>() {
//                    @Override
//                    public void onSuccess(InstanceIdResult instanceIdResult) {
//                        SendBird.registerPushTokenForCurrentUser(instanceIdResult.getToken(), new SendBird.RegisterPushTokenWithStatusHandler() {
//                            @Override
//                            public void onRegistered(SendBird.PushTokenRegistrationStatus status, SendBirdException e) {
//                                if (e != null) {        // Error.
//                                    return;
//                                }
//                            }
//                        });
//                    }
//                });
            }
        });
    }

    public static void logout(final SendBird.DisconnectHandler handler){
        SendBird.disconnect(new SendBird.DisconnectHandler() {
            @Override
            public void onDisconnected() {
                if (handler != null){
                    handler.onDisconnected();
                }
            }
        });
    }

    public static void addConnectionManagementHandler(String handlerId, final ConnectionManagementHandler handler){
        SendBird.addConnectionHandler(handlerId, new SendBird.ConnectionHandler() {
            @Override
            public void onReconnectStarted() {

            }

            @Override
            public void onReconnectSucceeded() {
                if (handler != null){
                    handler.onConnected(true);
                }
            }

            @Override
            public void onReconnectFailed() {
            }
        });

        // OPEN connection
        if (SendBird.getConnectionState() == SendBird.ConnectionState.OPEN) {
            if (handler != null){
                handler.onConnected(false);
            }
        }
        // push notification or system kill
        else if (SendBird.getConnectionState() == SendBird.ConnectionState.CLOSED) {
            String userId = PreferenceUtils.getUserId();
            SendBird.connect(userId, new SendBird.ConnectHandler() {
                @Override
                public void onConnected(User user, SendBirdException e) {
                    if (e != null){
                        return;
                    }

                    if (handler != null){
                        handler.onConnected(false);
                    }
                }
            });
        }
    }

    public static void removeConnectionManagementHandler(String handlerId){
        SendBird.removeChannelHandler(handlerId);
    }


    public interface ConnectionManagementHandler {
        /*
        * callback for when connected or reconnected to refresh.
        *  if connected -> false, reconnected -> true
         */
        void onConnected(boolean reconnect);
    }
}
