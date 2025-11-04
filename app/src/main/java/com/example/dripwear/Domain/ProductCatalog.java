package com.example.dripwear.Domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProductCatalog implements Iterable<ItemsModel> {
    private List<ItemsModel> products;

    public ProductCatalog() {
        this.products = new ArrayList<>();
    }

    public void addProduct(ItemsModel product) {
        products.add(product);
    }

    public void removeProduct(ItemsModel product) {
        products.remove(product);
    }

    public int getProductCount() {
        return products.size();
    }

    @Override
    public Iterator<ItemsModel> iterator() {
        return new ProductIterator();
    }

    private class ProductIterator implements Iterator<ItemsModel> {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < products.size();
        }

        @Override
        public ItemsModel next() {
            if (!hasNext()) {
                return null;
            }
            return products.get(currentIndex++);
        }

        @Override
        public void remove() {
            if (currentIndex <= 0) {
                throw new IllegalStateException("next() must be called before remove()");
            }
            products.remove(--currentIndex);
        }
    }

    public Iterator<ItemsModel> getDiscountedProductsIterator() {
        List<ItemsModel> discounted = new ArrayList<>();
        for (ItemsModel product : products) {
            if (product.hasSpecialPricing()) {
                discounted.add(product);
            }
        }
        return discounted.iterator();
    }

    public Iterator<ItemsModel> getHighRatedProductsIterator(double minRating) {
        List<ItemsModel> highRated = new ArrayList<>();
        for (ItemsModel product : products) {
            if (product.getRating() >= minRating) {
                highRated.add(product);
            }
        }
        return highRated.iterator();
    }
}