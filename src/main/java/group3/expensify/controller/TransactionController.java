package group3.expensify.controller;

import group3.expensify.model.Category;
import group3.expensify.model.Currency;
import group3.expensify.model.Transaction;
import group3.expensify.model.User;
import group3.expensify.service.CategoryService;
import group3.expensify.service.CurrencyService;
import group3.expensify.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CurrencyService currencyService;

    // Display all transactions for the logged-in user
    @GetMapping
    public String showTransactionsPage(Model model) {
        Long userId = 1L;  // Replace with actual user ID from session or security context
        model.addAttribute("transactions", transactionService.getTransactionsByUser(userId));  // Fetch transactions for the user
        return "transactionsPage";  // Render the transactionsPage.html
    }

    // Show the Add Transaction Page (GET request)
    @GetMapping("/add")
    public String showAddTransactionPage() {
        return "AddTransactionPage";  // Render the AddTransactionPage.html for adding a new transaction
    }

    // Add a transaction (POST request from the form submission)
    @PostMapping("/add")
    public String addTransaction(Transaction transaction,
                                 @RequestParam("category") Long categoryId,  // Get category ID
                                 @RequestParam("currency") Long currencyId,  // Get currency ID
                                 @RequestParam("transaction_type") String transactionType) {  // Get transaction type

        // Fetch the Category and Currency using their IDs
        Category transactionCategory = categoryService.getCategoryById(categoryId);  // Get category by ID
        Currency transactionCurrency = currencyService.getCurrencyById(currencyId);  // Get currency by ID

        // Ensure valid Category and Currency were found
        if (transactionCategory == null || transactionCurrency == null) {
            return "redirect:/transactions/add?error=Invalid category or currency";  // Error handling if category or currency is invalid
        }

        // Set the Category, Currency, and Transaction Type in the Transaction object
        transaction.setCategory(transactionCategory);
        transaction.setCurrency(transactionCurrency);
        transaction.setTransactionType(transactionType);  // Set the transaction type (Expense/Income)

        // Set the User (user ID is hardcoded here, but it should be fetched from session)
        Long userId = 1L;  // Replace with actual user ID (session-based or from security context)
        User user = new User();
        user.setId(userId);
        transaction.setUser(user);  // Associate the user with the transaction

        // Save the transaction to the database
        transactionService.createTransaction(transaction);

        // Redirect to the transactions page after adding the transaction
        return "redirect:/transactions";
    }
}
