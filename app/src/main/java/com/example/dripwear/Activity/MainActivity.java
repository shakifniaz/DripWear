package com.example.dripwear.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.dripwear.Domain.ItemsModel;
import com.example.dripwear.Domain.OrderComposite;
import com.example.dripwear.Domain.OrderItem;
import com.example.dripwear.Domain.ProductCatalog;
import com.example.dripwear.Helper.CartObserver;
import com.example.dripwear.Helper.FirebaseManager;
import com.example.dripwear.Helper.ManagementCart;
import com.example.dripwear.R;
import com.example.dripwear.ViewModel.MainViewModel;
import com.example.dripwear.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CartObserver {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private TextView userNameTextView;
    private ImageView profileImageView;
    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;
    private String userID;
    private ProductCatalog productCatalog;
    private ManagementCart managementCart;
    private long lastToastTime = 0;
    private static final long TOAST_DEBOUNCE_DELAY = 1000; // 1 second
    private String lastToastMessage = "";

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

        // Initialize cart management and register observer (Observer Pattern)
        managementCart = ManagementCart.getInstance(this);
        managementCart.registerCartObserver(this);

        //Check for a logged-in user
        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();

            // Using FirebaseManager instead of direct Firebase calls
            getUserNameWithFacade();
            getUserProfileImageWithFacade();
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

        // Initialize product catalog with actual app data (Iterator Pattern)
        initializeProductCatalogWithRealData();
    }

    // Initialize product catalog with actual app products (Iterator Pattern)
    private void initializeProductCatalogWithRealData() {
        productCatalog = new ProductCatalog();

        // Use the actual products from your ViewModel instead of demo data
        viewModel.loadPopular().observeForever(itemsModels -> {
            if (itemsModels != null && !itemsModels.isEmpty()) {
                // Add all actual products to the catalog
                for (ItemsModel product : itemsModels) {
                    productCatalog.addProduct(product);
                }

                // Demonstrate iterator pattern with real products
                Log.d("IteratorPattern", "=== Actual Products in Catalog ===");
                Log.d("IteratorPattern", "Total products loaded: " + productCatalog.getProductCount());

                // Iterate through all actual products
                for (ItemsModel product : productCatalog) {
                    Log.d("IteratorPattern", "Product: " + product.getTitle() +
                            " - $" + product.getPrice() + " - Rating: " + product.getRating());
                }

                // Find high-rated products from actual data
                Log.d("IteratorPattern", "=== High-Rated Actual Products (4.0+ rating) ===");
                var highRatedIterator = productCatalog.getHighRatedProductsIterator(4.0);
                int highRatedCount = 0;
                while (highRatedIterator.hasNext()) {
                    ItemsModel product = highRatedIterator.next();
                    highRatedCount++;
                    Log.d("IteratorPattern", "Highly Rated: " + product.getTitle() +
                            " - Rating: " + product.getRating());
                }
                Log.d("IteratorPattern", "Found " + highRatedCount + " high-rated products");

                // Find discounted products
                Log.d("IteratorPattern", "=== Products with Special Pricing ===");
                var discountedIterator = productCatalog.getDiscountedProductsIterator();
                int discountedCount = 0;
                while (discountedIterator.hasNext()) {
                    ItemsModel product = discountedIterator.next();
                    discountedCount++;
                    Log.d("IteratorPattern", "Discounted: " + product.getTitle() +
                            " - Strategy: " + product.getPricingStrategyName());
                }
                Log.d("IteratorPattern", "Found " + discountedCount + " products with special pricing");
            }
        });
    }

    // Create sample order using actual cart items (Composite Pattern)
    private void createSampleOrderFromCart() {
        ArrayList<ItemsModel> cartItems = managementCart.getListCart();

        if (!cartItems.isEmpty()) {
            // Create main order composite from actual cart items
            OrderComposite mainOrder = new OrderComposite("Current Cart Order");

            // Add actual cart items to the order
            for (ItemsModel cartItem : cartItems) {
                OrderItem orderItem = new OrderItem(cartItem, cartItem.getNumberInCart());
                mainOrder.addComponent(orderItem);
            }

            // Display order details using composite pattern
            Log.d("CompositePattern", "=== Actual Cart Order Details ===");
            mainOrder.displayOrderDetails();

            // Demonstrate composite properties with real data
            Log.d("CompositePattern", "Total Items in Cart: " + mainOrder.getTotalItemsCount());
            Log.d("CompositePattern", "Cart Total Price: $" + mainOrder.getPrice());

        } else {
            Log.d("CompositePattern", "Cart is empty - no order to display");
        }
    }

    // Debounce mechanism to prevent multiple toasts
    private void showSingleToast(String message) {
        long currentTime = System.currentTimeMillis();

        // Only show toast if enough time has passed AND it's a different message
        if (currentTime - lastToastTime > TOAST_DEBOUNCE_DELAY || !message.equals(lastToastMessage)) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            lastToastTime = currentTime;
            lastToastMessage = message;
            Log.d("ToastDebounce", "Showing toast: " + message);
        } else {
            Log.d("ToastDebounce", "Suppressed duplicate toast: " + message);
        }
    }

    // CartObserver implementation methods (Observer Pattern)
    @Override
    public void onCartUpdated(int itemCount, double totalAmount) {
        // Update UI or perform actions when cart changes
        runOnUiThread(() -> {
            Log.d("CartObserver", "Cart updated: " + itemCount + " items, Total: $" + totalAmount);

            // Update order view when cart changes (Composite Pattern)
            createSampleOrderFromCart();

            // Show subtle notification for cart updates
            if (itemCount > 0) {
                // You could update a cart badge here
                // updateCartBadge(itemCount);
            }
        });
    }

    @Override
    public void onItemAdded(String itemTitle, double itemPrice) {
        runOnUiThread(() -> {
            Log.d("CartObserver", "Item added to cart: " + itemTitle + " - $" + itemPrice);

            //showSingleToast("Added to cart: " + itemTitle);
        });
    }

    @Override
    public void onItemRemoved(String itemTitle) {
        runOnUiThread(() -> {
            Log.d("CartObserver", "Item removed from cart: " + itemTitle);

            //showSingleToast("Removed: " + itemTitle);
        });
    }


    //get user name using FirebaseManager facade
    private void getUserNameWithFacade() {
        FirebaseManager.getInstance().getUserData(userID, new FirebaseManager.SimpleCallback() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("name")) {
                    String name = snapshot.child("name").getValue(String.class);
                    userNameTextView.setText(name);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, "Error loading name: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

     //Get user profile image using FirebaseManager facade
    private void getUserProfileImageWithFacade() {
        FirebaseManager.getInstance().getUserData(userID, new FirebaseManager.SimpleCallback() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
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
            public void onError(String error) {
                Toast.makeText(MainActivity.this, "Error loading image: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // KEEP THE OLD METHODS FOR REFERENCE
    private void getUserProfileImage() {
        //Fetch user profile image (OLD METHOD - keep for reference)
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
                } else if (id == R.id.cart) {
                    startActivity(new Intent(MainActivity.this, CartActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else if (id == R.id.profile) {
                    startActivity(new Intent(MainActivity.this, CustomerSettingsActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        // Floating cart button click
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

        //Update order view when activity resumes (Composite Pattern)
        createSampleOrderFromCart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister observer to prevent memory leaks (Observer Pattern)
        if (managementCart != null) {
            managementCart.unregisterCartObserver(this);
        }
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

    // Utility method to get the product catalog (Iterator Pattern)
    public ProductCatalog getProductCatalog() {
        return productCatalog;
    }

    // Analyze current cart using (Iterator Pattern)
    private void analyzeCartWithIterator() {
        ArrayList<ItemsModel> cartItems = managementCart.getListCart();

        if (!cartItems.isEmpty()) {
            ProductCatalog cartCatalog = new ProductCatalog();

            // Add cart items to catalog for iterator operations
            for (ItemsModel item : cartItems) {
                cartCatalog.addProduct(item);
            }

            // Using iterator to analyze actual cart contents
            Log.d("CartAnalysis", "=== Actual Cart Contents Analysis ===");
            int totalItems = 0;
            double totalValue = 0;

            for (ItemsModel item : cartCatalog) {
                totalItems += item.getNumberInCart();
                double itemTotal = item.calculateTotalPrice(item.getNumberInCart());
                totalValue += itemTotal;
                Log.d("CartAnalysis", item.getTitle() + " x" + item.getNumberInCart() +
                        " = $" + itemTotal + " (Strategy: " + item.getPricingStrategyName() + ")");
            }

            Log.d("CartAnalysis", "Total Items: " + totalItems + ", Total Value: $" + totalValue);
            Log.d("CartAnalysis", "Average item value: $" + (totalValue / totalItems));
        } else {
            Log.d("CartAnalysis", "Cart is empty - nothing to analyze");
        }
    }
}