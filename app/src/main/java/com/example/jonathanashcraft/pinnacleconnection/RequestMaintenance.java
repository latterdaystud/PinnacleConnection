package com.example.jonathanashcraft.pinnacleconnection;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import pub.devrel.easypermissions.EasyPermissions;

public class RequestMaintenance extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    GoogleAccountCredential mCredential;
    private TextView mOutputText;
    private Button mCallApiButton;
    ProgressDialog mProgress;
    Uri fileToUpload;

    private ListView maintenanceListView;
    private CustomMaintenanceAdapter maintenanceAdapter;
    private ArrayList<MaintenanceRequest> maintenanceList;

    private Button bCreateRequest;
    private ImageView ivImagePreview;
    private EditText etIssueInput;
    private CheckBox cbUrgent;
    private EditText etSubjectOfIssue;
    private Boolean addedPhoto;
    Uri currImageURI;

    private static int RESULT_LOAD_IMG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String TAG = "onCreate";

        Log.d(TAG, "onCreate of MaintenanceRequest called!");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_maintenance);

        maintenanceList = new ArrayList<>();
        maintenanceListView = findViewById(R.id.listView);
        maintenanceAdapter = new CustomMaintenanceAdapter(this, maintenanceList);
        maintenanceListView.setAdapter(maintenanceAdapter);

        bCreateRequest = findViewById(R.id.buttonCreateRequest);
        //etIssueInput = findViewById(R.id.editTextIssueInput);
        cbUrgent = findViewById(R.id.checkBoxIsUrgent);
        addedPhoto = false;

        // Build the dialog box that will appear for creating a maintenance request
        AlertDialog.Builder builder = new AlertDialog.Builder(RequestMaintenance.this);

        builder.setTitle("Maintenance Request")
                .setView(R.layout.create_maintenance_request_dialog)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Reference the variables once the dialog is created since findViewById
                        // is done in the view hierarchy
                        Dialog dialogView = (Dialog) dialogInterface;
                        etIssueInput = dialogView.findViewById(R.id.editTextIssueInput);
                        cbUrgent = dialogView.findViewById(R.id.checkBoxIsUrgent);
                        etSubjectOfIssue = dialogView.findViewById(R.id.editTextTopicProblem);
                        //imagePreview = dialogView.findViewById(R.id.imageViewDialogCreateRequest);

                        String text = etIssueInput.getText().toString();

                        // TODO: don't know wtf is happening here
                        Log.d("MaintenanceRequest", text);

                        makeRequest();

                    }
                })
                .setNeutralButton("Attach Photo", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        // Finally create the dialog box(everything we did above)
        final AlertDialog maintenanceRequestDialog = builder.create();

        // Add a listener for the neutral button (open the gallery)
        maintenanceRequestDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_NEUTRAL);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openGallery(getCurrentFocus());
                        }
                    });
                };
            });

        // What happens if you push the create request button
        bCreateRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {maintenanceRequestDialog.show();}
                });
    }

    public void makeRequest() {

        // Get the input from the dialog box that the user inputted
        String description = etIssueInput.getText().toString();
        String topic = etSubjectOfIssue.getText().toString();

        //TODO: Make sure the topic is filled out

        // Checks to see if the user inputted useful data
        if(Objects.equals(description, "") || Objects.equals(description, " "))
            return;

        boolean isUrgent = cbUrgent.isChecked();

        // Make a temporary request to hold the data that has been inputted
        MaintenanceRequest tempMaintenanceRequest = new MaintenanceRequest(topic, description,
                AndroidUser.getUserFirstName() + " " + AndroidUser.getUserLastName(), isUrgent,
                fileToUpload.getLastPathSegment());

        Log.d("makeReqguest", "fileToUpload: " + fileToUpload.getLastPathSegment());
        Log.d("makeRequest", "fileLocation is: " + tempMaintenanceRequest.getPath());

        DatabaseReference maintenanceRequestRef = FirebaseDatabase.getInstance()
                .getReference("MaintenanceRequests");

        // Store in the database with the key of users name and the date of the maintenance request is sent
        maintenanceRequestRef.child(AndroidUser.getUserFirstName() + AndroidUser.getUserLastName()
                + " " + Calendar.getInstance().getTime().toString()).setValue(tempMaintenanceRequest);

        maintenanceAdapter.add(tempMaintenanceRequest);
        maintenanceAdapter.notifyDataSetChanged();
    }

    public void openGallery(View view) {

        // Before we do anything, let's make sure we have permissions to do it
        if(!EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, "We need to access your photos",
                    1, Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    // To handle when an image is selected from the browser, add the following to your Activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                // currImageURI is the global variable I'm using to hold the content:// URI of the image
                currImageURI = data.getData();
            }
        }

        String path;

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        path = getRealPathFromURI(currImageURI);

        fileToUpload = Uri.fromFile(new File(path));

        // TODO: When uploading images it displays photo failed to upload and photo uploaded at the same time
        StorageReference imageRef = storageRef.child("images/"+ fileToUpload.getLastPathSegment());

        UploadTask uploadTask = imageRef.putFile(fileToUpload);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // aw it didn't work
                Toast.makeText(RequestMaintenance.this, "Photo Failed to Upload",
                        Toast.LENGTH_SHORT).show();

                Log.d("onFailure", "The uploadTask failed");
                e.printStackTrace();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(RequestMaintenance.this, "Photo Uploaded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        Log.d("RequestMaintenance", "The path is " + path);
    }

    public String getRealPathFromURI(Uri contentUri) {

        // can post image
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    public class CustomMaintenanceAdapter extends BaseAdapter {

        ArrayList<MaintenanceRequest> myList = new ArrayList();
        LayoutInflater inflater;
        Context context;

        public CustomMaintenanceAdapter(Context context, ArrayList<MaintenanceRequest> myList) {
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
            View view = getLayoutInflater().inflate(R.layout.maintenance_layout, null);
            MaintenanceRequest request = myList.get(position);
            TextView urgent = view.findViewById(R.id.urgentBar);
            TextView urgent2 = view.findViewById(R.id.urgentBar2);
            TextView Body = view.findViewById(R.id.description);
            TextView Time = view.findViewById(R.id.timeSent);
            ImageView imageView = view.findViewById(R.id.attatchedImage);

            if(request.isUrgent()) {
                urgent.setBackgroundColor(Color.RED);
                urgent2.setBackgroundColor(Color.RED);
            }
            Log.d("Testing decode", "Decode occurs way before this message and after");
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                      R.drawable.ic_menu_gallery);
            if(request.getImage() != null && request.getImage() != icon)
                imageView.setImageBitmap(request.getImage());
            Log.d("Testing decode", "image was supposed to just set the bitmap" + request.getImage());

            Body.setText(request.getDescription());
            Time.setText(request.getTimeSent());
            Log.i("View set", "Values should be set");
            return view;
        }

        public void add(MaintenanceRequest request) {
            SimpleDateFormat df = new SimpleDateFormat("h:mma, EEE, MMM d");
            String date = df.format(Calendar.getInstance().getTime());
            request.setTimeSent("Sent at " + date);
            myList.add(request);

        }
    }

}
