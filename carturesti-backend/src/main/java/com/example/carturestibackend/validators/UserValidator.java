package com.example.carturestibackend.validators;

import com.example.carturestibackend.entities.User;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UserValidator {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
    private static final int MIN_ALLOWED_AGE = 18;

    public static boolean isValid(User user) {
        return isNameValid(user.getName()) &&
                isAddressValid(user.getAddress()) &&
                isEmailValid(user.getEmail()) &&
                isPasswordValid(user.getPassword()) &&
                isAgeValid(user.getAge()) &&
                isRoleValid(user.getRole());
    }

    public static boolean validateUser(User user) {
        return isValid(user);
    }

    private static boolean isNameValid(String name) {
        return name != null && !name.trim().isEmpty();
    }

    private static boolean isAddressValid(String address) {
        return address != null && !address.trim().isEmpty();
    }

    private static boolean isEmailValid(String email) {
        return email != null && Pattern.matches(EMAIL_REGEX, email);
    }

    private static boolean isPasswordValid(String password) {
        return password != null && Pattern.matches(PASSWORD_REGEX, password);
    }

    private static boolean isAgeValid(int age) {
        return age >= MIN_ALLOWED_AGE;
    }

    private static boolean isRoleValid(String role) {
        return role != null && (role.equals("ADMIN") || role.equals("USER"));
    }
}
