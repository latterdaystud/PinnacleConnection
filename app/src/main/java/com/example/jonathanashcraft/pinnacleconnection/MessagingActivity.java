package com.example.jonathanashcraft.pinnacleconnection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
    // Where to enter the text for the message
    private EditText message;

    // Hold all of the messages in an Array
    ArrayList<Message> messageFromJsonList;

    // To use for serializing and deserializing to JSON
    private Gson gson = new Gson();

    private Message[] ts;

    private String userId;
    private String name;


    // Firebase stuff.
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private ChildEventListener messagingListener;
    private DatabaseReference messagingRef;


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

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();
        messagingRef = databaseRef.child("Messaging").child(userId).child("Conversations");


        setContentView(R.layout.activity_messaging);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        message = findViewById(R.id.editText);

        // Load the existing messages from files on phone
        String jsonMessagesLoadedFromMyStorage = "";
        if (extraBundles != null && !extraBundles.isEmpty()) {
            name = extraBundles.getString("ID");
            // declare the path
            messagingRef = databaseRef.child("Messaging").child(userId).child("Conversations").child(name).child("Messages");
            FileInputStream inputStream;

            try {
                inputStream = openFileInput(name + userId);
                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(reader);
                StringBuilder sb = new StringBuilder();
                String tempString;
                while ((tempString = br.readLine()) != null) {
                    sb.append(tempString);
                }
                jsonMessagesLoadedFromMyStorage = sb.toString();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // empty first
        messageFromJsonList = new ArrayList<>();
        // Convert the JSON to an array of TextSents
        if(!jsonMessagesLoadedFromMyStorage.isEmpty()) {
            Log.d("Null?", "Was not NULL");
            ts = gson.fromJson(jsonMessagesLoadedFromMyStorage, Message[].class);
            messageFromJsonList = new ArrayList<>(Arrays.asList(ts));
        }
        Log.d("Null?", "Previous message should say not NULL");
        // Slap that ^ array into an ArrayList
        // Print some logs to make sure everything is working correctly
        Log.d(TAG, "The size of messageFromJsonList is " + messageFromJsonList.size());
        Log.d(TAG, "messageFromJsonList consists of: " + messageFromJsonList);

        // Create the arrayAdapter with the existing ArrayList with the messages preloaded
        arrayAdapter = new CustomMessagingAdapter(this, messageFromJsonList);
        setUpMessages();
        listView = (ListView) findViewById(R.id.messageListView);
        listView.setAdapter(arrayAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { onSend(view); }
        });
    }

    public void onSendTemp(View view) {
        // For log messages
        String TAG = "onSendTemp";

        // See if there is actually text in there, if it's just space, return from the function
        if(Objects.equals(message.getText().toString(), " ") || Objects.equals(message.getText().toString(), ""))
            return;

        String text = message.getText().toString();

    }

    /**
     * Called when the send button is pressed. Adds the message found in the text box into the
     * text message list and list view.
     * @param view Receives the current state of the activity.
     */
    public void onSend(View view) {
        // For log messages
        String TAG = "onSend";

        // See if there is actually text in there, if it's just space, return from the function
        if(Objects.equals(message.getText().toString(), " ") || Objects.equals(message.getText().toString(), ""))
            return;

        SimpleDateFormat df = new SimpleDateFormat("h:mma, EEE, MMM d");
        String date = df.format(Calendar.getInstance().getTime());
        String text = message.getText().toString();
        Message messageSent = new Message(name, userId, text, date);
        // Add o display.
        arrayAdapter.add(messageSent);

        String jsonMessages = gson.toJson(messageFromJsonList);

        // Add to file on phone.
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(name + userId, Context.MODE_PRIVATE);
            outputStream.write(jsonMessages.getBytes());
            outputStream.close();
        } catch (Exception e) {
            File file = new File(this.getFilesDir(), name);
        }

        //Up to firebase.
        DatabaseReference messagesRefOut =  database.getReference().child("Messaging").child(name).child("Conversations").child(userId).child("Messages");
        messagesRefOut.push().setValue(messageSent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Display a toast for user experience
                        Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_SHORT / 3).show();
                    }
                });

        // Notify the arrayAdapter that changes have been made to the arrayList
        arrayAdapter.notifyDataSetChanged();

        // Reset the EditText where the user typed the message
        message.setText("");
    }

    public final CustomMessagingAdapter getAdapter() {
        return arrayAdapter;
    }
    public final EditText getMessage() {
        return message;
    }
    public void setEditText(String value) { message.setText(value);}

    /**
     * The extends the base adapter to have a custom list view for the messages.
     * @author Jonathan Ashcraft
     */
    public class CustomMessagingAdapter extends BaseAdapter {

        ArrayList<Message> myList = new ArrayList();
        LayoutInflater inflater;
        Context context;


        /**
         * Non-default Constructor for the custom messaging adapter
         * @param context Context of the program
         * @param myList List to which we will add values.
         */
        public CustomMessagingAdapter(Context context, ArrayList<Message> myList) {
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
         * @return Returns the actual view or display of the individual message.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            Message message;
            message = myList.get(position);
            String addition;
            View view;
            TextView textview;
            TextView textView2;
            // Display differently based on if from user or to user.
            if (Objects.equals(message.getFrom(), userId)) {
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

            String time = addition + message.getTime();
            textView2.setText(time);
            textview.setText(message.getBody());
            return view;
        }

        /**
         * Saves the message into the list to be displayed in the list view.
         * @param message The message being saved
         */
        public void add(Message message) {
            myList.add(message);
            arrayAdapter.notifyDataSetChanged();
        }


    }

    /**
     * Set up the firebase messages on the list.
     */
    private void setUpMessages() {
        Log.d("MESSAGING DOWNLOAD", "The size of the MessagesFomJsonList is " + messageFromJsonList.size());
        messagingListener = new ChildEventListener() {
            // Add the child added to a tempAnnouncement

            /**
             * Sets up what happens when a value is received from firebase.
             * @param dataSnapshot The link to the data.
             * @param s not used.
             */
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("MESSAGING DOWNLOAD", "The size of the MessagesFomJsonList is ");
                Message messageFound = dataSnapshot.getValue(Message.class);

                // Once loaded, remove from firebase.
                databaseRef.child("Messaging").child(userId).child("Conversations").child(name).child("Messages").child(dataSnapshot.getKey()).removeValue();
                // Put message on display.
                arrayAdapter.add(messageFound);
                arrayAdapter.notifyDataSetChanged();
                // Make the current arrayList to a JSON and put it in the sharedPreferences
                String jsonMessages = gson.toJson(messageFromJsonList);
                // Store data in phone.
                FileOutputStream outputStream;
                 try {
                     outputStream = openFileOutput(name + userId, Context.MODE_PRIVATE);
                     outputStream.write(jsonMessages.getBytes());
                     outputStream.close();
                 } catch (Exception e) {
                     File file = new File(getApplicationContext().getFilesDir(), name);
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
        messagingRef.keepSynced(true);
    }

}
