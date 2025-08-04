package com.example.dripwear.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.request.RequestOptions;
import com.example.dripwear.Activity.DetailActivity;
import com.example.dripwear.Domain.ItemsModel;
import com.example.dripwear.databinding.ViewholderPopularBinding;

import java.util.ArrayList;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.Viewholder> {

    ArrayList<ItemsModel> items;
    Context context;

    public PopularAdapter(ArrayList<ItemsModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public PopularAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        //Inflate the layout for a single item
        ViewholderPopularBinding binding = ViewholderPopularBinding.inflate(LayoutInflater.from(context),parent,false);

        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularAdapter.Viewholder holder, int position) {
        //Set the title and prices
        holder.binding.titleTxt.setText(items.get(position).getTitle());
        holder.binding.priceTxt.setText("$"+items.get(position).getPrice());
        //Set rating and off percentage
        holder.binding.ratingTxt.setText("("+items.get(position).getRating()+")");
        holder.binding.offPercentTxt.setText(items.get(position).getOffPercent()+" Off");
        holder.binding.oldPriceTxt.setText("$"+items.get(position).getOldPrice());
        //Add a strikethrough to the old price
        holder.binding.oldPriceTxt.setPaintFlags(holder.binding.oldPriceTxt.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);

        //Apply Glide transformations
        RequestOptions options = new RequestOptions();
        options = options.transform(new CenterInside());

        //Load the item image with Glide
        Glide.with(context)
                .load(items.get(position).getPicUrl().get(0))
                .apply(options)
                .into(holder.binding.pic);
        //Handle item click to open DetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", items.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderPopularBinding binding;
        public Viewholder(ViewholderPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
