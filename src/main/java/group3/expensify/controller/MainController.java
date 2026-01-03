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
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        Map<Long, String> categoryMap = categoryService.getAllCategories().stream()
                .collect(Collectors.toMap(c -> c.getId(), c -> c.getName()));

        // 1. Balance Calculations
        BigDecimal totalIncome = allTransactions.stream()
                .filter(t -> "Income".equalsIgnoreCase(t.getTransactionType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = allTransactions.stream()
                .filter(t -> "Expense".equalsIgnoreCase(t.getTransactionType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalIncome.subtract(totalExpenses);

        // 2. Monthly Stats
        LocalDate now = LocalDate.now();
        List<Transaction> currentMonthTxs = allTransactions.stream()
                .filter(t -> t.getDate().getMonth() == now.getMonth() && t.getDate().getYear() == now.getYear())
                .collect(Collectors.toList());

        BigDecimal monthlyExpenses = currentMonthTxs.stream()
                .filter(t -> "Expense".equalsIgnoreCase(t.getTransactionType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Category Breakdown for Dashboard Chart
        Map<String, BigDecimal> categoryBreakdown = currentMonthTxs.stream()
                .filter(t -> "Expense".equalsIgnoreCase(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        tx -> categoryMap.getOrDefault(tx.getCategoryId(), "Other"),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        // 4. Budget Progress
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

        // 5. Recent Items
        List<Transaction> recentTransactions = allTransactions.stream()
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .limit(5)
                .collect(Collectors.toList());

        List<Bill> allBills = billService.getBillsByUserId(userId);
        List<Bill> upcomingBills = allBills.stream()
                .filter(b -> !"Paid".equalsIgnoreCase(b.getStatus()))
                .sorted(Comparator.comparing(Bill::getDueDate))
                .limit(3)
                .collect(Collectors.toList());

        // 6. Notification Alerts
        List<NotificationAlert> notifications = new ArrayList<>();
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Bill Notifications (based on reminder time)
        for (Bill bill : allBills) {
            if (!"Paid".equalsIgnoreCase(bill.getStatus()) && bill.getReminderTime() != null) {
                if (currentDateTime.isAfter(bill.getReminderTime())) {
                    notifications.add(new NotificationAlert(
                            "Bill Reminder: " + bill.getName(),
                            "Due on " + bill.getDueDate(),
                            "urgent"
                    ));
                }
            }
        }

        // Budget Notifications
        for (BudgetService.BudgetView bv : budgetViews) {
            String status = bv.getStatus();
            if ("Approaching Limit".equalsIgnoreCase(status) ||
                    "Limit Reached".equalsIgnoreCase(status) ||
                    "Exceeding Limit".equalsIgnoreCase(status)) {

                String categoryName = categoryMap.getOrDefault(bv.getBudget().getCategoryId(), "Category");
                String type = "Exceeding Limit".equalsIgnoreCase(status) ? "urgent" : "warning";

                notifications.add(new NotificationAlert(
                        "Budget " + status + "!",
                        categoryName + ": Spent " + bv.getSpent() + " / " + bv.getBudget().getLimitAmount(),
                        type
                ));
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("balance", balance);
        model.addAttribute("totalIncome", totalIncome); // Added for breakdown
        model.addAttribute("totalExpenses", totalExpenses); // Added for breakdown
        model.addAttribute("monthlyExpenses", monthlyExpenses);
        model.addAttribute("budgetPercent", Math.min(budgetPercent, 100));
        model.addAttribute("budgetStatusText", budgetPercent + "% of budget used");
        model.addAttribute("recentTransactions", recentTransactions);
        model.addAttribute("upcomingBills", upcomingBills);
        model.addAttribute("notifications", notifications);
        model.addAttribute("categoryMap", categoryMap);
        model.addAttribute("chartLabels", categoryBreakdown.keySet());
        model.addAttribute("chartData", categoryBreakdown.values());
        model.addAttribute("currencySymbol", user.getDefaultCurrency() != null ? user.getDefaultCurrency().getCurrencySymbol() : "RM");

        return "MainPage";
    }

    // Helper class for notifications
    public static class NotificationAlert {
        private String title;
        private String message;
        private String type; // 'urgent' or 'warning'

        public NotificationAlert(String title, String message, String type) {
            this.title = title;
            this.message = message;
            this.type = type;
        }
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public String getType() { return type; }
    }
}