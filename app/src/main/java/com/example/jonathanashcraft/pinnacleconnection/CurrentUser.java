package com.example.jonathanashcraft.pinnacleconnection;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Stores the current user from Google Firebase in a singleton pattern so that it is available
 * globally. Uses the Eager Singleton pattern.
 */
public class CurrentUser {
    private static final CurrentUser ourInstance = new CurrentUser();

    // To know whether it is loaded or not
    public static Boolean userLoaded;

    // To store the user
    private static final User user = new User();
    private static final String TAG = "CurrentUser";
    public static CurrentUser getInstance() {
        return ourInstance;
    }

    /**
     * Sees if the user is loaded or not
     * @return if the user is loaded or not
     */
    public static Boolean isCurrentUserLoaded() {
        return userLoaded;
    }

    /**
     * Returns a user
     * @return an instance of user that was loaded from Google Firebase
     */
    public static User getCurrentUser() { return user; }

    /**
     * Returns The users first name.
     * @return A string regarding the first name of the current user
     */
    public static String getFirstName() { return user.getFirstName(); }

    /**
     * Returns the users last name.
     * @return A string regarding the first name of the  current user
     */
    public static String getLastName() { return user.getLastName(); }

    /**
     * Returns the apartment number of the user
     * @return A string representing the current users apartment number
     */
    public static String getApartmentNumber() { return user.getApartmentNumber(); }

    /**
     * Returns true or false depending if the user is a manager or not.
     * @return A boolean indicating in they are a manager or not
     */
    public static Boolean isManager() { return user.isManager(); }

    // TODO: Handle the case where the user logs out and switches to a new user
    // current bug and crash in LoginActivity

    /**
     * lol current bug here
     * @param mTempUser A user that you want the CurrentUser to be. Changes the instance of User
     *                  inside the Android user class.
     */
    public static void updateUser(User mTempUser) { user.setUser(mTempUser);}

    public static void reloadUser() { loadUser(); }

    // The private constructor (gets called once)
    private CurrentUser() {
        // Going to call the database reference to grab the user
        Log.d(TAG, "Private Default Constructor getting called");
        userLoaded = false;
        loadUser();
    }

    private static void loadUser() {

        String userPath;

        // If there is a current user (or an authenticated user)
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userPath = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.d(TAG, "Current user UID is " + userPath);
        } else {
            // If the user is not an authenticated user or getCurrentUser equals null
            Log.d(TAG, "The user is not an authenicated user");
            userPath = "";
        }

        // Check to see if there is a current user
        if (userPath != "") {
            Log.d(TAG, "Loading FirebaseDatabase Reference");
            DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(userPath);

            Log.d(TAG, "Adding on valueEventListener");
            mUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User mTempUser = dataSnapshot.getValue(User.class);

                    // Log some messages to see if we are loading the correct information
                    Log.d(TAG, "Loaded user name is " + mTempUser.getFirstName() + " "
                            + mTempUser.getLastName());
                    Log.d(TAG, "Is the user a manager? " + mTempUser.isManager());

                    // Set the member variable user
                    user.setUser(mTempUser);

                    Log.d(TAG, "User successfully and  completely loaded");

                    userLoaded = true;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "Accessing User for failed.");
                }
            });
        } else {
            // There is not a current user
            Log.d(TAG, "There is not a current user");
            user.setUser(new User());
        }

        Log.d(TAG, "Leaving loadUser()");
        Log.d(TAG, "Hopefully this runs before the loaded user thing");
    }
}
