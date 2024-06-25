package com.example.skillnet.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.R;
import com.example.skillnet.Models.Categories;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostFragment extends Fragment {

    private ImageView backButton;
    private TextView titleTextView;
    private EditText topicEditText;
    private EditText descriptionEditText;
    private Spinner categorySpinner;
    private EditText locationEditText;
    private EditText priceEditText;
    private ImageButton addImageButton;
    private Button postButton;
    private String selectedCategory = "";
    private FirebaseFirestore fStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        // Initialize views
        backButton = view.findViewById(R.id.imageView2);
        titleTextView = view.findViewById(R.id.title);
        topicEditText = view.findViewById(R.id.topic);
        descriptionEditText = view.findViewById(R.id.description);
        categorySpinner = view.findViewById(R.id.category_spinner);
        locationEditText = view.findViewById(R.id.location);
        priceEditText = view.findViewById(R.id.price);
        addImageButton = view.findViewById(R.id.add_image_button);
        postButton = view.findViewById(R.id.button);
        fStore = FirebaseFirestore.getInstance();

        // Set up any required listeners here
        backButton.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        addImageButton.setOnClickListener(v -> {
            // Handle add image button click
        });

        postButton.setOnClickListener(v -> {
            // Validate inputs
            if (validateInputs()) {
                // Get values from inputs
                String topic = topicEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String location = locationEditText.getText().toString();
                double price = Double.parseDouble(priceEditText.getText().toString());

                // Create a map to store data
                Map<String, Object> post = new HashMap<>();
                post.put("topic", topic);
                post.put("description", description);
                post.put("category", selectedCategory);
                post.put("location", location);
                post.put("price", price);

                // Save data to Firestore
                DocumentReference documentReference = fStore.collection("posts").document();
                documentReference.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Post Created Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Setup category spinner
        setupCategorySpinner();

        return view;
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(topicEditText.getText().toString())) {
            topicEditText.setError("Topic is required");
            return false;
        }
        if (TextUtils.isEmpty(descriptionEditText.getText().toString())) {
            descriptionEditText.setError("Description is required");
            return false;
        }
        if (TextUtils.isEmpty(locationEditText.getText().toString())) {
            locationEditText.setError("Location is required");
            return false;
        }
        if (TextUtils.isEmpty(priceEditText.getText().toString())) {
            priceEditText.setError("Price is required");
            return false;
        }
        if (TextUtils.isEmpty(selectedCategory) || selectedCategory.equals("") || selectedCategory.equals("Select a Category")) {
            Toast.makeText(getContext(), "Category is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setupCategorySpinner() {
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("Select a Category");
        for (Categories category : GlobalVariables.categoriesList) {
            categoryNames.add(category.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle the selected category if needed
                selectedCategory = categoryNames.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when no category is selected
            }
        });
    }
}
