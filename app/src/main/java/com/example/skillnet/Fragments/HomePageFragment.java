package com.example.skillnet.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillnet.Adapters.CategoryAdapter;
import com.example.skillnet.Adapters.CategoryDataAdapter;
import com.example.skillnet.Adapters.PostAdapter;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.PersonData;
import com.example.skillnet.Models.Post;
import com.example.skillnet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomePageFragment extends Fragment {

    private RecyclerView recyclerView1, recyclerView2, recyclerView3;
    private CategoryAdapter categoryAdapter;
    private PostAdapter postAdapter;
    private CategoryDataAdapter categoryDataAdapter;
    private List<Categories> categoriesList;
    private List<Post> postList;
    private List<PersonData> personDataList;
    private Context context;
    private TextView feeds;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        context = getActivity().getApplicationContext();
        feeds = view.findViewById(R.id.feeds);

        // Initialize the RecyclerView
        recyclerView1 = view.findViewById(R.id.recycler_view);
        recyclerView3 = view.findViewById(R.id.recycler_view3);
        recyclerView2 = view.findViewById(R.id.recycler_view2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

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

        // Initialize data lists
        personDataList = new ArrayList<>();
        categoriesList = new ArrayList<>();
        postList = new ArrayList<>();

        // Fetch all data from the "users" collection
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PersonData person = document.toObject(PersonData.class);
                                personDataList.add(person);
                                Log.d("Firestore", "User: " + person.getName());
                            }
                            // Load data into adapter after fetching users
                            loadAdapters(GlobalVariables.isWorker);
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                        }
                    }
                });
        // Fetch all data from the "categories" collection
        db.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Categories category = document.toObject(Categories.class);
                                categoriesList.add(category);
                                Log.d("Firestore", "Category: " + category.getName());
                            }
                            // Load data into adapter after fetching categories
                            loadAdapters(GlobalVariables.isWorker);
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                        }
                    }
                });
// Fetch all data from the "posts" collection
        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Post post = document.toObject(Post.class);
                                postList.add(post);
                                Log.d("Firestore", "Post: " + post.getTitle());
                            }
                            // Load data into adapter after fetching posts
                            loadAdapters(GlobalVariables.isWorker);
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                        }
                    }
                });


        return view;
    }

    private void loadAdapters(boolean isWorker) {
        // Ensure all data is fetched before setting adapters
        if (personDataList.isEmpty() || categoriesList.isEmpty() || postList.isEmpty()) {
            return;
        }

        if (isWorker) {
            recyclerView3.setVisibility(View.VISIBLE);
            recyclerView1.setVisibility(View.GONE);
            recyclerView2.setVisibility(View.GONE);
            feeds.setText("Jobs");
            postAdapter = new PostAdapter(postList, categoriesList, personDataList, context);
            recyclerView3.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView3.setAdapter(postAdapter);
        } else {
            recyclerView3.setVisibility(View.GONE);
            recyclerView1.setVisibility(View.VISIBLE);
            recyclerView2.setVisibility(View.VISIBLE);
            feeds.setText("Categories");
            categoryAdapter = new CategoryAdapter(categoriesList);
            recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView1.setAdapter(categoryAdapter);

            categoryDataAdapter = new CategoryDataAdapter(categoriesList, personDataList, context);
            recyclerView2.setAdapter(categoryDataAdapter);
        }


        // Notify adapters of data changes
        if (categoryAdapter != null) categoryAdapter.notifyDataSetChanged();
        if (postAdapter != null) postAdapter.notifyDataSetChanged();
        if (categoryDataAdapter != null) categoryDataAdapter.notifyDataSetChanged();
    }
}
