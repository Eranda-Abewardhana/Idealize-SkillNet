package com.example.skillnet.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillnet.Fragments.ChatFragment;
import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.PersonData;
import com.example.skillnet.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    private List<PersonData> personDataList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClicked(PersonData person, int position);
    }

    public ChatListAdapter(List<PersonData> personDataList, OnItemClickListener onItemClickListener) {
        this.personDataList = personDataList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
        PersonData person = personDataList.get(position);
        holder.personName.setText(person.getName());
        Picasso.get().load(person.getImageUrl()).into(holder.personImage);

        // Set click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClicked(person, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return personDataList.size();
    }

    public static class ChatListViewHolder extends RecyclerView.ViewHolder {

        TextView personName, category;
        ImageView personImage;
        LinearLayout layout;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            personName = itemView.findViewById(R.id.name);
            personImage = itemView.findViewById(R.id.img);
            category = itemView.findViewById(R.id.category_emp);
            layout = itemView.findViewById(R.id.chatitem);
        }
    }
}
