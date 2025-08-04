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
        binding = ActivityCartBinding.inflate(getLayoutInflater()); //Inflate the layout using View Binding.
        setContentView(binding.getRoot());

        //Initialize the cart management helper class
        managementCart = new ManagmentCart(this);

        bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setItemSelected(R.id.cart, true);

        //Setup the bottom navigation to handle different activity transitions
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
                    finish();
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
            //Ensure the cart item is always selected when returning to this activity
            bottomNav.setItemSelected(R.id.cart, true);
        }
    }

    private void initCartList() {
        //Toggle visibility of the empty cart message or the cart list
        if (managementCart.getListCart().isEmpty()){
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollView4.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollView4.setVisibility(View.VISIBLE);
        }

        //Set up the RecyclerView for the cart items
        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //Create and set the adapter for the cart, passing the listener for updates
        binding.cartView.setAdapter(new CartAdapter(
                managementCart.getListCart(),
                this,
                this::calculatorCart,
                managementCart
        ));
    }

    private void setVariable() {
        //binding.backBtn.setOnClickListener(v -> finish());
    }

    private void calculatorCart() {
        //Define the tax rate
        double percentTax = 0.02;
        //Define the fixed delivery fee
        double delivery = 10;
        //Calculate the tax based on the total cart value
        tax = Math.round((managementCart.getTotalFee()*percentTax*100.0))/100.0;
        //Calculate the final total including tax and delivery
        double total = Math.round((managementCart.getTotalFee()+tax+delivery)*100.0)/100.0;
        //Get the total price of all items before tax and delivery
        double itemTotal = Math.round((managementCart.getTotalFee()*100.0))/100.0;

        //Update the UI with the calculated values
        binding.totalFeeTxt.setText("$ "+itemTotal);
        binding.taxTxt.setText("$ "+delivery);
        binding.deliveryTxt.setText("$ "+delivery);
        binding.totalTxt.setText("$ "+total);
    }
}
