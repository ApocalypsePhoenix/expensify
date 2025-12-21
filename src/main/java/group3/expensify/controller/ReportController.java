package group3.expensify.controller;

import group3.expensify.model.Transaction;
import group3.expensify.model.User;
import group3.expensify.repository.TransactionRepository;
import group3.expensify.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping
    public String showReportPage(@RequestParam(defaultValue = "monthly") String period, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/users/login";

        // 1. Data for Category Pie Chart (Existing logic)
        Map<String, BigDecimal> categoryData = reportService.getCategoryTotalsByPeriod(user.getId(), period);

        // 2. Data for Income vs Expense Bar Chart
        LocalDate now = LocalDate.now();
        LocalDate startDate = getStartDate(period, now);
        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(user.getId(), startDate, now);

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> "Income".equalsIgnoreCase(t.getTransactionType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> "Expense".equalsIgnoreCase(t.getTransactionType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Data for Trend Line Chart (Grouped by Date)
        Map<LocalDate, BigDecimal> trendData = transactions.stream()
                .filter(t -> "Expense".equalsIgnoreCase(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        Transaction::getDate,
                        TreeMap::new, // Keep dates sorted
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        model.addAttribute("categoriesJs", categoryData.keySet());
        model.addAttribute("totalsJs", categoryData.values());

        model.addAttribute("incomeVsExpenseLabels", Arrays.asList("Income", "Expense"));
        model.addAttribute("incomeVsExpenseData", Arrays.asList(totalIncome, totalExpense));

        model.addAttribute("trendLabels", trendData.keySet().stream().map(LocalDate::toString).collect(Collectors.toList()));
        model.addAttribute("trendTotals", trendData.values());

        model.addAttribute("selectedPeriod", period);
        model.addAttribute("currencySymbol", user.getDefaultCurrency() != null ? user.getDefaultCurrency().getCurrencySymbol() : "RM");

        return "GenerateReportPage";
    }

    @PostMapping("/generate")
    public void generateReport(@RequestParam String reportPeriod, HttpSession session, HttpServletResponse response) throws Exception {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            response.sendRedirect("/users/login");
            return;
        }

        ByteArrayOutputStream pdf = reportService.generatePdf(user.getId(), reportPeriod);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=expensify-report-" + reportPeriod + ".pdf");
        response.getOutputStream().write(pdf.toByteArray());
        response.getOutputStream().flush();
    }

    private LocalDate getStartDate(String period, LocalDate now) {
        return switch (period.toLowerCase()) {
            case "daily" -> now.minusDays(1);
            case "weekly" -> now.minusWeeks(1);
            case "monthly" -> now.minusMonths(1);
            case "yearly" -> now.minusYears(1);
            default -> now.minusMonths(1);
        };
    }
}