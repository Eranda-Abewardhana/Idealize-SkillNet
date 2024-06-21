package com.example.skillnet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.ReviewModel;
import com.example.skillnet.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<ReviewModel> reviewList;

    public ReviewAdapter(Context context, List<ReviewModel> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_notification_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewModel review = reviewList.get(position);
        holder.title.setText(review.getTitle());
        for (Categories categories : GlobalVariables.categoriesList){
            if(categories.getCode().equals(categories.getName())){
                holder.name.setText(review.getCategoryCode());
            }
        }
        holder.date.setText(review.getDate());

        if(review.getImageUrl() != null) {
            Picasso.get().load(review.getImageUrl()).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title;
        TextView name;
        TextView date;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profile_image);
            title = itemView.findViewById(R.id.rtitle);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.rdatetime);
        }
    }
}

