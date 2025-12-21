package group3.expensify.controller;

import group3.expensify.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String index(HttpSession session) {
        // If user is logged in, send them to dashboard
        if (session.getAttribute("loggedInUser") != null) {
            return "redirect:/dashboard";
        }
        // Otherwise send to login
        return "redirect:/users/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/users/login";
        }

        // Pass user to the view for personalized welcome message
        model.addAttribute("user", user);
        return "MainPage";
    }
}