package com.example.skillnet.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReviewFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView backButton;
    private EditText title;
    private EditText reviewEditText;
    private RatingBar ratingBar;
    private Button postButton;
    private ImageButton addImageButton;
    private FirebaseFirestore fStore;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri selectedImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        // Initialize views
        backButton = view.findViewById(R.id.imageView2);
        title = view.findViewById(R.id.topic);
        reviewEditText = view.findViewById(R.id.review_text);
        ratingBar = view.findViewById(R.id.rating_bar);
        postButton = view.findViewById(R.id.button);
        addImageButton = view.findViewById(R.id.add_image_button);
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Set up click listeners
        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        addImageButton.setOnClickListener(v -> openGallery());

        postButton.setOnClickListener(v -> {
            String topic = title.getText().toString().trim();
            String reviewText = reviewEditText.getText().toString().trim();
            float rating = ratingBar.getRating();

            if (validateInputs(topic, reviewText, rating)) {
                if (selectedImageUri != null) {
                    uploadImageAndPostReview(topic, reviewText, rating);
                } else {
                    Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private boolean validateInputs(String workerName, String reviewText, float rating) {
        if (workerName.isEmpty()) {
            title.setError("Worker's name is required");
            return false;
        }
        if (reviewText.isEmpty()) {
            reviewEditText.setError("Review text is required");
            return false;
        }
        if (rating == 0) {
            Toast.makeText(getActivity(), "Rating is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadImageAndPostReview(String title, String reviewText, float rating) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // Adjust compression level as needed
            byte[] imageData = baos.toByteArray();

            StorageReference imageRef = storageRef.child("review_images/" + System.currentTimeMillis() + ".jpg");

            UploadTask uploadTask = imageRef.putBytes(imageData);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    saveReviewToFirestore(GlobalVariables.otherPersonData.getpCode(), reviewText, rating, downloadUrl, title);
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Failed to process image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveReviewToFirestore(String clientCode, String reviewText, float rating, String imageUrl, String title) {
        // Create a SimpleDateFormat instance with the desired format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.getDefault());

        // Get the current date and time
        Date now = new Date();

        // Format the current time as a string
        String formattedTime = sdf.format(now);
        Map<String, Object> review = new HashMap<>();
        review.put("clientCode", clientCode);
        review.put("description", reviewText);
        review.put("stars", rating);
        review.put("imageUrl", imageUrl);
        review.put("dateTime", formattedTime);
        review.put("review", true);
        review.put("title", title);

        DocumentReference documentReference = fStore.collection("reviews")
                .document(GlobalVariables.code)
                .collection("worker_reviews")
                .document();

        documentReference.set(review)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Review submitted successfully", Toast.LENGTH_SHORT).show();
                    clearInputs();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to submit review", Toast.LENGTH_SHORT).show();
                });
    }

    private void clearInputs() {
        title.setText("");
        reviewEditText.setText("");
        ratingBar.setRating(0);
        addImageButton.setImageResource(R.drawable.ic_plus); // Reset image button to default icon
        selectedImageUri = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            Glide.with(this).load(selectedImageUri).into(addImageButton);
        }
    }
}
