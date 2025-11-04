package com.example.dripwear.Helper;

public interface CartObserver {
    void onCartUpdated(int itemCount, double totalAmount);
    void onItemAdded(String itemTitle, double itemPrice);
    void onItemRemoved(String itemTitle);
}
