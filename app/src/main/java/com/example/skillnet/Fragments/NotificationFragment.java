package com.example.skillnet.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillnet.Adapters.ReviewAdapter;
import com.example.skillnet.FirebaseHelper.Firebase;
import com.example.skillnet.FirebaseHelper.FirebaseCallback;
import com.example.skillnet.Models.ReviewModel;
import com.example.skillnet.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch reviews from Firebase
        Firebase firebase = new Firebase();
        firebase.getAllUserReviews(new FirebaseCallback<ReviewModel>() {
            @Override
            public void onCallback(List<ReviewModel> list) {
                // Set up RecyclerView with the fetched data
                reviewAdapter = new ReviewAdapter(getContext(), list);
                recyclerView.setAdapter(reviewAdapter);
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

        return view;
    }
}
