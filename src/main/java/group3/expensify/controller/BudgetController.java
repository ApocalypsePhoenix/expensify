package group3.expensify.controller;

import group3.expensify.model.Budget;
import group3.expensify.model.Category;
import group3.expensify.model.User;
import group3.expensify.repository.CategoryRepository;
import group3.expensify.service.BudgetService;
import group3.expensify.service.BudgetService.BudgetView;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String listBudgets(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/users/login";

        List<BudgetView> budgets = budgetService.getUserBudgets(user.getId());
        Map<Long, String> categoryMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        model.addAttribute("budgets", budgets);
        model.addAttribute("categoryMap", categoryMap);
        return "BudgetPage";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "AddBudgetPage";
    }

    @PostMapping("/add")
    public String addBudget(@RequestParam Map<String, String> form, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/users/login";

        Budget budget = new Budget();
        budget.setUserId(user.getId());
        budget.setCategoryId(Long.parseLong(form.get("category")));
        budget.setLimitAmount(new BigDecimal(form.get("limit")));
        budget.setPeriod(form.get("period"));

        budgetService.saveBudget(budget);
        return "redirect:/budgets";
    }
}