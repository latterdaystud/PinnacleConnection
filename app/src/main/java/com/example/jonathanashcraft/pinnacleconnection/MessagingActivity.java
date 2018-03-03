package com.example.jonathanashcraft.pinnacleconnection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class MessagingActivity extends AppCompatActivity {

    private CustomMessagingAdapter arrayAdapter;
    private ListView listView;
    private EditText message;
    private ArrayList arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        message = findViewById(R.id.editText);
        arrayList = new ArrayList();

        arrayAdapter = new CustomMessagingAdapter(this, arrayList);
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

    public final CustomMessagingAdapter getAdapter() {
        return arrayAdapter;
    }

    public final EditText getMessage() {
        return message;
    }
    public void setEditText(String value) { message.setText(value);}

    public class CustomMessagingAdapter extends BaseAdapter {

        ArrayList myList = new ArrayList();
        LayoutInflater inflater;
        Context context;



        public CustomMessagingAdapter(Context context, ArrayList myList) {
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
            MessageSent messageSent;
            View view;
            TextView textview;
            if (position % 2 == 1) {
                view = getLayoutInflater().inflate(R.layout.row, null);
                textview = view.findViewById(R.id.msgr);
            }
            else {
                view = getLayoutInflater().inflate(R.layout.row2, null);
                textview = view.findViewById(R.id.msgr2);
            }

            textview.setText(myList.get(position).toString());
            return view;
        }

        public void add(String string) {
            myList.add(string);
        }
    }

}
