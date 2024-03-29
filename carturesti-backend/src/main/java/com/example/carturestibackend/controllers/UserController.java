package com.example.carturestibackend.controllers;

import com.example.carturestibackend.constants.UserLogger;
import com.example.carturestibackend.dtos.UserDTO;
import com.example.carturestibackend.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class to handle HTTP requests related to users.
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    /**
     * Constructs a new UserController with the specified UserService.
     *
     * @param userService The UserService used to handle user-related business logic.
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves all users.
     *
     * @return A ResponseEntity containing a list of UserDTO objects representing the users.
     */
    @GetMapping()
    public ResponseEntity<List<UserDTO>> getUsers() {
        LOGGER.info(UserLogger.ALL_USERS_RETRIEVED);
        List<UserDTO> dtos = userService.findUsers();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /**
     * Inserts a new user.
     *
     * @param userDTO The UserDTO object representing the user to insert.
     * @return A ResponseEntity containing the ID of the newly inserted user.
     */
    @PostMapping()
    public ResponseEntity<String> insertUser(@Valid @RequestBody UserDTO userDTO) {
        String userID = userService.insert(userDTO);
        LOGGER.debug(UserLogger.USER_INSERTED, userID);
        return new ResponseEntity<>(userID, HttpStatus.CREATED);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userID The ID of the user to retrieve.
     * @return A ResponseEntity containing the UserDTO object representing the retrieved user.
     */
    @GetMapping(value = "/{id_user}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("id_user") String userID) {
        LOGGER.info(UserLogger.USER_RETRIEVED_BY_ID, userID);
        UserDTO dto = userService.findUserById(userID);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Retrieves a user by their name.
     *
     * @param name The name of the user to retrieve.
     * @return A ResponseEntity containing the UserDTO object representing the retrieved user.
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<UserDTO> getUserByName(@PathVariable("name") String name) {
        LOGGER.info(UserLogger.USER_NOT_FOUND_BY_NAME, name);
        UserDTO dto = userService.findUserByName(name);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email The email of the user to retrieve.
     * @return A ResponseEntity containing the UserDTO object representing the retrieved user.
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable("email") String email) {
        LOGGER.info(UserLogger.USER_NOT_FOUND_BY_EMAIL, email);
        UserDTO dto = userService.findUserByEmail(email);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userID The ID of the user to delete.
     * @return A ResponseEntity indicating the success of the operation.
     */
    @DeleteMapping(value = "/{id_user}")
    public ResponseEntity<String> deleteUser(@PathVariable("id_user") String userID) {
        userService.deleteUserById(userID);
        LOGGER.debug(UserLogger.USER_DELETED, userID);
        return new ResponseEntity<>("User with ID " + userID + " deleted successfully", HttpStatus.OK);
    }

    /**
     * Updates a user by their ID.
     *
     * @param userID   The ID of the user to update.
     * @param userDTO  The updated UserDTO object representing the new state of the user.
     * @return A ResponseEntity containing the updated UserDTO object.
     */
    @PutMapping(value = "/{id_user}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id_user") String userID, @Valid @RequestBody UserDTO userDTO) {
        LOGGER.debug(UserLogger.USER_UPDATED, userID);
        UserDTO updatedUser = userService.updateUser(userID, userDTO);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
}
