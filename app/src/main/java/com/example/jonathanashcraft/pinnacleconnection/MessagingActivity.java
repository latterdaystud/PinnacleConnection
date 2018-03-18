package com.example.jonathanashcraft.pinnacleconnection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MessagingActivity extends AppCompatActivity {

    // Adapter that handles displaying the information in the list view
    private CustomMessagingAdapter arrayAdapter;
    // The basic format of the texts
    private ListView listView;
    // Where to enter the text for the message
    private EditText message;

    // Hold all of the messages in an Array
    ArrayList<TextSent> MessagesFromJsonList;

    // To use for serializing and deserializing to JSON
    private Gson gson = new Gson();
    private String jsonMessages;

    private TextSent[] ts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final String TAG = "onCreate";

        Log.d(TAG, "Called onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        message = findViewById(R.id.editText);

        // Load the existing messages from the sharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String jsonMessagesLoadedFromMyPreferences = prefs.getString("jsonMessages", "[ ]");

        Log.d(TAG,"The json message is " + jsonMessagesLoadedFromMyPreferences);

        // Convert the JSON to an array of TextSents
        if(!jsonMessagesLoadedFromMyPreferences.isEmpty()) {
            ts = gson.fromJson(jsonMessagesLoadedFromMyPreferences, TextSent[].class);
        }
        // Slap that ^ array into an ArrayList
        MessagesFromJsonList = new ArrayList<>(Arrays.asList(ts));

        // Print some logs to make sure everything is working correctly
        Log.d(TAG, "The size of MessagesFromJsonList is " + MessagesFromJsonList.size());
        Log.d(TAG, "MessagesFromJsonList consists of: " + MessagesFromJsonList);

        // Create the arrayAdapter with the existing ArrayList with the messages preloaded
        arrayAdapter = new CustomMessagingAdapter(this, MessagesFromJsonList);
        listView = (ListView) findViewById(R.id.messageListView);
        listView.setAdapter(arrayAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { onSend(view); }
        });
    }
    public void onSend(View view) {
        // For log messages
        String TAG = "onSend";

        // See if there is actually text in there, if it's just space, return from the function
        if(Objects.equals(message.getText().toString(), " ") || Objects.equals(message.getText().toString(), ""))
            return;

        // Add the string in the message EditText to the preference manager
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(
                "Message", message.getText().toString()).apply();

        // Get the string we just saved and add it to the arrayAdapter
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String myStrValue = prefs.getString("Message", "[ ]");
        arrayAdapter.add(myStrValue);

        Log.d(TAG, "The size of the MessagesFromJsonList is " + MessagesFromJsonList.size());

        // Make the current arrayList to a JSON and put it in the sharedPreferences
        jsonMessages = gson.toJson(MessagesFromJsonList);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(
                "jsonMessages", jsonMessages).apply();

        Log.d(TAG, "jsonMessages: " + jsonMessages);

        // Notify the arrayAdapter that changes have been made to the arrayList
        arrayAdapter.notifyDataSetChanged();

        // Reset the EditText where the user typed the message
        message.setText("");

        // Display a toast for user experience
        Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_SHORT / 3).show();
    }

    public final CustomMessagingAdapter getAdapter() {
        return arrayAdapter;
    }

    public final EditText getMessage() {
        return message;
    }
    public void setEditText(String value) { message.setText(value);}

    public class CustomMessagingAdapter extends BaseAdapter {

        ArrayList<TextSent> myList = new ArrayList();
        LayoutInflater inflater;
        Context context;



        public CustomMessagingAdapter(Context context, ArrayList<TextSent> myList) {
            this.myList = myList;
            this.context = context;
            inflater = LayoutInflater.from(this.context);

        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public Object getItem(int i) {
            return myList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            TextSent textSent;
            String addition;
            View view;
            TextView textview;
            TextView textView2;
            if (position % 2 == 1) {
                view = getLayoutInflater().inflate(R.layout.message_right, null);
                textview = view.findViewById(R.id.msgr);
                textView2 = view.findViewById(R.id.TextView2);
                addition = "Sent: ";
            }
            else {
                view = getLayoutInflater().inflate(R.layout.message_left, null);
                textview = view.findViewById(R.id.msgr2);
                textView2 = view.findViewById(R.id.TextView2);
                addition = "Received: ";
            }
            textSent = myList.get(position);
            String time = addition + textSent.getTime();
            textView2.setText(time);
            textview.setText(textSent.getText());
            return view;
        }

        public void add(String string) {

            SimpleDateFormat df = new SimpleDateFormat("h:mma, EEE, MMM d");
            String date = df.format(Calendar.getInstance().getTime());
            TextSent textSent = new TextSent(string, date);
            myList.add(textSent);

        }
    }

    // A class which holds information pertaining to a text
    public class TextSent {
        private String text;
        private String time;
        public TextSent(String textGiven, String timeGiven) {
            text = textGiven;
            time = timeGiven;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String toString() {
            return "Text: " + text + " at " + time;
        }
    }

}
