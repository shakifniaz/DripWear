package com.example.dripwear.Strategy;

public class RegularPricing implements PricingStrategy{
    @Override
    public double calculatePrice(double originalPrice, int quantity) {
        return originalPrice * quantity;
    }

    @Override
    public String getStrategyName() {
        return "Regular Price";
    }
}
