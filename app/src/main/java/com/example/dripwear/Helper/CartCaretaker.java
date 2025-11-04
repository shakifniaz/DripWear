package com.example.dripwear.Helper;

import java.util.Stack;

//Caretaker that manages saved mementos (Undo history)
public class CartCaretaker {
    private final Stack<CartMemento> mementoStack = new Stack<>();

    public void saveState(CartMemento memento) {
        mementoStack.push(memento);
    }

    public CartMemento restoreState() {
        if (!mementoStack.isEmpty()) {
            return mementoStack.pop();
        }
        return null;
    }

    public boolean hasPreviousState() {
        return !mementoStack.isEmpty();
    }
}