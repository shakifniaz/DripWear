package com.example.dripwear.Helper;

import com.example.dripwear.Domain.ItemsModel;
import java.util.ArrayList;

//Memento class that stores a snapshot of the cart
public class CartMemento {
    private final ArrayList<ItemsModel> cartState;

    public CartMemento(ArrayList<ItemsModel> state) {
        //Deep copy to prevent modification
        this.cartState = new ArrayList<>(state);
    }

    public ArrayList<ItemsModel> getSavedState() {
        return cartState;
    }
}