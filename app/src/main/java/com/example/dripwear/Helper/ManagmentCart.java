package com.example.dripwear.Helper;

import android.content.Context;
import android.widget.Toast;
import com.example.dripwear.Domain.ItemsModel;
import java.util.ArrayList;

public class ManagmentCart {

    private static ManagmentCart instance;
    private Context context;
    private TinyDB tinyDB;

    // Private constructor to prevent instantiation
    private ManagmentCart(Context context) {
        this.context = context.getApplicationContext();
        this.tinyDB = new TinyDB(this.context);
    }

    // Thread-safe singleton instance getter
    public static synchronized ManagmentCart getInstance(Context context) {
        if (instance == null) {
            instance = new ManagmentCart(context);
        }
        return instance;
    }

    // Optional: Method to clear instance (for testing or logout scenarios)
    public static void clearInstance() {
        instance = null;
    }

    public void insertItem(ItemsModel item) {
        ArrayList<ItemsModel> listItem = getListCart();
        boolean existAlready = false;
        int n = 0;

        for (int y = 0; y < listItem.size(); y++) {
            if (listItem.get(y).getTitle().equals(item.getTitle())) {
                existAlready = true;
                n = y;
                break;
            }
        }

        if (existAlready) {
            listItem.get(n).setNumberInCart(item.getNumberInCart());
        } else {
            listItem.add(item);
        }

        tinyDB.putListObject("CartList", listItem);
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<ItemsModel> getListCart() {
        return tinyDB.getListObject("CartList");
    }

    public void minusItem(ArrayList<ItemsModel> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        if (listItem.get(position).getNumberInCart() == 1) {
            listItem.remove(position);
        } else {
            listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() - 1);
        }
        tinyDB.putListObject("CartList", listItem);
        changeNumberItemsListener.changed();
    }

    public void plusItem(ArrayList<ItemsModel> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() + 1);
        tinyDB.putListObject("CartList", listItem);
        changeNumberItemsListener.changed();
    }

    public Double getTotalFee() {
        ArrayList<ItemsModel> listItem2 = getListCart();
        double fee = 0;

        for (int i = 0; i < listItem2.size(); i++) {
            ItemsModel item = listItem2.get(i);
            fee = fee + item.calculateTotalPrice(item.getNumberInCart());
        }
        return fee;
    }
}