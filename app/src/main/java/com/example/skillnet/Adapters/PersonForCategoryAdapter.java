package com.example.skillnet.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillnet.Models.PersonData;
import com.example.skillnet.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PersonForCategoryAdapter extends RecyclerView.Adapter<PersonForCategoryAdapter.PersonViewHolder> {

    private List<PersonData> personDataList;

    public PersonForCategoryAdapter(List<PersonData> personDataList) {
        this.personDataList = personDataList;
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
        Picasso.get().load(person.getImageUrl()).into(holder.personImage);

        switch (person.getStars()){
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

        TextView personName;
        ImageView personImage,star1, star2, star3, star4, star5;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            personName = itemView.findViewById(R.id.service_name);
            personImage = itemView.findViewById(R.id.category_image);
            star1       = itemView.findViewById(R.id.imageView2);
            star2       = itemView.findViewById(R.id.imageView3);
            star3       = itemView.findViewById(R.id.imageView4);
            star4       = itemView.findViewById(R.id.imageView5);
            star5       = itemView.findViewById(R.id.imageView6);
        }
    }
}
