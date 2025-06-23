package group3.expensify.repository;

import group3.expensify.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Fetch all transactions by userId
    List<Transaction> findByUserId(Long userId);  // Finds all transactions associated with a userId
}
