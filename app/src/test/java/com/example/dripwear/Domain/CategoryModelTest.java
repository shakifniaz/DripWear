package com.example.dripwear.Domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class CategoryModelTest {

    @ParameterizedTest
    @CsvSource({
            "1, Electronics, electronics.jpg",
            "2, Clothing, clothing.png",
            "3, '', null",
            "0, null, ''"
    })
    void testPropertySettersAndGetters(int id, String title, String picUrl) {
        CategoryModel category = new CategoryModel();

        category.setId(id);
        category.setTitle(title);
        category.setPicUrl(picUrl);

        assertEquals(id, category.getId());
        assertEquals(title, category.getTitle());
        assertEquals(picUrl, category.getPicUrl());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, Integer.MAX_VALUE, Integer.MIN_VALUE})
    void testIdBoundaryValues(int id) {
        CategoryModel category = new CategoryModel();
        category.setId(id);
        assertEquals(id, category.getId());
    }
}