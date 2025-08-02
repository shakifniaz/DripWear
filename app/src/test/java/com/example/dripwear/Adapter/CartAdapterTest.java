package com.example.dripwear.Adapter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.view.View;

import com.example.dripwear.Domain.ItemsModel;
import com.example.dripwear.Helper.ChangeNumberItemsListener;
import com.example.dripwear.Helper.ManagmentCart;
import com.example.dripwear.databinding.ViewholderCartBinding;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class CartAdapterTest {

    @Mock
    private Context mockContext;
    @Mock
    private ChangeNumberItemsListener mockListener;
    @Mock
    private ManagmentCart mockManagementCart;

    private CartAdapter adapter;
    private ArrayList<ItemsModel> testItems;

    private final int initialQuantity;
    private final boolean isPlusButton;
    private final int expectedQuantity;

    public CartAdapterTest(int initialQuantity, boolean isPlusButton, int expectedQuantity) {
        this.initialQuantity = initialQuantity;
        this.isPlusButton = isPlusButton;
        this.expectedQuantity = expectedQuantity;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testCases() {
        return Arrays.asList(new Object[][] {
                {1, true, 2},    // Plus button from 1 to 2
                {5, true, 6},    // Plus button from 5 to 6
                {2, false, 1},  // Minus button from 2 to 1
                {1, false, 0}    // Minus button from 1 (should remove)
        });
    }

    @Before
    public void setUp() {
        testItems = new ArrayList<>();
        ItemsModel item = new ItemsModel();
        item.setTitle("Test Item");
        item.setPrice(29.99);
        item.setNumberInCart(initialQuantity);
        ArrayList<String> picUrls = new ArrayList<>();
        picUrls.add("https://example.com/item.jpg");
        item.setPicUrl(picUrls);
        testItems.add(item);

        adapter = new CartAdapter(testItems, mockContext, mockListener, mockManagementCart);
    }

    @Test
    public void testQuantityChange() {
        CartAdapter.Viewholder holder = mock(CartAdapter.Viewholder.class);
        holder.binding = mock(ViewholderCartBinding.class);
        when(holder.getAdapterPosition()).thenReturn(0);

        if (isPlusButton) {
            // Simulate plus button click
            adapter.onBindViewHolder(holder, 0);
            holder.binding.plsuCartBtn.performClick();
        } else {
            // Simulate minus button click
            adapter.onBindViewHolder(holder, 0);
            holder.binding.minusCartBtn.performClick();
        }

        if (expectedQuantity == 0) {
            assertTrue(testItems.isEmpty());
        } else {
            assertEquals(expectedQuantity, testItems.get(0).getNumberInCart());
        }
        verify(mockListener).changed();
    }
}