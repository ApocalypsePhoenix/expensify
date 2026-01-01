package group3.expensify.repository;

import group3.expensify.model.Transaction;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Fetch all transactions by userId
    List<Transaction> findByUserId(Long userId);

    // Support sorting for user transactions
    List<Transaction> findByUserId(Long userId, Sort sort);

    List<Transaction> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}