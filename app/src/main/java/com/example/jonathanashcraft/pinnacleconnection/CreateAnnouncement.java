package com.example.jonathanashcraft.pinnacleconnection;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class CreateAnnouncement extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
TimePickerDialog.OnTimeSetListener {

    private EditText title_of_announcement;
    private EditText time_of_announcement;
    private EditText date_of_announcement;
    private EditText description_of_announcement;

    FirebaseUser currentUser;

    // TODO: Create dialog fragments for the user and time input so that the user can have visual feedback on what to choose.

    User TempUser;
    DatePickerDialog datePicker5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String TAG = "onCreate";
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


        // Create a dialog box
        Calendar cal = Calendar.getInstance();

        datePicker5000= new DatePickerDialog(CreateAnnouncement.this, CreateAnnouncement.this,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));


        // The listener to see if date_of_announcement gets clicked
        date_of_announcement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick on date_of_announcement called");
                datePicker5000.show();
            }
        });

        final TimePickerDialog timePicker5000 = new TimePickerDialog(CreateAnnouncement.this, CreateAnnouncement.this,
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);

        time_of_announcement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker5000.show();
            }
        });
//        // Variable to hold the information that we get back from the database.
//        TempUser = new User();
//
//        Log.d(TAG, "**************************************************************************");
//        /*
//        Cannot get this running right now, For some reason the database isn't returning the user
//        class at all.
//
//        */
//        // Get a reference and the name of the first name and last name of the user
//        DatabaseReference mFirstNameRef = FirebaseDatabase.getInstance().getReference("Users")
//                .child(currentUser.getUid());
//
//        Log.d(TAG, "Currentuser is " + currentUser.getUid());
//
//        // Database access
//        mFirstNameRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                // The user class isn't even getting the information from the database
//                User user = dataSnapshot.getValue(User.class);
//
//                Log.d(TAG, "User first name = " + user.getFirstName());
//
//                TempUser.setUser(user);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "The database access was cancelled" + databaseError.toException());
//            }
//        });

    }

    public void onSubmitAnnouncement(View view) {
        String TAG = "onSubmitAnnouncement";

        Log.d(TAG, "The users name is " + AndroidUser.getUserFirstName());

        final Announcement tempAnnouncement = new Announcement(
                title_of_announcement.getText().toString(),
                description_of_announcement.getText().toString(),
                date_of_announcement.getText().toString(),
                time_of_announcement.getText().toString(),
                (AndroidUser.getUserFirstName() + " " + AndroidUser.getUserLastName())
        );

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



    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Log.d("onDateSet", "onDateSet got called son");
        String date = i1 + "/" + i2 + "/" + i;

        // Set the text of the date
        date_of_announcement.setText(date);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        Log.d("onTimeSet", "onTimeSet got called son");

        // Logic for converting 24 hour time to 12 hour time
        String time;

        if(i > 12) {
            if (i != 12) {
                i -= 10;
                time = i + ":" + i1 + " PM";
            }
        }

        time_of_announcement.setText(time);
    }
}
