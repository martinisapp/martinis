package com.chriswatnee.martinis.controller;

import com.chriswatnee.martinis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for handling user registration
 */
@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegistrationForm(Model model) {
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String processRegistration(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            Model model) {

        try {
            // Validate password confirmation
            if (password == null || !password.equals(confirmPassword)) {
                model.addAttribute("error", "Passwords do not match");
                model.addAttribute("username", username);
                model.addAttribute("firstName", firstName);
                model.addAttribute("lastName", lastName);
                return "register";
            }

            // Create the user
            userService.createUser(username, password, firstName, lastName);

            // Redirect to login page with success message
            return "redirect:/login?registered=true";

        } catch (IllegalArgumentException e) {
            // Handle validation errors
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("firstName", firstName);
            model.addAttribute("lastName", lastName);
            return "register";

        } catch (Exception e) {
            // Handle unexpected errors
            model.addAttribute("error", "Registration failed. Please try again.");
            model.addAttribute("username", username);
            model.addAttribute("firstName", firstName);
            model.addAttribute("lastName", lastName);
            return "register";
        }
    }

}
