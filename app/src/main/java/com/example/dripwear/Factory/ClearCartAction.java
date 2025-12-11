package com.example.dripwear.Factory;

import com.example.dripwear.Helper.ManagementCart;

public class ClearCartAction implements CartAction {
    private ManagementCart cart;

    public ClearCartAction(ManagementCart cart) {
        this.cart = cart;
    }

    @Override
    public void execute() {
        cart.saveCurrentStateToHistory(); // Save state before clearing using Memento
        cart.clearCart();
    }

    @Override
    public String getActionName() {
        return "Clear Cart";
    }
}