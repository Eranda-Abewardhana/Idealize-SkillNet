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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skillnet.Activities.SettingsActivity;
import com.example.skillnet.Activities.EditProfileActivity;
import com.example.skillnet.Adapters.GridAdapter;
import com.example.skillnet.Adapters.ReviewAdapter;
import com.example.skillnet.Adapters.ReviewProfileAdapter;
import com.example.skillnet.Adapters.ServiceGridAdapter;
import com.example.skillnet.FirebaseHelper.Firebase;
import com.example.skillnet.FirebaseHelper.FirebaseCallback;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.Post;
import com.example.skillnet.Models.Project;
import com.example.skillnet.Models.ReviewModel;
import com.example.skillnet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
// background thread
    private TextView fName;
    private ImageView profileImage;
    private Button btnEditProfile, btnSettings, btnPostProject, btnMessage ;
    private ReviewProfileAdapter reviewProfileAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private DocumentReference userDocRef;
    private RecyclerView completedProjectsRecyclerView, servicesRecyclerView, recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        fName = view.findViewById(R.id.name);
        btnMessage = view.findViewById(R.id.btn_msg);
        profileImage = view.findViewById(R.id.profile_picture);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnPostProject = view.findViewById(R.id.btn_post_project);
        btnSettings = view.findViewById(R.id.btn_settings);
        completedProjectsRecyclerView = view.findViewById(R.id.completed_projects_recycler_view);
        servicesRecyclerView = view.findViewById(R.id.service_projects_recycler_view);
        recyclerView = view.findViewById(R.id.review_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the current user's email
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Firebase firebase = new Firebase();

        // Set up the RecyclerView with GridLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3); // 3 columns
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getContext(), 3); // 3 columns
        completedProjectsRecyclerView.setLayoutManager(gridLayoutManager);
        servicesRecyclerView.setLayoutManager(gridLayoutManager2);

        if (currentUser != null) {
            String email = currentUser.getEmail();

            // Search for the user in users_signup collection using the email as document ID
            userDocRef = db.collection("users").document(GlobalVariables.code);

            // Fetch user details from Firestore
            userDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve name and profile picture URL from Firestore
                        String name = document.getString("name");
                        String imageUrl = document.getString("imageUrl");

                        // Set the retrieved name to the TextView
                        fName.setText(name);

                        // Load profile picture using Glide
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.profile)
                                    .error(R.drawable.profile)
                                    .into(profileImage);
                        }

                        else {
                            // Handle case where profile image URL is null or empty
                            profileImage.setImageResource(R.drawable.profile);
                        }
                        // Fetch completed projects
                        fetchCompletedProjects();
                    } else {
                        Toast.makeText(requireContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch document", Toast.LENGTH_SHORT).show();
                }
            });
        }
        btnPostProject.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new PostFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Set OnClickListener for the Edit Profile button
        btnEditProfile.setOnClickListener(v -> {
            // Launch the EditProfileActivity
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });
        // Fetch reviews from Firebase
        firebase.getAllUserReviews(new FirebaseCallback<ReviewModel>() {
            @Override
            public void onCallback(List<ReviewModel> list) {
                reviewProfileAdapter = new ReviewProfileAdapter(getContext(), list);
                recyclerView.setAdapter(reviewProfileAdapter);
                reviewProfileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDocumentSnapshotCallback(DocumentSnapshot snapshot) {
                // Handle document snapshot callback if needed
            }

            @Override
            public void onSingleCallback(ReviewModel item) {
                // Handle single item callback if needed
            }
        });

        // Set OnClickListener for the Settings button

        btnSettings.setOnClickListener(v -> {
            // Launch the SettingsActivity
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });
        btnMessage.setOnClickListener(v -> {
            // Create an instance of the fragment you want to navigate to
            ChatFragment chatFragment = new ChatFragment();

            // Perform the fragment transaction
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, chatFragment)
                    .addToBackStack(null)  // Optional: adds the transaction to the back stack so the user can navigate back
                    .commit();
        });

    }
    private void fetchCompletedProjects() {
        CollectionReference projectsRef = db.collection("projects")
                .document(GlobalVariables.code)
                .collection("worker's_projects");

        projectsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    List<Project> projectList = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Project project = document.toObject(Project.class);
                        projectList.add(project);
                    }

                    // Set up the adapter with the fetched projects
                    GridAdapter adapter1 = new GridAdapter(getContext(), projectList);
                    completedProjectsRecyclerView.setAdapter(adapter1);
                    adapter1.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(), "No projects found for this user", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Failed to fetch projects", Toast.LENGTH_SHORT).show();
            }
        });
        CollectionReference projectsRef2 = db.collection("projects")
                .document(GlobalVariables.code)
                .collection("worker's_services");

        projectsRef2.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    List<Project> projectList = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Project project = document.toObject(Project.class);
                        projectList.add(project);
                    }

                    // Set up the adapter with the fetched projects
                    ServiceGridAdapter adapter = new ServiceGridAdapter(getContext(), projectList);
                    servicesRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(), "No projects found for this user", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Failed to fetch projects", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
