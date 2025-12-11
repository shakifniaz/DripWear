package com.example.dripwear.Domain;

import java.util.ArrayList;
import java.util.List;

public class OrderComposite implements OrderComponent {
    private String orderName;
    private List<OrderComponent> orderComponents = new ArrayList<>();

    public OrderComposite(String orderName) {
        this.orderName = orderName;
    }

    public void addComponent(OrderComponent component) {
        orderComponents.add(component);
    }

    public void removeComponent(OrderComponent component) {
        orderComponents.remove(component);
    }

    public List<OrderComponent> getComponents() {
        return new ArrayList<>(orderComponents);
    }

    @Override
    public double getPrice() {
        double total = 0;
        for (OrderComponent component : orderComponents) {
            total += component.getPrice();
        }
        return total;
    }

    @Override
    public String getDescription() {
        return "Order: " + orderName + " (Total: $" + getPrice() + ")";
    }

    @Override
    public void displayOrderDetails() {
        System.out.println("=== " + getDescription() + " ===");
        for (OrderComponent component : orderComponents) {
            component.displayOrderDetails();
        }
        System.out.println("Total: $" + getPrice());
    }

    // Utility methods
    public int getTotalItemsCount() {
        int count = 0;
        for (OrderComponent component : orderComponents) {
            if (component instanceof OrderItem) {
                count += ((OrderItem) component).getQuantity();
            } else if (component instanceof OrderComposite) {
                count += ((OrderComposite) component).getTotalItemsCount();
            }
        }
        return count;
    }

    public boolean containsProduct(String productTitle) {
        for (OrderComponent component : orderComponents) {
            if (component instanceof OrderItem) {
                if (((OrderItem) component).getProduct().getTitle().equals(productTitle)) {
                    return true;
                }
            } else if (component instanceof OrderComposite) {
                if (((OrderComposite) component).containsProduct(productTitle)) {
                    return true;
                }
            }
        }
        return false;
    }
}