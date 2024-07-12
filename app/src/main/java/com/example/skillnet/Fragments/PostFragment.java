package com.example.skillnet.Fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.skillnet.FirebaseHelper.Firebase;
import com.example.skillnet.FirebaseHelper.FirebaseCallback;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.Post;
import com.example.skillnet.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private Firebase firebase;
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
    private static final int PERMISSION_REQUEST_CODE = 100;
    private Uri selectedImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        if (!checkPermission()) {
            requestPermission();
        }

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
        firebase = new Firebase(); // Initialize Firebase instance

        // Set up listeners
        backButton.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        addImageButton.setOnClickListener(v -> {
            // Open gallery to select an image
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        setupPostButton();
        setupCategorySpinner();

        return view;
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int resultRead = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && resultRead == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void setupPostButton() {
        firebase.getAllPosts(new FirebaseCallback<Post>() {
            @Override
            public void onCallback(List<Post> list) {
                int maxNumber = 0;

                for (Post post : list) {
                    String postCode = post.getPostCode();
                    int number = Integer.parseInt(postCode.replace("POST", ""));

                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                }
                maxNumber++;
                String buffer = "";
                if (maxNumber < 10) {
                    buffer = "000";
                } else if (10 <= maxNumber && maxNumber < 100) {
                    buffer = "00";
                } else if (100 <= maxNumber && maxNumber < 1000) {
                    buffer = "0";
                } else if (1000 <= maxNumber && maxNumber < 10000) {
                    buffer = "";
                } else {
                    Toast.makeText(getContext(), "Post limit reached", Toast.LENGTH_SHORT).show();
                    return;
                }

                String newPostCode = "POST" + buffer + maxNumber;

                postButton.setOnClickListener(v -> {
                    // Validate inputs
                    if (validateInputs()) {
                        // Get values from inputs
                        String topic = topicEditText.getText().toString();
                        String description = descriptionEditText.getText().toString();
                        String location = locationEditText.getText().toString();
                        double price = Double.parseDouble(priceEditText.getText().toString());
                        String categoryCode = "";

                        for (Categories categories : GlobalVariables.categoriesList) {
                            if (categories.getName().equals(selectedCategory)) {
                                categoryCode = categories.getCode();
                                break;
                            }
                        }

                        // Create a SimpleDateFormat instance with the desired format
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date now = new Date();
                        String formattedTime = sdf.format(now);

                        // Create a map to store data
                        Map<String, Object> post = new HashMap<>();
                        post.put("postCode", newPostCode);
                        post.put("categoryCode", categoryCode);
                        post.put("description", description);
                        post.put("dateTime", formattedTime);
                        post.put("findWorker", !GlobalVariables.isWorker);
                        post.put("price", price);
                        post.put("location", location);
                        post.put("mobileNo", GlobalVariables.person.getPhone());
                        post.put("title", topic);
                        post.put("userCode", GlobalVariables.code);

                        // Check if an image is selected
                        if (!TextUtils.isEmpty(downloadUrl)) {
                            post.put("imageUrl", downloadUrl);
                            savePostToFirestore(post, newPostCode);
                        } else {
                            uploadImageAndSavePost(post, newPostCode);
                        }
                    }
                });
            }

            @Override
            public void onDocumentSnapshotCallback(DocumentSnapshot snapshot) {
                // Handle document snapshot if needed
            }

            @Override
            public void onSingleCallback(Post item) {
                // Handle single post callback if needed
            }
        });
    }

    private void uploadImageAndSavePost(Map<String, Object> post, String newPostCode) {
        if (selectedImageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
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
                                post.put("imageUrl", downloadUrl);
                                savePostToFirestore(post, newPostCode);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
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
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePostToFirestore(Map<String, Object> post, String newPostCode) {
        DocumentReference documentReference = fStore.collection("posts").document(newPostCode);
        documentReference.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(), "Post Created Successfully", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to create post", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        // Validate all inputs (topic, description, location, price)
        // Return true if valid, otherwise show appropriate Toast messages and return false
        // Add your validation logic here
        return true;
    }

    private void setupCategorySpinner() {
        // Set up category spinner logic
        List<String> categories = new ArrayList<>();
        for (Categories category : GlobalVariables.categoriesList) {
            categories.add(category.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "";
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            // Optionally, display the selected image in an ImageView
            Glide.with(this).load(selectedImageUri).into(addImageButton); // Example using Glide to load image
        }
    }
}
