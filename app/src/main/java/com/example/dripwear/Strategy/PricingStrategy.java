package com.example.dripwear.Strategy;

public interface PricingStrategy {
    double calculatePrice(double originalPrice, int quantity);
    String getStrategyName();
}

