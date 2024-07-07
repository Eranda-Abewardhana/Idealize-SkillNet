package com.example.skillnet.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.example.skillnet.Adapters.ReviewProfileAdapter;
import com.example.skillnet.Adapters.ServiceGridAdapter;
import com.example.skillnet.FirebaseHelper.Firebase;
import com.example.skillnet.FirebaseHelper.FirebaseCallback;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.PersonData;
import com.example.skillnet.Models.Project;
import com.example.skillnet.Models.ReviewModel;
import com.example.skillnet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private TextView fName, profession, location, bio, phone, webSite;

    private View bioLayout;
    private View iconsLayout;

    private Dialog loadingDialog;

    private View reviewLayout;
    private View servicesLayout;
    private View completedLayout;
    private ImageView profileImage, back;
    private Button btnEditProfile, btnSettings, btnPostProject, btnMessage, btnReview ;
    private ReviewProfileAdapter reviewProfileAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private DocumentReference userDocRef, userDocRef2;
    private RecyclerView completedProjectsRecyclerView, servicesRecyclerView, recyclerView;
    private ImageView instagramImageView, facebookImageView, twitterImageView, linkedinImageView;
    private String code;
    private boolean otherPerson;
    private boolean isGuest = false;

    public ProfileFragment(boolean otherPerson) {
        this.otherPerson = otherPerson;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        // Access SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPrefs", getActivity().MODE_PRIVATE);
        isGuest = sharedPreferences.getBoolean("isGuest", false);

        initializeViews(view);
        if (otherPerson) {
            // api balana anik eka other person
            code = GlobalVariables.otherPersonData.getpCode();
            back.setVisibility(View.VISIBLE);
            if(!isGuest) {
                btnMessage.setVisibility(View.VISIBLE);
            }
            else {
                btnMessage.setVisibility(View.GONE);
            }
            btnPostProject.setVisibility(View.GONE);
            btnEditProfile.setVisibility(View.GONE);
            btnSettings.setVisibility(View.GONE);

            bioLayout.setVisibility(View.GONE);
            servicesLayout.setVisibility(View.GONE);
            completedLayout.setVisibility(View.VISIBLE);
            iconsLayout.setVisibility(View.GONE);
            reviewLayout.setVisibility(View.GONE);

            if(GlobalVariables.otherPersonData.isIsworker()){
                if(!isGuest) {
                    btnReview.setVisibility(View.VISIBLE);
                }
                else{
                    btnReview.setVisibility(View.GONE);
                }
                profession.setVisibility(View.VISIBLE);
                bioLayout.setVisibility(View.VISIBLE);
                servicesLayout.setVisibility(View.VISIBLE);
                completedLayout.setVisibility(View.GONE);
                iconsLayout.setVisibility(View.VISIBLE);
                reviewLayout.setVisibility(View.VISIBLE);

            }

        } else {
            code = GlobalVariables.code;

            back.setVisibility(View.GONE);
            btnEditProfile.setVisibility(View.VISIBLE);
            btnSettings.setVisibility(View.VISIBLE);
            btnMessage.setVisibility(View.GONE);
            btnReview.setVisibility(View.GONE);

            if (GlobalVariables.isWorker) {
                btnEditProfile.setVisibility(View.VISIBLE);
                btnPostProject.setVisibility(View.GONE);
                bioLayout.setVisibility(View.VISIBLE);
                servicesLayout.setVisibility(View.VISIBLE);
                completedLayout.setVisibility(View.GONE);
                iconsLayout.setVisibility(View.VISIBLE);
                reviewLayout.setVisibility(View.VISIBLE);
            } else {
                btnEditProfile.setVisibility(View.VISIBLE);
                btnPostProject.setVisibility(View.VISIBLE);
                profession.setVisibility(View.GONE);
                bioLayout.setVisibility(View.GONE);
                servicesLayout.setVisibility(View.GONE);
                completedLayout.setVisibility(View.VISIBLE);
                iconsLayout.setVisibility(View.GONE);
                reviewLayout.setVisibility(View.GONE);
            }
        }

        setRecyclerViewLayoutManagers();

        return view;
    }

    private void initializeViews(View view) {

        bioLayout = view.findViewById(R.id.bio_layout);
        iconsLayout= view.findViewById(R.id.icons_layout);
        servicesLayout = view.findViewById(R.id.services_layout);
        completedLayout= view.findViewById(R.id.completed_project_layout);
        reviewLayout = view.findViewById(R.id.review_layout) ;

        fName = view.findViewById(R.id.name);
        profession = view.findViewById(R.id.profession);
        location = view.findViewById(R.id.location);
        bio = view.findViewById(R.id.biodata);
        webSite = view.findViewById(R.id.link);
        back = view.findViewById(R.id.back);
        phone = view.findViewById(R.id.phone);
        btnMessage = view.findViewById(R.id.btn_msg);
        profileImage = view.findViewById(R.id.profile_picture);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnPostProject = view.findViewById(R.id.btn_post_project);
        btnSettings = view.findViewById(R.id.btn_settings);
        completedProjectsRecyclerView = view.findViewById(R.id.completed_projects_recycler_view);
        servicesRecyclerView = view.findViewById(R.id.service_projects_recycler_view);
        recyclerView = view.findViewById(R.id.review_recycler_view);
        btnReview = view.findViewById(R.id.btn_review);
        linkedinImageView = view.findViewById(R.id.linkedin);
        twitterImageView = view.findViewById(R.id.twitter);
        facebookImageView = view.findViewById(R.id.facebook);
        instagramImageView = view.findViewById(R.id.instagram);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setRecyclerViewLayoutManagers() {
        completedProjectsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        servicesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        Firebase firebase = new Firebase();

        loadingDialog = new Dialog(getActivity());
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        ImageView loadingGif = loadingDialog.findViewById(R.id.loading_gif);
        Glide.with(this).asGif().load(R.drawable.loading).into(loadingGif);
        loadingDialog.show();

        if (currentUser != null || isGuest) {
            userDocRef = db.collection("users").document(code);

            userDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document1 = task.getResult();
                    if (document1.exists()) {
                        userDocRef2 = db.collection("users_signup").document(document1.getString("email"));
                        userDocRef2.get().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful() && task2.getResult() != null) {
                                if (otherPerson) {
                                    GlobalVariables.otherPersonData.setPhone(task2.getResult().getString("PhoneNumber"));
                                    phone.setText(GlobalVariables.otherPersonData.getPhone());
                                } else {
                                    GlobalVariables.person.setPhone(task2.getResult().getString("PhoneNumber"));
                                    phone.setText(GlobalVariables.person.getPhone());
                                }
                            }
                        });
                        if (otherPerson) {
                            GlobalVariables.otherPersonData = document1.toObject(PersonData.class);
                            if(GlobalVariables.otherPersonData != null)
                                setUserData(GlobalVariables.otherPersonData);
                        } else {
                            GlobalVariables.person = document1.toObject(PersonData.class);
                            if(GlobalVariables.person != null)
                                setUserData(GlobalVariables.person);
                        }
                        fetchCompletedProjects();
                    } else {
                        showToast("Document does not exist");
                    }
                } else {
                    showToast("Failed to fetch document");
                }
            });
        }

        setButtonListeners(firebase);
    }

    private void setUserData(PersonData person) {
        fName.setText(person.getName() != null && !person.getName().isEmpty() ? person.getName() : " ");
        location.setText(person.getLocation() != null && !person.getLocation().isEmpty() ? person.getLocation() : " ");
        profession.setText(getUserCategories(person.getpCode()) != null && !getUserCategories(person.getpCode()).isEmpty() ? getUserCategories(person.getpCode()) : " ");
        bio.setText(person.getBio() != null && !person.getBio().isEmpty() ? person.getBio() : " ");
        webSite.setText(person.getWebsite() != null && !person.getWebsite().isEmpty() ? person.getWebsite() : " ");
        phone.setText(person.getPhone() != null && !person.getPhone().isEmpty() ? person.getPhone() : " ");
        setProfileImage(person.getImageUrl());
        setSocialMediaListeners(person);
        loadingDialog.dismiss();
    }

    private String getUserCategories(String pCode) {
        StringBuilder category = new StringBuilder();
        for (Categories categories : GlobalVariables.categoriesList) {
            for (String personData : categories.getPersonDataList()) {
                if (personData.equals(pCode)) {
                    category.append("\n").append(categories.getName());
                    break;
                }
            }
        }
        return category.toString();
    }

    private void setProfileImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.profile);
        }
    }

    private void setSocialMediaListeners(PersonData person) {
        setSocialMediaClickListener(linkedinImageView, person.getLinkedin());
        setSocialMediaClickListener(twitterImageView, person.getTwitter());
        setSocialMediaClickListener(facebookImageView, person.getFb());
        setSocialMediaClickListener(instagramImageView, person.getInsta());
        setSocialMediaClickListener(webSite, person.getWebsite());
    }

    private void setSocialMediaClickListener(View view, String url) {
        view.setOnClickListener(v -> {
            if (url != null && !url.isEmpty()) {
                openUrl(url);
            } else {
                showToast("No URL provided");
            }
        });
    }

    private void setButtonListeners(Firebase firebase) {
        back.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        btnPostProject.setOnClickListener(v -> navigateToFragment(new PostFragment()));
        btnReview.setOnClickListener(v -> navigateToFragment(new ReviewFragment()));
        btnEditProfile.setOnClickListener(v -> startActivity(new Intent(getActivity(), EditProfileActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(getActivity(), SettingsActivity.class)));
        btnMessage.setOnClickListener(v ->
                navigateToFragment(new ChatFragment(GlobalVariables.otherPersonData.getpCode(), true)));
        if(!isGuest)
            firebase.getAllUserReviews(new FirebaseCallback<ReviewModel>() {
            @Override
            public void onCallback(List<ReviewModel> list) {
                reviewProfileAdapter = new ReviewProfileAdapter(getContext(), list);
                recyclerView.setAdapter(reviewProfileAdapter);
                reviewProfileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDocumentSnapshotCallback(DocumentSnapshot snapshot) {
            }

            @Override
            public void onSingleCallback(ReviewModel item) {
            }
        });
    }

    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void fetchCompletedProjects() {
        fetchProjects("worker's_projects", completedProjectsRecyclerView, new GridAdapter(getContext(), new ArrayList<>()));
        fetchProjects("worker's_services", servicesRecyclerView, new ServiceGridAdapter(getContext(), new ArrayList<>()));
    }

    private void fetchProjects(String collection, RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        CollectionReference projectsRef = db.collection("projects")
                .document(code)
                .collection(collection);

        projectsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    List<Project> projectList = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        projectList.add(document.toObject(Project.class));
                    }
                    updateRecyclerView(recyclerView, adapter, projectList);
                } else {
                    showToast("No projects found for this user");
                }
            } else {
                showToast("Failed to fetch projects");
            }
        });
    }

    private void updateRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter, List<Project> projectList) {
        if (adapter instanceof GridAdapter) {
            ((GridAdapter) adapter).updateProjects(projectList);
        } else if (adapter instanceof ServiceGridAdapter) {
            ((ServiceGridAdapter) adapter).updateProjects(projectList);
        }
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void showToast(String message) {
        // Uncomment the below line to show toast messages
        // Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
