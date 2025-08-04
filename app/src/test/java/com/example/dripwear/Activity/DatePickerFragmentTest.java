package com.example.dripwear.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.widget.DatePicker;
import androidx.fragment.app.FragmentActivity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatePickerFragmentTest {

    @Mock
    private FragmentActivity mockActivity;

    @Mock
    private CustomerRegistrationActivity mockRegistrationActivity;

    @Mock
    private DatePicker mockDatePicker;

    private DatePickerFragment datePickerFragment;

    @BeforeEach
    void setUp() {
        datePickerFragment = spy(new DatePickerFragment());
    }

    @Test
    void testOnDateSet_withValidActivity_setsDateCorrectly() {
        when(datePickerFragment.getActivity()).thenReturn(mockRegistrationActivity);
        datePickerFragment.onDateSet(mockDatePicker, 2023, 5, 15);
        verify(mockRegistrationActivity).setSelectedDate("15/06/2023");
    }

    @Test
    void testOnDateSet_withNullActivity_doesNotCrash() {
        when(datePickerFragment.getActivity()).thenReturn(null);
        assertDoesNotThrow(() ->
                datePickerFragment.onDateSet(mockDatePicker, 2023, 5, 15)
        );
    }

    @Test
    void testOnDateSet_formatsSingleDigitValuesCorrectly() {
        when(datePickerFragment.getActivity()).thenReturn(mockRegistrationActivity);
        datePickerFragment.onDateSet(mockDatePicker, 2023, 0, 5);
        verify(mockRegistrationActivity).setSelectedDate("05/01/2023");
    }
}
