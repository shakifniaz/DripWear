package com.example.dripwear.Domain;

import java.io.Serializable;
import java.util.ArrayList;

// Import your strategy classes
import com.example.dripwear.Strategy.PricingStrategy;
import com.example.dripwear.Strategy.RegularPricing;
import com.example.dripwear.Strategy.DiscountPricing;


public class ItemsModel implements Serializable {
    private String title;
    private String description;
    private String offPercent;
    private ArrayList<String> size;
    private ArrayList<String> color;
    private ArrayList<String> picUrl;
    private double price;
    private double oldPrice;
    private int review;
    private double rating;
    private int NumberInCart;

    // Clean imports make this much simpler
    private transient PricingStrategy pricingStrategy;

    public ItemsModel(){
        // Default to regular pricing
        this.pricingStrategy = new RegularPricing();
    }

    // === YOUR EXISTING GETTERS/SETTERS ===
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOffPercent() {
        return offPercent;
    }

    public void setOffPercent(String offPercent) {
        this.offPercent = offPercent;
    }

    public ArrayList<String> getSize() {
        return size;
    }

    public void setSize(ArrayList<String> size) {
        this.size = size;
    }

    public ArrayList<String> getColor() {
        return color;
    }

    public void setColor(ArrayList<String> color) {
        this.color = color;
    }

    public ArrayList<String> getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(ArrayList<String> picUrl) {
        this.picUrl = picUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getNumberInCart() {
        return NumberInCart;
    }

    public void setNumberInCart(int numberInCart) {
        NumberInCart = numberInCart;
    }

    // === STRATEGY PATTERN METHODS ===

    public void setPricingStrategy(PricingStrategy strategy) {
        this.pricingStrategy = strategy;
    }

    public double calculateTotalPrice(int quantity) {
        if (pricingStrategy != null) {
            return pricingStrategy.calculatePrice(this.price, quantity);
        }
        return this.price * quantity; // fallback to regular calculation
    }

    public String getPricingStrategyName() {
        if (pricingStrategy != null) {
            return pricingStrategy.getStrategyName();
        }
        return "Regular Price"; // fallback
    }

    public PricingStrategy getPricingStrategy() {
        return pricingStrategy;
    }

    // Helper method to check if item has special pricing
    public boolean hasSpecialPricing() {
        return pricingStrategy != null && !(pricingStrategy instanceof RegularPricing);
    }

    // Helper method to get discount percentage (if any)
    public double getEffectiveDiscount() {
        if (pricingStrategy == null || pricingStrategy instanceof RegularPricing) {
            return 0.0;
        }

        double regularTotal = this.price * (this.NumberInCart > 0 ? this.NumberInCart : 1);
        double strategyTotal = calculateTotalPrice(this.NumberInCart > 0 ? this.NumberInCart : 1);

        return ((regularTotal - strategyTotal) / regularTotal) * 100;
    }
}