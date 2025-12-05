// CartAdapter.java
package com.example.dripwear.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dripwear.Domain.ItemsModel;
import com.example.dripwear.Helper.ChangeNumberItemsListener;
import com.example.dripwear.Helper.ManagementCart;
import com.example.dripwear.databinding.ViewholderCartBinding;

import com.example.dripwear.AdapterPattern.UsdToBdtService;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Viewholder> {
    ArrayList<ItemsModel> listItemsSelected;
    ChangeNumberItemsListener changeNumberItemsListener;
    private ManagementCart managementCart;
    private boolean isProcessingClick = false;

    private boolean showUsd = true;
    private final UsdToBdtService usdToBdt = new UsdToBdtService();

    public CartAdapter(ArrayList<ItemsModel> listItemsSelected, Context context,
                       ChangeNumberItemsListener changeNumberItemsListener,
                       ManagementCart managementCart) {
        this.listItemsSelected = listItemsSelected;
        this.changeNumberItemsListener = changeNumberItemsListener;
        this.managementCart = managementCart;
    }

    public void setShowUsd(boolean showUsd) {
        this.showUsd = showUsd;
    }

    @NonNull
    @Override
    public CartAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderCartBinding binding = ViewholderCartBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.Viewholder holder, int position) {
        ItemsModel item = listItemsSelected.get(position);

        holder.binding.titeTxt.setText(item.getTitle());

        holder.binding.feeEachItem.setText(formatPrice(item.getPrice()));

        double lineTotalUsd = item.calculateTotalPrice(item.getNumberInCart());
        holder.binding.totalEachItem.setText(formatPrice(lineTotalUsd));

        if (item.getOldPrice() > 0) {
            holder.binding.oldPriceTxt.setText(formatPrice(item.getOldPrice()));
            holder.binding.oldPriceTxt.setVisibility(android.view.View.VISIBLE);
        } else {
            holder.binding.oldPriceTxt.setVisibility(android.view.View.GONE);
        }

        holder.binding.numberItemTxt.setText(String.valueOf(item.getNumberInCart()));

        Glide.with(holder.itemView.getContext())
                .load(listItemsSelected.get(position).getPicUrl().get(0))
                .into(holder.binding.pic);

        holder.binding.plsuCartBtn.setOnClickListener(v -> {
            if (!isProcessingClick) {
                isProcessingClick = true;
                managementCart.plusItem(listItemsSelected, position, () -> {
                    notifyDataSetChanged();
                    changeNumberItemsListener.changed();
                    holder.itemView.postDelayed(() -> isProcessingClick = false, 300);
                });
            }
        });

        holder.binding.minusCartBtn.setOnClickListener(v -> {
            if (!isProcessingClick) {
                isProcessingClick = true;
                managementCart.minusItem(listItemsSelected, position, () -> {
                    notifyDataSetChanged();
                    changeNumberItemsListener.changed();
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

    private String formatUsd(double v) { return String.format("$ %.2f", v); }
    private String formatBdt(double v) { return String.format("৳ %.2f BDT", v); }

    private String formatPrice(double usdAmount) {
        if (showUsd) return formatUsd(usdAmount);
        double bdt = usdToBdt.convertUsdToBdt(usdAmount);
        return formatBdt(bdt);
    }
}
