package com.example.skillnet.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etPhone, etEmail;
    private Button btnSave;
    private ImageButton btnBack, btnEditProfilePic;
    private ImageView profileImage;
    private CardView serviceDetailsCard;
    private LinearLayout addPictures; // Changed from ConstraintSet.Layout to LinearLayout

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private DocumentReference userDocRef, userDocRef2;
    private StorageReference storageRef;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);
        profileImage = findViewById(R.id.profile_image);
        btnEditProfilePic = findViewById(R.id.btn_edit_profile_image);
        serviceDetailsCard = findViewById(R.id.service_details_card);
        addPictures = findViewById(R.id.add_pictures); // Initialize addPictures

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
                            userDocRef2 = db.collection("users").document(GlobalVariables.code);
                            userDocRef2.get().addOnCompleteListener(userTask -> {
                                if (userTask.isSuccessful()) {
                                    DocumentSnapshot userDoc = userTask.getResult();
                                    if (userDoc.exists()) {
                                        // Get imageUrl from the user document
                                        String imageUrl = userDoc.getString("imageUrl");
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

                                        // Check if the user is a worker
                                        boolean isWorker = userDoc.getBoolean("isworker");
                                        if (isWorker) {
                                            serviceDetailsCard.setVisibility(View.VISIBLE);
                                            addPictures.setVisibility(View.VISIBLE);
                                        } else {
                                            serviceDetailsCard.setVisibility(View.GONE);
                                            addPictures.setVisibility(View.GONE);
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
                // Create an intent to navigate back to MainActivity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // Add an extra indicating that we want to navigate to ProfileFragment
                intent.putExtra("navigateToProfile", true);
                startActivity(intent);
            }
        });

        // Image selection button click listener
        btnEditProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open gallery to select an image
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Compress the image
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // Adjust compression level as needed
                byte[] imageData = baos.toByteArray();

                // Define the storage reference
                StorageReference imageRef = storageRef.child("profile_images/" + mAuth.getCurrentUser().getUid() + ".jpg");

                // Upload the image to Firebase Storage
                UploadTask uploadTask = imageRef.putBytes(imageData);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();

                                // Load the image using Glide
                                Glide.with(EditProfileActivity.this)
                                        .load(downloadUrl)
                                        .placeholder(R.drawable.prof_placeholder)
                                        .error(R.drawable.profile)
                                        .into(profileImage);

                                // Update the imageUrl field in Firestore
                                userDocRef2.update("imageUrl", downloadUrl)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(EditProfileActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EditProfileActivity.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to compress image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
