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
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.Post;
import com.example.skillnet.Models.ReviewModel;
import com.example.skillnet.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
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
                Categories category = new Categories();
                for(Post post : GlobalVariables.postList){
                    ReviewModel reviewModel = new ReviewModel();
                    reviewModel.setDateTime(post.getDateTime());
                    reviewModel.setCategoryCode(post.getCategoryCode());
                    reviewModel.setDescription(post.getDescription());
                    reviewModel.setTitle(post.getTitle());
                    reviewModel.setReview(false);

                    if(post.getImageUrl() != null && !post.getImageUrl().equals("")){
                        reviewModel.setImageUrl(post.getImageUrl());
                    }
                    else {
                        for (Categories categories : GlobalVariables.categoriesList) {
                            if (post.getCategoryCode().equals(categories.getCode())) {
                                category = categories;
                                break;
                            }
                        }
                        reviewModel.setImageUrl(category.getUrl());
                    }
                    list.add(reviewModel);
                }
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
