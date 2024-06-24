package com.example.skillnet.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.skillnet.Activities.LoginActivity;
import com.example.skillnet.Activities.MainActivity;
import com.example.skillnet.FirebaseHelper.Firebase;
import com.example.skillnet.FirebaseHelper.FirebaseCallback;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.Models.PersonData;
import com.example.skillnet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class SettingFragment extends Fragment {

    private ImageView backButton;
    private TextView settingsTitle;
    private LinearLayout addAccountRow;
    private Switch switchAccountRow;
    private LinearLayout logOutRow;
    private GlobalVariables globalVariables;
    private Firebase firebase;
    private String code = "";
    private FirebaseAuth auth ;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backButton = view.findViewById(R.id.imageView2);
        settingsTitle = view.findViewById(R.id.textView2);
        addAccountRow = view.findViewById(R.id.add);
        switchAccountRow = view.findViewById(R.id.switch1);
        logOutRow = view.findViewById(R.id.logout);
        firebase = new Firebase();
        auth =  FirebaseAuth.getInstance();

        globalVariables = new ViewModelProvider(requireActivity()).get(GlobalVariables.class);

        // Set initial state of switch based on isWorker value
        switchAccountRow.setChecked(GlobalVariables.isWorker);

        // Set up any necessary listeners and handlers
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });

        addAccountRow.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            GlobalVariables.addAccount = true;
        });

        switchAccountRow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update isWorker state in GlobalVariables

            // Display toast message based on state change
            String message = isChecked ? "Worker mode Turned On" : "Worker mode Turned Off";
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

            // Revert to default mood if isChecked is false
            GlobalVariables.isWorker = !GlobalVariables.isWorker;
            firebase.updateDocumentValue("users", GlobalVariables.code, "isworker", GlobalVariables.isWorker, new FirebaseCallback() {
                @Override
                public void onCallback(List list) {

                }

                @Override
                public void onDocumentSnapshotCallback(DocumentSnapshot snapshot) {

                }

                @Override
                public void onSingleCallback(Object item) {

                }
            });

        });


        logOutRow.setOnClickListener(v -> {
            // Handle logout row click
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

    }
}
