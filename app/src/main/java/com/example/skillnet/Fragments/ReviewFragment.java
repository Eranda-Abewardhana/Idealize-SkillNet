package com.example.skillnet.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;
import com.example.skillnet.R;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.fragment.app.Fragment;


public class ReviewFragment extends Fragment {

    private ImageView backButton;
    private EditText workerNameEditText;
    private EditText reviewEditText;
    private RatingBar ratingBar;
    private Button postButton;
    private ImageButton addImageButton;
    private FirebaseFirestore fStore;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        // Initialize the views
        backButton = view.findViewById(R.id.imageView2);
        workerNameEditText = view.findViewById(R.id.worker_name);
        reviewEditText = view.findViewById(R.id.review_text);
        ratingBar = view.findViewById(R.id.rating_bar);
        postButton = view.findViewById(R.id.button);
        addImageButton = view.findViewById(R.id.add_image_button);
        fStore = FirebaseFirestore.getInstance();

        // Set up any required listeners here
        backButton.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        addImageButton.setOnClickListener(v -> {
            // Handle add image button click
        });

        postButton.setOnClickListener(v -> {
            String workerName = workerNameEditText.getText().toString().trim();
            String reviewText = reviewEditText.getText().toString().trim();
            float rating = ratingBar.getRating();

            if (workerName.isEmpty() && reviewText.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter both worker's name and review", Toast.LENGTH_SHORT).show();
                return;
            }

            // Here you can add logic to save the review, such as sending it to a server or saving it locally
            Toast.makeText(getActivity(), "Review submitted:\nWorker: " + workerName + "\nRating: " + rating + "\nReview: " + reviewText, Toast.LENGTH_LONG).show();

            // Optionally clear the fields after submission
            workerNameEditText.setText("");
            reviewEditText.setText("");
            ratingBar.setRating(0);
        });

        return view;
    }

}