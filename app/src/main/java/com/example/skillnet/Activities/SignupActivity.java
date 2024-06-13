package com.example.skillnet.Activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skillnet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore ;

import java.util.HashMap;
import java.util.Map;


public class SignupActivity extends AppCompatActivity {
    EditText editTextPassword, editTextEmail , Name , phoneNumber ;
    Button button;
    FirebaseAuth mAuth;
    FirebaseFirestore fstore ;
    ProgressBar progressBar;
    TextView textView, tvAlreadyHaveAccount;
    FirebaseFirestore fStore ;

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
        Name = findViewById(R.id.name);
        phoneNumber = findViewById(R.id.PhoneNumber);
        editTextPassword = findViewById(R.id.password);
        editTextEmail = findViewById(R.id.email);
        String email = editTextEmail.toString();
        progressBar = findViewById(R.id.progressbar);
        button = findViewById(R.id.SignupButton);
        tvAlreadyHaveAccount = findViewById(R.id.tv_already_have_account);
        fStore= FirebaseFirestore.getInstance();

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
                String email, password , fullName,Phone ;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                fullName =  String.valueOf(Name.getText());
                Phone = String.valueOf(phoneNumber.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignupActivity.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignupActivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                    DocumentReference documentReference = fStore.collection("users_signup").document(email);
                                    Map<String,Object> user = new HashMap<>();
                                    user.put("fName",fullName);
                                    user.put("email", email);
                                    user.put("PhoneNumber",Phone);
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG,"User Profile is Created for Email"+email);
                                        }
                                    });
                                    Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent2);
                                    finish();
                                } else {
                                    Toast.makeText(SignupActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
