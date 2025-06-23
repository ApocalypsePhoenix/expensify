package group3.expensify.service;

import group3.expensify.model.Budget;
import group3.expensify.model.User;
import group3.expensify.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    // Get all budgets for a user
    public List<Budget> getBudgetsByUser(Long userId) {
        User user = new User();  // Ideally, fetch user from database or security context
        user.setId(userId);
        return budgetRepository.findByUser(user);
    }

    // Create a new budget
    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }
}
