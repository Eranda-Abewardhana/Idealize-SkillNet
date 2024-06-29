package com.example.skillnet.Activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.skillnet.FirebaseHelper.Firebase;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.PersonData;
import com.example.skillnet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etPhone, etFb, etInsta, etLinkedin, etTwitter, etLocation, bio, etPassword,etWeb;
    private TextView etEmail, etNewPassword, categoriesSpinner;
    private Button btnSave;
    private ImageButton btnBack, btnEditProfilePic, btnAddPicture;
    private ImageView editName, editPassword, editBio, editNumber, editEmail, editFb, editInsta, editLinkedin, editTwitter, editLocation, editWebsite;
    private de.hdodenhof.circleimageview.CircleImageView profileImage;
    private LinearLayout addPictures;
    private CardView serviceDetailsCard;
    private FirebaseAuth mAuth;
    private StorageReference storageRef;
    private FirebaseFirestore fStore;
    private LinearLayout newPassword;
    boolean[] selectedLanguage;
    ArrayList<Integer> categories = new ArrayList<>();
    List<String> categoryList = new ArrayList<>();
    List<String> categoryCodeList = new ArrayList<>();
    String[] categoryArray = {};
    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        firebase = new Firebase();

        // Initialize Views
        categoriesSpinner = findViewById(R.id.spinner);
        newPassword = findViewById(R.id.newPass);
        etWeb = findViewById(R.id.et_web);
        editWebsite = findViewById(R.id.edit_web);
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
        etWeb.setText(GlobalVariables.person.getWebsite());

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
        // initialize selected language array
        selectedLanguage = new boolean[GlobalVariables.categoriesList.size()];
        for(Categories categories : GlobalVariables.categoriesList){
            categoryList.add(categories.getName());
        }
        categoryArray = categoryList.toArray(new String[0]);
        categoriesSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);

                // Set title
                builder.setTitle("Select Categories");

                // Set dialog non-cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(categoryArray, selectedLanguage, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
                        // Add or remove the selected category index
                        if (isChecked) {
                            categories.add(i);
                        } else {
                            categories.remove(Integer.valueOf(i));
                        }
                        // Sort the categories list
                        Collections.sort(categories);
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        categoryCodeList.clear();

                        // Create a map for category name to code lookup
                        Map<String, String> categoryMap = new HashMap<>();
                        for (Categories category : GlobalVariables.categoriesList) {
                            categoryMap.put(category.getName(), category.getCode());
                        }

                        // Build the selected categories string and populate categoryCodeList
                        for (int index : categories) {
                            String categoryName = categoryArray[index];
                            stringBuilder.append(categoryName);
                            String categoryCode = categoryMap.get(categoryName);
                            if (categoryCode != null) {
                                categoryCodeList.add(categoryCode);
                            }
                            stringBuilder.append(", ");
                        }

                        // Remove the trailing comma and space if necessary
                        if (stringBuilder.length() > 0) {
                            stringBuilder.setLength(stringBuilder.length() - 2);
                        }

                        // Set text on categoriesSpinner
                        categoriesSpinner.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Arrays.fill(selectedLanguage, false);
                        categories.clear();
                        categoriesSpinner.setText("");
                    }
                });

                // Show dialog
                builder.show();
            }
        });


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
            String website = etWeb.getText().toString();

            if(phone == null || phone.isEmpty() || name == null || name.isEmpty() ){
                Toast.makeText(EditProfileActivity.this, "Profile updated Failed Name and Phone Number Cannot Empty", Toast.LENGTH_SHORT).show();
                return;
            }
            Set<String> categoryCodeSet = new HashSet<>(categoryCodeList);
            String globalCode = GlobalVariables.code;

            // First, remove the global code from all personDataLists
            for (Categories category : GlobalVariables.categoriesList) {
                category.getPersonDataList().remove(globalCode);
            }

            // Then, add the global code to the personDataLists where the category code matches
            for (Categories category : GlobalVariables.categoriesList) {
                if (categoryCodeSet.contains(category.getCode())) {
                    if (!category.getPersonDataList().contains(globalCode)) {
                        category.getPersonDataList().add(globalCode);
                    }
                }
            }

            // Update Firestore document with edited fields
            DocumentReference documentReference = fStore.collection("users").document(GlobalVariables.code);
            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("isworker", GlobalVariables.isWorker);
            user.put("pCode", GlobalVariables.code);
            user.put("bio", bioData);
            user.put("fb", fb);
            user.put("insta", insta);
            user.put("linkedin", linkedin);
            user.put("location", location);
            user.put("twitter", twitter);
            user.put("website", website);
            user.put("imageUrl", GlobalVariables.person.getImageUrl());


            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    DocumentReference documentReference2 = fStore.collection("users_signup").document(email);
                    Map<String, Object> user2 = new HashMap<>();

                    user2.put("PhoneNumber", phone);
                    user2.put("email", email);
                    user2.put("fName", name);
                    user2.put("user", GlobalVariables.code);
                 documentReference2.set(user2).addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void unused) {
                         GlobalVariables.person.setPhone(phone);
                         firebase.updateCategories(GlobalVariables.categoriesList);
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
        setToggleEditListener(editWebsite, etWeb);
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
                                GlobalVariables.person.setImageUrl(downloadUrl);

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
