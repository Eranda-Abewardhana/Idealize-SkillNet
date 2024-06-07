package com.example.skillnet.Activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.skillnet.Adapters.ViewPagerAdapter;
import com.example.skillnet.Fragments.ChatBotFragment;
import com.example.skillnet.Fragments.HomePageFragment;
import com.example.skillnet.Fragments.NotificationFragment;
import com.example.skillnet.Fragments.ProfileFragment;
import com.example.skillnet.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.viewpager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPagerAdapter.addFragment(new HomePageFragment());
        viewPagerAdapter.addFragment(new ChatBotFragment());
        viewPagerAdapter.addFragment(new NotificationFragment());
        viewPagerAdapter.addFragment(new ProfileFragment());
        viewPager2.setAdapter(viewPagerAdapter);

        int color = ContextCompat.getColor(this, R.color.lite_black);

        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
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
