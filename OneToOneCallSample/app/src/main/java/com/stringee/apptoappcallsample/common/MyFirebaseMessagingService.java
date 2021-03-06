package com.stringee.apptoappcallsample.common;

import android.app.NotificationManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.stringee.apptoappcallsample.MainActivity;
import com.stringee.listener.StatusListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String TAG = "Stringee";

    @Override
    public void onNewToken(@NonNull String s) {
        if (MainActivity.client != null && MainActivity.client.isConnected()) {
            MainActivity.client.registerPushToken(s, new StatusListener() {
                @Override
                public void onSuccess() {

                }
            });
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String pushFromStringee = remoteMessage.getData().get("stringeePushNotification");
            if (pushFromStringee != null) {
                if (MainActivity.client == null || Common.isAppInBackground) {
                    try {
                        JSONObject jsonObject = new JSONObject(remoteMessage.getData().get("data"));
                        String callStatus = jsonObject.optString("callStatus", null);
                        String callId = jsonObject.optString("callId", null);
                        String from = jsonObject.getJSONObject("from").getString("alias");
                        if (callId != null && callStatus != null) {
                            switch (callStatus) {
                                case "started":
                                    //make a notification when app in background or killed
                                    Notification.notifyIncomingCall(getApplicationContext(), from);
                                    break;
                                case "ended":
                                    //remove notification
                                    NotificationManager nm = (NotificationManager) getSystemService
                                            (NOTIFICATION_SERVICE);
                                    if (nm != null) {
                                        nm.cancel(44448888);
                                    }
                                    break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
