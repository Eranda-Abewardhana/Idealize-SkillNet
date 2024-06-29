package com.example.skillnet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillnet.Fragments.ProfileFragment;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.Models.PersonData;
import com.example.skillnet.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PersonForCategoryAdapter extends RecyclerView.Adapter<PersonForCategoryAdapter.PersonViewHolder> {

    private List<PersonData> personDataList;
    private String categories;
    private FragmentActivity activity;

    public PersonForCategoryAdapter(List<PersonData> personDataList, String categories, FragmentActivity activity) {
        this.personDataList = personDataList;
        this.categories = categories;
        this.activity = activity;
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_category_item, parent, false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        PersonData person = personDataList.get(position);
        holder.personName.setText(person.getName());
        holder.category.setText(categories);
        Picasso.get().load(person.getImageUrl()).into(holder.personImage);
        holder.personLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalVariables.otherPersonData = person;
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new ProfileFragment(true));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        switch (person.getStars()) {
            case 1:
                holder.star1.setBackgroundResource(R.drawable.starfull);
                holder.star2.setBackgroundResource(R.drawable.starfill);
                holder.star3.setBackgroundResource(R.drawable.starfill);
                holder.star4.setBackgroundResource(R.drawable.starfill);
                holder.star5.setBackgroundResource(R.drawable.starfill);
                break;
            case 2:
                holder.star1.setBackgroundResource(R.drawable.starfull);
                holder.star2.setBackgroundResource(R.drawable.starfull);
                holder.star3.setBackgroundResource(R.drawable.starfill);
                holder.star4.setBackgroundResource(R.drawable.starfill);
                holder.star5.setBackgroundResource(R.drawable.starfill);
                break;
            case 3:
                holder.star1.setBackgroundResource(R.drawable.starfull);
                holder.star2.setBackgroundResource(R.drawable.starfull);
                holder.star3.setBackgroundResource(R.drawable.starfull);
                holder.star4.setBackgroundResource(R.drawable.starfill);
                holder.star5.setBackgroundResource(R.drawable.starfill);
                break;
            case 4:
                holder.star1.setBackgroundResource(R.drawable.starfull);
                holder.star2.setBackgroundResource(R.drawable.starfull);
                holder.star3.setBackgroundResource(R.drawable.starfull);
                holder.star4.setBackgroundResource(R.drawable.starfull);
                holder.star5.setBackgroundResource(R.drawable.starfill);
                break;
            case 5:
                holder.star1.setBackgroundResource(R.drawable.starfull);
                holder.star2.setBackgroundResource(R.drawable.starfull);
                holder.star3.setBackgroundResource(R.drawable.starfull);
                holder.star4.setBackgroundResource(R.drawable.starfull);
                holder.star5.setBackgroundResource(R.drawable.starfull);
                break;
            default:
                holder.star1.setBackgroundResource(R.drawable.starfill);
                holder.star2.setBackgroundResource(R.drawable.starfill);
                holder.star3.setBackgroundResource(R.drawable.starfill);
                holder.star4.setBackgroundResource(R.drawable.starfill);
                holder.star5.setBackgroundResource(R.drawable.starfill);
        }
    }

    @Override
    public int getItemCount() {
        return personDataList.size();
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        TextView personName, category;
        ImageView personImage, star1, star2, star3, star4, star5;
        ConstraintLayout personLayout;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            personName = itemView.findViewById(R.id.service_name);
            category = itemView.findViewById(R.id.category_type);
            personImage = itemView.findViewById(R.id.category_image);
            personLayout = itemView.findViewById(R.id.personLayout);
            star1 = itemView.findViewById(R.id.imageView2);
            star2 = itemView.findViewById(R.id.imageView3);
            star3 = itemView.findViewById(R.id.imageView4);
            star4 = itemView.findViewById(R.id.imageView5);
            star5 = itemView.findViewById(R.id.imageView6);
        }
    }
}
