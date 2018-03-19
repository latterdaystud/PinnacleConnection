package com.example.jonathanashcraft.pinnacleconnection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ContactList extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    ListView contactListView;
    ArrayList<User> userList;
    CustomContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        userList = new ArrayList<>();
        contactListView = findViewById(R.id.contactList);
        contactAdapter = new CustomContactAdapter(this, userList);
        contactListView.setAdapter(contactAdapter);
        User user = new User();
        for (int i = 0; i < 10; i++)
            contactAdapter.add(user);
        contactAdapter.add(user);
        contactAdapter.add(user);
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onContactPressed(view);
            }
        });
    }

    public void onContactPressed(View view){
        Intent intent = new Intent(ContactList.this, MessagingActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

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
        public Object getItem(int i) {
            return myList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view = getLayoutInflater().inflate(R.layout.contact_view, null);
            User user = myList.get(position);

            TextView name = view.findViewById(R.id.contactName);
            TextView body = view.findViewById(R.id.contactConversation);

            name.setText(user.getFirstName());
            Log.i("View set", "Values should be set");
            return view;
        }

        public void add(User user) {
            myList.add(user);

        }
    }
}
