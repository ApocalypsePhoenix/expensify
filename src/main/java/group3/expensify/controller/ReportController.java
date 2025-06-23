package group3.expensify.controller;

import group3.expensify.model.Report;
import group3.expensify.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // Get all reports for a user
    @GetMapping("/user/{userId}")
    public List<Report> getReportsByUser(@PathVariable Long userId) {
        return reportService.getReportsByUser(userId);  // Fetch reports based on the userId
    }

    // Create a new report (if needed for manually created reports)
    @PostMapping
    public Report createReport(@RequestBody Report report) {
        return reportService.createReport(report);  // Save the manually provided report to the database
    }

    // Generate a new report (e.g., generate report from transactions)
    @GetMapping("/generate/{userId}")
    public Report generateReport(@PathVariable Long userId) {
        // Generate a report dynamically based on user's transaction data
        return reportService.generateReportForUser(userId);  // Call the service method to generate the report
    }
}
