package com.example.jonathanashcraft.pinnacleconnection;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;

/**
 * This is to ensure firebase is working correctly and it returns a valid instance
 * Created by Joseph on 4/7/2018.
 */
public class FirebaseTest {
    @Test
    public void test1() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference managersPassword = database.getReference().child("ManagersPassword");

        managersPassword.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String holder = dataSnapshot.getValue(String.class);

                assert(holder == "12345");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
