package com.example.jonathanashcraft.pinnacleconnection;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class CreateProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText mPassword;
    private EditText mLastName;
    private EditText mFirstName;
    private EditText mAptNumber;
    private EditText mEmail;
    private Button bVerifyManager;
    private String managerPassword;
    private Boolean isUserManager;
    FirebaseUser currentUser;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String TAG = "onCreate";

        super.onCreate(savedInstanceState);

        // Set to false for default
        isUserManager = false;

        // TODO: get the manager in a different way from hardcoding it
        // Was trying to access firebase but realized that the rules for our database in firebase
        // is that the user needs to be authenticated (in the system) and at this point, when they
        // are creating a profile, they are not authenticated.
        managerPassword = "12345";

        setContentView(R.layout.activity_create_profile);

        // Get the current instance of the firebase database
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Set the EditTexts
        mPassword = findViewById(R.id.editTextPassword);
        mLastName = findViewById(R.id.editTextLastName);
        mFirstName = findViewById(R.id.editTextFirstName);
        mAptNumber = findViewById(R.id.editTextAptNumber);
        mEmail = findViewById(R.id.editTextEmail);
        bVerifyManager = findViewById(R.id.buttonVerifyManager);

        // TODO: Create dialog boxes to prompt for a manager password to validate a user as a manager
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateProfile.this);

        // Create a view so that the dialog box has an input

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Enter Manager Password");

        // Build the dialog box
        builder.setTitle("Manager Password")
                .setView(input)
                .setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(validateManager(input.getText().toString())) {
                            isUserManager = true;
                        } else {
                            isUserManager = false;
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        // Create the dialog box we just set up
        final AlertDialog managerPasswordDialog = builder.create();

        // Lets add an onClickListener
        bVerifyManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                managerPasswordDialog.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private boolean validateManager(String attemptedManagerPassword) {
        final String TAG = "validateManager";

        if(Objects.equals(attemptedManagerPassword, managerPassword)) {
            Log.d(TAG, "The attempted password matched the managers password");
            return true;
        } else {
            Log.d(TAG, "The passwords did not match!");
            Log.d(TAG, "Your password: " + attemptedManagerPassword);
            Log.d(TAG, "Actual manager password: " + managerPassword);
            return false;
        }
    }

    public void onButtonCreateProfile(View view) {
        final String TAG = "onButtonCreateProfile";

        String Password = mPassword.getText().toString();
        String Email = mEmail.getText().toString();

        Log.d(TAG, "Creating profile");

        // Make an entry in the real time database with all the users information
        final DatabaseReference mUserRef = database.getReference("Users");

        final String FirstName = mFirstName.getText().toString();
        final String LastName = mLastName.getText().toString();
        final String apartmentNumber = mAptNumber.getText().toString();

        Log.d(TAG, "************************************************************************");
        Log.d(TAG, "Is this running?");
        // Create Profile
        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail: Successful");
                            currentUser = mAuth.getCurrentUser();

                            // Create a temp user to slap into the database
                            User tempUser = new User();
                            tempUser.setFirstName(FirstName);
                            tempUser.setLastName(LastName);
                            tempUser.setApartmentNumber(apartmentNumber);
                            tempUser.setManager(isUserManager);

                                    // Slap into the database
                            mUserRef.child(currentUser.getUid()).setValue(tempUser);

                            Toast.makeText(CreateProfile.this, "Created Profile",
                                    Toast.LENGTH_SHORT).show();

                            finish();
                        } else {
                            Log.d(TAG, "createUserWithEmail: Failed", task.getException());
                            Toast.makeText(CreateProfile.this, "Profile Creation failed, Try again later",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }); // end .addOnCompleteListener


        Log.d(TAG, "Closing the method onButtonCreateProfile()");

        Toast.makeText(CreateProfile.this, "Creating Profile...",
                Toast.LENGTH_SHORT).show();
    }
}
