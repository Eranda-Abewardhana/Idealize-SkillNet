package com.example.skillnet.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.skillnet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etPhone, etEmail;
    private Button btnSave;
    private ImageButton btnBack;
    private ImageView profileImage;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private DocumentReference userDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);
        profileImage = findViewById(R.id.profile_image);

        // Get the current user's email
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();

            // Search for the user in users_signup collection using the email as document ID
            userDocRef = db.collection("users_signup").document(email);

            // Fetch and set user details from Firestore
            userDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        etName.setText(document.getString("fName"));
                        etPhone.setText(document.getString("PhoneNumber"));
                        etEmail.setText(document.getString("email"));

                        // Get the user field value
                        String user = document.getString("user");

                        // Access the users collection and fetch the document using 'user' as document ID
                        if (user != null) {
                            DocumentReference userRef = db.collection("users").document(user);
                            userRef.get().addOnCompleteListener(userTask -> {
                                if (userTask.isSuccessful()) {
                                    DocumentSnapshot userDoc = userTask.getResult();
                                    if (userDoc.exists()) {
                                        // Get imageUrl from the user document and set it to profileImage
                                        String imageUrl = userDoc.getString("imageUrl");
                                        if (imageUrl != null && !imageUrl.isEmpty()) {
                                            // Load image using your preferred image loading library or method
                                            // For simplicity, assuming it's a URL and using Glide:
                                            // Glide.with(this).load(imageUrl).into(profileImage);
                                            // However, since we're using a drawable placeholder in XML, let's set it directly
                                            profileImage.setImageResource(R.drawable.person);
                                        }
                                    } else {
                                        // Handle case where user document doesn't exist
                                        Toast.makeText(EditProfileActivity.this, "User document does not exist", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Handle task failure when fetching user document
                                    Toast.makeText(EditProfileActivity.this, "Failed to fetch user document", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Handle case where 'user' field is null or empty
                            Toast.makeText(EditProfileActivity.this, "'user' field is null or empty", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle case where document doesn't exist in users_signup collection
                        Toast.makeText(EditProfileActivity.this, "Document does not exist in users_signup collection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle task failure when fetching document from users_signup collection
                    Toast.makeText(EditProfileActivity.this, "Failed to fetch data from users_signup collection", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle the case where there is no authenticated user
            Toast.makeText(EditProfileActivity.this, "No authenticated user", Toast.LENGTH_SHORT).show();
        }

        btnSave.setOnClickListener(v -> {
            // Update Firestore document with edited fields
            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();
            String email = etEmail.getText().toString();

            // Update fields in Firestore
            userDocRef.update("fName", name,
                            "PhoneNumber", phone,
                            "email", email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to previous activity (MainActivity in this example)
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
