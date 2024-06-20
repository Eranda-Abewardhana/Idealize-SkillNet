package com.example.skillnet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.PersonData;
import com.example.skillnet.Models.Post;
import com.example.skillnet.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList = new ArrayList<>();
    private List<Categories> categoriesList;
    private List<PersonData> personDataList; // Assuming you get this list from somewhere
    private Context context;

    public PostAdapter(List<Post> postList, List<Categories> categoriesList, List<PersonData> personDataList, Context context) {
        this.postList = postList;
        this.categoriesList = categoriesList;
        this.personDataList = personDataList;
        this.context = context;
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
        Categories category = new Categories();
        PersonData personData = new PersonData();

        for(Categories categories : categoriesList){
            if(post.getCategoryCode().equals(categories.getCode())){
                category = categories;
            }
        }
        for(PersonData person : personDataList){
            if(post.getUserCode().equals(person.getpCode())){
                personData = person;
            }
        }

        // Bind the data to the views
        holder.categoryName.setText(category.getName());
        holder.location.setText(post.getLocation());
        holder.title.setText(post.getTitle());
        holder.price.setText("Rs " + String.valueOf(post.getPrice()));
        holder.category2.setText(category.getName());
        holder.datetime.setText(post.getDateTime());
        holder.contact.setText(post.getMobileNo());
        holder.description.setText(post.getDescription());
        Picasso.get().load(personData.getImageUrl()).into(holder.profileImage);
        Picasso.get().load(category.getUrl()).into(holder.image);

        // Set the see more button action
        holder.seeMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.see_more.getVisibility() == View.VISIBLE) {
                    // If layout is visible, hide it and change button text to "See More"
                    holder.see_more.setVisibility(View.GONE);
                    holder.seeMoreButton.setText("See More");
                } else {
                    // If layout is not visible, show it and change button text to "See Less"
                    holder.see_more.setVisibility(View.VISIBLE);
                    holder.seeMoreButton.setText("See Less");
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName, location, title, price, category2, datetime, description, contact;
        ImageView profileImage, image;
        Button seeMoreButton;
        LinearLayout see_more;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image);
            categoryName = itemView.findViewById(R.id.category1);
            location = itemView.findViewById(R.id.location);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            category2 = itemView.findViewById(R.id.category2);
            image = itemView.findViewById(R.id.image);
            seeMoreButton = itemView.findViewById(R.id.see_more);
            datetime = itemView.findViewById(R.id.datetime);
            description = itemView.findViewById(R.id.description);
            contact = itemView.findViewById(R.id.contact);
            see_more = itemView.findViewById(R.id.seemoredetails);
        }
    }
}
