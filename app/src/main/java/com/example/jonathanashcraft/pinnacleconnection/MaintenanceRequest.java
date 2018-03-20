package com.example.jonathanashcraft.pinnacleconnection;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class MaintenanceRequest extends AppCompatActivity {

    private ListView maintenanceListView;
    private CustomMaintenanceAdapter maintenanceAdapter;
    private ArrayList<MaintenanceRequestSent> maintenanceList;
    private EditText issue;
    private CheckBox urgency;
    private ImageView imageButton;
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_request);
        maintenanceList = new ArrayList<>();
        maintenanceListView = findViewById(R.id.listView);
        maintenanceAdapter = new CustomMaintenanceAdapter(this, maintenanceList);
        maintenanceListView.setAdapter(maintenanceAdapter);
        issue = findViewById(R.id.editText_issue);
        urgency = findViewById(R.id.urgent_checkBox);
        imageButton = findViewById(R.id.imageViewer);
        imageButton.setImageDrawable(null);
        Log.d("Test For Closing ERROR", "wHAT IS GOING ON!?");
    }

    public void submitPressed(View view) {
        String title = "New Request";
        String description = issue.getText().toString();
        if(Objects.equals(description, "") || Objects.equals(description, " "))
            return;
        boolean isUrgent = urgency.isChecked();
        Bitmap image;
        if(imageButton.getDrawable() != null) {
            image = ((BitmapDrawable) imageButton.getDrawable()).getBitmap();
        }
        else {
            image = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.ic_menu_gallery);
        }
        Log.d("Testing decode", "Decode occurs before this message");
        User fakeUser = new User();
        MaintenanceRequestSent request = new MaintenanceRequestSent(title, description, fakeUser, isUrgent, image);
        maintenanceAdapter.add(request);
        maintenanceAdapter.notifyDataSetChanged();
        issue.setText("");
        //Clear the image box
        //imageButton.setImageResource(0);
        imageButton.setImageDrawable(null);
        urgency.setChecked(false);
    }

    public void openGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Testing for exception", "Before the try catch block");
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first message_right
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                //Set the image
                imageButton.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
            e.printStackTrace();
        }

    }

    public class CustomMaintenanceAdapter extends BaseAdapter {

        ArrayList<MaintenanceRequestSent> myList = new ArrayList();
        LayoutInflater inflater;
        Context context;

        public CustomMaintenanceAdapter(Context context, ArrayList<MaintenanceRequestSent> myList) {
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
            MaintenanceRequestSent request = myList.get(position);
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

        public void add(MaintenanceRequestSent requestSent) {
            SimpleDateFormat df = new SimpleDateFormat("h:mma, EEE, MMM d");
            String date = df.format(Calendar.getInstance().getTime());
            requestSent.setTimeSent("Sent at " + date);
            myList.add(requestSent);

        }
    }
}
