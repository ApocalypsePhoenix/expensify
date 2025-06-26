package group3.expensify.service;

import group3.expensify.model.Budget;
import group3.expensify.model.Transaction;
import group3.expensify.repository.BudgetRepository;
import group3.expensify.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public List<BudgetView> getUserBudgets(Long userId) {
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        List<Transaction> transactions = transactionRepository.findByUserId(userId);

        List<BudgetView> views = new ArrayList<>();

        for (Budget budget : budgets) {
            BigDecimal spent = transactions.stream()
                    .filter(tx -> tx.getCategoryId().equals(budget.getCategoryId()))
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String status;
            BigDecimal usage = spent.divide(budget.getLimitAmount(), 2, BigDecimal.ROUND_HALF_UP);

            if (usage.compareTo(BigDecimal.ONE) > 0) {
                status = "Exceeding Limit";
            } else if (usage.compareTo(BigDecimal.ONE) == 0) {
                status = "Limit Reached";
            } else if (usage.compareTo(new BigDecimal("0.75")) >= 0) {
                status = "Approaching Limit";
            } else {
                status = "On Track";
            }

            views.add(new BudgetView(budget, spent, status));
        }
        return views;
    }

    public void saveBudget(Budget budget) {
        budgetRepository.save(budget);
    }

    public static class BudgetView {
        public Budget budget;
        public BigDecimal spent;
        public String status;

        public BudgetView(Budget budget, BigDecimal spent, String status) {
            this.budget = budget;
            this.spent = spent;
            this.status = status;
        }

        public Budget getBudget() { return budget; }
        public BigDecimal getSpent() { return spent; }
        public String getStatus() { return status; }
    }
}