package com.example.dripwear.Activity;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
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
import com.example.dripwear.databinding.ActivityDetailBinding;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private ItemsModel object;
    private int numberOrder = 1;
    private ManagmentCart managmentCart;
    private ManagmentFavorites managmentFavorites;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Initialize our cart and favorites management helper classes
        managmentCart = new ManagmentCart(this);
        managmentFavorites = new ManagmentFavorites(this);

        //Fetch the item details and set up the UI
        getBundles();
        initPicList();
        initSize();
        initColor();
    }

    private void initColor() {
        //Set up the horizontal RecyclerView for product colors
        binding.recyclerColor.setAdapter(new ColorAdapter(object.getColor()));
        binding.recyclerColor.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void initSize() {
        //Set up the horizontal RecyclerView for product sizes
        binding.recyclerSize.setAdapter(new SizeAdapter(object.getSize()));
        binding.recyclerSize.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
    }

    private void initPicList() {
        //Get the list of pictures for this item
        ArrayList<String> picList=new ArrayList<>(object.getPicUrl());

        //Load the first picture into the main image view using Glide
        Glide.with(this)
                .load(picList.get(0))
                .into((binding.pic));

        //Set up the horizontal RecyclerView for the other pictures
        binding.picList.setAdapter(new PicListAdapter(picList,binding.pic));
        binding.picList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void getBundles() {
        //Get the item object passed from the previous activity
        object = (ItemsModel) getIntent().getSerializableExtra("object");

        //Set the UI elements with data from the item object
        binding.titleTxt.setText(object.getTitle());
        binding.priceTxt.setText("$"+object.getPrice());
        binding.oldPriceTxt.setText("$"+object.getOldPrice());
        //Add a strikethrough to the old price text
        binding.oldPriceTxt.setPaintFlags(binding.oldPriceTxt.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        binding.descriptionTxt.setText(object.getDescription());

        //Check if the current item is already in the user's favorites
        isFavorite = isItemInFavorites(object);
        updateFavoriteButton();

        //Set up the click listener for the "add to cart" button
        binding.addToCartBtn.setOnClickListener(v -> {
            object.setNumberInCart(numberOrder);
            managmentCart.insertItem(object);
        });

        //Set up the click listener for the favorites button
        binding.favBtn.setOnClickListener(v -> {
            if (isFavorite) {
                //If it's a favorite, remove it
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
                //If it's not a favorite, add it
                managmentFavorites.insertItem(object);
                isFavorite = true;
                updateFavoriteButton();
            }
        });

        //Set up the click listener for the back button to close the activity
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private boolean isItemInFavorites(ItemsModel item) {
        //A helper method to check if an item is already in the favorites list
        ArrayList<ItemsModel> favoritesList = managmentFavorites.getListFav();
        for (ItemsModel favoriteItem : favoritesList) {
            if (favoriteItem.getTitle().equals(item.getTitle())) {
                return true;
            }
        }
        return false;
    }

    private int getItemPosition(ItemsModel item) {
        //A helper method to find the position of an item in the favorites list
        for (int i = 0; i < managmentFavorites.getListFav().size(); i++) {
            if (managmentFavorites.getListFav().get(i).getTitle().equals(item.getTitle())) {
                return i;
            }
        }
        return -1;
    }

    private void updateFavoriteButton() {
        //Update the favorites button icon based on the favorite status
        if (isFavorite) {
            //NOTE: There are conflicting calls here
            binding.favBtn.setImageResource(R.drawable.fav1);
            binding.favBtn.setImageResource(R.drawable.favv2);
            //binding.favBtn.setColorFilter(ContextCompat.getColor(this, R.color.orange));
        } else {
            //NOTE: There are conflicting calls here.
            binding.favBtn.setImageResource(R.drawable.favv2);
            binding.favBtn.setImageResource(R.drawable.fav1);
            //binding.favBtn.setColorFilter(ContextCompat.getColor(this, R.color.black));
        }
    }
}
