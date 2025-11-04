package com.example.dripwear.Helper;

import java.util.ArrayList;
import java.util.List;

public class CartObservable {
    private static CartObservable instance;
    private List<CartObserver> observers = new ArrayList<>();

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
        }
    }

    public void unregisterObserver(CartObserver observer) {
        observers.remove(observer);
    }

    public void notifyCartUpdated(int itemCount, double totalAmount) {
        for (CartObserver observer : observers) {
            observer.onCartUpdated(itemCount, totalAmount);
        }
    }

    public void notifyItemAdded(String itemTitle, double itemPrice) {
        for (CartObserver observer : observers) {
            observer.onItemAdded(itemTitle, itemPrice);
        }
    }

    public void notifyItemRemoved(String itemTitle) {
        for (CartObserver observer : observers) {
            observer.onItemRemoved(itemTitle);
        }
    }
}