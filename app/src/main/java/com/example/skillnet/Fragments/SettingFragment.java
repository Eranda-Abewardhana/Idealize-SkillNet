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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.skillnet.Activities.LoginActivity;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingFragment extends Fragment {

    private ImageView backButton;
    private TextView settingsTitle;
    private LinearLayout addAccountRow;
    private Switch switchAccountRow;
    private LinearLayout logOutRow;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Handle fragment arguments if needed
        }
    }

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

        // Set up any necessary listeners and handlers
        backButton.setOnClickListener(v -> {
            // Handle back button click
        });

        addAccountRow.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            GlobalVariables.addAccount = true;

        });

        switchAccountRow.setOnClickListener(v -> {
            // Handle switch account row click
            switchAccount();
        });

        logOutRow.setOnClickListener(v -> {
            // Handle logout row click
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
    }

    private void switchAccount() {
        // Sign out the current user
        FirebaseAuth.getInstance().signOut();

        // Toggle the account type
        if (GlobalVariables.isWorker) {
            GlobalVariables.isWorker = false;

        } else {
            GlobalVariables.isWorker = true;

        }

        // Start LoginActivity to sign in with the new account type
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}