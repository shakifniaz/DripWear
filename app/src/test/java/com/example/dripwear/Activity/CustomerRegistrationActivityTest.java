package com.example.dripwear.Activity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerRegistrationActivityTest {

    //This pattern checks for proper email format with @ and domain
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static Stream<Arguments> registrationDataProvider() {
        return Stream.of(
                //Happy path - everything correct
                Arguments.of("test@example.com", "password123", "password123",
                        "Ishrak Saleh", "01/01/2002", "1234567890", "Male", true),

                //Emails that should fail
                Arguments.of("invalid-email", "password123", "password123",
                        "Ishfak Akbar", "01/01/2002", "1234567890", "Male", false),
                Arguments.of("missing@domain", "password123", "password123",
                        "Shakif Niaz", "01/01/2002", "1234567890", "Male", false),
                Arguments.of("", "password123", "password123",
                        "Ishrak Saleh", "01/01/2002", "1234567890", "Male", false),

                //Password problems we should catch
                Arguments.of("test@example.com", "short", "short",
                        "Ishfak Akbar", "01/01/2002", "1234567890", "Male", false),
                Arguments.of("test@example.com", "password123", "mismatch",
                        "Shakif Niaz", "01/01/2002", "1234567890", "Male", false),

                //Checking required fields
                Arguments.of("test@example.com", "password123", "password123",
                        "", "01/01/2002", "1234567890", "Male", false), //Forgot to enter name
                Arguments.of("test@example.com", "password123", "password123",
                        "Ishrak Saleh", "", "1234567890", "Male", false), //Missing birth date
                Arguments.of("test@example.com", "password123", "password123",
                        "Ishfak Akbar", "01/01/2002", "", "Male", false), //No phone number
                Arguments.of("test@example.com", "password123", "password123",
                        "Shakif Niaz", "01/01/2002", "1234567890", "", false) //Gender not selected
        );
    }

    @ParameterizedTest
    @MethodSource("registrationDataProvider")
    void testValidationLogic(
            String email,
            String password,
            String confirmPassword,
            String name,
            String dob,
            String phone,
            String gender,
            boolean expectedResult
    ) {
        boolean actualResult = validateInputsTestImplementation(
                email, password, confirmPassword, name, dob, phone, gender
        );

        assertEquals(expectedResult, actualResult,
                () -> {
                    String message = "Validation failed for:\n" +
                            "Email: " + email + "\n" +
                            "Password: " + password + "\n" +
                            "Confirm Password: " + confirmPassword + "\n" +
                            "Name: " + name + "\n" +
                            "DOB: " + dob + "\n" +
                            "Phone: " + phone + "\n" +
                            "Gender: " + gender + "\n" +
                            "Expected: " + expectedResult + "\n" +
                            "Actual: " + actualResult;
                    return message;
                }
        );
    }

    private boolean validateInputsTestImplementation(
            String email,
            String password,
            String confirmPassword,
            String name,
            String dob,
            String phone,
            String gender
    ) {
        //First check if the email looks right
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            return false;
        }

        //Passwords need to be at least 6 characters
        if (password.length() < 6) {
            return false;
        }

        //Both password fields should match exactly
        if (!password.equals(confirmPassword)) {
            return false;
        }

        //All these fields are required
        if (name.isEmpty()) {
            return false;
        }

        if (dob.isEmpty()) {
            return false;
        }

        if (phone.isEmpty()) {
            return false;
        }

        return !gender.isEmpty();
    }
}