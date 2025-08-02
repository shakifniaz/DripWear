package com.example.dripwear.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dripwear.R;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ChipNavigationBar bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                if (id == R.id.home) {
                    startActivity(new Intent(NotificationsActivity.this, MainActivity.class));
                    finish();
                } else if (id == R.id.favorites) {
                    startActivity(new Intent(NotificationsActivity.this, FavoritesActivity.class));
                    finish();
                } else if (id == R.id.cart) {
                    startActivity(new Intent(NotificationsActivity.this, CartActivity.class));
                    finish();
                }
            }
        });

    }
}