package com.example.dripwear.Domain;

public class OrderItem implements OrderComponent {
    private ItemsModel product;
    private int quantity;

    public OrderItem(ItemsModel product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    @Override
    public double getPrice() {
        return product.calculateTotalPrice(quantity);
    }

    @Override
    public String getDescription() {
        return quantity + " x " + product.getTitle() + " - $" + getPrice();
    }

    @Override
    public void displayOrderDetails() {
        System.out.println(getDescription());
    }

    public ItemsModel getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}