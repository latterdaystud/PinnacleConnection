package com.example.jonathanashcraft.pinnacleconnection;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Joseph on 4/5/2018.
 */

public class ViewMaintenanceRequestFragment extends Fragment {
    // For reference when trying to load from Firebase Storage
    final long ONE_MEGABYTE = 1024 * 1024;
    final long THREE_MEGABYTES = ONE_MEGABYTE * 3;
    MaintenanceRequest maintenanceRequest;

    private String Title;
    private String Description;
    private String Date;
    private String Author;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String TAG = "RequestFragment";

        Bundle dataRecieved = getArguments();

        if (dataRecieved != null) {
            Log.d(TAG, "Did we get the title" + dataRecieved.getString("_Title"));
            Log.d(TAG, "Did we get the author" + dataRecieved.getString("_Author"));
            Log.d(TAG, "Did we get the description" + dataRecieved.getString("_Description"));
        } else {
            Log.d(TAG, "dataRecieved = null");
        }

        Log.d(TAG, "Creating reference maintenance Request");

        Title = dataRecieved.getString("_Title");
        Description = dataRecieved.getString("_Description");
        Date = dataRecieved.getString("_Date");
        Author = dataRecieved.getString("_Author");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        String TAG = "onCreateView";

        if (container != null) {
            Log.d(TAG, "The contain is not equal to null, remove all current views");

            //getActivity().getFragmentManager().popBackStack();
            //container.removeAllViews();

        }

        Log.d(TAG, "onCreateView has been called");
        View view = inflater.inflate(R.layout.fragment_view_maintenance_request, container,
                false);

        TextView Title = view.findViewById(R.id.textViewMaintenanceRequestTitle);
        TextView Author = view.findViewById(R.id.textViewMaintenanceRequestPerson);
        TextView Description = view.findViewById(R.id.textViewMaintenanceRequestDescrip);
        TextView Date = view.findViewById(R.id.textViewMaintenanceViewDate);

        Title.setText(this.Title);
        Author.setText(this.Author);
        Description.setText(this.Description);
        Date.setText(this.Date);

        return view;
    }


}
