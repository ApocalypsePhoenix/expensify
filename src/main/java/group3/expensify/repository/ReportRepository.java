package group3.expensify.repository;

import group3.expensify.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    // Optional: custom queries can go here
}