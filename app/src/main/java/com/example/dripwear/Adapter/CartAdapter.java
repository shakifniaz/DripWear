package com.example.dripwear.Adapter;

import android.content.Context;
import android.text.Layout;
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
import com.example.dripwear.databinding.ViewholderColorBinding;
import com.example.dripwear.databinding.ViewholderPiclistBinding;
import com.example.dripwear.databinding.ViewholderSizeBinding;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Viewholder> {
    ArrayList<ItemsModel> listItemsSelected;
    ChangeNumberItemsListener changeNumberItemsListener;
    private ManagmentCart managmentCart;

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
        //Set item title
        holder.binding.titeTxt.setText(listItemsSelected.get(position).getTitle());
        //Set price per item
        holder.binding.feeEachItem.setText("$ "+listItemsSelected.get(position).getPrice());
        //Calculate and set total price for this item
        holder.binding.totalEachItem.setText("$ "+Math.round((listItemsSelected.get(position).getNumberInCart()*
                listItemsSelected.get(position).getPrice())));
        //Set the number of items
        holder.binding.numberItemTxt.setText(String.valueOf(listItemsSelected.get(position).getNumberInCart()));

        //Load item image with Glide
        Glide.with(holder.itemView.getContext())
                .load(listItemsSelected.get(position).getPicUrl().get(0))
                .into(holder.binding.pic);

        //Handle plus button click
        holder.binding.plsuCartBtn.setOnClickListener(v -> managmentCart.plusItem(listItemsSelected, position, () -> {
            notifyDataSetChanged();
            changeNumberItemsListener.changed();
        }));

        //Handle minus button click
        holder.binding.minusCartBtn.setOnClickListener(v -> managmentCart.minusItem(listItemsSelected, position, () -> {
            notifyDataSetChanged();
            changeNumberItemsListener.changed();
        }));
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
