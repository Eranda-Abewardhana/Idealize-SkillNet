package com.example.skillnet.FirebaseHelper;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.skillnet.Models.ReviewModel;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.PersonData;
import com.example.skillnet.Models.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Firebase {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration listenerRegistration;


    public void getUserByEmail(String email, final FirebaseCallback<QueryDocumentSnapshot> callback) {
        db.collection("users_signup")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<QueryDocumentSnapshot> users = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userData = document.getString("email").toLowerCase();
                                if (userData.equals(email.toLowerCase())) {
                                    users.add(document);
                                    Log.d("Firestore", document.getId() + " => " + document.getData());
                                }
                            }
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                        }
                        callback.onCallback(users);
                    }

                });
    }
    public void updateDocumentValue(String collectionName, String documentName, String key, Object value, final FirebaseCallback callback) {
        DocumentReference docRef = db.collection(collectionName).document(documentName);

        Map<String, Object> updates = new HashMap<>();
        updates.put(key, value);

        docRef.update(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firestore", "DocumentSnapshot successfully updated!");

                        } else {
                            Log.w("Firestore", "Error updating document", task.getException());

                        }
                    }
                });
    }
    public void getChatsByCode(String code, final FirebaseCallback<QueryDocumentSnapshot> callback) {
        db.collection("chat_rooms")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<QueryDocumentSnapshot> filteredDocuments = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                if (documentId.contains(code)) {
                                    filteredDocuments.add(document);
                                }
                            }
                            for (QueryDocumentSnapshot doc : filteredDocuments) {
                                Log.d("Firestore", doc.getId() + " => " + doc.getData());
                            }
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                        }
                        callback.onCallback(filteredDocuments);
                    }
                });
    }
    public void getUserDataById(String docId, final FirebaseCallback<PersonData> callback) {
        db.collection("users")
                .document(docId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                PersonData person = document.toObject(PersonData.class);
                                Log.d("Firestore", "DocumentSnapshot data: " + document.getData());
                                callback.onSingleCallback(person);
                            } else {
                                Log.d("Firestore", "No such document");
                                callback.onSingleCallback(null);
                            }
                        } else {
                            Log.w("Firestore", "Error getting document.", task.getException());
                            callback.onSingleCallback(null);
                        }
                    }
                });
    }
    public void addChatData(String documentId, String fieldName, Object fieldValue, final FirebaseCallback<Void> callback) {
        DocumentReference documentReference = db.collection("chat_rooms").document(documentId);

        // Use FieldPath to handle special characters in fieldName
        FieldPath fieldPath = FieldPath.of(fieldName);

        // Update document in Firestore
        documentReference.update(fieldPath, fieldValue)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firestore", "DocumentSnapshot successfully updated!");
//                            callback.onCallback(null);
                        } else {
                            Log.w("Firestore", "Error updating document", task.getException());
//                            callback.onCallback(null);
                        }
                    }
                });
    }
    public void listenToDocumentUpdates(String documentId, final FirebaseCallback<DocumentSnapshot> callback) {
        if(!documentId.equals("")) {
            DocumentReference documentReference = db.collection("chat_rooms").document(documentId);

            listenerRegistration = documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("Firestore", "Listen failed.", e);
                        callback.onDocumentSnapshotCallback(null); // Callback on error
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d("Firestore", "Current data: " + snapshot.getData());
                        callback.onDocumentSnapshotCallback(snapshot);
                    } else {
                        Log.d("Firestore", "Current data: null");
                        callback.onDocumentSnapshotCallback(null);
                    }
                }
            });
        }
    }


    public void removeListener() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
    public void getAllUserReviews(final FirebaseCallback<ReviewModel> callback) {
        db.collection("reviews").document(GlobalVariables.code).collection("worker_reviews")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                        List<ReviewModel> reviewModels = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ReviewModel reviewModel = document.toObject(ReviewModel.class);
                                reviewModels.add(reviewModel);
                            }
                            callback.onCallback(reviewModels);
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                            callback.onCallback(Collections.emptyList()); // or handle the error in callback
                        }
                    }
                });
    }


    public void getAllUsers(final FirebaseCallback<PersonData> callback) {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                        List<PersonData> personDataList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PersonData person = document.toObject(PersonData.class);
                                personDataList.add(person);
                                Log.d("Firestore", "User: " + person.getName());
                            }
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                        }
                        callback.onCallback(personDataList);
                    }
                });
    }

    public void getAllCategories(final FirebaseCallback<Categories> callback) {
        db.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                        List<Categories> categoriesList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Categories category = document.toObject(Categories.class);
                                categoriesList.add(category);
                                Log.d("Firestore", "Category: " + category.getName());
                            }
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                        }
                        callback.onCallback(categoriesList);
                    }
                });
    }
    // Method to start listening for chat_rooms updates
    public void getAllPosts(final FirebaseCallback<Post> callback) {
        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                        List<Post> postList = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Post post = document.toObject(Post.class);
                                postList.add(post);
                                Log.d("Firestore", "Post: " + post.getTitle());
                            }
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                        }
                        callback.onCallback(postList);
                    }
                });
    }
    }

