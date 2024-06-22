package com.example.skillnet.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.skillnet.Activities.SettingsActivity;
import com.example.skillnet.Activities.EditProfileActivity;
import com.example.skillnet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private TextView tvName;
    private ImageView profileImage;
    private Button btnEditProfile, btnSettings, btnPostProject;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private DocumentReference userDocRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvName = view.findViewById(R.id.name);
        profileImage = view.findViewById(R.id.profile_picture);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnPostProject = view.findViewById(R.id.btn_post_project);
        btnSettings = view.findViewById(R.id.btn_settings);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the current user's email
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();

            // Search for the user in users_signup collection using the email as document ID
            userDocRef = db.collection("users_signup").document(email);

            // Fetch user details from Firestore
            userDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve name and profile picture URL from Firestore
                        String name = document.getString("fName");
                        String imageUrl = document.getString("imageUrl");

                        // Set the retrieved name to the TextView
                        tvName.setText(name);

                        // Load profile picture using Glide
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.profile)
                                    .error(R.drawable.profile)
                                    .into(profileImage);
                        } else {
                            // Handle case where profile image URL is null or empty
                            profileImage.setImageResource(R.drawable.profile);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch document", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Set OnClickListener for the Edit Profile button
        btnEditProfile.setOnClickListener(v -> {
            // Launch the EditProfileActivity
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        // Set OnClickListener for the Settings button
        btnSettings.setOnClickListener(v -> {
            // Launch the SettingsActivity
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });
    }
}
