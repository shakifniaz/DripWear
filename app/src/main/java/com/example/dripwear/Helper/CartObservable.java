package com.example.dripwear.Helper;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CartObservable {
    private static CartObservable instance;
    private List<CartObserver> observers = new ArrayList<>();
    private boolean isNotifying = false;

    private CartObservable() {}

    public static synchronized CartObservable getInstance() {
        if (instance == null) {
            instance = new CartObservable();
        }
        return instance;
    }

    public void registerObserver(CartObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            Log.d("CartObservable", "Observer registered. Total observers: " + observers.size());
        }
    }

    public void unregisterObserver(CartObserver observer) {
        observers.remove(observer);
        Log.d("CartObservable", "Observer unregistered. Total observers: " + observers.size());
    }

    public void notifyCartUpdated(int itemCount, double totalAmount) {
        if (isNotifying) {
            Log.d("CartObservable", "Already notifying, skipping duplicate");
            return;
        }

        isNotifying = true;
        Log.d("CartObservable", "Notifying " + observers.size() + " observers about cart update");

        for (CartObserver observer : observers) {
            try {
                observer.onCartUpdated(itemCount, totalAmount);
            } catch (Exception e) {
                Log.e("CartObservable", "Error notifying observer", e);
            }
        }
        isNotifying = false;
    }

    public void notifyItemAdded(String itemTitle, double itemPrice) {
        if (isNotifying) {
            Log.d("CartObservable", "Already notifying, skipping duplicate item added");
            return;
        }

        isNotifying = true;
        Log.d("CartObservable", "Notifying " + observers.size() + " observers about item added: " + itemTitle);

        for (CartObserver observer : observers) {
            try {
                observer.onItemAdded(itemTitle, itemPrice);
            } catch (Exception e) {
                Log.e("CartObservable", "Error notifying observer", e);
            }
        }
        isNotifying = false;
    }

    public void notifyItemRemoved(String itemTitle) {
        if (isNotifying) {
            Log.d("CartObservable", "Already notifying, skipping duplicate item removed");
            return;
        }

        isNotifying = true;
        Log.d("CartObservable", "Notifying " + observers.size() + " observers about item removed: " + itemTitle);

        for (CartObserver observer : observers) {
            try {
                observer.onItemRemoved(itemTitle);
            } catch (Exception e) {
                Log.e("CartObservable", "Error notifying observer", e);
            }
        }
        isNotifying = false;
    }
}