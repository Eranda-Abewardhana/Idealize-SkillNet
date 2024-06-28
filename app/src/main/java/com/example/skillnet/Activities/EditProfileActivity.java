package com.example.skillnet.Activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etPhone, etFb, etInsta, etLinkedin, etTwitter, etLocation, bio, etPassword;
    private TextView etEmail, etNewPassword;
    private Button btnSave;
    private ImageButton btnBack, btnEditProfilePic, btnAddPicture;
    private ImageView editName, editPassword, editBio, editNumber, editEmail, editFb, editInsta, editLinkedin, editTwitter, editLocation;
    private de.hdodenhof.circleimageview.CircleImageView profileImage;
    private LinearLayout addPictures;
    private CardView serviceDetailsCard;
    private FirebaseAuth mAuth;
    private StorageReference storageRef;
    private LinearLayout newPassword;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Initialize Views
        newPassword = findViewById(R.id.newPass);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etFb = findViewById(R.id.et_fb);
        etInsta = findViewById(R.id.et_insta);
        etLinkedin = findViewById(R.id.et_linkedin);
        etTwitter = findViewById(R.id.et_twitter);
        etLocation = findViewById(R.id.et_location);
        bio = findViewById(R.id.bio);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);
        profileImage = findViewById(R.id.profile_image);
        btnEditProfilePic = findViewById(R.id.btn_edit_profile_image);
        serviceDetailsCard = findViewById(R.id.service_details_card);
        addPictures = findViewById(R.id.add_pictures);
        btnAddPicture = findViewById(R.id.btn_add_picture);
        editName = findViewById(R.id.edit_name);
        editPassword = findViewById(R.id.edit_password);
        editBio = findViewById(R.id.edit_bio);
        editNumber = findViewById(R.id.edit_number);
        editEmail = findViewById(R.id.edit_email);
        editFb = findViewById(R.id.edit_fb);
        editInsta = findViewById(R.id.edit_insta);
        editLinkedin = findViewById(R.id.edit_linkedin);
        editTwitter = findViewById(R.id.edit_twitter);
        editLocation = findViewById(R.id.edit_location);

        // Use data from GlobalVariables
        etName.setText(GlobalVariables.person.getName());
        etPhone.setText(GlobalVariables.person.getPhone());
        etEmail.setText(mAuth.getCurrentUser().getEmail());
        bio.setText(GlobalVariables.person.getBio());
        etFb.setText(GlobalVariables.person.getFb());
        etInsta.setText(GlobalVariables.person.getInsta());
        etLinkedin.setText(GlobalVariables.person.getLinkedin());
        etLocation.setText(GlobalVariables.person.getLocation());
        etTwitter.setText(GlobalVariables.person.getTwitter());

        String imageUrl = GlobalVariables.person.getImageUrl();
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
        boolean isWorker = GlobalVariables.isWorker;
        if (isWorker) {
            serviceDetailsCard.setVisibility(View.VISIBLE);
            addPictures.setVisibility(View.VISIBLE);
        } else {
            serviceDetailsCard.setVisibility(View.GONE);
            addPictures.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(v -> {
            // Retrieve values from EditText fields
            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();
            String email = etEmail.getText().toString().toLowerCase();
            String bioData = bio.getText().toString();
            String fb = etFb.getText().toString();
            String insta = etInsta.getText().toString();
            String linkedin = etLinkedin.getText().toString();
            String location = etLocation.getText().toString();
            String twitter = etTwitter.getText().toString();

            if(phone != null || phone.isEmpty() || name != null || name.isEmpty() ){
                Toast.makeText(EditProfileActivity.this, "Profile updated Failed Name and Phone NUmber Cannot Empty", Toast.LENGTH_SHORT).show();
                return;
            }
            // Update Firestore document with edited fields
            DocumentReference documentReference = fStore.collection("users").document(GlobalVariables.code);
            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("email", email);
            user.put("bio", bioData);
            user.put("fb", fb);
            user.put("insta", insta);
            user.put("linkedin", linkedin);
            user.put("location", location);
            user.put("twitter", twitter);
            DocumentReference documentReference2 = fStore.collection("users_signup").document(email);
            Map<String, Object> user2 = new HashMap<>();
            user2.put("PhoneNumber", phone);
            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                 documentReference2.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void unused) {
                         Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                         intent.putExtra("navigateToProfile", true);
                         startActivity(intent);
                         Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Toast.makeText(EditProfileActivity.this, "Profile updated Failed", Toast.LENGTH_SHORT).show();
                     }
                 });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, "Profile updated Failed", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });


        // Image selection button click listener
        btnEditProfilePic.setOnClickListener(v -> {
            // Open gallery to select an image
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Set OnClickListeners for edit icons to toggle EditText enabled state
        setToggleEditListener(editName, etName);
        setToggleEditListener(editPassword, etPassword);
        setToggleEditListener(editBio, bio);
        setToggleEditListener(editNumber, etPhone);
//        setToggleEditListener(editEmail, etEmail);
        setToggleEditListener(editFb, etFb);
        setToggleEditListener(editInsta, etInsta);
        setToggleEditListener(editLinkedin, etLinkedin);
        setToggleEditListener(editTwitter, etTwitter);
        setToggleEditListener(editLocation, etLocation);
    }

    private void setToggleEditListener(ImageView editIcon, EditText editText) {
        editIcon.setOnClickListener(v -> {

            // Check if the editIcon corresponds to the password field
            if(editIcon.getId() != R.id.edit_password) {
                boolean isEnabled = editText.isEnabled();
                editText.setEnabled(!isEnabled);
//                etNewPassword.setEnabled(!isEnabled);
//                if(newPassword != null) {
//                    newPassword.setVisibility(isEnabled ? View.GONE : View.VISIBLE);
//                }
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

                                // Update the imageUrl field in GlobalVariables
                                GlobalVariables.person.setImageUrl(downloadUrl);

                                // You may also want to update Firestore here if needed
                                // db.collection("users").document(GlobalVariables.person.getId()).update("imageUrl", downloadUrl);

                                Toast.makeText(EditProfileActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
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
