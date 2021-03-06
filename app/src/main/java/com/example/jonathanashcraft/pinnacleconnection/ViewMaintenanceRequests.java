package com.example.jonathanashcraft.pinnacleconnection;

import android.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

/**
 * Used to view the maintenance requests that tenants send to the manager. Fragments are used
 * to actually set the list view and the individual maintenance requests
 */
public class ViewMaintenanceRequests extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_maintenance_requests);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.pinnacle_logo);
        setTitle(" Requests Received");

        final String TAG = "onCreate";

        Log.d(TAG, "Starting listFragment Fragment");

        getFragmentManager().beginTransaction()
                .replace(R.id.ConstraintLayoutForFrags,
                        new ViewMaintenanceRequestsListFragment())
                .addToBackStack(null)
                .commit();

        ViewMaintenanceRequestsListFragment.setListener
                (new ViewMaintenanceRequestsListFragment.DataSenderInterface() {

                    @Override
                    public void sendMaintenceRequestData(String Title, String Date, String Author, String Description, String Path) {

                        Log.d(TAG, "The listener got called");

                        Bundle data = new Bundle();
                        data.putString("_Title", Title);
                        data.putString("_Date", Date);
                        data.putString("_Author", Author);
                        data.putString("_Description", Description);
                        data.putString("_Path", Path);

                        ViewMaintenanceRequestFragment newFrag = new ViewMaintenanceRequestFragment();

                        newFrag.setArguments(data);


                        // The activity starts the new frag
                        getFragmentManager().beginTransaction()
                                .replace(R.id.ConstraintLayoutForFrags, newFrag)
                                .addToBackStack(null)
                                .commit();

                    }
                });
    }
}

