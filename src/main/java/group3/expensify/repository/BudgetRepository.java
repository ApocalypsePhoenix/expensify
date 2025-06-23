package group3.expensify.repository;

import group3.expensify.model.Budget;
import group3.expensify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    // Custom query methods can be added here if needed
    List<Budget> findByUser(User user);  // Example: Find all budgets for a user
}

