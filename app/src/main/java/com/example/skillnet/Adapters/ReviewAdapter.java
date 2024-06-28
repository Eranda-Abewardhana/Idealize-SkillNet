package com.example.skillnet.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.PersonData;
import com.example.skillnet.Models.Post;
import com.example.skillnet.Models.ReviewModel;
import com.example.skillnet.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<ReviewModel> reviewList;

    public ReviewAdapter(Context context, List<ReviewModel> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
        sortPostsByDatetime();
    }
    private void sortPostsByDatetime() {
        Collections.sort(reviewList, new Comparator<ReviewModel>() {
            @Override
            public int compare(ReviewModel o1, ReviewModel o2) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ssa", Locale.getDefault());
                try {
                    Date date1 = sdf.parse(o1.getDateTime());
                    Date date2 = sdf.parse(o2.getDateTime());
                    return date2.compareTo(date1); // Sort in descending order
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
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
        for (PersonData personData : GlobalVariables.personDataList){
            if(personData.getpCode().equals(review.getClientCode())){
                holder.name.setText(personData.getName());
                break;
            }
        }
        for (Categories categories : GlobalVariables.categoriesList){
            if(categories.getCode().equals(review.getCategoryCode())){
                holder.title.setText(categories.getName());
                break;
            }
        }
        holder.date.setText(review.getDateTime());

        if(review.getImageUrl() != null) {
            Picasso.get().load(review.getImageUrl()).into(holder.imageView);
        }
        else {

        }
        if(review.isReview()){
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.ratingBar.setIsIndicator(true);
            holder.ratingBar.setRating(review.getStars());
            holder.ratingBar.setNumStars(5);
            holder.name.setTextColor(context.getResources().getColor(R.color.gold));
        }
        else {
            holder.ratingBar.setVisibility(View.GONE);
            holder.name.setTextColor(context.getResources().getColor(R.color.black));
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
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profile_image);
            title = itemView.findViewById(R.id.rtitle);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.rdatetime);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}

