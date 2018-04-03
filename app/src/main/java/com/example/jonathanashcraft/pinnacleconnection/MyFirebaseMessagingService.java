package com.example.jonathanashcraft.pinnacleconnection;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by Joseph on 3/30/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String RECIEVED_DATA = "Data Received";
    Map<String, String> MessageReceivedHolder;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("onCreate", "onCreate for FirebaseMessagingService was called");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(RECIEVED_DATA, "We have received data");

            MessageReceivedHolder = remoteMessage.getData();

            Log.d(RECIEVED_DATA, "The size of the data is " + MessageReceivedHolder.size());

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

}
