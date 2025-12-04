package com.example.dripwear.Command;

import android.content.Context;
import android.widget.Toast;

import com.example.dripwear.Domain.ItemsModel;
import com.example.dripwear.Helper.ManagementCart;

//command 2: move to mart
public class MoveToCartCommand implements FavoriteItemCommand {
    private Context context;
    private ItemsModel item;
    private int position;
    private OnItemMovedListener listener;

    public MoveToCartCommand(Context context, ItemsModel item,
                             int position, OnItemMovedListener listener) {
        this.context = context;
        this.item = item;
        this.position = position;
        this.listener = listener;
    }

    @Override
    public void execute() {
        //adding to cart
        ItemsModel cartItem = createCartItem(item);
        ManagementCart.getInstance(context).insertItem(cartItem);

        //notify users to remove from favorites
        if (listener != null) {
            listener.onItemMoved(position);
        }

        Toast.makeText(context,
                "➡️ Moved to cart",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getCommandName() {
        return "Move to Cart";
    }

    private ItemsModel createCartItem(ItemsModel source) {
        ItemsModel cartItem = new ItemsModel();
        cartItem.setTitle(source.getTitle());
        cartItem.setPrice(source.getPrice());
        cartItem.setOldPrice(source.getOldPrice());
        cartItem.setPicUrl(source.getPicUrl());
        cartItem.setDescription(source.getDescription());
        cartItem.setNumberInCart(1);
        return cartItem;
    }

    public interface OnItemMovedListener {
        void onItemMoved(int position);
    }
}
