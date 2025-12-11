package com.example.dripwear.Factory;

import com.example.dripwear.Helper.ManagementCart;

public class CartActionFactory {

    public CartAction createCartAction(String actionType, ManagementCart cart) {
        CartAction action = null;

        if (actionType.equals("clear")) {
            action = new ClearCartAction(cart);
        }
        // Add more actions here if needed later

        return action;
    }
}