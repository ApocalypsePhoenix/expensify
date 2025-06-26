package group3.expensify.controller;

import group3.expensify.model.User;
import group3.expensify.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import group3.expensify.model.Currency;
import group3.expensify.service.CurrencyService;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CurrencyService currencyService;

    @PostMapping("/register")
    public String register(@RequestParam Map<String, String> body) {
        String fullname = body.get("fullname");
        String email = body.get("email");
        String password = body.get("password");
        String confirmPassword = body.get("confirmpassword");

        if (!password.equals(confirmPassword)) {
            return "redirect:/register?error=password_mismatch";
        }

        userService.registerUser(fullname, email, password);
        return "redirect:/users/login?success=1";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "RegisterPage";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "LoginPage"; // make sure this matches your actual login page
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session) {

        User user = userService.loginUser(email, password);

        if (user != null) {
            // Login successful - store in session
            session.setAttribute("loggedInUser", user);
            return "redirect:/transactions"; // or any page after login
        } else {
            // Login failed - redirect with error
            return "redirect:/users/login?error=1";
        }
    }

    @GetMapping("/settings")
    public String showSettingsPage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/users/login";

        List<Currency> currencies = currencyService.getAllCurrencies();
        model.addAttribute("user", loggedInUser);
        model.addAttribute("currencies", currencies);
        return "SettingsPage";
    }

    @PostMapping("/settings")
    public String updateSettings(@RequestParam String name,
                                 @RequestParam String email,
                                 @RequestParam Long currency,
                                 HttpSession session) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/users/login";

        userService.updateUserSettings(loggedInUser.getId(), name, email, currency);

        // Update session with latest data
        User updatedUser = userService.loginUser(email, loggedInUser.getPassword());
        session.setAttribute("loggedInUser", updatedUser);

        return "redirect:/users/settings?success=1";
    }
}
