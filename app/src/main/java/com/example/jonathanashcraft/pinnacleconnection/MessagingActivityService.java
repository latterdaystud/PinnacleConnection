package com.example.jonathanashcraft.pinnacleconnection;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;

public class MessagingActivityService extends FirebaseMessagingService {

    String TAG = "MessagingActivityService";

    public MessagingActivityService() {
    }

    /**
     * Function gets called when a message is received (either a notification or an actual message)
     * @param remoteMessage
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Let's log to see if we get the message
        Log.i(TAG, "<-------------------------------------------------------->");
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            // Log some messages to see what we are dealing with
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Log.d(TAG, "The size of the message is " + remoteMessage.getData().size());

            if (/* Check if data needs to be processed by long running job */ true) {
                Log.d(TAG, "Message: " + remoteMessage.getData().toString());

                String messageReceived = remoteMessage.getData().toString();

            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            // Send this to messagingActivity
        }


    }

    private void handleNow() {
    }

}
