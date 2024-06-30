package com.example.skillnet.Activities;

import androidx.appcompat.app.AppCompatActivity;
import com.example.skillnet.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // Find the button by its ID
        Button continueButton = findViewById(R.id.button2);

        // Set an OnClickListener on the button
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the next activity
                Intent intent = new Intent(LandingActivity.this, OptionActivity.class);
                startActivity(intent);
            }
        });
    }
}
