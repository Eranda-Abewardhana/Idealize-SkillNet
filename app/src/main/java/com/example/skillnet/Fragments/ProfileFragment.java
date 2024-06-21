package com.example.skillnet.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skillnet.Activities.SettingsActivity;
import com.example.skillnet.Activities.edit_profile;
import com.example.skillnet.Activities.SettingsActivity;
import com.example.skillnet.R;

public class ProfileFragment extends Fragment {

    private Button btnEditProfile , btnSettings , btnPostProject ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnPostProject = view.findViewById(R.id.btn_post_project) ;
        btnSettings =view.findViewById(R.id.btn_settings) ;

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set OnClickListener for the Edit Profile button
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the edit_profile activity
                Intent intent = new Intent(getActivity(), edit_profile.class);
                startActivity(intent);
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}
