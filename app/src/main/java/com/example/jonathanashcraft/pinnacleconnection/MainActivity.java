package com.example.jonathanashcraft.pinnacleconnection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Testing our git repository - Joe
        System.out.println("Hello World");
        System.out.println("Joseph is our master");
        System.out.println("Anthony is Joseph's master");
        System.out.print("puh");
    }
    public void buttonPressed(View view) {
        Intent intent = new Intent(this, MessagingActivity.class);
        startActivity(intent);
    }
}
