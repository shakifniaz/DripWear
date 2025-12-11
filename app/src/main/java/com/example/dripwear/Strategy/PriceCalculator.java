package com.example.dripwear.Strategy;

public class PriceCalculator {
    private PricingStrategy strategy;
    public PriceCalculator(PricingStrategy strategy) {
        this.strategy = strategy;
    }
    public void setStrategy(PricingStrategy strategy) {
        this.strategy = strategy;
    }
    public double calculateTotal(double price, int quantity) {
        return strategy.calculatePrice(price, quantity);
    }
    public String getStrategyDisplay() {
        return strategy.getStrategyName();
    }
}