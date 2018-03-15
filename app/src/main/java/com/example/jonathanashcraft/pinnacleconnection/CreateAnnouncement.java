package com.example.jonathanashcraft.pinnacleconnection;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateAnnouncement extends AppCompatActivity {

    private EditText title_of_announcement;
    private EditText time_of_announcement;
    private EditText date_of_announcement;
    private EditText description_of_announcement;

    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String TAG = "onCreate";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_announcement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Log.d(TAG, "currentUser is equal to null");
        } else {
            Log.d(TAG, "currentUser equals something!!");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Load all of the objects in the xml into variables
        title_of_announcement = findViewById(R.id.announcement_title);
        time_of_announcement = findViewById(R.id.announcement_time);
        date_of_announcement = findViewById(R.id.announcement_date);
        description_of_announcement = findViewById(R.id.announcement_description);
    }

    public void onSubmitAnnouncement(View view) {
        String TAG = "onSubmitAnnouncement";


        DatabaseReference firstNameRef = FirebaseDatabase.getInstance().getReference(currentUser.getUid());

        firstNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User tempUser = dataSnapshot.getValue(User.class);
                // get this temp user out of the profile and
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Announcement tempAnnouncement = new Announcement(
                title_of_announcement.getText().toString(),
                description_of_announcement.getText().toString(),
                date_of_announcement.getText().toString(),
                time_of_announcement.getText().toString(),
                "Marissa"
        );

        // Push to database

        // Get the database and the reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference AnnouncementsRef = database.getReference("Announcements");


        AnnouncementsRef.child(tempAnnouncement.getID()).setValue(tempAnnouncement);


        Log.d(TAG, "Added announcement to database");

        // Make a toast for user feedback
        Toast.makeText(CreateAnnouncement.this, "Created Announcement",
                Toast.LENGTH_SHORT).show();

        finish();
    }

}
