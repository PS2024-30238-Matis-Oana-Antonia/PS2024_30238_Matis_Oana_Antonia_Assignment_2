package com.example.carturestibackend.services;

import com.example.carturestibackend.config.RestTemplateConfig;
import com.example.carturestibackend.entities.NotificationRequest;
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
    private final RestTemplateConfig restTemplateConfig;

    public RegistrationService(UserRepository userRepository, CartRepository cartRepository, RestTemplateConfig restTemplateConfig) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.restTemplateConfig = restTemplateConfig;
    }

    @Transactional
    public User registerUser(String name, String address, String email, String password, int age) {
        User existingUser = userRepository.findByNameAndAddressAndEmail(name, address, email);
        if (existingUser != null) {
            return null;
        } else {

            User newUser = User.builder()
                    .name(name)
                    .address(address)
                    .email(email)
                    .password(password)
                    .age(age)
                    .role("client")
                    .build();

            newUser = userRepository.save(newUser);

            Cart newCart = new Cart();
            newCart.setUser(newUser);
            newCart = cartRepository.save(newCart);

            newUser.setCart(newCart);
            newUser = userRepository.save(newUser);

            restTemplateConfig.sendEmail(createNotificationRequest(newUser), restTemplateConfig.restTemplate());

            return newUser;
        }
    }

    public boolean isUserExistsByEmail(String email) {
        User existingUser = userRepository.findByEmail(email);
        return existingUser != null;
    }

    private NotificationRequest createNotificationRequest(User user) {
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setName(user.getName());
        notificationRequest.setEmail(user.getEmail());
        notificationRequest.setSubject("Welcome to Cărturești Online Shop!");

        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(user.getName()).append(",<br><br>")
                .append("Welcome to our online shop! We're thrilled to have you join us.<br>")
                .append("If you have any questions or need assistance, feel free to contact our support team.<br><br>")
                .append("Happy shopping!<br>")
                .append("The Cărturești Team.");

        notificationRequest.setBody(body.toString());

        return notificationRequest;
    }



}
