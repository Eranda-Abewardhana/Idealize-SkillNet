package com.example.skillnet.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillnet.Fragments.ProfileFragment;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.PersonData;
import com.example.skillnet.Models.Post;
import com.example.skillnet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;
    private List<Categories> categoriesList;
    private List<PersonData> personDataList;
    private Context context;
    private FragmentActivity activity;
    private FirebaseFirestore db;

    public PostAdapter(List<Post> postList, List<Categories> categoriesList, List<PersonData> personDataList, Context context, FragmentActivity activity) {
        this.postList = postList;
        this.categoriesList = categoriesList;
        this.personDataList = personDataList;
        this.context = context;
        this.activity = activity;
        this.db = FirebaseFirestore.getInstance();
        sortPostsByDatetime();
    }

    private void sortPostsByDatetime() {
        Collections.sort(postList, (o1, o2) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ssa", Locale.getDefault());
            try {
                Date date1 = sdf.parse(o1.getDateTime());
                Date date2 = sdf.parse(o2.getDateTime());
                return date2.compareTo(date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });
    }

    @NonNull
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.PostViewHolder holder, int position) {
        Post post = postList.get(position);
        Categories category = findCategoryByCode(post.getCategoryCode());
        PersonData personData = findPersonByCode(post.getUserCode());

        // Bind the data to the views
        holder.bind(post, category, personData);
        checkIfRequested(post.getPostID(), holder);

        holder.profile.setOnClickListener(v -> openProfileFragment(personData));
        holder.requestButton.setOnClickListener(v -> handleRequestButtonClick(post.getPostID(), holder));
        holder.seeMoreButton.setOnClickListener(v -> toggleSeeMore(holder));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    private Categories findCategoryByCode(String code) {
        for (Categories category : categoriesList) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        return new Categories();
    }

    private PersonData findPersonByCode(String code) {
        for (PersonData person : personDataList) {
            if (person.getpCode().equals(code)) {
                return person;
            }
        }
        return new PersonData();
    }

    private void checkIfRequested(String postID, PostViewHolder holder) {
        db.collection("posts").document(postID)
                .collection(postID).document(GlobalVariables.code)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        holder.setRequestedState(true);
                    } else {
                        holder.setRequestedState(false);
                    }
                });
    }

    private void handleRequestButtonClick(String postID, PostViewHolder holder) {
        if (holder.isRequested) {
            holder.setRequestedState(false);
            db.collection("posts").document(postID)
                    .collection(postID).document(GlobalVariables.code)
                    .delete();
        } else {
            holder.setRequestedState(true);
            db.collection("posts").document(postID)
                    .collection(postID).document(GlobalVariables.code)
                    .set(new HashMap<>());
        }
    }

    private void openProfileFragment(PersonData personData) {
        GlobalVariables.otherPersonData = personData;
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new ProfileFragment(true));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void toggleSeeMore(PostViewHolder holder) {
        if (holder.see_more.getVisibility() == View.VISIBLE) {
            holder.see_more.setVisibility(View.GONE);
            holder.seeMoreButton.setText("See More");
        } else {
            holder.see_more.setVisibility(View.VISIBLE);
            holder.seeMoreButton.setText("See Less");
        }
    }

    public void setPostList(List<Post> newPostList) {
        this.postList = newPostList;
        sortPostsByDatetime();
        notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName, location, title, price, category2, datetime, description, contact, name;
        ImageView profileImage, image;
        Button seeMoreButton, requestButton;
        LinearLayout see_more, profile;
        boolean isRequested;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            categoryName = itemView.findViewById(R.id.category1);
            location = itemView.findViewById(R.id.location);
            name = itemView.findViewById(R.id.name);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            category2 = itemView.findViewById(R.id.category2);
            image = itemView.findViewById(R.id.image);
            seeMoreButton = itemView.findViewById(R.id.see_more);
            datetime = itemView.findViewById(R.id.datetime);
            description = itemView.findViewById(R.id.description);
            contact = itemView.findViewById(R.id.contact);
            see_more = itemView.findViewById(R.id.seemoredetails);
            requestButton = itemView.findViewById(R.id.btn_request);
            profile = itemView.findViewById(R.id.profile);
        }

        public void bind(Post post, Categories category, PersonData personData) {
            DecimalFormat decimalFormat = new DecimalFormat("#.00");

            name.setText(TextUtils.isEmpty(personData.getName()) ? "N/A" : personData.getName());
            categoryName.setText(TextUtils.isEmpty(category.getName()) ? "N/A" : category.getName());
            location.setText(TextUtils.isEmpty(post.getLocation()) ? "N/A" : post.getLocation());
            title.setText(TextUtils.isEmpty(post.getTitle()) ? "N/A" : post.getTitle());
            price.setText("Rs " + decimalFormat.format(post.getPrice()));
            category2.setText(TextUtils.isEmpty(category.getName()) ? "N/A" : category.getName());
            datetime.setText(TextUtils.isEmpty(post.getDateTime()) ? "N/A" : post.getDateTime());
            contact.setText(TextUtils.isEmpty(post.getMobileNo()) ? "N/A" : post.getMobileNo());
            description.setText(TextUtils.isEmpty(post.getDescription()) ? "N/A" : post.getDescription());

            if (!TextUtils.isEmpty(personData.getImageUrl())) {
                Picasso.get().load(personData.getImageUrl()).into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.person); // Default image resource
            }

            if (!TextUtils.isEmpty(post.getImageUrl())) {
                Picasso.get().load(post.getImageUrl()).into(image);
            } else {
                Picasso.get().load(category.getUrl()).into(image);
            }
        }

        public void setRequestedState(boolean isRequested) {
            this.isRequested = isRequested;
            if (isRequested) {
                requestButton.setBackgroundResource(R.drawable.button_background_see_more);
                requestButton.setText("Requested");
                requestButton.setTextColor(Color.BLACK);
            } else {
                requestButton.setBackgroundResource(R.drawable.button_background);
                requestButton.setText("Request");
                requestButton.setTextColor(ContextCompat.getColor(requestButton.getContext(), R.color.white));
            }
        }
    }
}
