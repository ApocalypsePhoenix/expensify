package group3.expensify.repository;

import group3.expensify.model.Bill;
import group3.expensify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    // Custom query methods can be added here if needed
    List<Bill> findByUser(User user);  // Example: Find all bills for a user
}
