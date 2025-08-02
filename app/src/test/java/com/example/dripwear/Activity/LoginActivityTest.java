package com.example.dripwear.Activity;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class LoginActivityTest {

    // Combined email validation tests
    @ParameterizedTest(name = "Email validation: {0} => {1}")
    @CsvSource({
            // Valid emails
            "test@example.com, true",
            "user+tag@domain.com, true",
            "first.last@example.co.uk, true",

            // Invalid emails
            "invalidEmail, false",
            "'', false",
            "user@domain, false",
            ".invalid@example.com, false",
            "null, false"
    })
    void testEmailValidation(String email, boolean expected) {
        if ("null".equals(email)) email = null;
        assertEquals(expected, LoginValidator.isValidEmail(email));
    }

    // Password validation tests
    @ParameterizedTest(name = "Password validation: {0} => {1}")
    @CsvSource({
            "validpass, true",
            "short, false",
            "'', false",
            "null, false"
    })
    void testPasswordValidation(String password, boolean expected) {
        if ("null".equals(password)) password = null;
        assertEquals(expected, LoginValidator.isValidPassword(password));
    }

    // Empty field validation
    @ParameterizedTest(name = "Empty check: email={0}, password={1} => {2}")
    @CsvSource({
            "test@example.com, password, false",
            "'', password, true",
            "test@example.com, '', true",
            "'', '', true",
            "null, null, true"
    })
    void testEmptyFieldValidation(String email, String password, boolean expected) {
        if ("null".equals(email)) email = null;
        if ("null".equals(password)) password = null;
        assertEquals(expected, LoginValidator.hasEmptyFields(email, password));
    }
}

class LoginValidator {
    private static final String EMAIL_REGEX =
            "^[\\w+&-]+(?:\\.[\\w+&-]+)*@" +
                    "(?:[\\w-]+\\.)+[a-zA-Z]{2,7}$";

    public static boolean isValidEmail(String email) {
        return email != null && !email.isEmpty() && email.matches(EMAIL_REGEX);
    }

    public static boolean isValidPassword(String password) {
        return password != null && !password.isEmpty() && password.length() >= 6;
    }

    public static boolean hasEmptyFields(String email, String password) {
        return email == null || email.isEmpty() ||
                password == null || password.isEmpty();
    }
}