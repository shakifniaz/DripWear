package com.example.dripwear.Helper;

import android.content.Context;
import android.widget.Toast;
import com.example.dripwear.Domain.ItemsModel;
import java.util.ArrayList;

public class ManagmentCart {

    private static ManagmentCart instance;
    private Context context;
    private TinyDB tinyDB;
    private CartObservable cartObservable;

    // Private constructor to prevent instantiation
    private ManagmentCart(Context context) {
        this.context = context.getApplicationContext();
        this.tinyDB = new TinyDB(this.context);
        this.cartObservable = CartObservable.getInstance();
    }

    // Thread-safe singleton instance getter
    public static synchronized ManagmentCart getInstance(Context context) {
        if (instance == null) {
            instance = new ManagmentCart(context);
        }
        return instance;
    }

    //Method to clear instance (for testing or logout scenarios)
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

        // Notify observers about cart update (Observer Pattern)
        cartObservable.notifyItemAdded(item.getTitle(), item.getPrice());
        cartObservable.notifyCartUpdated(getTotalItemsCount(), getTotalFee());

        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<ItemsModel> getListCart() {
        return tinyDB.getListObject("CartList");
    }

    public void minusItem(ArrayList<ItemsModel> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        ItemsModel removedItem = listItem.get(position);
        String itemTitle = removedItem.getTitle();

        if (listItem.get(position).getNumberInCart() == 1) {
            listItem.remove(position);
            // Notify observers about item removal (Observer Pattern)
            cartObservable.notifyItemRemoved(itemTitle);
        } else {
            listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() - 1);
        }

        tinyDB.putListObject("CartList", listItem);
        // Notify observers about cart update (Observer Pattern)
        cartObservable.notifyCartUpdated(getTotalItemsCount(), getTotalFee());
        changeNumberItemsListener.changed();
    }

    public void plusItem(ArrayList<ItemsModel> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() + 1);
        tinyDB.putListObject("CartList", listItem);
        // Notify observers about cart update
        cartObservable.notifyCartUpdated(getTotalItemsCount(), getTotalFee());
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

    // Helper method to get total items count
    private int getTotalItemsCount() {
        ArrayList<ItemsModel> listItem = getListCart();
        int totalCount = 0;
        for (ItemsModel item : listItem) {
            totalCount += item.getNumberInCart();
        }
        return totalCount;
    }

    // Method to register cart observers
    public void registerCartObserver(CartObserver observer) {
        cartObservable.registerObserver(observer);
    }

    // Method to unregister cart observers
    public void unregisterCartObserver(CartObserver observer) {
        cartObservable.unregisterObserver(observer);
    }
}