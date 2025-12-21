package group3.expensify.controller;

import group3.expensify.model.Bill;
import group3.expensify.model.Transaction;
import group3.expensify.model.User;
import group3.expensify.service.BillService;
import group3.expensify.service.BudgetService;
import group3.expensify.service.CategoryService;
import group3.expensify.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MainController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BillService billService;

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String index(HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            return "redirect:/dashboard";
        }
        return "redirect:/users/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/users/login";
        }

        Long userId = user.getId();
        List<Transaction> allTransactions = transactionService.getTransactionsByUserId(userId);

        // 1. Calculate Total Balance
        BigDecimal totalIncome = allTransactions.stream()
                .filter(t -> "Income".equalsIgnoreCase(t.getTransactionType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = allTransactions.stream()
                .filter(t -> "Expense".equalsIgnoreCase(t.getTransactionType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalIncome.subtract(totalExpenses);

        // 2. Monthly Expenses (Current Month)
        LocalDate now = LocalDate.now();
        BigDecimal monthlyExpenses = allTransactions.stream()
                .filter(t -> "Expense".equalsIgnoreCase(t.getTransactionType()))
                .filter(t -> t.getDate().getMonth() == now.getMonth() && t.getDate().getYear() == now.getYear())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Budget Progress (Aggregated)
        List<BudgetService.BudgetView> budgetViews = budgetService.getUserBudgets(userId);
        BigDecimal totalBudgetLimit = budgetViews.stream()
                .map(bv -> bv.getBudget().getLimitAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalBudgetSpent = budgetViews.stream()
                .map(BudgetService.BudgetView::getSpent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int budgetPercent = 0;
        if (totalBudgetLimit.compareTo(BigDecimal.ZERO) > 0) {
            budgetPercent = totalBudgetSpent.multiply(new BigDecimal("100"))
                    .divide(totalBudgetLimit, 0, RoundingMode.HALF_UP).intValue();
        }

        // 4. Recent Transactions (Last 5)
        List<Transaction> recentTransactions = allTransactions.stream()
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .limit(5)
                .collect(Collectors.toList());

        // 5. Upcoming Bills (Unpaid, next 3)
        List<Bill> upcomingBills = billService.getBillsByUserId(userId).stream()
                .filter(b -> !"Paid".equalsIgnoreCase(b.getStatus()))
                .sorted(Comparator.comparing(Bill::getDueDate))
                .limit(3)
                .collect(Collectors.toList());

        // Category Map for Names
        Map<Long, String> categoryMap = categoryService.getAllCategories().stream()
                .collect(Collectors.toMap(c -> c.getId(), c -> c.getName()));

        model.addAttribute("user", user);
        model.addAttribute("balance", balance);
        model.addAttribute("monthlyExpenses", monthlyExpenses);
        model.addAttribute("budgetPercent", Math.min(budgetPercent, 100)); // Cap UI at 100% for bar
        model.addAttribute("budgetStatusText", budgetPercent + "% Used");
        model.addAttribute("recentTransactions", recentTransactions);
        model.addAttribute("upcomingBills", upcomingBills);
        model.addAttribute("categoryMap", categoryMap);
        model.addAttribute("currencySymbol", user.getDefaultCurrency() != null ? user.getDefaultCurrency().getCurrencySymbol() : "RM");

        return "MainPage";
    }
}