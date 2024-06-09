package com.example.skillnet.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillnet.Adapters.CategoryAdapter;
import com.example.skillnet.Adapters.CategoryDataAdapter;
import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.PersonData;
import com.example.skillnet.R;

import java.util.ArrayList;
import java.util.List;

public class HomePageFragment extends Fragment {

    private RecyclerView recyclerView1, recyclerView2;
    private CategoryAdapter categoryAdapter;
    private CategoryDataAdapter categoryDataAdapter;
    private List<Categories> categoriesList;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        context = getActivity().getApplicationContext();

        // Initialize the RecyclerView
        recyclerView1 = view.findViewById(R.id.recycler_view);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Initialize the RecyclerView
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
        // Create sample person data
        List<PersonData> personDataList = new ArrayList<>();
        personDataList.add(new PersonData("Person 1", "P001", "https://firebasestorage.googleapis.com/v0/b/skillnet-9828e.appspot.com/o/Category%20Icons%2Faccount.png?alt=media&token=fcefb637-6865-4dbc-b23b-d91c1e5c2f50"));
        personDataList.add(new PersonData("Person 2", "P002", "https://firebasestorage.googleapis.com/v0/b/skillnet-9828e.appspot.com/o/Category%20Icons%2Faccount.png?alt=media&token=fcefb637-6865-4dbc-b23b-d91c1e5c2f50"));
        personDataList.add(new PersonData("Person 3", "P003", "https://firebasestorage.googleapis.com/v0/b/skillnet-9828e.appspot.com/o/Category%20Icons%2Faccount.png?alt=media&token=fcefb637-6865-4dbc-b23b-d91c1e5c2f50"));
        personDataList.add(new PersonData("Person 4", "P004", "https://firebasestorage.googleapis.com/v0/b/skillnet-9828e.appspot.com/o/Category%20Icons%2Faccount.png?alt=media&token=fcefb637-6865-4dbc-b23b-d91c1e5c2f50"));
        personDataList.add(new PersonData("Person 5", "P005", "https://firebasestorage.googleapis.com/v0/b/skillnet-9828e.appspot.com/o/Category%20Icons%2Faccount.png?alt=media&token=fcefb637-6865-4dbc-b23b-d91c1e5c2f50"));


        // Initialize the categories list and adapter
        categoriesList = new ArrayList<>();
        categoriesList.add(new Categories("C001","Category 1", "https://firebasestorage.googleapis.com/v0/b/skillnet-9828e.appspot.com/o/Category%20Icons%2Fbath-tub.png?alt=media&token=06234fc6-4629-4c7a-9bff-3cc1bab17409",personDataList));
        categoriesList.add(new Categories("C002","Category 2", "https://firebasestorage.googleapis.com/v0/b/skillnet-9828e.appspot.com/o/Category%20Icons%2Fbath-tub.png?alt=media&token=06234fc6-4629-4c7a-9bff-3cc1bab17409",personDataList));
        categoriesList.add(new Categories("C003","Category 3", "https://firebasestorage.googleapis.com/v0/b/skillnet-9828e.appspot.com/o/Category%20Icons%2Fbath-tub.png?alt=media&token=06234fc6-4629-4c7a-9bff-3cc1bab17409",personDataList));
        categoriesList.add(new Categories("C004","Category 4", "https://firebasestorage.googleapis.com/v0/b/skillnet-9828e.appspot.com/o/Category%20Icons%2Fbath-tub.png?alt=media&token=06234fc6-4629-4c7a-9bff-3cc1bab17409",personDataList));
        categoriesList.add(new Categories("C005","Category 5", "https://firebasestorage.googleapis.com/v0/b/skillnet-9828e.appspot.com/o/Category%20Icons%2Fbath-tub.png?alt=media&token=06234fc6-4629-4c7a-9bff-3cc1bab17409",personDataList));


        categoryAdapter = new CategoryAdapter(categoriesList);
        recyclerView1.setAdapter(categoryAdapter);


        categoryDataAdapter = new CategoryDataAdapter(categoriesList,personDataList,context);
        recyclerView2.setAdapter(categoryDataAdapter);

        return view;
    }
}
