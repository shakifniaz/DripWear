package com.example.dripwear.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dripwear.Adapter.CartAdapter;
import com.example.dripwear.Helper.ManagementCart;
import com.example.dripwear.R;
import com.example.dripwear.databinding.ActivityCartBinding;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class CartActivity extends AppCompatActivity {
    private ActivityCartBinding binding;
    private double tax;
    private ManagementCart managementCart;
    private ChipNavigationBar bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //singleton
        managementCart = ManagementCart.getInstance(this);
        bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setItemSelected(R.id.cart, true);

        bottomNav.setOnItemSelectedListener(id -> {
            if (id == R.id.home) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            } else if (id == R.id.favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            } else if (id == R.id.profile) {
                startActivity(new Intent(this, CustomerSettingsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        calculatorCart();
        setVariable();
        initCartList();

        //Memento Undo Button
        binding.undoBtn.setOnClickListener(v -> {
            if (managementCart.canUndo()) {
                managementCart.undoLastCartChange();
                initCartList();
                calculatorCart();
            } else {
                Toast.makeText(this, "No previous cart state", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNav != null) {
            bottomNav.setItemSelected(R.id.cart, true);
        }
    }

    private void initCartList() {
        if (managementCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollView4.setVisibility(android.view.View.GONE);
        } else {
            binding.emptyTxt.setVisibility(android.view.View.GONE);
            binding.scrollView4.setVisibility(android.view.View.VISIBLE);
        }

        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(new CartAdapter(
                managementCart.getListCart(),
                this,
                this::calculatorCart,
                managementCart
        ));
    }

    private void setVariable() {
        // optional UI setup
    }

    private void calculatorCart() {
        double percentTax = 0.02;
        double delivery = 10;
        tax = Math.round((managementCart.getTotalFee() * percentTax * 100.0)) / 100.0;
        double total = Math.round((managementCart.getTotalFee() + tax + delivery) * 100.0) / 100.0;
        double itemTotal = Math.round((managementCart.getTotalFee() * 100.0)) / 100.0;

        binding.totalFeeTxt.setText("$ " + itemTotal);
        binding.taxTxt.setText("$ " + tax);
        binding.deliveryTxt.setText("$ " + delivery);
        binding.totalTxt.setText("$ " + total);
    }
}
