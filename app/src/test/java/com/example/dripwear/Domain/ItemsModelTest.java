package com.example.dripwear.Domain;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemsModelTest {

    @Test
    public void testItemsModelGettersAndSetters() {
        ItemsModel item = new ItemsModel();

        // Test setters and getters
        item.setTitle("Test Shirt");
        assertEquals("Test Shirt", item.getTitle());

        item.setDescription("A test description");
        assertEquals("A test description", item.getDescription());

        item.setPrice(29.99);
        assertEquals(29.99, item.getPrice(), 0.001);

        ArrayList<String> sizes = new ArrayList<>(Arrays.asList("S", "M", "L"));
        item.setSize(sizes);
        assertEquals(sizes, item.getSize());

        ArrayList<String> colors = new ArrayList<>(Arrays.asList("#FF0000", "#00FF00"));
        item.setColor(colors);
        assertEquals(colors, item.getColor());

        item.setNumberInCart(3);
        assertEquals(3, item.getNumberInCart());
    }
}