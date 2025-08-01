package com.example.dripwear.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.dripwear.Adapter.FavoritesAdapter;
import com.example.dripwear.Helper.ManagmentFavorites;
import com.example.dripwear.R;
import com.example.dripwear.databinding.ActivityFavoritesBinding;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class FavoritesActivity extends AppCompatActivity {
    private ActivityFavoritesBinding binding;
    private ManagmentFavorites managementFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managementFavorites = new ManagmentFavorites(this);

        ChipNavigationBar bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setItemSelected(R.id.favorites, true);

        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                if (id == R.id.home) {
                    startActivity(new Intent(FavoritesActivity.this, MainActivity.class));
                    //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else if (id == R.id.cart) {
                    startActivity(new Intent(FavoritesActivity.this, CartActivity.class));
                    //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        initFavoritesList();
        setVariable();
    }

    private void initFavoritesList() {
        if (managementFavorites.getListFav().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollView4.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollView4.setVisibility(View.VISIBLE);
        }

        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(new FavoritesAdapter(managementFavorites.getListFav(), this));    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
    }
}