package group3.expensify.controller;

import group3.expensify.model.Transaction;
import group3.expensify.model.User;
import group3.expensify.service.CategoryService;
import group3.expensify.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public String showTransactions(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/users/login";

        List<Transaction> transactions = transactionService.getTransactionsByUserId(user.getId());
        model.addAttribute("transactions", transactions);

        // Prepare category map for displaying category names
        Map<Long, String> categoryMap = categoryService.getAllCategories().stream()
                .collect(Collectors.toMap(c -> c.getId(), c -> c.getName()));
        model.addAttribute("categoryMap", categoryMap);

        return "TransactionsPage";
    }

    @GetMapping("/add")
    public String showAddForm() {
        return "AddTransactionPage";
    }

    @PostMapping("/add")
    public String addTransaction(@RequestParam Map<String, String> form, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/users/login";

        Transaction tx = new Transaction();
        tx.setDate(LocalDate.parse(form.get("date")));
        tx.setDescription(form.get("description"));
        tx.setAmount(new BigDecimal(form.get("amount")));
        tx.setTransactionType(form.get("transaction_type"));
        tx.setCategoryId(Long.parseLong(form.get("category")));
        tx.setCurrencyId(Long.parseLong(form.get("currency")));
        tx.setUserId(user.getId());

        transactionService.saveTransaction(tx);
        return "redirect:/transactions";
    }

    // Edit Transaction
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Transaction transaction = transactionService.getTransactionById(id);
        model.addAttribute("transaction", transaction);
        return "EditTransactionPage"; // You need to create this page
    }

    @PostMapping("/edit/{id}")
    public String updateTransaction(@PathVariable Long id, @RequestParam Map<String, String> form) {
        Transaction transaction = transactionService.getTransactionById(id);
        transaction.setDate(LocalDate.parse(form.get("date")));
        transaction.setDescription(form.get("description"));
        transaction.setAmount(new BigDecimal(form.get("amount")));
        transaction.setTransactionType(form.get("transaction_type"));
        transaction.setCategoryId(Long.parseLong(form.get("category")));
        transaction.setCurrencyId(Long.parseLong(form.get("currency")));

        transactionService.updateTransaction(transaction);
        return "redirect:/transactions";
    }

    // Delete Transaction
    @PostMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return "redirect:/transactions";
    }
}
