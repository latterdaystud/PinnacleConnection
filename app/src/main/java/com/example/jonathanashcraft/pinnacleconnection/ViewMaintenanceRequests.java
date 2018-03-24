package com.example.jonathanashcraft.pinnacleconnection;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ViewMaintenanceRequests extends AppCompatActivity {

    static private DatabaseReference MaintenanceRequestRef;
    static private ChildEventListener MaintenanceRequestListner;
    private MaintenanceRequest tempMaintenanceRequest;

    static private ArrayList<MaintenanceRequest> MaintenanceRequestList;
    static private CustomerArrayAdapter arrayAdapter;
    static private ListView listViewMaintenanceRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_maintenance_requests);

        // Create the reference to the database and set the childeventlistener
        MaintenanceRequestRef = FirebaseDatabase.getInstance().getReference()
                .child("MaintenanceRequests");

        MaintenanceRequestList = new ArrayList<>();

        arrayAdapter = new CustomerArrayAdapter(this, MaintenanceRequestList);
        listViewMaintenanceRequests = (ListView) findViewById(R.id.ListViewMaintenanceRequests);
        listViewMaintenanceRequests.setAdapter(arrayAdapter);

        MaintenanceRequestListner= new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // What happens when a new child is added
                tempMaintenanceRequest = dataSnapshot.getValue(MaintenanceRequest.class);

                if (tempMaintenanceRequest != null) {
                    // add it to the array adapter
                    arrayAdapter.add(tempMaintenanceRequest);
                    arrayAdapter.notifyDataSetChanged();
                } else {
                    // lol so tempMaintenanceRequest is null and it shouldn't ever be null
                    Log.e("MaintenanceReqListner", "tempMaintenanceRequest is null");
                }
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
        };

        MaintenanceRequestRef.addChildEventListener(MaintenanceRequestListner);

    }

    public class CustomerArrayAdapter extends BaseAdapter {

        ArrayList<MaintenanceRequest> arrayList = new ArrayList();
        LayoutInflater inflater;
        Context context;

        public CustomerArrayAdapter(Context context,
                                    ArrayList<MaintenanceRequest> maintenanceRequestsList) {
            this.arrayList = maintenanceRequestsList;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return arrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public void add(MaintenanceRequest maintenanceRequest) {
            arrayList.add(maintenanceRequest);
        }

        @Override
        public View getView(int position, View conertView, ViewGroup viewGroup) {
            // Create a few with an inflater
            View view = getLayoutInflater().inflate(R.layout.maintence_request, null);

            MaintenanceRequest newMaintenanceRequest = arrayList.get(getCount() - (position + 1));

            Log.i("getView", "Created a new MaintenanceRequest and loaded it");

            // Get the references
            TextView Topic = view.findViewById(R.id.textViewMaintenanceRequestTitle);
            TextView Description = view.findViewById(R.id.textViewMaintenanceRequestDescrip);
            TextView Date = view.findViewById(R.id.textViewMaintenanceRequestDate);
            ImageView image = view.findViewById(R.id.imageViewMaintenaceRequest);

            Log.i("getView", "Got all the references for the objects in the .xml");

            Topic.setText(newMaintenanceRequest.getTitle());
            Description.setText(newMaintenanceRequest.getDescription());
            Date.setText(newMaintenanceRequest.getTimeSent());

            Log.i("getView", "Set the view");

            return view;
        }
    }
}
