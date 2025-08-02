package com.example.dripwear.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dripwear.Adapter.CartAdapter;
import com.example.dripwear.Helper.ChangeNumberItemsListener;
import com.example.dripwear.Helper.ManagmentCart;
import com.example.dripwear.R;
import com.example.dripwear.databinding.ActivityCartBinding;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class CartActivity extends AppCompatActivity {
    private ActivityCartBinding binding;
    private double tax;
    private ManagmentCart managementCart;
    private ChipNavigationBar bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCartBinding.inflate(getLayoutInflater()); //This line was modified
        setContentView(binding.getRoot());

        managementCart = new ManagmentCart(this);

        bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setItemSelected(R.id.cart, true);

        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                if (id == R.id.home) {
                    startActivity(new Intent(CartActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else if (id == R.id.favorites) {
                    startActivity(new Intent(CartActivity.this, FavoritesActivity.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else if (id == R.id.profile) {
                    startActivity(new Intent(CartActivity.this, CustomerSettingsActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        calculatorCart();
        setVariable();
        initCartList();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNav != null) {
            bottomNav.setItemSelected(R.id.cart, true);
        }
    }

    private void initCartList() {
        if (managementCart.getListCart().isEmpty()){
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollView4.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollView4.setVisibility(View.VISIBLE);
        }

        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(new CartAdapter(
                managementCart.getListCart(),
                this,
                this::calculatorCart,
                managementCart  // Add this parameter
        ));
    }

    private void setVariable() {
        //binding.backBtn.setOnClickListener(v -> finish());
    }

    private void calculatorCart() {
        double percentTax = 0.02;
        double delivery = 10;
        tax = Math.round((managementCart.getTotalFee()*percentTax*100.0))/100.0;
        double total = Math.round((managementCart.getTotalFee()+tax+delivery)*100.0)/100.0;
        double itemTotal = Math.round((managementCart.getTotalFee()*100.0))/100.0;

        binding.totalFeeTxt.setText("$ "+itemTotal);
        binding.taxTxt.setText("$ "+delivery);
        binding.deliveryTxt.setText("$ "+delivery);
        binding.totalTxt.setText("$ "+total);
    }
}