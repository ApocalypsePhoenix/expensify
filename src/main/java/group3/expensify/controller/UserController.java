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
import org.springframework.web.multipart.MultipartFile;

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

        String currencyCode = body.get("currency");
        userService.registerUser(fullname, email, password, currencyCode);
        return "redirect:/users/login?success=1";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "RegisterPage";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "LoginPage";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session) {

        User user = userService.loginUser(email, password);

        if (user != null) {
            session.setAttribute("loggedInUser", user);
            return "redirect:/dashboard";
        } else {
            return "redirect:/users/login?error=1";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/login";
    }

    @GetMapping("/settings")
    public String showSettingsPage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/users/login";

        // Refresh user from DB to ensure latest data (like profile image) is shown
        User user = userService.getUserById(loggedInUser.getId());
        List<Currency> currencies = currencyService.getAllCurrencies();

        model.addAttribute("user", user);
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

        // Update session
        User updatedUser = userService.getUserById(loggedInUser.getId());
        session.setAttribute("loggedInUser", updatedUser);

        return "redirect:/users/settings?success=1";
    }

    // New Profile Image Upload endpoint
    @PostMapping("/settings/upload-avatar")
    public String uploadAvatar(@RequestParam("avatar") MultipartFile file, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/users/login";

        try {
            if (!file.isEmpty()) {
                userService.updateProfileImage(loggedInUser.getId(), file);
                // Refresh session
                User updatedUser = userService.getUserById(loggedInUser.getId());
                session.setAttribute("loggedInUser", updatedUser);
            }
        } catch (Exception e) {
            return "redirect:/users/settings?error=upload_failed";
        }

        return "redirect:/users/settings?success=avatar_updated";
    }
}