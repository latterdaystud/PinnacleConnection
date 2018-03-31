package com.example.jonathanashcraft.pinnacleconnection;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

public class ContactList extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private ListView conversationListView;
    private ArrayList<User> userList;
    private CustomContactAdapter contactAdapter;

    ArrayList<User> ContactsFromJsonList;
    ArrayList<User> ContactsFromJsonMatch;
    private Gson gson = new Gson();
    private User[] users;

    private ListView contactListView;
    private CustomContactListAdapter contactListAdapter;
    private EditText contactSearch;
    private Dialog myDialog;
    private ImageButton searchButton;
    private FloatingActionButton contactFab;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private ChildEventListener contactListener;
    private DatabaseReference contactRef;


    /**
     * Creates an instance of the Contact list activity which has recorded conversations and
     * a link to the contact list.
     * @author Jonathan Ashcraft
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        //setting up the dialog
        myDialog = new Dialog(this);

        myDialog.setContentView(R.layout.contact_list);
        myDialog.setCancelable(false);


        // initializing some values for suture referrence.
        userList = new ArrayList<>();
        conversationListView = findViewById(R.id.contactList);
        contactAdapter = new CustomContactAdapter(this, userList);
        conversationListView.setAdapter(contactAdapter);
        contactFab = findViewById(R.id.contactFab);

        // Temp values for display purposes.
        User user = new User(AndroidUser.getUser());
        Log.d("Show Token", user.getDeviceToken());
        User user2 = new User("George", "Romania", "0", true, "123qwe456asd");
        User user3 = new User();
        contactAdapter.add(user);
        contactAdapter.add(user2);
        contactAdapter.add(user3);

        //Firebase connection
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();
        contactRef = database.getReference().child("Users");

        // shared preferences to be shared to the phone.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String jsonContactsLoadedFromMyPreferences = prefs.getString("Contacts", "[ ]");

        Log.d("Contacts","The json Contact is " + jsonContactsLoadedFromMyPreferences);

        // Convert the JSON to an array of TextSents
        if(!jsonContactsLoadedFromMyPreferences.isEmpty()) {
            users = gson.fromJson(jsonContactsLoadedFromMyPreferences, User[].class);
        }
        // Slap that ^ array into an ArrayList
        ContactsFromJsonList = new ArrayList<>(Arrays.asList(users));
        ContactsFromJsonMatch = new ArrayList<>();

        // Set up the clickable list of conversaations.
        conversationListView.setClickable(true);
        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ContactList.this, MessagingActivity.class);
                TextView name = view.findViewById(R.id.contactName);
                // Pass the data to the messaging activity.
                String passedInfo = contactAdapter.getItem(i).getDeviceToken();
                Bundle bundle = new Bundle();
                bundle.putString("ID", passedInfo);
                Log.d("Given ID", passedInfo);
                intent.putExtra("Contact Name", bundle);
                startActivity(intent);
            }
        });

        // Set up the dialog boxes values.
        contactListView = myDialog.findViewById(R.id.listOfContacts);
        contactListAdapter = new CustomContactListAdapter(myDialog.getContext(), ContactsFromJsonMatch);
        contactListView.setAdapter(contactListAdapter);
        contactSearch = myDialog.findViewById(R.id.searchName);
        searchButton = myDialog.findViewById(R.id.searchSelect);

        // Read in any missing items from firebase.
        setUpContacts();

        // The fab opens the contact list dialog.
        contactFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactListAdapter.clear();
                myDialog.setTitle("Search Contacts");
                myDialog.show();

                // For the back arrow, cancel the
                myDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            // Clear the input values.
                            dialog.cancel();
                            Log.i("Close dialog","Dialog view should be closed");
                            return true;
                        }
                        return false;
                    }
                });
                // Change the list based on the entered name.
                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Clear first
                        contactListAdapter.clear();
                        contactListAdapter.notifyDataSetChanged();
                        String name = contactSearch.getText().toString();
                        searchForName(name);
                    }
                });
                // Make the list of contacts clickable.
                contactListView.setClickable(true);
                contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(ContactList.this, MessagingActivity.class);
                        TextView name = view.findViewById(R.id.ContactNameView);
                        // Pass the data to the messaging activity.
                        String passedInfo = contactListAdapter.getItem(i).getDeviceToken();
                        Bundle bundle = new Bundle();
                        bundle.putString("ID", passedInfo);
                        Log.d("Given ID", passedInfo);
                        intent.putExtra("Contact Name", bundle);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    /**
     * Defines the adapter for the list of contacts
     * @author Jonathan Ashcraft
     */
    public class CustomContactListAdapter extends BaseAdapter {

        ArrayList<User> myList = new ArrayList();
        LayoutInflater inflater;
        Context context;

        /**
         * Non-default Constructor for the custom contact adapter
         * @param context Context of the program
         * @param myList List to which we will add values.
         */
        public CustomContactListAdapter(Context context, ArrayList<User> myList) {
            this.myList = myList;
            this.context = context;
            inflater = LayoutInflater.from(this.context);
        }

        public void clear() { myList.clear(); }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public User getItem(int i) {
            return myList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        /**
         * Includes the code that will determine how the contacts are displayed.
         * @param position The index of the data in the list.
         * @param convertView Unused
         * @param viewGroup Unused
         * @return Returns the actual view or display of the individual contacts.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            User tempUser = getItem(position);
            String name = tempUser.getFirstName() + " " + tempUser.getLastName();
            View view;
            view = getLayoutInflater().inflate(R.layout.contact_name, null);
            TextView nameView = view.findViewById(R.id.ContactNameView);
            nameView.setText(name);
            return view;
        }

        /**
         * Saves the contacts into the list to be displayed in the list view.
         * @param user The User being saved
         */
        public void add(User user) {

            myList.add(user);

        }
    }

    /**
     * The custom adapter for the list of conversations.
     */
    public class CustomContactAdapter extends BaseAdapter {

        ArrayList<User> myList = new ArrayList();
        LayoutInflater inflater;
        Context context;

        public CustomContactAdapter(Context context, ArrayList<User> myList) {
            this.myList = myList;
            this.context = context;
            inflater = LayoutInflater.from(this.context);
        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public User getItem(int i) {
            return myList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        /**
         * Displays individual conversations
         * @param position Used for the location of the user.
         * @param convertView not used
         * @param viewGroup not used
         * @return the view that was created.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view = getLayoutInflater().inflate(R.layout.contact_view, null);
            User user = myList.get(position);

            TextView name = view.findViewById(R.id.contactName);
            TextView body = view.findViewById(R.id.contactConversation);

            String nameGiven = user.getFirstName() + " " + user.getLastName();
            name.setText(nameGiven);
            Log.i("View set", "Values should be set");
            return view;
        }

        public void add(User user) {
            myList.add(user);
        }
    }

    private void setUpContacts() {
        contactListener = new ChildEventListener() {
            // Add the child added to a tempAnnouncement

            /**
             * Sets up what happens when a value is received from firebase.
             * @param dataSnapshot The link to the data.
             * @param s not used.
             */
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("Contact", "The size of the ContactsFromJsonList is " + ContactsFromJsonList.size());
                User tempUser = dataSnapshot.getValue(User.class);
                String otherUsers = gson.toJson(tempUser);
                Log.d("Testing for firebase", tempUser.getFirstName());
                Log.d("Testing for firebase", otherUsers);
                // Add the string in the message EditText to the preference manager
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(
                        "Contact", otherUsers).apply();
                ContactsFromJsonList.add(tempUser);

                // Make the current arrayList to a JSON and put it in the sharedPreferences
                String jsonContacts = gson.toJson(ContactsFromJsonList);
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(
                        "jsonContacts", jsonContacts).apply();
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
        contactRef.addChildEventListener(contactListener);
        contactRef.keepSynced(true);
    }

    /**
     * Adds the contacts that match the search.
     * @param search The searched name.
     */
    public void searchForName(String search) {
        Log.d("Name Given", search);
        // Go through the list from the shared preferences and save the ones that match the search.
        for (int i = 0; i < ContactsFromJsonList.size(); i++) {
            User temp = ContactsFromJsonList.get(i);
            String name = temp.getFirstName() + " " + temp.getLastName();
            Log.d("Name found", name);
            // If the name contans the search then save it.
            if (name.toLowerCase().contains(search.toLowerCase())) {
                Log.d("Names Match", name);
                contactListAdapter.add(temp);
                contactListAdapter.notifyDataSetChanged();
            }
        }

    }
}
