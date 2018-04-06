package com.example.jonathanashcraft.pinnacleconnection;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateAnnouncement extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
TimePickerDialog.OnTimeSetListener {

    private EditText title_of_announcement;
    private EditText time_of_announcement;
    private EditText date_of_announcement;
    private EditText description_of_announcement;
    private Announcement editAnnouncement;
    private static int RESULT_LOAD_IMG = 1;
    private String imgDecodableString;
    private ImageView imageButton;
    private Uri selectedImage;
    private Bitmap image;
    private String pathToImage;
    private byte[] imageAsBytes;

    FirebaseUser currentUser;

    // TODO: Create dialog fragments for the user and time input so that the user can have visual feedback on what to choose.

    User TempUser;
    DatePickerDialog datePicker5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String TAG = "onCreate";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_announcement);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //imageButton = findViewById(R.id.AnnouncementImage);
        //image = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_menu_gallery);

        // Get the current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Log.d(TAG, "currentUser is equal to null");
        } else {
            Log.d(TAG, "currentUser equals something!!");
        }

        FloatingActionButton fab = findViewById(R.id.fab);
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

        if (getIntent().hasExtra("announcement"))
        {
            String announcement = getIntent().getExtras().getString("announcement");

            editAnnouncement = new Gson().fromJson(announcement, Announcement.class);

            title_of_announcement.setText(editAnnouncement.getTitle());
            time_of_announcement.setText(editAnnouncement.getTimeOfAnnouncement());
            date_of_announcement.setText(editAnnouncement.getDate());
            description_of_announcement.setText(editAnnouncement.getBody());
           // imageButton.setImageBitmap(new Gson().fromJson(editAnnouncement.getJsonImage(), Bitmap.class));
        }


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

    public void openGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Testing for exception", "Before the try catch block");
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                selectedImage = data.getData();

                //FirebaseStorage storage = Firebase
               /* String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first message_right
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                //Set the image
                imageButton.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

                */

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
            e.printStackTrace();
        }

    }

    public void onSubmitAnnouncement(View view) {
        String TAG = "onSubmitAnnouncement";

        Log.d(TAG, "The users name is " + CurrentUser.getFirstName());

        Announcement tempAnnouncement;

        //BitmapDrawable drawable = (BitmapDrawable) imageButton.getDrawable();

        /*
        if (drawable != null) {
            image = drawable.getBitmap();
        }
        else {
            image = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_menu_gallery);
        }

        */

        if (getIntent().hasExtra("announcement")) {
            editAnnouncement.setTitle(title_of_announcement.getText().toString());
            editAnnouncement.setTimeOfAnnouncement(time_of_announcement.getText().toString());
            editAnnouncement.setDate(date_of_announcement.getText().toString());
            editAnnouncement.setBody(description_of_announcement.getText().toString());
           // editAnnouncement.setJsonImage(jsonImage);
            tempAnnouncement = editAnnouncement;
        }
        else {
            tempAnnouncement = new Announcement(
                    title_of_announcement.getText().toString(),
                    description_of_announcement.getText().toString(),
                    date_of_announcement.getText().toString(),
                    time_of_announcement.getText().toString(),
                    (CurrentUser.getFirstName() + " " + CurrentUser.getLastName()),
                    pathToImage
            );
        }

        tempAnnouncement.setIndexInArray(getIntent().getExtras().getInt("index"));

        // Get the database and the reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference AnnouncementsRef = database.getReference("Announcements");


        AnnouncementsRef.child(tempAnnouncement.getID()).setValue(tempAnnouncement);

        Log.d(TAG, "Added announcement to database");

        // Make a toast for user feedback
        Toast.makeText(CreateAnnouncement.this, "Announcement Created",
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

        // TODO: Implent logic for 24 hour to 12 hour conversion
        String time = i + ":" + i1;

        time_of_announcement.setText(time);
    }
}
