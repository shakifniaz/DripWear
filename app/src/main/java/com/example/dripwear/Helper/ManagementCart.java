package com.example.dripwear.Helper;

import android.content.Context;
import android.widget.Toast;
import com.example.dripwear.Domain.ItemsModel;

import java.util.ArrayList;

public class ManagementCart {

    private static ManagementCart instance;
    private Context context;
    private TinyDB tinyDB;
    private CartObservable cartObservable;
    private boolean isNotifying = false;

    //Memento pattern fields
    private CartCaretaker caretaker;

    private ManagementCart() {}

    //Singleton method
    public static synchronized ManagementCart getInstance(Context context) {
        if (instance == null) {
            instance = new ManagementCart();
            instance.initialize(context.getApplicationContext());
        }
        return instance;
    }

    private void initialize(Context appContext) {
        this.context = appContext;
        this.tinyDB = new TinyDB(this.context);
        this.cartObservable = CartObservable.getInstance();
        this.caretaker = new CartCaretaker();
    }

    //Memento methods
    public CartMemento saveCartState() {
        ArrayList<ItemsModel> currentList = getListCart();
        return new CartMemento(new ArrayList<>(currentList));
    }

    public void restoreCartState(CartMemento memento) {
        if (memento != null) {
            ArrayList<ItemsModel> restoredList = memento.getSavedState();
            tinyDB.putListObject("CartList", restoredList);
            notifyCartStateChanged();
            Toast.makeText(context, "Previous cart state restored", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "No previous cart state found", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveCurrentStateToHistory() {
        caretaker.saveState(saveCartState());
    }

    public void undoLastCartChange() {
        CartMemento previousState = caretaker.restoreState();
        restoreCartState(previousState);
    }

    public boolean canUndo() {
        return caretaker.hasPreviousState();
    }


    //Other methods
    public void insertItem(ItemsModel item) {
        saveCurrentStateToHistory(); // Save before change

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
        if (!isNotifying) {
            isNotifying = true;
            cartObservable.notifyItemAdded(item.getTitle(), item.getPrice());
            cartObservable.notifyCartUpdated(getTotalItemsCount(), getTotalFee());
            isNotifying = false;
        }

        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<ItemsModel> getListCart() {
        return tinyDB.getListObject("CartList");
    }

    public void minusItem(ArrayList<ItemsModel> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        saveCurrentStateToHistory(); // Save before change

        ItemsModel removedItem = listItem.get(position);
        String itemTitle = removedItem.getTitle();
        boolean isRemovingCompletely = listItem.get(position).getNumberInCart() == 1;

        if (isRemovingCompletely) {
            listItem.remove(position);
        } else {
            listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() - 1);
        }

        tinyDB.putListObject("CartList", listItem);

        // Notify observers about cart update (Observer Pattern)
        if (!isNotifying) {
            isNotifying = true;
            if (isRemovingCompletely) {
                cartObservable.notifyItemRemoved(itemTitle);
            }
            cartObservable.notifyCartUpdated(getTotalItemsCount(), getTotalFee());
            isNotifying = false;
        }

        changeNumberItemsListener.changed();
    }

    public void plusItem(ArrayList<ItemsModel> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        saveCurrentStateToHistory(); // Save before change

        listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() + 1);
        tinyDB.putListObject("CartList", listItem);

        // Notify observers about cart update (Observer Pattern)
        if (!isNotifying) {
            isNotifying = true;
            cartObservable.notifyCartUpdated(getTotalItemsCount(), getTotalFee());
            isNotifying = false;
        }

        changeNumberItemsListener.changed();
    }

    public Double getTotalFee() {
        ArrayList<ItemsModel> listItem2 = getListCart();
        double fee = 0;

        for (ItemsModel item : listItem2) {
            fee += item.calculateTotalPrice(item.getNumberInCart());
        }
        return fee;
    }

    private int getTotalItemsCount() {
        ArrayList<ItemsModel> listItem = getListCart();
        int totalCount = 0;
        for (ItemsModel item : listItem) {
            totalCount += item.getNumberInCart();
        }
        return totalCount;
    }

    public void registerCartObserver(CartObserver observer) {
        cartObservable.registerObserver(observer);
    }

    public void unregisterCartObserver(CartObserver observer) {
        cartObservable.unregisterObserver(observer);
    }

    public void notifyCartStateChanged() {
        if (!isNotifying) {
            isNotifying = true;
            cartObservable.notifyCartUpdated(getTotalItemsCount(), getTotalFee());
            isNotifying = false;
        }
    }
}
