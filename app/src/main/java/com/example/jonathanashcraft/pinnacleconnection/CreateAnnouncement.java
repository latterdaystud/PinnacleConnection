package com.example.jonathanashcraft.pinnacleconnection;

import android.*;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.File;

import pub.devrel.easypermissions.EasyPermissions;

public class CreateAnnouncement extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
TimePickerDialog.OnTimeSetListener {

    private EditText title_of_announcement;
    private EditText time_of_announcement;
    private EditText date_of_announcement;
    private EditText description_of_announcement;
    private Announcement editAnnouncement;
    private static int RESULT_LOAD_IMG = 1;
    private String pathToImage;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Uri selectedImage;
    private Uri fileToUpload;
    private Bitmap image;
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

        fileToUpload = null;

        imageView = findViewById(R.id.AnnouncementImage);
        image = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_menu_gallery);
        imageView.setVisibility(View.GONE);

        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);

        // Get the current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Log.d(TAG, "currentUser is equal to null");
        } else {
            Log.d(TAG, "currentUser equals something!!");
        }

        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        // Load all of the objects in the xml into variables
        title_of_announcement = findViewById(R.id.announcement_title);
        description_of_announcement = findViewById(R.id.announcement_description);

        if (getIntent().hasExtra("announcement"))
        {
            String announcement = getIntent().getExtras().getString("announcement");

            editAnnouncement = new Gson().fromJson(announcement, Announcement.class);

            title_of_announcement.setText(editAnnouncement.getTitle());
            description_of_announcement.setText(editAnnouncement.getBody());

            if (editAnnouncement.getPathToImage() != "no_path") {

                this.pathToImage = editAnnouncement.getPathToImage();

                //Set image in
                imageView.setImageBitmap( BitmapFactory
                        .decodeFile(this.pathToImage) );

                imageView.setVisibility(View.VISIBLE);

            }
        }



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

        // Before we do anything, let's make sure we have permissions to do it
        if(!EasyPermissions.hasPermissions(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, "We need to access your photos",
                    1, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if(isExternalStorageReadable()) {
            // Create intent to Open Image applications like Gallery, Google Photos
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Testing for exception", "Before the try catch block");
        try {
            // When an Image is picked
            if (resultCode == RESULT_OK) {

                if (requestCode == 1) {
                    // currImageURI is the global variable I'm using to hold the content:// URI of the image
                    selectedImage = data.getData();
                }
            }

                pathToImage = getRealPathFromURI(selectedImage);


                //Set the image
                imageView.setImageBitmap(BitmapFactory
                        .decodeFile(pathToImage));

                imageView.setVisibility(View.VISIBLE);

            //Get references to firebase storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            fileToUpload = Uri.fromFile(new File(pathToImage));

            // TODO: When uploading images it displays photo failed to upload and photo uploaded at the same time
            StorageReference imageRef = storageRef.child("images/"+ fileToUpload.getLastPathSegment());

            UploadTask uploadTask = imageRef.putFile(fileToUpload);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // aw it didn't work
                    Toast.makeText(CreateAnnouncement.this, "Photo Failed to Upload",
                            Toast.LENGTH_SHORT).show();

                    Log.d("onFailure", "The uploadTask failed");
                    e.printStackTrace();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(CreateAnnouncement.this, "Photo Uploaded",
                            Toast.LENGTH_SHORT).show();
                }
            });




        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
            e.printStackTrace();
        }

    }

    public String getRealPathFromURI(Uri contentUri) {

        // can post image
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    public void onSubmitAnnouncement(View view) {
        String TAG = "onSubmitAnnouncement";

        Log.d(TAG, "The users name is " + CurrentUser.getFirstName());

        Announcement tempAnnouncement;

        //BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();

        /*
        if (drawable != null) {
            image = drawable.getBitmap();
        }
        else {
            image = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_menu_gallery);
        }

        */


        if (getIntent().hasExtra("announcement") && fileToUpload != null) {
            editAnnouncement.setTitle(title_of_announcement.getText().toString());
            editAnnouncement.setBody(description_of_announcement.getText().toString());
            editAnnouncement.setPathToImage(fileToUpload.getLastPathSegment());
            tempAnnouncement = editAnnouncement;
            tempAnnouncement.setIndexInArray(getIntent().getExtras().getInt("index"));

        }
        if (getIntent().hasExtra("announcement") && fileToUpload == null) {
            editAnnouncement.setTitle(title_of_announcement.getText().toString());
            editAnnouncement.setBody(description_of_announcement.getText().toString());
            editAnnouncement.setPathToImage("no_path");
            tempAnnouncement = editAnnouncement;
            tempAnnouncement.setIndexInArray(getIntent().getExtras().getInt("index"));

        }
        else if (fileToUpload != null){
            tempAnnouncement = new Announcement(
                    title_of_announcement.getText().toString(),
                    description_of_announcement.getText().toString(),
                    (CurrentUser.getFirstName() + " " + CurrentUser.getLastName()),
                    fileToUpload.getLastPathSegment()
            );
        }
        else {
            tempAnnouncement = new Announcement(
                    title_of_announcement.getText().toString(),
                    description_of_announcement.getText().toString(),
                    (CurrentUser.getFirstName() + " " + CurrentUser.getLastName()),
                    "no_path"
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

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
