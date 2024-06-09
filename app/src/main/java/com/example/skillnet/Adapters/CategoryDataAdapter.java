package com.example.skillnet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.PersonData;
import com.example.skillnet.R;

import java.util.List;

public class CategoryDataAdapter extends RecyclerView.Adapter<CategoryDataAdapter.CategoryViewHolder> {

    private List<Categories> categoriesList;
    private List<PersonData> personDataList; // Assuming you get this list from somewhere
    private Context context;

    public CategoryDataAdapter(List<Categories> categoriesList, List<PersonData> personDataList, Context context) {
        this.categoriesList = categoriesList;
        this.personDataList = personDataList;
        this.context        = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_for_each_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Categories category = categoriesList.get(position);
        holder.categoryName.setText(category.getName());

        // Set up the nested RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        holder.personRecyclerView.setLayoutManager(layoutManager);
        PersonForCategoryAdapter personForCategoryAdapter = new PersonForCategoryAdapter(personDataList);
        holder.personRecyclerView.setAdapter(personForCategoryAdapter);
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView categoryName;
        ImageView categoryImage;
        RecyclerView personRecyclerView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
            personRecyclerView = itemView.findViewById(R.id.person_list);
        }
    }
}
