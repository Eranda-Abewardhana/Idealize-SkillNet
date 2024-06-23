package com.example.skillnet.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skillnet.Models.Project;
import com.example.skillnet.R;

import java.util.List;

public class ServiceGridAdapter extends RecyclerView.Adapter<ServiceGridAdapter.ViewHolder> {

    private Context context;
    private List<Project> projectList;

    public ServiceGridAdapter(Context context, List<Project> projectList) {
        this.context = context;
        this.projectList = projectList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_services_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Project project = projectList.get(position);
        if(project.getTitle() != null)
            holder.titleTextView.setText(project.getTitle());
        if(project.getDescription() != null)
            holder.descriptionTextView.setText(project.getDescription());
        Glide.with(context)
                .load(project.getImageUrl())
                .placeholder(R.drawable.placeholder) // Placeholder image
                .error(R.drawable.img_3)            // Error image
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView, descriptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.service_image);
            titleTextView = itemView.findViewById(R.id.project_title);
            descriptionTextView = itemView.findViewById(R.id.project_description);
        }
    }
}
