package com.example.jonathanashcraft.pinnacleconnection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This activity is the main activty that holds the announcements. When a user first logs in or the
 * system recognizes that user has already logged in, the user will be brought to this activity
 * everytime. From this activity, the user is able to start other activities.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // For the list of announcements
    private CustomAnnouncementsAdapter arrayAdapter;
    // The basic format of the texts
    private ListView listView;
    ArrayList<Announcement> MessagesFromJsonList;
    private Gson gson = new Gson();
    private String jsonMessages;
    private Announcement[] an;


    private Announcement tempAnnouncement;

    // Firebase database stuff
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;

    private ChildEventListener announcementListener;
    private DatabaseReference AnnouncementRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String TAG = "onCreate";

        Log.d(TAG,"You are now in MainActivity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Firebase
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();
        AnnouncementRef = database.getReference().child("Announcements");
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Welcome Back", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, ContactList.class);
                startActivity(intent);
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);


        /*********** Not Sure to what extent this is needed *******************/

        // Set the items for the Navigation View
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_admin).setVisible(false);

        Log.d(TAG, "Is the user a manager?" + CurrentUser.isManager());

        // All the things the manager can see
        if(CurrentUser.isManager()) {
            menu.findItem(R.id.nav_admin).setVisible(true);
            menu.findItem(R.id.nav_maintenance).setTitle("View Maintenance Requests");
        }

        Log.d(TAG, "The current device ID is: " + FirebaseInstanceId.getInstance().getToken().toString());
        /*
        MenuItem nav_camera = menu.findItem(R.id.nav_manage);
        nav_camera.setTitle("Maintenance Request");

        MenuItem nav_send = menu.findItem(R.id.nav_send);
        nav_send.setTitle("Message Manager");

        MenuItem nav_share = menu.findItem(R.id.nav_share);
        nav_share.setTitle("Request Theater");

        MenuItem nav_gallery = menu.findItem(R.id.nav_gallery);
        nav_gallery.setTitle("Request Theater");
        MenuItem nav_manage = menu.findItem(R.id.nav_manage);
        nav_share.setTitle("Log in");

        // Things to ignore in the navigation bar
        menu.findItem(R.id.nav_slideshow).setVisible(false);*/

        navigationView.setNavigationItemSelectedListener(this);

        //This displays the AnnouncementFragment as soon as the app is opened
        AnnouncementFragment announcementFragment = new AnnouncementFragment();
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        //manager.beginTransaction().replace(R.id.mainLayout, announcementFragment).commit();

        // Trying to use JSon
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String jsonMessagesLoadedFromMyPreferences = prefs.getString("jsonAnnouncements", "[ ]");

        // TODO: look into jsonMessagesLoadedFromMyPreferences and see if it has to do with announcements, if it does, change the name of the announcements

        // Convert the JSON to an array of TextSents
        if (!jsonMessagesLoadedFromMyPreferences.isEmpty()) {
            an = gson.fromJson(jsonMessagesLoadedFromMyPreferences, Announcement[].class);
        }
        // Slap that ^ array into an ArrayList
        MessagesFromJsonList = new ArrayList<>(Arrays.asList(an));

        // Instantiation for the list view.
        arrayAdapter = new CustomAnnouncementsAdapter(this, MessagesFromJsonList);
        listView = findViewById(R.id.announcementsListView);
        listView.setAdapter(arrayAdapter);

        // Value event listener to listen for the real time datachanges
        announcementListener = new ChildEventListener() {
            // Add the child added to a tempAnnouncement
            // TODO: change the onChildAdded so that it will only add new items
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                tempAnnouncement = dataSnapshot.getValue(Announcement.class);
                if (tempAnnouncement != null) {
                    Log.d(TAG, "TempAnnouncements is not equal to null");
                    arrayAdapter.add(tempAnnouncement);
                    arrayAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "TempAnnouncements is equal to null");
                }

                Log.d(TAG, "temp Announcement was created and added to the arrayAdapter");
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

        // Attach the childEventListener
        AnnouncementRef.addChildEventListener(announcementListener);

        // Let's greet the user because we are nice
        Toast.makeText(MainActivity.this, "Welcome " + CurrentUser.getFirstName(),
                Toast.LENGTH_SHORT).show();

        // Static method is static
        // Let's check to see if we need to reload the device token
        TokenAccess.loadToken();
    }

    protected void onStart() {
        super.onStart();
        final String TAG = "onStart";

//        CurrentUser.reloadUser();
    }

    public void createAnnouncement(View view) {
//        Announcement new1 = new Announcement();
//        arrayAdapter.add(new1);
//        arrayAdapter.notifyDataSetChanged();
//        Announcement new2 = new Announcement("Second title", "Always and forever", "today buddy", "Weellllll lets hope this works out.");
//        arrayAdapter.add(new2);
//        arrayAdapter.notifyDataSetChanged();

       Intent intent = new Intent(this, CreateAnnouncement.class);
        startActivityForResult(intent, 1);
    }


    public void loginPressed(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        String TAG = "onNavigationItem";

        // Handle navigation view item clicks here.
        Log.d("onNavigationItem", "Method Opened!");

        int id = item.getItemId();

        if (id == R.id.nav_announcements) {
            // What happens when you tap the announcements tab

        } else if (id == R.id.nav_theater) {

            Intent intent = new Intent(this, TheaterRequestActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_maintenance) {

            if(!CurrentUser.isManager()) {
                // If the user is not a manager
                Intent intent = new Intent(this, RequestMaintenance.class);
                startActivity(intent);
            } else {
                // If the user is a manager
                Intent intent = new Intent(this, ViewMaintenanceRequests.class);
                startActivity(intent);
            }

        } else if (id == R.id.nav_admin) {

            createAnnouncement(this.listView);

        } else if (id == R.id.nav_login) {

            Log.d(TAG, "You pressed sign out, let's sign you out");
            // Sign out
            FirebaseAuth.getInstance().signOut();

            Log.d(TAG, "Let's start the loginActivity");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_message) {

            Intent intent = new Intent(this, ContactList.class);
            startActivity(intent);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class CustomAnnouncementsAdapter extends BaseAdapter {

        ArrayList<Announcement> myList = new ArrayList();
        LayoutInflater inflater;
        Context context;


        public CustomAnnouncementsAdapter(Context context, ArrayList<Announcement> myList) {
            this.myList = myList;
            this.context = context;
            inflater = LayoutInflater.from(this.context);

        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public Announcement getItem(int i) {
            return myList.get(getCount() - (i + 1));
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view = getLayoutInflater().inflate(R.layout.announcements_with_image, null);
            Announcement newAnnouncement = getItem(position);
            TextView Title = view.findViewById(R.id.title);
            TextView Body = view.findViewById(R.id.body);
            TextView Time = view.findViewById(R.id.time);
            TextView Manager = view.findViewById(R.id.manager);
            Title.setText(newAnnouncement.getTitle());
            Body.setText(newAnnouncement.getBody());
            Time.setText(newAnnouncement.getTimeOfAnnouncement());
            Manager.setText(newAnnouncement.getAuthor());
            Log.i("View set", "Values should be set");

            return view;
        }

        public void add(Announcement newAnnouncement) {
            myList.add(newAnnouncement);

        }
    }
}
