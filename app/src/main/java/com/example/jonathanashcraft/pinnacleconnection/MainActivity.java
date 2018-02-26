package com.example.jonathanashcraft.pinnacleconnection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Testing our git repository - Joe
        System.out.println("Hello World");
        System.out.println("Joseph is our master");
        System.out.println("Anthony is Joseph's master");
        System.out.println("Anthony is Joseph's");
    }
}
