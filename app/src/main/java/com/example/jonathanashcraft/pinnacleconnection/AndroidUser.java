package com.example.jonathanashcraft.pinnacleconnection;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

/**
 * Class representing the user with their loaded information from the firedatabase
 */

public class AndroidUser {
    private static final AndroidUser ourInstance = new AndroidUser();

    // To store the user
    private static final User user = new User();
    private static final String TAG = "AndroidUser";
    public static AndroidUser getInstance() {
        return ourInstance;
    }

    // Basic user  functions
    public static User getUser() { return user; }
    public static String getUserFirstName() { return user.getFirstName(); }
    public static String getUserLastName() { return user.getLastName(); }
    public static String getUserApartmentNumber() { return user.getApartmentNumber(); }
    public static Boolean isUserManager() { return user.isManager(); }

    // TODO: Handle the case where the user logs out and switches to a new user
    // current bug and crash in LoginActivity
    public static void updateUser(User mTempUser) { user.setUser(mTempUser);}

    // The private constructor (gets called once)
    private AndroidUser() {
        // Going to call the database reference to grab the user
        Log.d(TAG, "Private Default Constructor getting called");

        String userPath = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Set the reference to where the user is at of the current user
        DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(userPath);

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User mTempUser = dataSnapshot.getValue(User.class);

                // Set the member variable user
                user.setUser(mTempUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Accessing User for " +
                        FirebaseAuth.getInstance().getCurrentUser().getUid() + " failed.");
            }
        });
    }

}
