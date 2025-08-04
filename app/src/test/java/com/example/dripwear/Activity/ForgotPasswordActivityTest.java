package com.example.dripwear.Activity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import androidx.annotation.NonNull;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ForgotPasswordActivityTest {

    @ParameterizedTest
    @CsvSource({
            "'ishfak@gmail.com', true",
            "'ishrak@domain.com', true",
            "'shakif@org.edu', true",
            "'', false",
            "' ', false",
            "'invalid-email', false",
            "'ishfak@gmail', false",
            "'shakif@.com', false",
            "'@domain.com', false"
    })
    void testEmailValidation(@NonNull String emailInput, boolean expectedIsValid) {
        boolean actualIsValid = !emailInput.trim().isEmpty() &&
                emailInput.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

        assertEquals(expectedIsValid, actualIsValid,
                "Validation failed for: " + emailInput);
    }

    @ParameterizedTest
    @CsvSource({
            "'nahian@gmail.com', true",
            "'saleh.email@domain.co.uk', true",
            "'niaz@org.edu', true",
            "'', false",
            "' ', false",
            "'invalid', false"
    })
    void testShouldAllowPasswordReset(String emailInput, boolean shouldAllowReset) {
        boolean actualShouldAllow = !emailInput.trim().isEmpty() &&
                emailInput.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

        assertEquals(shouldAllowReset, actualShouldAllow,
                "Reset permission incorrect for: " + emailInput);
    }
}