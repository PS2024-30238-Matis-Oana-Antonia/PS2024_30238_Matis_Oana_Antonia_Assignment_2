package com.example.carturestibackend.services;

import com.example.carturestibackend.entities.User;
import com.example.carturestibackend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final Set<String> validRoles;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;

        validRoles = new HashSet<>();
        validRoles.add("admin");
        validRoles.add("client");

    }

    public String getRole(String name) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByName(name));

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String role = user.getRole();

            // Validate role against the set of valid roles
            if (validRoles.contains(role)) {
                return role;
            } else {
                // Handle case where the role is not valid
                throw new IllegalStateException("Invalid role: " + role);
            }
        }

        return null;
    }

    public User checkUser(String name, String password) {
        return userRepository.findByNameAndPassword(name, password);
    }
}

