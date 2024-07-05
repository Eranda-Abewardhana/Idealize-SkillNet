package com.example.skillnet.Activities;

import androidx.appcompat.app.AppCompatActivity;
import com.example.skillnet.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        // Get references to the buttons
        Button guestButton = findViewById(R.id.button3);
        Button workButton = findViewById(R.id.button4);
        Button hireButton = findViewById(R.id.button5);

        // Set click listeners for each button
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().popBackStack();
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
