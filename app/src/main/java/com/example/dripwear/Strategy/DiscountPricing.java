package com.example.dripwear.Strategy;

public class DiscountPricing implements PricingStrategy {
    private double discountRate;
    public DiscountPricing(double discountRate) {
        this.discountRate = discountRate;
    }
    @Override
    public double calculatePrice(double originalPrice, int quantity) {
        return (originalPrice * (1 - discountRate)) * quantity;
    }
    @Override
    public String getStrategyName() {
        return String.format("%.0f%% Off", discountRate * 100);
    }
}
