package com.example.jonathanashcraft.pinnacleconnection;

import android.app.FragmentManager;
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

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // For the list of announcements
    private MainActivity.CustomAnnouncementsAdapter arrayAdapter;
    // The basic format of the texts
    private ListView listView;
    ArrayList<Announcement> MessagesFromJsonList;
    private Gson gson = new Gson();
    private String jsonMessages;
    private Announcement[] an;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(MainActivity.this, MessagingActivity.class);
                startActivity(intent);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //This displays the AnnouncementFragment as soon as the app is opened
        AnnouncementFragment announcementFragment = new AnnouncementFragment();
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.mainLayout, announcementFragment).commit();

        // Trying to use JSon
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String jsonMessagesLoadedFromMyPreferences = prefs.getString("jsonAnnouncements", "[ ]");

        //Log.d(TAG,"The json message is " + jsonMessagesLoadedFromMyPreferences);

        // Convert the JSON to an array of TextSents
        if(!jsonMessagesLoadedFromMyPreferences.isEmpty()) {
            an = gson.fromJson(jsonMessagesLoadedFromMyPreferences, Announcement[].class);
        }
        // Slap that ^ array into an ArrayList
        MessagesFromJsonList = new ArrayList<>(Arrays.asList(an));

        // Instantiation for the list view.
        arrayAdapter = new CustomAnnouncementsAdapter(this, MessagesFromJsonList);
        listView = (ListView) findViewById(R.id.announcementsListView);
    }

    public void buttonPressed(View view) {
        Intent intent = new Intent(this, MessagingActivity.class);
        startActivity(intent);
    }
    public void loginPressed(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_announcements) {
            AnnouncementFragment announcementFragment = new AnnouncementFragment();
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, announcementFragment).commit();

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        public Object getItem(int i) {
            return myList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view = getLayoutInflater().inflate(R.layout.announcements_with_image, null);
            Announcement newAnnouncement = myList.get(position);
            TextView Title = view.findViewById(R.id.title);
            TextView Body = view.findViewById(R.id.body);
            TextView Time = view.findViewById(R.id.time);
            TextView Manager = view.findViewById(R.id.manager);
            Title.setText(newAnnouncement.getTitle());
            Body.setText(newAnnouncement.getAnnouncement());
            Time.setText(newAnnouncement.getTimePeriod());
            Manager.setText(newAnnouncement.getAuthor());

            return view;
        }

        public void add(Announcement newAnnouncement) {
            myList.add(newAnnouncement);

        }
    }
}
