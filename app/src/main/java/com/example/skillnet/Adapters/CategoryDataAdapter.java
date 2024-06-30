package com.example.skillnet.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.PersonData;
import com.example.skillnet.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryDataAdapter extends RecyclerView.Adapter<CategoryDataAdapter.CategoryViewHolder> {

    private List<Categories> categoriesList;
    private List<PersonData> personDataList;
    private FragmentActivity activity;
    private boolean more = false;

    public CategoryDataAdapter(List<Categories> categoriesList, List<PersonData> personDataList, FragmentActivity activity) {
        this.categoriesList = categoriesList;
        this.personDataList = personDataList;
        this.activity = activity;
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

        List<PersonData> personDataList1 = new ArrayList<>();
        for (PersonData person : personDataList) {
            List<String> personCodes = category.getPersonDataList();
            for (String code : personCodes) {
                if (code.equals(person.getpCode())) {
                    personDataList1.add(person);
                }
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        holder.personRecyclerView.setLayoutManager(layoutManager);
        PersonForCategoryAdapter personForCategoryAdapter = new PersonForCategoryAdapter(personDataList1, category.getName(), activity);
        holder.personRecyclerView.setAdapter(personForCategoryAdapter);

        holder.seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (more) {
                    holder.personRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
                    holder.seeMore.setText("SEE ALL >");
                } else {
                    holder.personRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false));
                    holder.seeMore.setText("SEE LESS <");
                }
                more = !more;
                personForCategoryAdapter.notifyDataSetChanged();
            }
        });

        personForCategoryAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView categoryName;
        RecyclerView personRecyclerView;
        Button seeMore;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
            personRecyclerView = itemView.findViewById(R.id.person_list);
            seeMore = itemView.findViewById(R.id.see_more_person);
        }
    }
}
