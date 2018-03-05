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
import java.util.Objects;
import android.text.format.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MessagingActivity extends AppCompatActivity {

    private CustomMessagingAdapter arrayAdapter;
    private ListView listView;
    private EditText message;
    private ArrayList<TextSent> arrayList;
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
            }
        });

    }
    public void onSend(View view) {

        if(Objects.equals(message.getText().toString(), " ") || Objects.equals(message.getText().toString(), ""))
            return;
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(
                "Message", message.getText().toString()).apply();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String myStrValue = prefs.getString("Message", "myStringToSave");
        arrayAdapter.add(myStrValue);
        arrayAdapter.notifyDataSetChanged();
        message.setText("");
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
                view = getLayoutInflater().inflate(R.layout.row, null);
                textview = view.findViewById(R.id.msgr);
                textView2 = view.findViewById(R.id.TextView2);
                addition = "Sent: ";
            }
            else {
                view = getLayoutInflater().inflate(R.layout.row2, null);
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
    }

}
