package com.example.jonathanashcraft.pinnacleconnection;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText mPassword;
    private EditText mLastName;
    private EditText mFirstName;
    private EditText mAptNumber;
    private EditText mEmail;
    FirebaseUser currentUser;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    public void onButtonCreateProfile(View view) {
        final String TAG = "onButtonCreateProfile";

        String Password = mPassword.getText().toString();
        String Email = mEmail.getText().toString();

        // Create Profile
        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail: Sucecssful");
                            currentUser = mAuth.getCurrentUser();
                        } else {
                            Log.d(TAG, "createUserWithEmail: Failed", task.getException());
                            Toast.makeText(CreateProfile.this, "Authenication Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }); // end .addOnCompleteListener

        if(currentUser != null) {
            Log.d(TAG, "Creating profile");

            // Make an entry in the real time database with all the users information
            DatabaseReference userRef = database.getReference("Users");

            String FirstName = mFirstName.getText().toString();
            String LastName = mLastName.getText().toString();
            String apartmentNumber = mAptNumber.getText().toString();

            // Create a temp user to slap into the database
            User tempUser = new User(FirstName, LastName, apartmentNumber, false);

            // Slap into the database
            userRef.child(currentUser.getUid()).setValue(tempUser);

            Toast.makeText(CreateProfile.this, "Created Profile",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
