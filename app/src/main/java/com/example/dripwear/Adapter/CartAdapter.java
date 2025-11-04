package com.example.dripwear.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dripwear.Domain.ItemsModel;
import com.example.dripwear.Helper.ChangeNumberItemsListener;
import com.example.dripwear.Helper.ManagmentCart;
import com.example.dripwear.databinding.ViewholderCartBinding;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Viewholder> {
    ArrayList<ItemsModel> listItemsSelected;
    ChangeNumberItemsListener changeNumberItemsListener;
    private ManagmentCart managmentCart;
    private boolean isProcessingClick = false;

    public CartAdapter(ArrayList<ItemsModel> listItemsSelected, Context context,
                       ChangeNumberItemsListener changeNumberItemsListener,
                       ManagmentCart managmentCart) {
        this.listItemsSelected = listItemsSelected;
        this.changeNumberItemsListener = changeNumberItemsListener;
        this.managmentCart = managmentCart;
    }

    @NonNull
    @Override
    public CartAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderCartBinding binding = ViewholderCartBinding.inflate(LayoutInflater
                .from(parent.getContext()), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.Viewholder holder, int position) {
        ItemsModel item = listItemsSelected.get(position);

        holder.binding.titeTxt.setText(item.getTitle());
        holder.binding.feeEachItem.setText("$ "+item.getPrice());
        //Calculate and set total price for this item
        holder.binding.totalEachItem.setText("$ "+Math.round(item.calculateTotalPrice(item.getNumberInCart())));
        //Set the number of items
        holder.binding.numberItemTxt.setText(String.valueOf(item.getNumberInCart()));

        //Load item image with Glide
        Glide.with(holder.itemView.getContext())
                .load(listItemsSelected.get(position).getPicUrl().get(0))
                .into(holder.binding.pic);

        //Handle plus button click with click prevention
        holder.binding.plsuCartBtn.setOnClickListener(v -> {
            if (!isProcessingClick) {
                isProcessingClick = true;
                managmentCart.plusItem(listItemsSelected, position, () -> {
                    notifyDataSetChanged();
                    changeNumberItemsListener.changed();
                    // Reset click prevention after a short delay
                    holder.itemView.postDelayed(() -> isProcessingClick = false, 300);
                });
            }
        });

        //Handle minus button click with click prevention
        holder.binding.minusCartBtn.setOnClickListener(v -> {
            if (!isProcessingClick) {
                isProcessingClick = true;
                managmentCart.minusItem(listItemsSelected, position, () -> {
                    notifyDataSetChanged();
                    changeNumberItemsListener.changed();
                    // Reset click prevention after a short delay
                    holder.itemView.postDelayed(() -> isProcessingClick = false, 300);
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItemsSelected.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderCartBinding binding;
        public Viewholder(ViewholderCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}