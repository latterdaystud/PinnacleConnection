package com.example.jonathanashcraft.pinnacleconnection;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.ContentValues.TAG;


/**
 * Created by Joseph on 4/5/2018.
 */
public class ViewMaintenanceRequestsListFragment extends Fragment {

    private MaintenanceRequestsArrayAdapter arrayAdapter;
    private ListView maintenanceRequests;
    private ArrayList<MaintenanceRequest> maintenanceRequestArrayList;

    static public DataSenderInterface dataSender;

    // For reference when trying to load from Firebase Storage
    final long ONE_MEGABYTE = 1024 * 1024;
    final long THREE_MEGABYTES = ONE_MEGABYTE * 3;

    public static void setListener(DataSenderInterface ml) {
        dataSender = ml;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String TAG = "onCreate";

        Log.d(TAG, "onCreate of the fragment got called");
        maintenanceRequestArrayList = new ArrayList<>();
        arrayAdapter = new MaintenanceRequestsArrayAdapter(maintenanceRequestArrayList);

        // Load all maintenance requests from Firebase
        DatabaseReference MaintenanceRequestRef = FirebaseDatabase.getInstance().getReference()
                .child("MaintenanceRequests");

        MaintenanceRequestRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String TAG = "onChildAdded";

                Log.i(TAG, "How many times does this get called");

                MaintenanceRequest temp = dataSnapshot.getValue(MaintenanceRequest.class);

                // Let's make sure it loaded something
                if (temp != null) {
                    arrayAdapter.add(temp);

                    Log.d(TAG, "There are currently " + arrayAdapter.getCount() + " requests" +
                            " in your arraylist");
                } else {
                    Log.e(TAG, "temp maintenanceRequest equals null.");
                    Log.e(TAG, "Check the dataSnap shot and it is receiving information");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // probs need to handle this
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // probs need to handle this
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // probs need to handle this
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // lol need to handle this
            }
        });

        MaintenanceRequestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Okay all of the children should have been added");
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // lol probs need to handle this
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_maintenance_requests_list, container, false);

        // Let's hope these aren't equal to null
        maintenanceRequests = view.findViewById(R.id.listViewMaintenanceRequests);

        // Set the adapter to display the list
        maintenanceRequests.setAdapter(arrayAdapter);

        // If the user clicks an item in the list
        maintenanceRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                i = (arrayAdapter.getCount() - i) - 1;

                Log.d(TAG, "You clicked item" + l);
                Log.d(TAG, " This item is in position " + i);

                Log.d(TAG, "The title of request in position " + i + " is " +
                        arrayAdapter.getRequest(i).getTitle());

                Log.d(TAG, "Begging the transaction");

                MaintenanceRequest temp = arrayAdapter.getRequest(i);

                // Notify the listener
                if (dataSender != null ) {
                    dataSender.sendMaintenceRequestData(temp.getTitle(), temp.getTimeSent(), temp.getAuthor(),
                            temp.getDescription(), temp.getPath());

                } else {
                    // dataSender is equal to null
                    Log.wtf(TAG, "dataSender equals null. Make sure to sert the listener");
                }
                Log.d(TAG, "Transaction should be over");

            }
        });
        return view;
    }

    private class MaintenanceRequestsArrayAdapter extends BaseAdapter {

        ArrayList<MaintenanceRequest> requests = new ArrayList();
        LayoutInflater inflater;

        public MaintenanceRequestsArrayAdapter(ArrayList<MaintenanceRequest> ml) {
            requests = ml;
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return requests.size();
        }

        @Override
        public Object getItem(int i) {
            return requests.get(i);
        }

        public MaintenanceRequest getRequest(int i) { return requests.get(i); }
        @Override
        public long getItemId(int i) {
            return 0;
        }

        public void add(MaintenanceRequest request) {
            requests.add(request);
        }
        @Override
        public View getView(int i, View currentView, ViewGroup viewGroup) {
            View view = inflater.inflate(R.layout.view_maintenance_request_listview, null);

            MaintenanceRequest tempMaintenanceRequest = requests.get(getCount() - (i + 1));

            // Get the references from the layout
            TextView Topic = view.findViewById(R.id.textViewMaintenanceViewTopic);
            TextView Description = view.findViewById(R.id.textViewMaintenanceViewDescription);
            TextView Date = view.findViewById(R.id.textViewMaintenanceViewDate);
            TextView Author = view.findViewById(R.id.textViewMaintenanceViewName);
            TextView image = view.findViewById(R.id.textViewMaintenanceViewImageAttached);

            if(Objects.equals(tempMaintenanceRequest.getPath(), "")) {
                image.setText("No Image");
            }

            // Set the references with our available information
            Topic.setText(tempMaintenanceRequest.getTitle());
            Description.setText(tempMaintenanceRequest.getDescription());
            Date.setText(tempMaintenanceRequest.getTimeSent());
            Author.setText(tempMaintenanceRequest.getAuthor());

            Log.i("getView", "Set the view");
            return view;
        }
    }

    public interface DataSenderInterface {
        public void sendMaintenceRequestData(String Title, String Date, String Author,
                                             String Description, String Path);
    }

}
