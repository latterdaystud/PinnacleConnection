package com.example.jonathanashcraft.pinnacleconnection;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.content.ContentValues.TAG;

/**
 * Created by Joseph on 4/5/2018.
 */

public class ViewMaintenanceRequestFragment extends Fragment {
    // For reference when trying to load from Firebase Storage
    final long ONE_MEGABYTE = 1024 * 1024;
    final long THREE_MEGABYTES = ONE_MEGABYTE * 3;

    // Variables to hold the maintenance request information to display
    private String Title;
    private String Description;
    private String Date;
    private String Author;
    private String Path;

    View currentView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String TAG = "RequestFragment";

        Bundle dataRecieved = getArguments();

        if (dataRecieved != null) {
            Log.d(TAG, "Did we get the title" + dataRecieved.getString("_Title"));
            Log.d(TAG, "Did we get the author" + dataRecieved.getString("_Author"));
            Log.d(TAG, "Did we get the description" + dataRecieved.getString("_Description"));
            Log.d(TAG, "Did we get the path" + dataRecieved.getString("_Path"));
        } else {
            Log.d(TAG, "dataRecieved = null");
        }

        Log.d(TAG, "Creating reference maintenance Request");

        Title = dataRecieved.getString("_Title");
        Description = dataRecieved.getString("_Description");
        Date = dataRecieved.getString("_Date");
        Author = dataRecieved.getString("_Author");
        Path = dataRecieved.getString("_Path");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        String TAG = "onCreateView";

        Log.d(TAG, "onCreateView has been called");
        View view = inflater.inflate(R.layout.fragment_view_maintenance_request, container,
                false);

        TextView Title = view.findViewById(R.id.textViewMaintenanceRequestTitle);
        TextView Author = view.findViewById(R.id.textViewMaintenanceRequestPerson);
        TextView Description = view.findViewById(R.id.textViewMaintenanceRequestDescrip);
        TextView Date = view.findViewById(R.id.textViewMaintenanceViewDate);

        currentView = view;

        Title.setText(this.Title);
        Author.setText(this.Author);
        Description.setText(this.Description);
        Date.setText(this.Date);

        // Here we load the image
         loadImage(Path);

        return view;
    }

    // This class loads an image from Firebase Storage with a passed in path
    private void loadImage(String pathToImage) {
        final String TAG = "loadImage";

        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("images")
                .child(pathToImage);

        imageRef.getBytes(THREE_MEGABYTES)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG, "We successfully loaded the bytes!");

                        Bitmap bipy = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        // Load the UI references
                        ProgressBar progressBar = currentView.findViewById(R.id.progressBarImageLoading);
                        TextView textViewImageLoading = currentView.findViewById(R.id.textViewImageLoading);
                        ImageView photo = currentView.findViewById(R.id.imageViewMaintenanceRequestPhoto);

                        Log.d(TAG, "photo equals " + photo);

                        // Set the imageView
                        photo.setImageBitmap(bipy);

                        // Make things visible and invisible
                        photo.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        textViewImageLoading.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to load image");
                        e.printStackTrace();
                    }
                });
    }


}
