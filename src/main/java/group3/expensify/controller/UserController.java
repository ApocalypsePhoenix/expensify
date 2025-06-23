package group3.expensify.controller;

import group3.expensify.model.User;
import group3.expensify.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Show the login page
    @GetMapping("/login")
    public String showLoginForm() {
        return "LoginPage";  // Render the login page
    }

    // Handle login (decorative action)
    @PostMapping("/login")
    public String login(User user, Model model) {
        // This is just a decorative login, no authentication logic
        return "redirect:/transactions";  // Redirect to transactions page after submitting the form
    }

    // Show the registration page
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "RegisterPage";  // Render the registration page
    }

    // Handle registration
    @PostMapping("/register")
    public String register(User user, Model model) {
        // Check if passwords match
        if (!user.getPassword().equals(user.getConfirmpassword())) {
            model.addAttribute("error", "Passwords do not match");  // Pass error to the page
            return "RegisterPage";  // Return to registration page if passwords don't match
        }

        try {
            // Save the new user to the database (password will be hashed)
            userService.register(user);
            return "redirect:/login";  // Redirect to the login page after successful registration
        } catch (Exception e) {
            model.addAttribute("error", "Error occurred during registration: " + e.getMessage());
            return "RegisterPage";  // Return to registration page if an error occurs
        }
    }
}
