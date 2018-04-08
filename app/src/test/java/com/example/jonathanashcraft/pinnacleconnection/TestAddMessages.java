package com.example.jonathanashcraft.pinnacleconnection;

import android.content.Context;
import android.test.mock.MockContext;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Jonathan Ashcraft on 4/7/2018.
 */

public class TestAddMessages {
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private ChildEventListener messagingListener;
    private DatabaseReference messagingRef;
    String testID;
    String testToID;
    Message messageSent;
    @Test
    public void testFirebaseMessages() {

        // All this code does not work due to errors with android interface and context.
        /*
        //MockContext context = new MockContext();
        Context context = Mockito.mock(Context.class);
        //MessagingActivity mockMessage = Mockito.mock(MessagingActivity.class);
        FirebaseApp.initializeApp(context);
        testID = "Test From";
        testToID = "Test To";
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();
        messagingRef = databaseRef.child("Messaging").child(testID).child("Conversations").child(testToID).child("Messages");
        messageSent = new Message(testToID, testID, "Testing Testing", "4/7/2018");
        DatabaseReference messagesRefOut =  database.getReference().child("Messaging").child(testToID).child("Conversations").child(testID).child("Messages");
        messagesRefOut.push().setValue(messageSent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Delete just created announcement
                        findMessage();
                    }
                });
                */
    }


    /*
    This function is meant to find the just added value and delete it!

    private void findMessage() {
        messagingListener = new ChildEventListener() {
            // Add the child added to a tempAnnouncement

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Message messageFound = dataSnapshot.getValue(Message.class);
                // Once loaded, remove from firebase.
                if(messageFound == messageSent) {
                    Log.d("MESSAGING DOWNLOAD", "Message has been found ");
                    databaseRef.child("Messaging").child(testID).child("Conversations").child(testToID).child("Messages").child(dataSnapshot.getKey()).removeValue();
                    Log.d("Message deleted", "Message should be deleted");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // What is going to happen if the child changes
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // What are we going to do if the child is removed
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // What happens if we move the child
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // What if we cancel... what....
            }
        };
        messagingRef.addChildEventListener(messagingListener);
    }
    */

}
