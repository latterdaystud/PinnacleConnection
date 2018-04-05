package com.example.jonathanashcraft.pinnacleconnection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Messaging activity, contains information for the activity as well as how the messages are stored
 * and displayed.
 * @author Jonathan Ashcraft
 */
public class MessagingActivity extends AppCompatActivity {

    // Adapter that handles displaying the information in the list view
    private CustomMessagingAdapter arrayAdapter;
    // The basic format of the texts
    private ListView listView;
    // Where to enter the text for the messageBox
    private EditText messageBox;

    // Hold all of the messages in an Array
    ArrayList<Text> MessagesFromJsonList;

    // To use for serializing and deserializing to JSON
    private Gson gson = new Gson();
    private String jsonMessages;


    private Text[] ts;

    /**
     * Load the saved messages from the phone onto the list view.
     * @param savedInstanceState instance of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String TAG = "onCreate";
        Log.d(TAG, "Called onCreate");
        super.onCreate(savedInstanceState);
        Intent intentExtras = getIntent();
        Bundle extraBundles = intentExtras.getBundleExtra("Contact Name");
        String name;

        setContentView(R.layout.activity_messaging);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        messageBox = findViewById(R.id.editText);

        // pull from firebase the messages that haven't been loaded yet
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message messageTemp = dataSnapshot.getValue(Message.class);

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

        // Load the existing messages from the sharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String jsonMessagesLoadedFromMyPreferences = prefs.getString("jsonMessages", "[ ]");

        Log.d(TAG,"The json messageBox is " + jsonMessagesLoadedFromMyPreferences);

        // Convert the JSON to an array of TextSents
        if(!jsonMessagesLoadedFromMyPreferences.isEmpty()) {
            ts = gson.fromJson(jsonMessagesLoadedFromMyPreferences, Text[].class);
        }

        // Slap that ^ array into an ArrayList
        MessagesFromJsonList = new ArrayList<>(Arrays.asList(ts));

        // Print some logs to make sure everything is working correctly
        Log.d(TAG, "The size of MessagesFromJsonList is " + MessagesFromJsonList.size());
        Log.d(TAG, "MessagesFromJsonList consists of: " + MessagesFromJsonList);

        // Create the arrayAdapter with the existing ArrayList with the messages preloaded
        arrayAdapter = new CustomMessagingAdapter(this, MessagesFromJsonList);
        listView = findViewById(R.id.messageListView);
        listView.setAdapter(arrayAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { onSendTemp(view); }
        });
        if (extraBundles != null && !extraBundles.isEmpty()) {
            name = extraBundles.getString("ID");
            arrayAdapter.add(name);
            this.setTitle(name);

        }
    }

    public void onSendTemp(View view) {
        // For log messages
        final String TAG = "onSendTemp";

        // See if there is actually text in there, if it's just space, return from the function
        if(Objects.equals(messageBox.getText().toString(), " ") || Objects.equals(messageBox.getText().toString(), ""))
            return;

        String messageToSend = messageBox.getText().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Make a database reference to Joseph Ridgleys messages area
        DatabaseReference messagesRef = database.getReference().child("Messages").child("MI1NAYHwywaWuwOyjXR7WOur23z2");

        Message message = new Message("MI1NAYHwywaWuwOyjXR7WOur23z2",
                FirebaseAuth.getInstance().getCurrentUser().getUid(), messageToSend);


        messageBox.setText("");

        // Set the value and monitor to see if it is successful or not
        messagesRef.setValue(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MessagingActivity.this, "Message Sent",
                                Toast.LENGTH_SHORT).show();

                        Log.d(TAG, "Sent the messageBox");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MessagingActivity.this, "Failed to Send",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Called when the send button is pressed. Adds the messageBox found in the text box into the
     * text messageBox list and list view.
     * @param view Receives the current state of the activity.
     */
    public void onSend(View view) {
        // For log messages
        String TAG = "onSend";

        // See if there is actually text in there, if it's just space, return from the function
        if(Objects.equals(messageBox.getText().toString(), " ") || Objects.equals(messageBox.getText().toString(), ""))
            return;


        // Add the string in the messageBox EditText to the preference manager
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(
                "Message", messageBox.getText().toString()).apply();

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

        // Reset the EditText where the user typed the messageBox
        messageBox.setText("");

        // Display a toast for user experience
        Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_SHORT / 3).show();
    }

    public final CustomMessagingAdapter getAdapter() {
        return arrayAdapter;
    }
    public final EditText getMessageBox() {
        return messageBox;
    }
    public void setEditText(String value) { messageBox.setText(value);}

    /**
     * The extends the base adapter to have a custom list view for the messages.
     * @author Jonathan Ashcraft
     */
    public class CustomMessagingAdapter extends BaseAdapter {

        ArrayList<Text> myList = new ArrayList();
        LayoutInflater inflater;
        Context context;


        /**
         * Non-default Constructor for the custom messaging adapter
         * @param context Context of the program
         * @param myList List to which we will add values.
         */
        public CustomMessagingAdapter(Context context, ArrayList<Text> myList) {
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

        /**
         * Includes the code that will determine how the messages are displayed.
         * @param position The index of the data in the list.
         * @param convertView Unused
         * @param viewGroup Unused
         * @return Returns the actual view or display of the individual messageBox.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            Text text;
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
            text = myList.get(position);
            String time = addition + text.getTime();
            textView2.setText(time);
            textview.setText(text.getText());
            return view;
        }

        /**
         * Saves the messageBox into the list to be displayed in the list view.
         * @param string The messageBox being saved
         */
        public void add(String string) {

            SimpleDateFormat df = new SimpleDateFormat("h:mma, EEE, MMM d");
            String date = df.format(Calendar.getInstance().getTime());
            TextSent textSent = new TextSent(string, date);
            myList.add(textSent);

        }


    }

    /**
     * Holds the basic information for a text messageBox
     */
    public class Text {
        private String text;
        private String time;

        public Text(String text, String time) {
            text = text;
            time = time;
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
            return "Message: " + text + " at " + time;
        }
    }

    public class TextSent extends Text {

        public TextSent(String textGiven, String timeGiven) {
            super(textGiven, timeGiven);
        }

    }

    public class TextReceived extends Text {

        public TextReceived(String textReceived, String timeReceived) {
            super(timeReceived, timeReceived);
        }
    }

}
