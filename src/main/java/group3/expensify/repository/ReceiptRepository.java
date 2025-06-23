package group3.expensify.repository;

import group3.expensify.model.Receipt;
import group3.expensify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    // Custom query methods can be added here if needed
    List<Receipt> findByUser(User user);  // Example: Find all receipts for a user
}

