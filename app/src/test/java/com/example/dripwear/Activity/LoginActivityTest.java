package com.example.dripwear.Activity;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LoginActivityTest {

    @ParameterizedTest(name = "Email ''{0}'' should be valid? {1}")
    @CsvSource({
            "user@example.com, true",
            "invalidEmail, false",
            "'', false",
            "user@domain, false",
            "user@domain.com, true",
            "null, false",
            ".username@example.com, false",
            "username@example..com, false",
            "email+tag@example.com, true",
            "first.last@example.co.uk, true",
            "user.name+tag@domain.com, true"
    })
    void testEmailValidation(String email, boolean expected) {
        if ("null".equals(email)) email = null;
        assertEquals(expected, LoginValidator.isValidEmail(email),
                "Email validation failed for: " + email);
    }

    @ParameterizedTest(name = "Invalid email format: {0}")
    @ValueSource(strings = {
            "plainaddress",
            "@missingusername.com",
            "username@.com",
            ".username@example.com",
            "username@example..com",
            "username@.example.com",
            "username@example.com."
    })
    void testInvalidEmailFormats(String email) {
        assertFalse(LoginValidator.isValidEmail(email),
                "Email '" + email + "' should be invalid");
    }

    @ParameterizedTest(name = "Valid email format: {0}")
    @ValueSource(strings = {
            "test@example.com",
            "first.last@example.co.uk",
            "email@subdomain.example.com",
            "123456@example.com",
            "email+tag@example.com",
            "user.name@domain.com",
            "user_name@domain.org",
            "user.name+tag@domain.com",
            "u@domain.com",
            "me@sub.sub.sub.domain.com"
    })
    void testValidEmailFormats(String email) {
        assertTrue(LoginValidator.isValidEmail(email),
                "Email '" + email + "' should be valid");
    }

    @ParameterizedTest(name = "Password ''{0}'' (length {1}) should be valid? {2}")
    @MethodSource("providePasswordTestCases")
    void testPasswordValidation(String password, int length, boolean expected) {
        assertEquals(expected, LoginValidator.isValidPassword(password),
                "Password validation failed for: " + password);
    }

    private static Stream<Arguments> providePasswordTestCases() {
        return Stream.of(
                Arguments.of("short", 5, false),
                Arguments.of("validpass", 8, true),
                Arguments.of("", 0, false),
                Arguments.of(null, 0, false),
                Arguments.of("longpassword123", 14, true),
                Arguments.of("space pass", 9, true),
                Arguments.of("special!@#", 9, true)
        );
    }

    @ParameterizedTest(name = "Fields ''{0}'' and ''{1}'' should be empty? {2}")
    @CsvSource({
            "'user@test.com', 'password', false",
            "'', 'password', true",
            "'user@test.com', '', true",
            "'', '', true",
            "'null', 'null', true"
    })
    void testEmptyFieldValidation(String email, String password, boolean expectedEmpty) {
        if ("null".equals(email)) email = null;
        if ("null".equals(password)) password = null;

        assertEquals(expectedEmpty, LoginValidator.hasEmptyFields(email, password),
                "Empty field check failed for email: " + email + ", password: " + password);
    }
}
// Helper class for validation logic that would be extracted from LoginActivity
class LoginValidator {
    // Robust email regex pattern
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.matches(EMAIL_REGEX);
    }

    public static boolean isValidPassword(String password) {
        return password != null && !password.isEmpty() && password.length() >= 6;
    }

    public static boolean hasEmptyFields(String email, String password) {
        return email == null || email.isEmpty() ||
                password == null || password.isEmpty();
    }
}