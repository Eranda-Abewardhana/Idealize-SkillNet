package com.example.skillnet.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.skillnet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etPhone, etEmail;
    private Button btnSave;
    private ImageButton btnBack, btnEditProfilePic;
    private ImageView profileImage;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private DocumentReference userDocRef;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri; // Store selected image Uri

    // Firebase Storage
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase Auth, Firestore, and Storage
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Initialize views
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);
        profileImage = findViewById(R.id.profile_image);
        btnEditProfilePic = findViewById(R.id.btn_edit_profile_image);

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

                        // Get imageUrl from the user document
                        String imageUrl = document.getString("imageUrl");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            // Load image using Glide
                            Glide.with(EditProfileActivity.this)
                                    .load(imageUrl) // Image URL
                                    .placeholder(R.drawable.prof_placeholder) // Placeholder image
                                    .error(R.drawable.profile) // Error image if loading fails
                                    .into(profileImage); // ImageView to load into
                        } else {
                            // Handle case where imageUrl is null or empty
                            profileImage.setImageResource(R.drawable.profile);
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

        // Save button click listener
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

        // Back button click listener
        btnBack.setOnClickListener(v -> {
            // Navigate back to previous activity (MainActivity in this example)
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });

        // Edit profile picture button click listener
        btnEditProfilePic.setOnClickListener(v -> {
            // Open gallery to select an image
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Load selected image into profileImage using Glide
            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.prof_placeholder)
                    .error(R.drawable.profile)
                    .into(profileImage);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if imageUri is not null and update Firestore with the new image URL
        if (imageUri != null) {
            uploadImageToFirebaseStorage(); // Implement this method to upload image to Firebase Storage
        }
    }

    private void uploadImageToFirebaseStorage() {
        // Generate a random UUID for the image name
        String imageName = UUID.randomUUID().toString();

        // Create a reference to 'images/[imageName].jpg'
        StorageReference imageRef = storageRef.child("images/" + imageName + ".jpg");

        // Upload image to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Update imageUrl in Firestore document
                        userDocRef.update("imageUrl", uri.toString())
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(EditProfileActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(EditProfileActivity.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }
}
