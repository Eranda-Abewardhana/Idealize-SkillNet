package com.example.skillnet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.ReviewModel;
import com.example.skillnet.R;

import java.util.List;

public class ReviewProfileAdapter extends RecyclerView.Adapter<ReviewProfileAdapter.ReviewViewHolder> {

    private Context context;
    private List<ReviewModel> reviewList;

    public ReviewProfileAdapter(Context context, List<ReviewModel> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewModel review = reviewList.get(position);

        holder.reviewTitle.setText(review.getTitle());
        holder.reviewDateTime.setText(review.getDateTime());
        holder.reviewDescription.setText(review.getDescription());
        holder.reviewStars.setText(review.getStars() + " Stars");
        for (Categories categories : GlobalVariables.categoriesList) {
            if (categories.getCode().equals(categories.getCode())) {
                holder.reviewCategory.setText(categories.getName());
                break;
            }
        }
        holder.ratingBar.setRating(review.getStars());

        // Load the image using Glide or any other image loading library
        Glide.with(context).load(review.getImageUrl()).into(holder.reviewImage);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView reviewTitle, reviewDateTime, reviewDescription, reviewStars, reviewCategory;
        ImageView reviewImage;
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            reviewTitle = itemView.findViewById(R.id.review_title);
            reviewDateTime = itemView.findViewById(R.id.review_date_time);
            reviewDescription = itemView.findViewById(R.id.review_description);
            reviewStars = itemView.findViewById(R.id.review_stars);
            reviewCategory = itemView.findViewById(R.id.review_category);
            reviewImage = itemView.findViewById(R.id.review_image);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
