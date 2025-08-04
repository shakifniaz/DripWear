package com.example.dripwear.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.dripwear.Domain.ItemsModel;
import com.example.dripwear.Helper.ManagmentFavorites;
import com.example.dripwear.databinding.ViewholderFavoritesBinding;
import java.util.ArrayList;
import com.example.dripwear.R;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.Viewholder> {
    private ArrayList<ItemsModel> favoritesList;
    private ManagmentFavorites managmentFavorites;
    private Context context;

    public FavoritesAdapter(ArrayList<ItemsModel> favoritesList, Context context) {
        this.favoritesList = favoritesList;
        this.context = context;
        //Initialize the favorites management helper
        this.managmentFavorites = new ManagmentFavorites(context);
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderFavoritesBinding binding = ViewholderFavoritesBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        //Get the item for the current position
        ItemsModel item = favoritesList.get(position);

        //Set the item title
        holder.binding.titleTxt.setText(item.getTitle());

        //Set prices
        holder.binding.priceTxt.setText("$" + item.getPrice());
        holder.binding.priceTxt.setTextColor(context.getResources().getColor(R.color.orange));

        //Set old price and add a strikethrough
        holder.binding.oldPriceTxt.setText("$" + item.getOldPrice());
        holder.binding.oldPriceTxt.setTextColor(context.getResources().getColor(R.color.darkGrey));
        holder.binding.oldPriceTxt.setPaintFlags(
                holder.binding.oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        //Load the item image with Glide
        Glide.with(context)
                .load(item.getPicUrl().get(0))
                .into(holder.binding.pic);

        //Handle the remove button click
        holder.binding.removeBtn.setOnClickListener(v -> {
            managmentFavorites.removeItem(favoritesList, position, () -> {
                notifyDataSetChanged();
            });
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderFavoritesBinding binding;

        public Viewholder(ViewholderFavoritesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
