package group3.expensify.service;

import group3.expensify.model.Report;
import group3.expensify.model.Transaction;
import group3.expensify.model.User;
import group3.expensify.repository.ReportRepository;
import group3.expensify.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // Get all reports for a user
    public List<Report> getReportsByUser(Long userId) {
        // Fetch user from database
        User user = new User();  // Normally, you would fetch the user from the database using the userId
        user.setId(userId);
        return reportRepository.findByUser(user);  // Fetch reports for this user
    }

    // Create a new report
    public Report createReport(Report report) {
        return reportRepository.save(report);  // Save the report to the database
    }

    // Generate a report for the user (aggregating transactions)
    public Report generateReportForUser(Long userId) {
        // Fetch the user (in real cases, you would retrieve the user from the database)
        User user = new User();
        user.setId(userId); // In reality, retrieve the user object from the database

        // Fetch all transactions for the user
        List<Transaction> transactions = transactionRepository.findByUserId(userId);

        // Variables to store aggregated amounts
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        // Loop through transactions to calculate total income and expenses
        for (Transaction transaction : transactions) {
            if ("income".equalsIgnoreCase(transaction.getTransactionType())) {
                totalIncome = totalIncome.add(transaction.getAmount());
            } else if ("expense".equalsIgnoreCase(transaction.getTransactionType())) {
                totalExpense = totalExpense.add(transaction.getAmount());
            }
        }

        // Generate report content
        BigDecimal netBalance = totalIncome.subtract(totalExpense);
        String content = "Generated Report for User ID: " + userId + "\n" +
                "Total Income: " + totalIncome + "\n" +
                "Total Expenses: " + totalExpense + "\n" +
                "Net Balance: " + netBalance + "\n";

        // Create and save the report
        Report report = new Report();
        report.setUser(user);  // Set the user object (not userId)
        report.setTimePeriod("monthly");  // Example of setting time period (could be dynamic)
        report.setData(content);  // Set the generated report content

        return reportRepository.save(report);  // Save the report to the database
    }
}
