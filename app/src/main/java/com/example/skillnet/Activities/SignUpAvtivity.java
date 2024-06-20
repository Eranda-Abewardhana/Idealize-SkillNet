package com.example.skillnet.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skillnet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpAvtivity extends AppCompatActivity {
    EditText editTextPassword, editTextEmail;
    Button button;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView, tvAlreadyHaveAccount;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        editTextPassword = findViewById(R.id.password);
        editTextEmail = findViewById(R.id.email);
        progressBar = findViewById(R.id.progressbar);
        button = findViewById(R.id.SignupButton);
        tvAlreadyHaveAccount = findViewById(R.id.tv_already_have_account);

        // Set clickable text for "Log in"
        tvAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
               startActivity(intent);
           }
       });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpAvtivity.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpAvtivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpAvtivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                    Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent2);
                                    finish();
                                } else {
                                    Toast.makeText(SignUpAvtivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
