package com.example.skillnet.Fragments;

import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private static RecyclerView recyclerView;
    private static ReviewAdapter reviewAdapter;
    private static FirebaseFirestore fStore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fStore = FirebaseFirestore.getInstance();

        // Fetch reviews from Firebase
        fetchNotificationData();

        return view;
    }

    public static void fetchNotificationData() {
        Firebase firebase = new Firebase();
        firebase.getAllUserReviews(new FirebaseCallback<ReviewModel>() {
            @Override
            public void onCallback(List<ReviewModel> list) {
                for (Post post : GlobalVariables.postList) {
                    if (post.getUserCode().equals(GlobalVariables.code)) {
                        CollectionReference subCollection = fStore.collection("posts").document(post.getPostCode()).collection(post.getPostCode());

                        subCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> subTask) {
                                if (subTask.isSuccessful()) {
                                    for (QueryDocumentSnapshot subDocument : subTask.getResult()) {
                                        ReviewModel reviewModel = new ReviewModel();
                                        reviewModel.setPostId(post.getPostCode());
                                        reviewModel.setDateTime(post.getDateTime());
                                        reviewModel.setCategoryCode(post.getCategoryCode());
                                        reviewModel.setDescription(post.getDescription());
                                        reviewModel.setTitle(post.getTitle());
                                        reviewModel.setClientCode(subDocument.getId());
                                        reviewModel.setAccept((Boolean) subDocument.get("approved"));
                                        reviewModel.setFindWorker(post.isFindWorker());
                                        if(reviewModel.isAccept()){
                                            reviewModel.setFindWorker(true);
                                        }
                                        reviewModel.setReview(false);

                                        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                                            reviewModel.setImageUrl(post.getImageUrl());
                                        } else {
                                            for (Categories categories : GlobalVariables.categoriesList) {
                                                if (post.getCategoryCode().equals(categories.getCode())) {
                                                    reviewModel.setImageUrl(categories.getUrl());
                                                    break;
                                                }
                                            }
                                        }
                                        list.add(reviewModel);
                                    }
                                    reviewAdapter = new ReviewAdapter(recyclerView.getContext(), list);
                                    recyclerView.setAdapter(reviewAdapter);
//                                    reviewAdapter.updateData(list);
                                    reviewAdapter.notifyDataSetChanged();
                                } else {
                                    Log.d("Firestore", "Error getting sub-collection documents: ", subTask.getException());
                                }
                            }
                        });
                    }
                }
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
    }
}
