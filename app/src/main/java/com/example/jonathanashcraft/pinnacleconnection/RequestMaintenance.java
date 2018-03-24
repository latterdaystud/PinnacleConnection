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

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Properties;


import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.*;

import pub.devrel.easypermissions.EasyPermissions;

public class RequestMaintenance extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    GoogleAccountCredential mCredential;
    private TextView mOutputText;
    private Button mCallApiButton;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call Gmail API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { GmailScopes.GMAIL_LABELS };

    private ListView maintenanceListView;
    private CustomMaintenanceAdapter maintenanceAdapter;
    private ArrayList<MaintenanceRequest> maintenanceList;

    private Button bCreateRequest;
    private ImageView ivImagePreview;
    private EditText etIssueInput;
    private CheckBox cbUrgent;
    private EditText etSubjectOfIssue;
    private Boolean addedPhoto;

    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

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

        // TODO: For some reason this is null by the time we attach an onclick listener to this button

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

                        String text = etIssueInput.getText().toString();

                        // TODO: don't know wtf is happening here
                        Log.d("MaintenanceRequest", text);

                        makeRequest();

                    }
                })
                .setNeutralButton("Attach Photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        openGallery(getCurrentFocus());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        // Finally create the dialog box(everything we did above)
        final AlertDialog maintenanceRequestDialog = builder.create();

        // What happens if you push the create request button
        bCreateRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maintenanceRequestDialog.show();
            }
        });


//        // TODO: ibGallery equals null in my testing
//        if(ibGallery != null) {
//            Log.d(TAG, "ibGallery is not equal to null!!");
//        } else {
//            Log.d(TAG, "ibGallery equals null");
//        }

//        // TODO: The program is crashing right here because ibGallery equals null
//        ibGallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "Does this run?");
//            }
//        });
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
                AndroidUser.getUser(), isUrgent);

        DatabaseReference maintenanceRequestRef = FirebaseDatabase.getInstance()
                .getReference("MaintenanceRequests");

        // Store in the database with the key of users name and the date of the maintenance request is sent
        maintenanceRequestRef.child(AndroidUser.getUserFirstName() + AndroidUser.getUserLastName()
                + " " + Calendar.getInstance().getTime().toString()).setValue(tempMaintenanceRequest);

        maintenanceAdapter.add(tempMaintenanceRequest);
        maintenanceAdapter.notifyDataSetChanged();
    }

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to email address of the receiver
     * @param from email address of the sender, the mailbox account
     * @param subject subject of the email
     * @param bodyText body text of the email
     * @return the MimeMessage to be used to send email
     * @throws MessagingException
     */
    public static MimeMessage createEmail(String to,
                                          String from,
                                          String subject,
                                          String bodyText)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    /**
     * Create a message from an email.
     *
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws IOException
     * @throws MessagingException
     */
    public static Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        //message.setRaw(encodedEmail);
        return message;
    }

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to Email address of the receiver.
     * @param from Email address of the sender, the mailbox account.
     * @param subject Subject of the email.
     * @param bodyText Body text of the email.
     * @param file Path to the file to be attached.
     * @return MimeMessage to be used to send email.
     * @throws MessagingException
     */
    public static MimeMessage createEmailWithAttachment(String to,
                                                        String from,
                                                        String subject,
                                                        String bodyText,
                                                        File file)
            throws MessagingException, IOException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(bodyText, "text/plain");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        mimeBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(file);

        mimeBodyPart.setDataHandler(new DataHandler(source));
        mimeBodyPart.setFileName(file.getName());

        multipart.addBodyPart(mimeBodyPart);
        email.setContent(multipart);

        return email;
    }

    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @param emailContent Email to be sent.
     * @return The sent message
     * @throws MessagingException
     * @throws IOException
     */
    public static Message sendMessage(Gmail service,
                                      String userId,
                                      MimeMessage emailContent)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(emailContent);
        //message = service.users().messages().send(userId, message).execute();

        //System.out.println("Message id: " + message.getId());
        //System.out.println(message.toPrettyString());
        return message;
    }


    public void submitPressed(View view) {


        Bitmap image;

        // This is to get an image that was saved as the button, we won't be hold the image like
        // that anymore
//        if(imageButton.getDrawable() != null) {
//            image = ((BitmapDrawable) imageButton.getDrawable()).getBitmap();
//        }
//        else {
//            image = BitmapFactory.decodeResource(this.getResources(),
//                    R.drawable.ic_menu_gallery);
//        }

        // Check to see if the user added a photo or not, handle that
        if (addedPhoto) {
            // code to add a photo that was selected over in open gallery
        } else {

        }


//        Log.d("Testing decode", "Decode occurs before this message");
//        User fakeUser = new User();
//        MaintenanceRequestSent request = new MaintenanceRequestSent(title, description, fakeUser, isUrgent, image);
//        maintenanceAdapter.add(request);
//        maintenanceAdapter.notifyDataSetChanged();
//        issue.setText("");
//        //Clear the image box
//        //imageButton.setImageResource(0);
//        imageButton.setImageDrawable(null);
//        urgency.setChecked(false);
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


        String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(this, galleryPermissions)) {
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

//                    //Set the image
//                    imageButton.setImageBitmap(BitmapFactory
//                            .decodeFile(imgDecodableString));

                } else {
                    Toast.makeText(this, "You haven't picked Image",
                            Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                        .show();
                e.printStackTrace();
            }
        } else {
            EasyPermissions.requestPermissions(this, "Access for storage", 101, galleryPermissions);

        }
//        try {
//            // When an Image is picked
//            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
//                    && null != data) {
//                // Get the Image from data
//
//                Uri selectedImage = data.getData();
//                String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//                // Get the cursor
//                Cursor cursor = getContentResolver().query(selectedImage,
//                        filePathColumn, null, null, null);
//                // Move to first message_right
//                cursor.moveToFirst();
//
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                imgDecodableString = cursor.getString(columnIndex);
//                cursor.close();
//
//                //Set the image
//                imageButton.setImageBitmap(BitmapFactory
//                        .decodeFile(imgDecodableString));
//
//            } else {
//                Toast.makeText(this, "You haven't picked Image",
//                        Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
//                    .show();
//            e.printStackTrace();
//        }

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
