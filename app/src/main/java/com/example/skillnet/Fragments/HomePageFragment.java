package com.example.skillnet.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skillnet.Activities.MainActivity;
import com.example.skillnet.Adapters.CategoryAdapter;
import com.example.skillnet.Adapters.CategoryDataAdapter;
import com.example.skillnet.Adapters.PostAdapter;
import com.example.skillnet.FirebaseHelper.Firebase;
import com.example.skillnet.FirebaseHelper.FirebaseCallback;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.PersonData;
import com.example.skillnet.Models.Post;
import com.example.skillnet.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomePageFragment extends Fragment {

    private RecyclerView recyclerView1, recyclerView2, recyclerView3;
    private CategoryAdapter categoryAdapter;
    private PostAdapter postAdapter;
    private CategoryDataAdapter categoryDataAdapter;
    private Context context;
    private TextView feeds;
    private static final String TAG = "Home Page Fragment";
    private static final int MAX_RETRIES = 10;
    private static final long RETRY_INTERVAL_MS = 1000; // 1 second
    private int retryCount = 0;
    private Handler handler;
    private FirebaseFirestore fStore;
    Firebase firebase = new Firebase();
    private boolean workerUpdated = false;
    private LinearLayout layout;
    private Dialog loadingDialog;
    private Button moreCategories;
    private boolean more = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        context = getActivity().getApplicationContext();
        feeds = view.findViewById(R.id.feeds);
        handler = new Handler();
        fStore = FirebaseFirestore.getInstance();

        // Initialize the loading dialog
        loadingDialog = new Dialog(getActivity());
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        ImageView loadingGif = loadingDialog.findViewById(R.id.loading_gif);
        Glide.with(this).asGif().load(R.drawable.loading).into(loadingGif);
        loadingDialog.show();

        // Initialize the RecyclerView
        recyclerView1 = view.findViewById(R.id.recycler_view);
        recyclerView3 = view.findViewById(R.id.recycler_view3);
        recyclerView2 = view.findViewById(R.id.recycler_view2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        layout = view.findViewById(R.id.data);
        layout.setVisibility(View.GONE);

        moreCategories = view.findViewById(R.id.more_categories);

        moreCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(more){
                    recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    moreCategories.setText("See More");
                }
                else {
                    recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    moreCategories.setText("See Less");
                }
                more = ! more;
            }
        });

        // Add the OnClickListener for massage_tab
        LinearLayout massageTab = view.findViewById(R.id.massage_tab);
        massageTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform fragment transaction
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new ChatFragment()); // Replace with your target fragment
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        GlobalVariables.personDataList.clear();
        GlobalVariables.categoriesList.clear();
        GlobalVariables.postList.clear();

        retryGetUserCode();

        firebase.getAllUsers(new FirebaseCallback<PersonData>() {
            @Override
            public void onCallback(List<PersonData> list) {
                GlobalVariables.personDataList = list;
                if(workerUpdated)
                    checkDataReady();
            }

            @Override
            public void onDocumentSnapshotCallback(DocumentSnapshot snapshot) {

            }

            @Override
            public void onSingleCallback(PersonData item) {

            }
        });

        firebase.getAllCategories(new FirebaseCallback<Categories>() {
            @Override
            public void onCallback(List<Categories> list) {
                GlobalVariables.categoriesList = list;
                if(workerUpdated)
                    checkDataReady();
            }

            @Override
            public void onDocumentSnapshotCallback(DocumentSnapshot snapshot) {

            }

            @Override
            public void onSingleCallback(Categories item) {

            }
        });

        firebase.getAllPosts(new FirebaseCallback<Post>() {
            @Override
            public void onCallback(List<Post> list) {
                GlobalVariables.postList = list;
                if(workerUpdated)
                    checkDataReady();
            }

            @Override
            public void onDocumentSnapshotCallback(DocumentSnapshot snapshot) {

            }

            @Override
            public void onSingleCallback(Post item) {

            }
        });

        return view;
    }

    private void checkDataReady() {
        if (!GlobalVariables.personDataList.isEmpty() && !GlobalVariables.categoriesList.isEmpty() && !GlobalVariables.postList.isEmpty()) {
            loadAdapters(GlobalVariables.isWorker);
            layout.setVisibility(View.VISIBLE);
            loadingDialog.dismiss();
        }
    }

    private void loadAdapters(boolean isWorker) {
        // Ensure all data is fetched before setting adapters
        if (GlobalVariables.personDataList.isEmpty() || GlobalVariables.categoriesList.isEmpty() || GlobalVariables.postList.isEmpty()) {
            return;
        }

        if (isWorker) {
            recyclerView3.setVisibility(View.VISIBLE);
            recyclerView1.setVisibility(View.GONE);
            recyclerView2.setVisibility(View.GONE);
            feeds.setText("Jobs");
            postAdapter = new PostAdapter(GlobalVariables.postList, GlobalVariables.categoriesList, GlobalVariables.personDataList, context);
            recyclerView3.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView3.setAdapter(postAdapter);
        } else {
            recyclerView3.setVisibility(View.GONE);
            recyclerView1.setVisibility(View.VISIBLE);
            recyclerView2.setVisibility(View.VISIBLE);
            feeds.setText("Categories");
            categoryAdapter = new CategoryAdapter(GlobalVariables.categoriesList);
            recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView1.setAdapter(categoryAdapter);

            categoryDataAdapter = new CategoryDataAdapter(GlobalVariables.categoriesList, GlobalVariables.personDataList, context);
            recyclerView2.setAdapter(categoryDataAdapter);
        }

        // Notify adapters of data changes
        if (categoryAdapter != null) categoryAdapter.notifyDataSetChanged();
        if (postAdapter != null) postAdapter.notifyDataSetChanged();
        if (categoryDataAdapter != null) categoryDataAdapter.notifyDataSetChanged();
    }
    private void retryGetUserCode() {
        firebase.getUserByEmail(GlobalVariables.email, new FirebaseCallback<QueryDocumentSnapshot>() {
            @Override
            public void onCallback(List<QueryDocumentSnapshot> users) {
                if (!users.isEmpty()) {
                    QueryDocumentSnapshot user = users.get(0);
                    GlobalVariables.code = user.getString("user");
                    DocumentReference documentReference2 = fStore.collection("users").document(GlobalVariables.code);
                    documentReference2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                GlobalVariables.isWorker = documentSnapshot.getBoolean("isworker");
                                workerUpdated = true;
                                checkDataReady();
                            } else {
                                // Handle the case where the document does not exist
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@android.support.annotation.NonNull Exception e) {
                            // Handle any errors
                        }
                    });

                    Log.d(TAG, "User code retrieved: " + GlobalVariables.code);
                } else {
                    Log.d(TAG, "No users found with the "+ GlobalVariables.email);
                }

                if (GlobalVariables.code == null || GlobalVariables.code.isEmpty()) {
                    if (retryCount < MAX_RETRIES) {
                        retryCount++;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                retryGetUserCode();
                            }
                        }, RETRY_INTERVAL_MS);
                    } else {
                        Toast.makeText(context, "Operation timed out", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Operation timed out. GlobalVariables.code: " + GlobalVariables.code);
                    }
                }
            }

            @Override
            public void onDocumentSnapshotCallback(DocumentSnapshot snapshot) {
                // Not used
            }

            @Override
            public void onSingleCallback(QueryDocumentSnapshot item) {
                // Not used
            }
        });
    }
}
