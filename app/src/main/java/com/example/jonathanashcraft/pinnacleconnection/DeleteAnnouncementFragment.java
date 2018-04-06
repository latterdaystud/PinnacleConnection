package com.example.jonathanashcraft.pinnacleconnection;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

/**
 * Created by dasil on 3/24/2018.
 */

public class DeleteAnnouncementFragment extends DialogFragment {

    Announcement announcement;

    public void setAnnouncement(Announcement announcement) {
        this.announcement = announcement;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_delete_announcement)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // Get the database and the reference
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference AnnouncementsRef = database.getReference("Announcements");

                        AnnouncementsRef.child(announcement.getID()).removeValue();

                        Log.d("DeleteAnnouncement", "Removed announcement from database");

                        // Make a toast for user feedback
                        Toast.makeText(getActivity(), "Announcement Deleted",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
