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
import com.example.skillnet.FirebaseHelper.Firebase;
import com.example.skillnet.FirebaseHelper.FirebaseCallback;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.PersonData;
import com.example.skillnet.Models.Post;
import com.example.skillnet.R;
import com.google.firebase.firestore.DocumentSnapshot;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        Firebase firebase = new Firebase();
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

        GlobalVariables.personDataList.clear();
        GlobalVariables.categoriesList.clear();
        GlobalVariables.postList.clear();

        firebase.getAllUsers(new FirebaseCallback<PersonData>() {
            @Override
            public void onCallback(List<PersonData> list) {
                GlobalVariables.personDataList = list;
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
}
