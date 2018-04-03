package com.example.jonathanashcraft.pinnacleconnection;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Jonathan Ashcraft on 3/28/2018.
 */

public class TokenAccess extends FirebaseInstanceIdService {
    private static final String TAG = "Firebase Messaging";
    private static Boolean tokenRefreshed = false;
    private static String refreshedToken;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        // Get updated InstanceID token.
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // This means that the new token needs to be loaded
        tokenRefreshed = true;
    }


    public void loadToken() {
        if (tokenRefreshed) {
            Log.d(TAG, "Token needs to be refreshed");

            sendRegistrationToServer(refreshedToken);

            tokenRefreshed = false;
        } else {
            Log.d(TAG, "Token does not need to be refresged");
        }
    }
    private void sendRegistrationToServer(String refreshedToken) {
        // Make the changes to Firebase so that the token is updated

        String firebasePath = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d(TAG, "firebasePath: " + firebasePath);

        // Hopefully that finds the correct reference
        DatabaseReference userUpdateRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(firebasePath).child("deviceToken");

        // Let's log it just to make sure
        Log.d(TAG, "Current user is: " + FirebaseAuth.getInstance().getCurrentUser().getUid());


        // Give the updated token
        userUpdateRef.setValue(refreshedToken)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully updated token");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.wtf(TAG, "Failed to update token");
                    }
                });


    }
}
