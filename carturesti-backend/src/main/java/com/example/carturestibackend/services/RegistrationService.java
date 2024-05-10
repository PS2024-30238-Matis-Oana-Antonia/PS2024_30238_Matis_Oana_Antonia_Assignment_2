package com.example.carturestibackend.services;

import com.example.carturestibackend.entities.User;
import com.example.carturestibackend.entities.Cart;
import com.example.carturestibackend.repositories.UserRepository;
import com.example.carturestibackend.repositories.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public RegistrationService(UserRepository userRepository, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional
    public User registerUser(String name, String address, String email, String password, int age) {
        // Check if the user already exists
        User existingUser = userRepository.findByNameAndAddressAndEmail(name, address, email);
        if (existingUser != null) {
            // User already exists, return null
            return null;
        } else {
            // User does not exist, proceed with registration
            User newUser = User.builder()
                    .name(name)
                    .address(address)
                    .email(email)
                    .password(password)
                    .age(age)
                    .role("client") // Set the role to "client" by default
                    .build();
            // Save the new user
            newUser = userRepository.save(newUser);

            // Create a new cart for the user
            Cart newCart = new Cart();
            newCart.setUser(newUser); // Associate the cart with the user
            newCart = cartRepository.save(newCart);

            // Associate the cart with the user and save the user again
            newUser.setCart(newCart);
            newUser = userRepository.save(newUser);

            return newUser;
        }
    }
    public boolean isUserExistsByEmail(String email) {
        User existingUser = userRepository.findByEmail(email);
        return existingUser != null;
    }
}
