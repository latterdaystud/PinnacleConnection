package com.example.jonathanashcraft.pinnacleconnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Used to view the maintenance requests that tenants send.
 */
public class ViewMaintenanceRequests extends AppCompatActivity {
    static private DatabaseReference MaintenanceRequestRef;
    static private ChildEventListener MaintenanceRequestListner;
    private MaintenanceRequest tempMaintenanceRequest;

    static private ArrayList<MaintenanceRequest> MaintenanceRequestList;
    static private CustomerArrayAdapter arrayAdapter;
    static private ListView listViewMaintenanceRequests;
    static private FirebaseStorage storage;
    static private StorageReference imageRef;

    // First load is always set to true;
    static private Boolean firstLoad = true;

    // For reference when trying to load from Firebase Storage
    final long ONE_MEGABYTE = 1024 * 1024;
    final long THREE_MEGABYTES = ONE_MEGABYTE * 3;

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("onStart", "onStart got called");

        Log.d("onStart", "notifying the arrayAdapter of the changes");
        //arrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_maintenance_requests);

        final String TAG = "onCreate";

        Log.d(TAG, "First load is equal to: " + firstLoad);

        storage = FirebaseStorage.getInstance();
        imageRef = storage.getReference();

        // Create the reference to the database and set the childeventlistener
        MaintenanceRequestRef = FirebaseDatabase.getInstance().getReference()
                .child("MaintenanceRequests");

        MaintenanceRequestList = new ArrayList<>();

        arrayAdapter = new CustomerArrayAdapter(this, MaintenanceRequestList);
        listViewMaintenanceRequests = findViewById(R.id.ListViewMaintenanceRequests);
        listViewMaintenanceRequests.setAdapter(arrayAdapter);
        MaintenanceRequestListner = new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                String TAG = "onChildAdded";

                // What happens when a new child is added
                Log.i("MaintenanceReqListner", "How many times does this get called?");

                Log.i("MaintenanceReqListner", "*******************8");
                tempMaintenanceRequest = dataSnapshot.getValue(MaintenanceRequest.class);

                if (tempMaintenanceRequest != null) {
                    // add it to the array adapter
                    arrayAdapter.add(tempMaintenanceRequest);
                    arrayAdapter.notifyDataSetChanged();

                    Log.d("MaintenanceReqListner", "There are currently " +
                            arrayAdapter.getCount() + " in your arrayList fag");
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

        // This is called when all the data has been loaded on the onChildAdded from above
        MaintenanceRequestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This runs when all the data is pulled from the addChildEventListener above
                // AT THIS POINT ALL THE CHILD DATA HAS BEEN LOADED FROM FIREBASE
                Log.d(TAG, "Let's load some images");
                arrayAdapter.loadImages();

                Log.d(TAG, "Lets notify of the datachange");
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        MaintenanceRequestRef.addChildEventListener(MaintenanceRequestListner);


        // If it isn't the first load, then we need to load the images manually and not with the
        // method above.
        if (!firstLoad)

        {
            Log.d(TAG, "Going to load images");
            //arrayAdapter.loadImages();

            //arrayAdapter.notifyDataSetChanged();
        }

        firstLoad = false;


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

        public void loadImage(final int index) {
            final String TAG = "loadImage";

            Log.d(TAG, "opened loadImage()");

            final MaintenanceRequest tempRequest = this.arrayList.get(index);

            imageRef = FirebaseStorage.getInstance().getReference().child("images/" + tempRequest
                    .getPath());

            imageRef.getBytes(THREE_MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Log.d(TAG, "We loaded the image, let's set it");
                    tempRequest.setBytes(bytes);

                    // Add it to the arrayList to the certin index
                    addBytesToArrayList(bytes, index);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.wtf(TAG, "Failed to load image");
                    // let's print out the error
                    e.printStackTrace();
                }
            });

            Log.d(TAG, "Leaving loadImage()");
        }

        public void loadImages() {
            String TAG = "loadImages";

            Log.d(TAG, "We are in loadImages now");

            Log.d(TAG, "There are " + this.arrayList.size() + " in the array");
            for (int i = 0; i < this.arrayList.size(); i++) {
                // This is the path to the current image on firebase
                final MaintenanceRequest tempRequest = this.arrayList.get(i);

                // get the path on firebase
                imageRef = FirebaseStorage.getInstance().getReference().child("images/" +
                        tempRequest.getPath());

                // Let's log the path that we searched to see if it is correct
                Log.d(TAG, "images/" + tempRequest.getPath());

                // Lets get what is stored in that reference and add on onSuccessListener
                final int finalI = i;
                imageRef.getBytes(THREE_MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d("onSuccess", "We loaded image: " + tempRequest.getPath());
                        Log.d("onSuccess", "Length of byte array " + bytes.length);
                        tempRequest.setBytes(bytes);

                        Log.d("onSuccess", "setting the arraylist index: " + finalI + " with the new bytes");

                        addBytesToArrayList(bytes, finalI);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.wtf("onFailure", "Unable to load image: " +
                                tempRequest.getPath());
                        e.printStackTrace();
                    }
                });
                Log.d(TAG, "Are we going to loop again in loadImages?");
            }

            Log.d(TAG, "We are leaving loadImages()");
        }

        // This is to add bytes to specific entries in the arrayList
        private void addBytesToArrayList(byte[] bytes, int index) {
            Log.d("addBytesToArrayList", "Adding bytes to index " + index);
            this.arrayList.get(index).setBytes(bytes);

            notifyDataSetChanged();
        }


        @Override
        public View getView(int position, View conertView, ViewGroup viewGroup) {
            // Create a few with an inflater
            View view = getLayoutInflater().inflate(R.layout.view_maintenance_request_listview, null);

            MaintenanceRequest newMaintenanceRequest = arrayList.get(getCount() - (position + 1));

            Log.i("getView", "Created a new MaintenanceRequest and loaded it");

            // Get the references
            TextView Topic = view.findViewById(R.id.textViewMaintenanceViewTopic);
            TextView Description = view.findViewById(R.id.textViewMaintenanceViewDescription);
            TextView Date = view.findViewById(R.id.textViewMaintenanceViewDate);
            TextView Author = view.findViewById(R.id.textViewMaintenanceViewName);
            //ImageView Photo = view.findViewById(R.id.imageViewMaintenanceRequestPhoto);
            //ProgressBar imageLoadingProgressBar = view.findViewById(R.id.progressBarImageLoading);
            //TextView imageLoadingTextView = view.findViewById(R.id.textViewImageLoading);

//            // See if there is an image to load already.
//            if (newMaintenanceRequest.getBytes() != null) {
//                Log.d("getView", "What is the length of bytes " + newMaintenanceRequest.getBytes().toString());
//
//                Log.i("getView", "Got all the references for the objects in the .xml");
//
//                Bitmap bity = BitmapFactory.decodeByteArray(newMaintenanceRequest.getBytes(), 0, newMaintenanceRequest.getBytes().length);
//
//                Log.i("getView", "Setting the new photo");
//
//                // Set the image to the imageView
//                Photo.setImageBitmap(bity);
//                Photo.setVisibility(View.VISIBLE);
//
//                // Set the loading bar and image loading textview go away
//                imageLoadingProgressBar.setVisibility(View.GONE);
//                imageLoadingTextView.setVisibility(View.GONE);
//
//            } else {
//                Log.d("getView", "getBytes equals null");
//                Log.d("getView", "Let's try to load from the path of the object");
//
//                loadImage(position);
//            }

            Topic.setText(newMaintenanceRequest.getTitle());
            Description.setText(newMaintenanceRequest.getDescription());
            Date.setText(newMaintenanceRequest.getTimeSent());
            Author.setText(newMaintenanceRequest.getAuthor());

            Log.i("getView", "Set the view");

            return view;
        }
    }
}


//    public class CustomerArrayAdapter extends BaseAdapter {
//
//        ArrayList<MaintenanceRequest> arrayList = new ArrayList();
//        LayoutInflater inflater;
//        Context context;
//
//        public CustomerArrayAdapter(Context context,
//                                    ArrayList<MaintenanceRequest> maintenanceRequestsList) {
//            this.arrayList = maintenanceRequestsList;
//            this.context = context;
//            inflater = LayoutInflater.from(context);
//        }
//
//        @Override
//        public int getCount() {
//            return arrayList.size();
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return arrayList.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return 0;
//        }
//
//        public void add(MaintenanceRequest maintenanceRequest) {
//            arrayList.add(maintenanceRequest);
//        }
//
//        public void loadImage(final int index) {
//            final String TAG = "loadImage";
//
//            Log.d(TAG, "opened loadImage()");
//
//            final MaintenanceRequest tempRequest = this.arrayList.get(index);
//
//            imageRef = FirebaseStorage.getInstance().getReference().child("images/" + tempRequest
//                    .getPath());
//
//            imageRef.getBytes(THREE_MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                @Override
//                public void onSuccess(byte[] bytes) {
//                    Log.d(TAG, "We loaded the image, let's set it");
//                    tempRequest.setBytes(bytes);
//
//                    // Add it to the arrayList to the certin index
//                    addBytesToArrayList(bytes, index);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.wtf(TAG, "Failed to load image");
//                    // let's print out the error
//                    e.printStackTrace();
//                }
//            });
//
//            Log.d(TAG, "Leaving loadImage()");
//        }
//
//        public void loadImages() {
//            String TAG = "loadImages";
//
//            Log.d(TAG, "We are in loadImages now");
//
//            Log.d(TAG, "There are " + this.arrayList.size() + " in the array");
//            for (int i = 0; i < this.arrayList.size(); i++) {
//                // This is the path to the current image on firebase
//                final MaintenanceRequest tempRequest = this.arrayList.get(i);
//
//                // get the path on firebase
//                imageRef = FirebaseStorage.getInstance().getReference().child("images/" +
//                        tempRequest.getPath());
//
//                // Let's log the path that we searched to see if it is correct
//                Log.d(TAG, "images/" + tempRequest.getPath());
//
//                // Lets get what is stored in that reference and add on onSuccessListener
//                final int finalI = i;
//                imageRef.getBytes(THREE_MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                    @Override
//                    public void onSuccess(byte[] bytes) {
//                        Log.d("onSuccess", "We loaded image: " + tempRequest.getPath());
//                        Log.d("onSuccess", "Length of byte array " + bytes.length);
//                        tempRequest.setBytes(bytes);
//
//                        Log.d("onSuccess", "setting the arraylist index: " + finalI + " with the new bytes");
//
//                        addBytesToArrayList(bytes, finalI);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.wtf("onFailure", "Unable to load image: " +
//                                tempRequest.getPath());
//                        e.printStackTrace();
//                    }
//                });
//                Log.d(TAG, "Are we going to loop again in loadImages?");
//            }
//
//            Log.d(TAG, "We are leaving loadImages()");
//        }
//
//        // This is to add bytes to specific entries in the arrayList
//        private void addBytesToArrayList(byte[] bytes, int index) {
//            Log.d("addBytesToArrayList", "Adding bytes to index " + index);
//            this.arrayList.get(index).setBytes(bytes);
//
//            notifyDataSetChanged();
//        }
//
//
//        @Override
//        public View getView(int position, View conertView, ViewGroup viewGroup) {
//            // Create a few with an inflater
//            View view = getLayoutInflater().inflate(R.layout.maintence_request, null);
//
//            MaintenanceRequest newMaintenanceRequest = arrayList.get(getCount() - (position + 1));
//
//            Log.i("getView", "Created a new MaintenanceRequest and loaded it");
//
//            // Get the references
//            TextView Topic = view.findViewById(R.id.textViewMaintenanceRequestTitle);
//            TextView Description = view.findViewById(R.id.textViewMaintenanceRequestDescrip);
//            TextView Date = view.findViewById(R.id.textViewMaintenanceViewDate);
//            TextView Author = view.findViewById(R.id.textViewMaintenanceRequestPerson);
//            ImageView Photo = view.findViewById(R.id.imageViewMaintenanceRequestPhoto);
//            ProgressBar imageLoadingProgressBar = view.findViewById(R.id.progressBarImageLoading);
//            TextView imageLoadingTextView = view.findViewById(R.id.textViewImageLoading);
//
//            // See if there is an image to load already.
//            if (newMaintenanceRequest.getBytes() != null) {
//                Log.d("getView", "What is the length of bytes " + newMaintenanceRequest.getBytes().toString());
//
//                Log.i("getView", "Got all the references for the objects in the .xml");
//
//                Bitmap bity = BitmapFactory.decodeByteArray(newMaintenanceRequest.getBytes(), 0, newMaintenanceRequest.getBytes().length);
//
//                Log.i("getView", "Setting the new photo");
//
//                // Set the image to the imageView
//                Photo.setImageBitmap(bity);
//                Photo.setVisibility(View.VISIBLE);
//
//                // Set the loading bar and image loading textview go away
//                imageLoadingProgressBar.setVisibility(View.GONE);
//                imageLoadingTextView.setVisibility(View.GONE);
//
//            } else {
//                Log.d("getView", "getBytes equals null");
//                Log.d("getView", "Let's try to load from the path of the object");
//
//                loadImage(position);
//            }
//
//            Topic.setText(newMaintenanceRequest.getTitle());
//            Description.setText(newMaintenanceRequest.getDescription());
//            Date.setText(newMaintenanceRequest.getTimeSent());
//            Author.setText(newMaintenanceRequest.getAuthor());
//
//            Log.i("getView", "Set the view");
//
//            return view;
//        }
//    }
//}
