package com.example.dripwear.Command;

import android.content.Context;
import android.widget.Toast;

import com.example.dripwear.Domain.ItemsModel;
import com.example.dripwear.Helper.ManagementCart;

//command 1: add to cart
public class AddToCartCommand implements FavoriteItemCommand {
    private Context context;
    private ItemsModel item;

    public AddToCartCommand(Context context, ItemsModel item) {
        this.context = context;
        this.item = item;
    }

    @Override
    public void execute() {
        //creating item copy abd adding to cart
        ItemsModel cartItem = createCartItem(item);
        ManagementCart.getInstance(context).insertItem(cartItem);

        Toast.makeText(context,
                "✓ Added to cart",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getCommandName() {
        return "Add to Cart";
    }

    //copy of the favorite item
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
}
