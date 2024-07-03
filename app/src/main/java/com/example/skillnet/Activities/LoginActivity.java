package com.example.skillnet.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_STORAGE = 112;
    private TextInputEditText editTextPassword, editTextEmail;
    private Button buttonLogin;
    private ImageView back;
    private ProgressBar progressBar;
    private TextView textView;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private GlobalVariables globalVariables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        editTextPassword = findViewById(R.id.password);
        editTextEmail = findViewById(R.id.email);
        progressBar = findViewById(R.id.progressbar);
        buttonLogin = findViewById(R.id.LoginButton);
        back = findViewById(R.id.backButton);
        textView = findViewById(R.id.SigninNow);

        // Initialize GlobalVariables ViewModel
        globalVariables = new ViewModelProvider(this).get(GlobalVariables.class);

        // Back button click listener
        back.setOnClickListener(v -> {
            user = auth.getCurrentUser();
            if (user == null) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                GlobalVariables.addAccount = false;
                startActivity(intent);
                finish();
            }
        });

        // Check and request permissions if necessary
        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermissions();
        }

        // Create a temporary file
        createTemporaryFile();

        // Sign up TextView click listener
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
            startActivity(intent);
            finish();
        });

        // Login button click listener
        buttonLogin.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Authenticate user
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();

        // Redirect to MainActivity if user is already logged in and no new account is being added
        if (currentUser != null && !GlobalVariables.addAccount) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Method to check and request WRITE_EXTERNAL_STORAGE permission
    private void checkAndRequestPermissions() {
        int permissionWriteStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        }
    }

    // Method to create a temporary file
    private void createTemporaryFile() {
        File cacheDir = this.getCacheDir();
        File myFile = new File(cacheDir, "my_temp_file.txt");

        try (FileOutputStream fos = new FileOutputStream(myFile)) {
            fos.write("Hello, World!".getBytes());
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
