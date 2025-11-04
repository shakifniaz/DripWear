package com.example.dripwear.Activity;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.dripwear.Adapter.ColorAdapter;
import com.example.dripwear.Adapter.PicListAdapter;
import com.example.dripwear.Adapter.SizeAdapter;
import com.example.dripwear.Domain.ItemsModel;
import com.example.dripwear.Helper.ManagmentCart;
import com.example.dripwear.Helper.ManagmentFavorites;
import com.example.dripwear.R;
import com.example.dripwear.Strategy.DiscountPricing;
import com.example.dripwear.Strategy.PricingStrategy;
import com.example.dripwear.Strategy.PricingStrategyFactory; //Factory
import com.example.dripwear.databinding.ActivityDetailBinding;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private ItemsModel object;
    private int numberOrder = 1;
    private ManagmentCart managmentCart;
    private ManagmentFavorites managmentFavorites;
    private boolean isFavorite = false;
    private boolean isAddingToCart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = ManagmentCart.getInstance(this);
        managmentFavorites = ManagmentFavorites.getInstance(this);

        getBundles();
        initPicList();
        initSize();
        initColor();
    }

    private void initColor() {
        binding.recyclerColor.setAdapter(new ColorAdapter(object.getColor()));
        binding.recyclerColor.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void initSize() {
        binding.recyclerSize.setAdapter(new SizeAdapter(object.getSize()));
        binding.recyclerSize.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
    }

    private void initPicList() {
        ArrayList<String> picList = new ArrayList<>(object.getPicUrl());

        Glide.with(this)
                .load(picList.get(0))
                .into((binding.pic));

        binding.picList.setAdapter(new PicListAdapter(picList, binding.pic));
        binding.picList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void getBundles() {
        object = (ItemsModel) getIntent().getSerializableExtra("object");

        binding.titleTxt.setText(object.getTitle());
        binding.priceTxt.setText("$" + object.getPrice());
        binding.oldPriceTxt.setText("$" + object.getOldPrice());
        binding.oldPriceTxt.setPaintFlags(binding.oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        binding.descriptionTxt.setText(object.getDescription());

        isFavorite = isItemInFavorites(object);
        updateFavoriteButton();

        binding.addToCartBtn.setOnClickListener(v -> {
            if (!isAddingToCart) {
                isAddingToCart = true;
                object.setNumberInCart(numberOrder);

                //Factory Pattern: Client calls Factory to get Product
                PricingStrategy strategy = PricingStrategyFactory.getStrategy(object); //Factory
                object.setPricingStrategy(strategy); //Product set on Client

                managmentCart.insertItem(object);

                binding.addToCartBtn.postDelayed(() -> isAddingToCart = false, 1000);
            }
        });

        binding.favBtn.setOnClickListener(v -> {
            if (isFavorite) {
                ArrayList<ItemsModel> favorites = managmentFavorites.getListFav();
                int position = -1;
                for (int i = 0; i < favorites.size(); i++) {
                    if (favorites.get(i).getTitle().equals(object.getTitle())) {
                        position = i;
                        break;
                    }
                }
                if (position != -1) {
                    managmentFavorites.removeItem(favorites, position, () -> {
                        isFavorite = false;
                        updateFavoriteButton();
                    });
                }
            } else {
                managmentFavorites.insertItem(object);
                isFavorite = true;
                updateFavoriteButton();
            }
        });

        binding.backBtn.setOnClickListener(v -> finish());
    }

    private boolean isItemInFavorites(ItemsModel item) {
        ArrayList<ItemsModel> favoritesList = managmentFavorites.getListFav();
        for (ItemsModel favoriteItem : favoritesList) {
            if (favoriteItem.getTitle().equals(item.getTitle())) {
                return true;
            }
        }
        return false;
    }

    private void updateFavoriteButton() {
        if (isFavorite) {
            binding.favBtn.setImageResource(R.drawable.fav1);
        } else {
            binding.favBtn.setImageResource(R.drawable.favv2);
        }
    }
}
