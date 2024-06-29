package com.example.skillnet.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.skillnet.Activities.EditProfileActivity;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.R;
import com.example.skillnet.Models.Categories;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PostFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView backButton;
    private FirebaseAuth mAuth;
    private StorageReference storageRef;
    private TextView titleTextView;
    private EditText topicEditText;
    private EditText descriptionEditText;
    private Spinner categorySpinner;
    private EditText locationEditText;
    private EditText priceEditText;
    private ImageButton addImageButton;
    private Button postButton;
    private String selectedCategory = "";
    private FirebaseFirestore fStore;
    private String downloadUrl = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        // Initialize views
        backButton = view.findViewById(R.id.imageView2);
        titleTextView = view.findViewById(R.id.title);
        topicEditText = view.findViewById(R.id.topic);
        descriptionEditText = view.findViewById(R.id.description);
        categorySpinner = view.findViewById(R.id.category_spinner);
        locationEditText = view.findViewById(R.id.location);
        priceEditText = view.findViewById(R.id.price);
        addImageButton = view.findViewById(R.id.add_image_button);
        postButton = view.findViewById(R.id.button);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Set up any required listeners here
        backButton.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        addImageButton.setOnClickListener(v -> {
            // Open gallery to select an image
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        postButton.setOnClickListener(v -> {
            // Validate inputs
            if (validateInputs()) {
                // Get values from inputs
                String topic = topicEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String location = locationEditText.getText().toString();
                double price = Double.parseDouble(priceEditText.getText().toString());
                String categoryCode = "";

                for(Categories categories : GlobalVariables.categoriesList){
                    if(categories.getName().equals(selectedCategory)){
                        categoryCode = categories.getCode();
                        break;
                    }
                }
                // Create a SimpleDateFormat instance with the desired format
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.getDefault());

                // Get the current date and time
                Date now = new Date();

                // Format the current time as a string
                String formattedTime = sdf.format(now);
                // Create a map to store data
                Map<String, Object> post = new HashMap<>();
                post.put("categoryCode", categoryCode);
                post.put("description", description);
                post.put("dateTime", formattedTime);
                post.put("findWorker", !GlobalVariables.isWorker);
                post.put("price", price);
                post.put("imageUrl", downloadUrl);
                post.put("location", location);
                post.put("mobileNo", GlobalVariables.person.getPhone());
                post.put("title", topic);
                post.put("userCode", GlobalVariables.code);

                // Save data to Firestore
                DocumentReference documentReference = fStore.collection("posts").document();
                documentReference.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Post Created Successfully", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    }
                });
            }
        });

        // Setup category spinner
        setupCategorySpinner();

        return view;
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(topicEditText.getText().toString())) {
            topicEditText.setError("Topic is required");
            return false;
        }
        if (TextUtils.isEmpty(descriptionEditText.getText().toString())) {
            descriptionEditText.setError("Description is required");
            return false;
        }
        if (TextUtils.isEmpty(locationEditText.getText().toString())) {
            locationEditText.setError("Location is required");
            return false;
        }
        if (TextUtils.isEmpty(priceEditText.getText().toString())) {
            priceEditText.setError("Price is required");
            return false;
        }
        if (TextUtils.isEmpty(selectedCategory) || selectedCategory.equals("") || selectedCategory.equals("Select a Category")) {
            Toast.makeText(getContext(), "Category is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setupCategorySpinner() {
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("Select a Category");
        for (Categories category : GlobalVariables.categoriesList) {
            categoryNames.add(category.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle the selected category if needed
                selectedCategory = categoryNames.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when no category is selected
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Compress the image
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // Adjust compression level as needed
                byte[] imageData = baos.toByteArray();

                // Define the storage reference
                StorageReference imageRef = storageRef.child("post_images/" + mAuth.getCurrentUser().getUid() + ".jpg");

                // Upload the image to Firebase Storage
                UploadTask uploadTask = imageRef.putBytes(imageData);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadUrl = uri.toString();
                                Toast.makeText(getContext(), "Image updated", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to compress image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
