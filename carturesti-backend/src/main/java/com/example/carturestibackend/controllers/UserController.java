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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller class to handle HTTP requests related to users.
 */
@Controller
@CrossOrigin
@RequestMapping("/user")
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
     * @return A ModelAndView containing a list of UserDTO objects representing the users.
     */
    @GetMapping()
    public ModelAndView getUsers() {
        LOGGER.info(UserLogger.ALL_USERS_RETRIEVED);
        List<UserDTO> dtos = userService.findUsers();
        ModelAndView modelAndView = new ModelAndView("/user");
        modelAndView.addObject("users", dtos);
        return modelAndView;
    }

    /**
     * Inserts a new user.
     *
     * @param userDTO The UserDTO object representing the user to insert.
     * @return A ModelAndView containing the ID of the newly inserted user.
     */
    @PostMapping("/insertUser")
    public ModelAndView insertUser(@Valid @ModelAttribute UserDTO userDTO) {
        String userID = userService.insert(userDTO);
        LOGGER.debug(UserLogger.USER_INSERTED, userID);
        ModelAndView modelAndView = new ModelAndView("/user");
        modelAndView.addObject("userID", userID);
        return new ModelAndView("redirect:/user");
    }


    /**
     * Retrieves a user by their ID.
     *
     * @param userID The ID of the user to retrieve.
     * @return A ModelAndView containing the UserDTO object representing the retrieved user.
     */
    @GetMapping(value = "/{id_user}")
    public ModelAndView getUser(@PathVariable("id_user") String userID) {
        LOGGER.info(UserLogger.USER_RETRIEVED_BY_ID, userID);
        UserDTO dto = userService.findUserById(userID);
        ModelAndView modelAndView = new ModelAndView("/user");
        modelAndView.addObject("user", dto);
        return modelAndView;
    }

    /**
     * Retrieves a user by their name.
     *
     * @param name The name of the user to retrieve.
     * @return A ModelAndView containing the UserDTO object representing the retrieved user.
     */
    @GetMapping("/name/{name}")
    public ModelAndView getUserByName(@PathVariable("name") String name) {
        LOGGER.info(UserLogger.USER_NOT_FOUND_BY_NAME, name);
        UserDTO dto = userService.findUserByName(name);
        ModelAndView modelAndView = new ModelAndView("/user");
        modelAndView.addObject("user", dto);
        return modelAndView;
    }

    @GetMapping("/role/{role}")
    public ModelAndView getUserByRole(@PathVariable("role") String role) {
        LOGGER.info(UserLogger.USER_NOT_FOUND_BY_ROLE, role);
        UserDTO dto = userService.findUserByRole(role);
        ModelAndView modelAndView = new ModelAndView("/user");
        modelAndView.addObject("user", dto);
        return modelAndView;
    }
    @GetMapping("/password/{name}")
    public ModelAndView getUserByNameAndPassword(@PathVariable("name") String name, @RequestParam("password") String password) {
        LOGGER.info(UserLogger.USER_NOT_FOUND_BY_NAME, name);
        UserDTO dto = userService.findUserByNameAndPassword(name, password);
        ModelAndView modelAndView = new ModelAndView("/user");
        modelAndView.addObject("user", dto);
        return modelAndView;
    }


    /**
     * Retrieves a user by their email.
     *
     * @param email The email of the user to retrieve.
     * @return A ModelAndView containing the UserDTO object representing the retrieved user.
     */
    @GetMapping("/email/{email}")
    public ModelAndView getUserByEmail(@PathVariable("email") String email) {
        LOGGER.info(UserLogger.USER_NOT_FOUND_BY_EMAIL, email);
        UserDTO dto = userService.findUserByEmail(email);
        ModelAndView modelAndView = new ModelAndView("/user");
        modelAndView.addObject("user", dto);
        return modelAndView;
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userID The ID of the user to delete.
     * @return A ModelAndView indicating the success of the operation.
     */
    @PostMapping(value = "/delete")
    public ModelAndView deleteUser(@RequestParam("id_user") String userID, RedirectAttributes redirectAttributes) {
        ModelAndView mav = new ModelAndView("redirect:/user"); // Redirecting back to the user page
        try {
            userService.deleteUserById(userID);
            LOGGER.debug(UserLogger.USER_DELETED, userID);
            redirectAttributes.addFlashAttribute("successMessage", "User with ID " + userID + " deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete user with ID " + userID + ". Please try again.");
        }
        return mav;
    }



    /**
     * Updates a user by their ID.
     *
     * @param id_user   The ID of the user to update.
     * @param userDTO  The updated UserDTO object representing the new state of the user.
     * @return A ModelAndView containing the updated UserDTO object.
     */
    @PostMapping("/userUpdate")
    public ModelAndView updateUser(@RequestParam("id_user") String id_user, @Valid @ModelAttribute UserDTO userDTO) {
        ModelAndView mav = new ModelAndView("redirect:/user"); // Redirecting back to the user page
        try {
            UserDTO updatedUser = userService.updateUser(id_user, userDTO);
            // You can optionally add a success message to be displayed on the redirected page
            mav.addObject("successMessage", "User updated successfully!");
        } catch (Exception e) {
            // If an error occurs during the update, you can add an error message to be displayed on the redirected page
            mav.addObject("errorMessage", "Failed to update user. Please try again.");
        }
        return mav;
    }

}
