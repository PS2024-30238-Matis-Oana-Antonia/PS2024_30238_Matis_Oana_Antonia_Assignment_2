package com.example.carturestibackend.services;

import com.example.carturestibackend.entities.User;
import com.example.carturestibackend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getRole(String name, String password) {
        // Find user by username
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByName(name));

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                return user.getRole();
            }
        }

        return null; // Return null if user does not exist or password does not match
    }

    public User checkUser(String name, String password) {
        // Check if a user with the given username and password exists
        return userRepository.findByNameAndPassword(name, password);
    }

}
