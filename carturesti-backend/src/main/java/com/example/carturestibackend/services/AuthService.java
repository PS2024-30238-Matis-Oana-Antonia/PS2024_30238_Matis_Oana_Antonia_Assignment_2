package com.example.carturestibackend.services;

import com.example.carturestibackend.dtos.UserDTO;
import com.example.carturestibackend.dtos.mappers.UserMapper;
import com.example.carturestibackend.entities.Cart;
import com.example.carturestibackend.entities.User;
import com.example.carturestibackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CartService cartService;
    private final Set<String> validRoles;

    @Autowired
    public AuthService(UserRepository userRepository, CartService cartService) {
        this.userRepository = userRepository;
        this.cartService = cartService;

        validRoles = new HashSet<>();
        validRoles.add("admin");
        validRoles.add("client");
    }

    public String getRole(String name) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByName(name));
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String role = user.getRole();
            if (validRoles.contains(role)) {
                return role;
            } else {
                throw new IllegalStateException("Invalid role: " + role);
            }
        }
        return null;
    }

    public UserDTO checkUser(String name, String password) {
        User user = userRepository.findByNameAndPassword(name, password);
        if (user != null) {
            // Fetch the cart ID for the user and set it
            String cartId = cartService.findCartIdByUser(user);
            UserDTO userDTO = UserMapper.toUserDTO(user); // Convert User to UserDTO
            userDTO.setId_cart(cartId); // Set cartId in UserDTO
            return userDTO;
        }
        return null;
    }


}
