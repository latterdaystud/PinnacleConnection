package com.example.jonathanashcraft.pinnacleconnection;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MessagingActivity extends AppCompatActivity {

    private ArrayAdapter<String> arrayAdapter;
    private ListView listView;
    private EditText message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        message = findViewById(R.id.editText);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listView = (ListView) findViewById(R.id.messageListView);
        listView.setAdapter(arrayAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSend(view);
                /*
                Snackbar.make(view, "Message Sent", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */
                Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void onSend(View view) {

        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(
                "Message", message.getText().toString()).apply();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String myStrValue = prefs.getString("Message", "myStringToSave");
        arrayAdapter.add(myStrValue);
        message.setText("");
    }

    public final ArrayAdapter<String> getAdapter() {
        return arrayAdapter;
    }

    public final EditText getMessage() {
        return message;
    }
    public void setEditText(String value) { message.setText(value);}

}
