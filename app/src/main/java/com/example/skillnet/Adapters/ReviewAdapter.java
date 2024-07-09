package com.example.skillnet.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skillnet.Fragments.NotificationFragment;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.PersonData;
import com.example.skillnet.Models.ReviewModel;
import com.example.skillnet.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final Context context;
    private List<ReviewModel> reviewList;
    private final FirebaseFirestore fStore;

    public ReviewAdapter(Context context, List<ReviewModel> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
        this.fStore = FirebaseFirestore.getInstance();
        sortReviewsByDatetime();
        notifyDataSetChanged();
    }

    public void updateData(List<ReviewModel> newReviewList) {
        this.reviewList = newReviewList;
        sortReviewsByDatetime();
        notifyDataSetChanged();
    }

    private void sortReviewsByDatetime() {
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

        holder.name.setText(getPersonNameByCode(review.getClientCode()));
        holder.title.setText(review.getTitle() + " " + getCategoryNameByCode(review.getCategoryCode()));
        holder.date.setText(review.getDateTime());

        if (review.getImageUrl() != null) {
            Picasso.get().load(review.getImageUrl()).into(holder.imageView);
        } else {
            // holder.imageView.setImageResource(R.drawable.default_image); // Set a default image if URL is null
        }

        if (review.isReview()) {
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.ratingBar.setIsIndicator(true);
            holder.ratingBar.setRating(review.getStars());
            holder.name.setTextColor(context.getResources().getColor(R.color.gold));
            holder.accept.setVisibility(View.GONE);
            holder.reject.setVisibility(View.GONE);
        } else {
            holder.ratingBar.setVisibility(View.GONE);
            holder.name.setTextColor(context.getResources().getColor(R.color.black));
            holder.accept.setVisibility(View.VISIBLE);
            holder.reject.setVisibility(View.VISIBLE);
            holder.accept.setBackgroundResource(review.isAccept() ? R.drawable.accept : R.drawable.check);
            if (!review.isFindWorker()) {
                holder.itemView.setAlpha(1.0f);
                holder.accept.setClickable(true);
                holder.accept.setEnabled(true);
            } else {
                if (review.isAccept()) {
                    holder.itemView.setAlpha(1.0f);
                    holder.accept.setClickable(true);
                    holder.accept.setEnabled(true);
                } else {
                    holder.itemView.setAlpha(0.5f);
                    holder.accept.setClickable(false);
                    holder.accept.setEnabled(false);
                }
            }
        }

        holder.accept.setOnClickListener(view -> toggleApproval(holder, review, position));
        holder.reject.setOnClickListener(view -> deleteReview(review, position));
    }

    private String getPersonNameByCode(String code) {
        for (PersonData personData : GlobalVariables.personDataList) {
            if (personData.getpCode().equals(code)) {
                return personData.getName();
            }
        }
        return "";
    }

    private String getCategoryNameByCode(String code) {
        for (Categories category : GlobalVariables.categoriesList) {
            if (category.getCode().equals(code)) {
                return category.getName();
            }
        }
        return "";
    }

    private void toggleApproval(ReviewViewHolder holder, ReviewModel review, int position) {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.getDefault());
        Date now2 = new Date();
        String formattedTime2 = sdf2.format(now2);
        DocumentReference documentReference = fStore.collection("posts").document(review.getPostId()).collection(review.getPostId()).document(review.getClientCode());
        Map<String, Object> user = new HashMap<>();
        user.put("approved", !review.isAccept());
        user.put("datetime", formattedTime2);

        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                DocumentReference documentReference = fStore.collection("posts").document(review.getPostId());
                Map<String, Object> user = new HashMap<>();
                user.put("findWorker", !review.isAccept());
                documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        NotificationFragment.fetchNotificationData();
                        notifyDataSetChanged();
                        holder.accept.setBackgroundResource(review.isAccept() ? R.drawable.accept : R.drawable.check);
                        notifyItemChanged(position);
                    }
                });
            }
        });
    }

    private void deleteReview(ReviewModel review, int position) {
        DocumentReference documentReference = fStore.collection("posts")
                .document(review.getPostId())
                .collection(review.getPostId())
                .document(GlobalVariables.code);

        documentReference.delete().addOnSuccessListener(aVoid -> {
            reviewList.remove(review);
            notifyItemRemoved(position);
        }).addOnFailureListener(e -> Log.d("Firestore", "Error deleting document: ", e));
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, accept, reject;
        TextView title, name, date;
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profile_image);
            title = itemView.findViewById(R.id.rtitle);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.rdatetime);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            accept = itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.reject);
        }
    }
}
