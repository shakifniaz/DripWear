package com.example.dripwear.Helper;

import android.content.Context;
import android.widget.Toast;


import com.example.dripwear.Domain.ItemsModel;

import java.util.ArrayList;

public class ManagmentCart {

    private Context context;
    private TinyDB tinyDB;

    public ManagmentCart(Context context) {
        this.context = context;
        //Initialize TinyDB to handle local storage
        this.tinyDB = new TinyDB(context);
    }

    public void insertItem(ItemsModel item) {
        ArrayList<ItemsModel> listItem = getListCart();
        boolean existAlready = false;
        int n = 0;
        //Check if the item already exists in the cart
        for (int y = 0; y < listItem.size(); y++) {
            if (listItem.get(y).getTitle().equals(item.getTitle())) {
                existAlready = true;
                n = y;
                break;
            }
        }
        //If it exists, update the quantity; otherwise, add the new item
        if (existAlready) {
            listItem.get(n).setNumberInCart(item.getNumberInCart());
        } else {
            listItem.add(item);
        }
        //Save the updated cart list to TinyDB
        tinyDB.putListObject("CartList", listItem);
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<ItemsModel> getListCart() {
        //Retrieve the cart list from TinyDB
        return tinyDB.getListObject("CartList");
    }

    public void minusItem(ArrayList<ItemsModel> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        //Decrease the item count, removing the item if count becomes 1
        if (listItem.get(position).getNumberInCart() == 1) {
            listItem.remove(position);
        } else {
            listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() - 1);
        }
        tinyDB.putListObject("CartList", listItem);
        changeNumberItemsListener.changed();
    }

    public void plusItem(ArrayList<ItemsModel> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        //Increase the item count
        listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() + 1);
        tinyDB.putListObject("CartList", listItem);
        changeNumberItemsListener.changed();
    }

    public Double getTotalFee() {
        ArrayList<ItemsModel> listItem2 = getListCart();
        double fee = 0;
        //Calculate the total cost of all items in the cart
        for (int i = 0; i < listItem2.size(); i++) {
            fee = fee + (listItem2.get(i).getPrice() * listItem2.get(i).getNumberInCart());
        }
        return fee;
    }
}
