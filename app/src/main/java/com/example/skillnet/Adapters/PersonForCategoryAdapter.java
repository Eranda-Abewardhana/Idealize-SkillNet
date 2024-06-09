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
    }

    @Override
    public int getItemCount() {
        return personDataList.size();
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        TextView personName;
        ImageView personImage;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            personName = itemView.findViewById(R.id.service_name);
            personImage = itemView.findViewById(R.id.category_image);
        }
    }
}
