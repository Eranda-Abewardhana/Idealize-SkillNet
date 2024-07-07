package com.example.skillnet.Activities;

import androidx.appcompat.app.AppCompatActivity;
import com.example.skillnet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class OptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPrefs", MODE_PRIVATE);
        boolean isGust = sharedPreferences.getBoolean("isGust", false);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null || isGust) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Get references to the buttons
        Button guestButton = findViewById(R.id.button3);
        Button workButton = findViewById(R.id.button4);
        Button hireButton = findViewById(R.id.button5);
        ImageView back     = findViewById(R.id.imageView2);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().popBackStack();
            }
        });

        // Set click listeners for each button
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("isGust", true);
                editor.apply(); // or editor.commit();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        workButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle I want to work button click
                Intent workIntent = new Intent(OptionActivity.this, SignupActivity.class);
                workIntent.putExtra("isWorker", true);
                startActivity(workIntent);
            }
        });

        hireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle I want to hire button click
                Intent hireIntent = new Intent(OptionActivity.this, SignupActivity.class);
                hireIntent.putExtra("isWorker", false);
                startActivity(hireIntent);
            }
        });
    }
}
