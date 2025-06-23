package group3.expensify.repository;

import group3.expensify.model.Report;
import group3.expensify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    // Find all reports by the user (using the user object)
    List<Report> findByUser(User user);  // Use the user object, not userId directly
}
