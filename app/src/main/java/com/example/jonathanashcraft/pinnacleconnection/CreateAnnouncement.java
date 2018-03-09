package com.example.jonathanashcraft.pinnacleconnection;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class CreateAnnouncement extends AppCompatActivity {

    private EditText title_of_announcement;
    private EditText time_of_announcement;
    private EditText date_of_announcement;
    private EditText description_of_announcement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_announcement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        // Create a temp announcement to hold all the information that was in the
        Announcement tempAnnouncement = new Announcement(
                time_of_announcement.getText().toString(),
                time_of_announcement.getText().toString(),
                date_of_announcement.getText().toString(),
                description_of_announcement.getText().toString()
        );
    }

}
