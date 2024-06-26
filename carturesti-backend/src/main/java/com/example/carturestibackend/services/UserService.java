package com.example.carturestibackend.services;

import com.example.carturestibackend.constants.UserLogger;
import com.example.carturestibackend.dtos.UserDTO;
import com.example.carturestibackend.dtos.mappers.UserMapper;
import com.example.carturestibackend.entities.*;
import com.example.carturestibackend.repositories.*;
import com.example.carturestibackend.validators.UserValidator;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import java.util.stream.Collectors;


/**
 * Service class to handle business logic related to users.
 */
@Service
@Transactional
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final UserValidator userValidator;

    /**
     * Constructor for UserService.
     *
     * @param userRepository       The repository for User entities.
     * @param reviewRepository    The repository for Review entities.
     * @param cartRepository      The repository for Cart entities.
     * @param productRepository   The repository for Product entities.
     * @param orderRepository     The repository for Order entities.
     * @param orderItemRepository The repository for OrderItem entities.
     * @param userValidator       The validator for User entities.
     */
    @Autowired
    public UserService(UserRepository userRepository, ReviewRepository reviewRepository, CartRepository cartRepository, ProductRepository productRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userValidator = userValidator;
    }


    /**
     * Retrieves all users from the database.
     *
     * @return A list of UserDTO objects representing the users.
     */
    public List<UserDTO> findUsers() {
        LOGGER.info(UserLogger.ALL_USERS_RETRIEVED);
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(UserMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id_user The ID of the user to retrieve.
     * @return The UserDTO object representing the retrieved user.
     * @throws ResourceNotFoundException if the user with the specified ID is not found.
     */
    public UserDTO findUserById(String id_user) {
        Optional<User> userOptional = userRepository.findById(id_user);
        if (!userOptional.isPresent()) {
            LOGGER.error(UserLogger.USER_NOT_FOUND_BY_ID, id_user);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id_user);
        }
        return UserMapper.toUserDTO(userOptional.get());
    }

    /**
     * Retrieves a user by their name.
     *
     * @param name The name of the user to retrieve.
     * @return The UserDTO object representing the retrieved user.
     * @throws ResourceNotFoundException if the user with the specified name is not found.
     */
    public UserDTO findUserByName(String name) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByName(name));
        if (!userOptional.isPresent()) {
            LOGGER.error(UserLogger.USER_NOT_FOUND_BY_NAME, name);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with name: " + name);
        }
        return UserMapper.toUserDTO(userOptional.get());
    }
    /**
     * Retrieves a user by their name.
     *
     * @param role The name of the user to retrieve.
     * @return The UserDTO object representing the retrieved user.
     * @throws ResourceNotFoundException if the user with the specified name is not found.
     */
    public UserDTO findUserByRole(String role) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByRole(role));
        if (!userOptional.isPresent()) {
            LOGGER.error(UserLogger.USER_NOT_FOUND_BY_ROLE, role);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with role: " + role);
        }
        return UserMapper.toUserDTO(userOptional.get());
    }
    /**
     * Retrieves a user by their name and password.
     *
     * @param name and password The name of the user to retrieve.
     * @return The UserDTO object representing the retrieved user.
     * @throws ResourceNotFoundException if the user with the specified name is not found.
     */
    public UserDTO findUserByNameAndPassword(String name, String password) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByNameAndPassword(name, password));
        if (!userOptional.isPresent()) {
            LOGGER.error(UserLogger.USER_NOT_FOUND_BY_NAME_AND_PASSWORD, name, password);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with username and password: " + name + ", " + password);
        }
        return UserMapper.toUserDTO(userOptional.get());
    }


    /**
     * Retrieves a user by their email.
     *
     * @param email The email of the user to retrieve.
     * @return The UserDTO object representing the retrieved user.
     * @throws ResourceNotFoundException if the user with the specified email is not found.
     */
    public UserDTO findUserByEmail(String email) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
        if (!userOptional.isPresent()) {
            LOGGER.error(UserLogger.USER_NOT_FOUND_BY_EMAIL, email);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with email: " + email);
        }
        return UserMapper.toUserDTO(userOptional.get());
    }

    /**
     * Inserts a new user into the database.
     *
     * @param userDTO The UserDTO object representing the user to insert.
     * @return The ID of the newly inserted user.
     */
    @Transactional
    public String insert(UserDTO userDTO) {

        User user = UserMapper.fromUserDTO(userDTO);
        //aici folosesc validator
        UserValidator.isValid(user);

        Cart cart = new Cart();
        cart = cartRepository.save(cart);
        user.setCart(cart);
        user = userRepository.save(user);
        cart.setUser(user);

        cart = cartRepository.save(cart);

        LOGGER.debug(UserLogger.USER_INSERTED, user.getId_user());

        return user.getId_user();
    }


    /**
     * Deletes a user from the database by their ID.
     *
     * @param id The ID of the user to delete.
     * @throws ResourceNotFoundException if the user with the specified ID is not found.
     */
    @Transactional
    public void deleteUserById(String id) throws Exception {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Verifică și șterge comenzile asociate utilizatorului
            List<Order> orders = user.getOrders();
            if (orders != null && !orders.isEmpty()) {
                for (Order order : orders) {
                    order.setUser(null);
                    orderRepository.save(order); // Salvează modificările în baza de date
                    orderRepository.deleteById(order.getId_order());
                    orderRepository.delete(order);// Șterge comanda
                }
            }
            userRepository.deleteById(id);
            LOGGER.info(UserLogger.USER_DELETED, id);
        } else {
            LOGGER.info(UserLogger.USER_NOT_FOUND_BY_ID, id);
            throw new Exception("User with id " + id + " was not found in the database.");
        }
    }


    /**
     * Updates an existing user in the database.
     *
     * @param id_user  The ID of the user to update.
     * @param userDTO The updated UserDTO object representing the new state of the user.
     * @return The updated UserDTO object.
     * @throws ResourceNotFoundException if the user with the specified ID is not found.
     */
    public UserDTO updateUser(String id_user, UserDTO userDTO) {
        Optional<User> userOptional = userRepository.findById(id_user);
        if (!userOptional.isPresent()) {
            LOGGER.error(UserLogger.USER_NOT_FOUND_BY_ID, id_user);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id_user);
        }

        User existingUser = userOptional.get();
        existingUser.setName(userDTO.getName());
        existingUser.setAge(userDTO.getAge());
        existingUser.setAddress(userDTO.getAddress());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setPassword(userDTO.getPassword());
        existingUser.setRole(userDTO.getRole());

        User updatedUser = userRepository.save(existingUser);
        LOGGER.debug(UserLogger.USER_UPDATED, updatedUser.getId_user());

        return UserMapper.toUserDTO(updatedUser);
    }

}