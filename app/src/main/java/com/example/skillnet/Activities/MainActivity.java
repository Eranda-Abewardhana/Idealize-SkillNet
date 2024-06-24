package com.example.skillnet.Activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.skillnet.Adapters.ViewPagerAdapter;
import com.example.skillnet.FirebaseHelper.Firebase;
import com.example.skillnet.FirebaseHelper.FirebaseCallback;
import com.example.skillnet.Fragments.ChatBotFragment;
import com.example.skillnet.Fragments.HomePageFragment;
import com.example.skillnet.Fragments.NotificationFragment;
import com.example.skillnet.Fragments.ProfileFragment;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

import javax.microedition.khronos.opengles.GL;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private FirebaseAuth auth;
    private Button button;
    private FirebaseUser user;

    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        firebase = new Firebase();
        user = auth.getCurrentUser();

        if (user != null) {
            GlobalVariables.email = user.getEmail();
        }

        button = findViewById(R.id.logout);

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.viewpager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPagerAdapter.addFragment(new HomePageFragment());
        viewPagerAdapter.addFragment(new ChatBotFragment());
        viewPagerAdapter.addFragment(new NotificationFragment());
        viewPagerAdapter.addFragment(new ProfileFragment());
        viewPager2.setAdapter(viewPagerAdapter);

        int color = ContextCompat.getColor(this, R.color.lite_black);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setIcon(R.drawable.home);
                    tab.getIcon().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    break;
                case 1:
                    tab.setIcon(R.drawable.robot);
                    tab.getIcon().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    break;
                case 2:
                    tab.setIcon(R.drawable.application);
                    tab.getIcon().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    break;
                case 3:
                    tab.setIcon(R.drawable.profile);
                    tab.getIcon().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    break;
            }
        }).attach();
    }

}
