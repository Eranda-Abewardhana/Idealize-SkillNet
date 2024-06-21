package com.example.skillnet.Activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.skillnet.Fragments.SettingFragment;
import com.example.skillnet.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Check if the fragment container is available and add the fragment
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return; // If fragment already added, do nothing
            }

            // Add SettingFragment to the fragment container
            SettingFragment settingFragment = new SettingFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, settingFragment).commit();
        }
    }
}
