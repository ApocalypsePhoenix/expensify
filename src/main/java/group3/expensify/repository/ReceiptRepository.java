package group3.expensify.repository;

import group3.expensify.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    // Custom query to find receipts by userId
    List<Receipt> findByUserId(Long userId);
}
