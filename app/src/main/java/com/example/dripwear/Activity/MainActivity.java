package com.example.dripwear.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.bumptech.glide.Glide;
import com.example.dripwear.Adapter.CategoryAdapter;
import com.example.dripwear.Adapter.PopularAdapter;
import com.example.dripwear.Adapter.SliderAdapter;
import com.example.dripwear.Domain.BannerModel;
import com.example.dripwear.R;
import com.example.dripwear.ViewModel.MainViewModel;
import com.example.dripwear.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private TextView userNameTextView;
    private ImageView profileImageView;
    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new MainViewModel();

        //Initialize the various UI views
        userNameTextView = findViewById(R.id.textView5);
        profileImageView = findViewById(R.id.imageView2);
        mAuth = FirebaseAuth.getInstance();

        //Check for a logged-in user
        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
            //Set up Firebase database reference
            mCustomerDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child("Customers")
                    .child(userID);
            //Get user name and image
            getUserName();
            getUserProfileImage();
        }

        //User name click listener
        userNameTextView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CustomerSettingsActivity.class);
            startActivity(intent);
        });

        //Initialize all the components
        initCategory();
        initSlider();
        initPopular();
        //Set up the bottom navigation
        bottomNavigation();

        ImageView bellIcon = findViewById(R.id.imageView5);
        //Notification bell icon click
        bellIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });
    }

    private void getUserProfileImage() {
        //Fetch user profile image
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("profileImageUrl")) {
                    String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);
                    //Load image with Glide
                    Glide.with(getApplicationContext())
                            .load(profileImageUrl)
                            .centerCrop()
                            .into(profileImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Show error loading image
                Toast.makeText(MainActivity.this,
                        "Failed to load profile image: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bottomNavigation() {
        //Set initial bottom navigation item
        binding.bottomNavigation.setItemSelected(R.id.home, true);
        //Handle bottom navigation clicks
        binding.bottomNavigation.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener(){
            @Override
            public void onItemSelected(int id) {
                if (id == R.id.favorites) {
                    startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else if (id == R.id.cart) {
                    startActivity(new Intent(MainActivity.this, CartActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else if (id == R.id.profile) {
                    startActivity(new Intent(MainActivity.this, CustomerSettingsActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
            }
        });

        //Floating cart button click
        binding.cartBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
            binding.bottomNavigation.setItemSelected(R.id.cart, true);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Ensure home is selected
        binding.bottomNavigation.setItemSelected(R.id.home, true);
    }

    private void initPopular() {
        //Load popular items
        binding.progressBarPopular.setVisibility(View.VISIBLE);
        viewModel.loadPopular().observeForever(itemsModels -> {
            if(!itemsModels.isEmpty()){
                binding.popularView.setLayoutManager(
                        new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false));
                binding.popularView.setAdapter(new PopularAdapter(itemsModels));
                binding.popularView.setNestedScrollingEnabled(true);
            }
            binding.progressBarPopular.setVisibility(View.GONE);
        });
        viewModel.loadPopular();
    }

    private void initSlider() {
        //Load banner data
        binding.progressBarSlider.setVisibility(View.VISIBLE);
        viewModel.loadBanner().observeForever(bannerModels -> {
            if(bannerModels!=null && !bannerModels.isEmpty()){
                //Set up the ViewPager2
                banners(bannerModels);
                binding.progressBarSlider.setVisibility(View.GONE);
            }
        });
        viewModel.loadBanner();
    }

    private void banners(ArrayList<BannerModel> bannerModels) {
        //Set the adapter
        binding.viewPagerSlider.setAdapter(new SliderAdapter(bannerModels,binding.viewPagerSlider));
        //Configure ViewPager2 appearance
        binding.viewPagerSlider.setClipToPadding(false);
        binding.viewPagerSlider.setClipChildren(false);
        binding.viewPagerSlider.setOffscreenPageLimit(3);
        binding.viewPagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        //Apply page transformer for spacing
        CompositePageTransformer compositePageTransformer=new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));

        binding.viewPagerSlider.setPageTransformer(compositePageTransformer);
    }

    private void initCategory() {
        //Load category data
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        viewModel.loadCategory().observeForever(categoryModels -> {
            binding.categoryView.setLayoutManager(new LinearLayoutManager(
                    MainActivity.this, LinearLayoutManager.HORIZONTAL,false));
            binding.categoryView.setAdapter(new CategoryAdapter(categoryModels));
            binding.categoryView.setNestedScrollingEnabled(true);
            binding.progressBarCategory.setVisibility(View.GONE);
        });
    }

    private void getUserName() {
        //Fetch user name
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("name")) {
                    String name = snapshot.child("name").getValue(String.class);
                    userNameTextView.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Show name loading error
                Toast.makeText(MainActivity.this,
                        "Failed to load user name: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
