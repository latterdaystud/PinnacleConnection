package com.example.jonathanashcraft.pinnacleconnection;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.content.ContentValues.TAG;

/**
 * Created by Joseph on 3/30/2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    String TOKEN_REFRESH = "TokenRefresh";

    @Override
    public void onTokenRefresh() {
        Log.d(TOKEN_REFRESH, "Token is being reshreshed");

        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TOKEN_REFRESH, "Refreshed token: " + refreshedToken);

        updateUserTokenOnFirebase(refreshedToken);
    }

    public void updateUserTokenOnFirebase(String token) {
        // TODO: Update the users token on the database
//        DatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users")
//                .child(AndroidUser.)

        Log.d(TOKEN_REFRESH, "Updated the token on the database");

    }
}
