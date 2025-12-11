package com.example.dripwear.Strategy;

import com.example.dripwear.Domain.ItemsModel;

public class PricingStrategyFactory {

    //Factory Method: Returns proper PricingStrategy based on item properties
    public static PricingStrategy getStrategy(ItemsModel item) {
        if (item.getPrice() > 100) { //Premium items
            return new DiscountPricing(0.15); //15% discount
        } else if (item.getPrice() > 50) { //Mid-range
            return new DiscountPricing(0.10); //10% discount
        } else if (item.getTitle().toLowerCase().contains("sale") ||
                item.getTitle().toLowerCase().contains("clearance")) { //Sale items
            return new DiscountPricing(0.20); // 20% discount
        } else if (item.getRating() >= 4.5) { //High-rated items
            return new DiscountPricing(0.05); //5% discount
        }
        return new DiscountPricing(0.0); //No discount by default
    }
}
