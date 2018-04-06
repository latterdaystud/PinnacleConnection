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
 * This class handles the device tokens and updating these tokens.
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


    public static void loadToken() {
        if (tokenRefreshed) {
            Log.d(TAG, "Token needs to be refreshed");

            sendRegistrationToServer(refreshedToken);

            tokenRefreshed = false;
        } else {
            Log.d(TAG, "Token does not need to be refresged");
        }
    }
    private static void sendRegistrationToServer(String refreshedToken) {
        // Make the changes to Firebase so that the token is updated

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d(TAG, "firebasePath: " + currentUID);

        // Hopefully that finds the correct reference
        DatabaseReference userUpdateRef = database.getReference().child("Users")
                .child(currentUID).child("deviceToken");

        // Find out if the user is a manager by this point
        Log.d(TAG, "Is the user a manager right now? " + CurrentUser.isManager());

        // See if the user is a manager
        if (CurrentUser.isManager()) {
            // Get the reference to the manager section of the users
            DatabaseReference managerUpdateRef = database.getReference().child("Managers")
                    .child(currentUID).child("deviceToken");

            // Update the token
            managerUpdateRef.setValue(refreshedToken)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Refreshed Manager Token");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Failed to Refresh Manager Token");
                        }
                    });
        } else {
            // User is not a manager
            DatabaseReference tenantUpdateRef = database.getReference().child("Tenants")
                    .child(currentUID).child("deviceToken");

            // Update the token
            tenantUpdateRef.setValue(refreshedToken)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Tenant Token Refreshed!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Failed To Refresh Tenant Token");
                        }
                    });
        }

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
