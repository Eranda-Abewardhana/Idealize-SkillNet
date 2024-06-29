package com.example.skillnet.Activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skillnet.FirebaseHelper.Firebase;
import com.example.skillnet.FirebaseHelper.FirebaseCallback;
import com.example.skillnet.Models.PersonData;
import com.example.skillnet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {
    EditText editTextPassword, editTextEmail, Name, phoneNumber;
    Button button;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    String maxPCode = "";
    ProgressBar progressBar;
    TextView tvAlreadyHaveAccount;
    Firebase firebase ;

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
        firebase = new Firebase();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        Name = findViewById(R.id.name);
        phoneNumber = findViewById(R.id.PhoneNumber);
        editTextPassword = findViewById(R.id.password);
        editTextEmail = findViewById(R.id.email);
        progressBar = findViewById(R.id.progressbar);
        button = findViewById(R.id.SignupButton);
        tvAlreadyHaveAccount = findViewById(R.id.tv_already_have_account);

        CircleImageView circleImageView = findViewById(R.id.circleImageView);

        // Set an OnClickListener on the CircleImageView
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to go back to the login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                // Optionally, finish the current activity
                finish();
            }
        });

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
                String email, password, fullName, Phone;
                email = String.valueOf(editTextEmail.getText()).toLowerCase();
                password = String.valueOf(editTextPassword.getText());
                fullName = String.valueOf(Name.getText());
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

                firebase.getAllUsers(new FirebaseCallback<PersonData>() {
                    @Override
                    public void onCallback(List<PersonData> list) {
                        int maxNumber = 0 ;


                        for (PersonData person : list) {
                            String pCode = person.getpCode();
                            int number = Integer.parseInt(pCode.replace("P",""));

                            if (number > maxNumber) {
                                maxNumber = number;

                            }
                        }
                        maxNumber++;
                        String buffer = "";
                        if(maxNumber < 10) {
                            buffer = "00" ;
                        } else if(10 <= maxNumber && maxNumber< 100 ){
                            buffer = "0";
                        } else if (100 <= maxNumber && maxNumber< 1000 ){
                        }
                        else{
                            Toast.makeText(SignupActivity.this, "Accounts are Full", Toast.LENGTH_SHORT).show();
                            return ;
                        }

                        maxPCode = "P" + buffer + String.valueOf(maxNumber);


                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful()) {

                                            DocumentReference documentReference = fStore.collection("users_signup").document(email);
                                            Map<String, Object> user = new HashMap<>();
                                            user.put("fName", fullName);
                                            user.put("email", email);
                                            user.put("PhoneNumber", Phone);
                                            user.put("user", maxPCode);  // Add the new P code here
                                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
//                                                    Log.d(TAG, "User Profile is Created for Email: " + email);
                                                }
                                            });
                                            DocumentReference documentReference2 = fStore.collection("users").document(maxPCode);
                                            Map<String, Object> user2 = new HashMap<>();
                                            user2.put("imageUrl", "");
                                            user2.put("isworker", false);
                                            user2.put("name", fullName);
                                            user2.put("pCode", maxPCode);
                                            user2.put("stars", 0);
                                            documentReference2.set(user2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d(TAG, "User Profile is Created for Email: " + email);
                                                }
                                            });
                                            Toast.makeText(SignupActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                            Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent2);
                                            finish();
                                        } else {
                                            Toast.makeText(SignupActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onDocumentSnapshotCallback(DocumentSnapshot snapshot) {
                        // Implement if needed
                    }

                    @Override
                    public void onSingleCallback(PersonData item) {
                        // Implement if needed
                    }
                });
            }
        });
    }

    private String generateNewPCode(int maxNumber) {
        int newNumber = maxNumber + 1;
        return "P" + String.format("%03d", newNumber);
    }
}
