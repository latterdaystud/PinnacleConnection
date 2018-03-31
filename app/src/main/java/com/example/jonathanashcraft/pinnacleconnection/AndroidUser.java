package com.example.jonathanashcraft.pinnacleconnection;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Stores the current user from Google Firebase in a singleton pattern so that it is available
 * globally. Uses the Eager Singleton pattern.
 */
public class AndroidUser {
    private static final AndroidUser ourInstance = new AndroidUser();

    // To store the user
    private static final User user = new User();
    private static final String TAG = "AndroidUser";
    public static AndroidUser getInstance() {
        return ourInstance;
    }

    /**
     * Returns a user
     * @return an instance of user that was loaded from Google Firebase
     */
    public static User getUser() { return user; }

    /**
     * Returns The users first name.
     * @return A string regarding the first name of the current user
     */
    public static String getUserFirstName() { return user.getFirstName(); }

    /**
     * Returns the users last name.
     * @return A string regarding the first name of the  current user
     */
    public static String getUserLastName() { return user.getLastName(); }

    /**
     * Returns the apartment number of the user
     * @return A string representing the current users apartment number
     */
    public static String getUserApartmentNumber() { return user.getApartmentNumber(); }

    /**
     * Returns true or false depending if the user is a manager or not.
     * @return A boolean indicating in they are a manager or not
     */
    public static Boolean isUserManager() { return user.isManager(); }

    // TODO: Handle the case where the user logs out and switches to a new user
    // current bug and crash in LoginActivity

    /**
     * lol current bug here
     * @param mTempUser A user that you want the AndroidUser to be. Changes the instance of User
     *                  inside the Android user class.
     */
    public static void updateUser(User mTempUser) { user.setUser(mTempUser);}

    public static void reloadUser() { loadUser(); }

    // The private constructor (gets called once)
    private AndroidUser() {
        // Going to call the database reference to grab the user
        Log.d(TAG, "Private Default Constructor getting called");

        loadUser();
    }

    private static void loadUser() {

        String userPath;

        // If there is a current user (or an authenticated user)
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userPath = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            // If the user is not an authenticated user
            userPath = "";
        }

        // Check to see if there is a current user
        if (userPath != "") {
            DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(userPath);

            mUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User mTempUser = dataSnapshot.getValue(User.class);

                    // Set the member variable user
                    user.setUser(mTempUser);
                    Log.d(TAG, "User successfully and  completely loaded");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "Accessing User for failed.");
                }
            });
        } else {
            // There is not a current user
            user.setUser(new User());
        }
    }

}
