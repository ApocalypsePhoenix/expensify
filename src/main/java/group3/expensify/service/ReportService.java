package group3.expensify.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import group3.expensify.model.Report;
import group3.expensify.model.Transaction;
import group3.expensify.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired private ReportRepository reportRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private BillRepository billRepository;
    @Autowired private BudgetRepository budgetRepository;
    @Autowired private CategoryRepository categoryRepository;

    public Map<String, BigDecimal> getCategoryTotalsByPeriod(Long userId, String period) {
        LocalDate now = LocalDate.now();  // Use LocalDate instead of LocalDateTime
        LocalDate startDate = switch (period.toLowerCase()) {
            case "daily" -> now.minusDays(1);
            case "weekly" -> now.minusWeeks(1);
            case "monthly" -> now.minusMonths(1);
            case "yearly" -> now.minusYears(1);
            default -> now.minusMonths(1);
        };

        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(userId, startDate, now);
        return transactions.stream().collect(
                Collectors.groupingBy(
                        tx -> categoryRepository.findById(tx.getCategoryId()).map(c -> c.getName()).orElse("Other"),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                )
        );
    }

    public ByteArrayOutputStream generatePdf(Long userId, String reportPeriod) throws Exception {
        // Use LocalDate instead of LocalDateTime
        LocalDate now = LocalDate.now();  // Current date (without time)
        LocalDate startDate = switch (reportPeriod.toLowerCase()) {
            case "daily" -> now.minusDays(1);
            case "weekly" -> now.minusWeeks(1);
            case "monthly" -> now.minusMonths(1);
            case "yearly" -> now.minusYears(1);
            default -> now.minusMonths(1);
        };

        // Fetch transactions using LocalDate, as it seems your repository uses LocalDate for 'date'
        List<Transaction> txs = transactionRepository.findByUserIdAndDateBetween(userId, startDate, now);
        var bills = billRepository.findByUserId(userId);
        var budgets = budgetRepository.findByUserId(userId);

        // Creating the report
        Report report = new Report();
        report.setUserId(userId);
        report.setTimePeriod(reportPeriod);
        report.setGeneratedDate(LocalDate.now());  // Set the generated date to today's date
        reportRepository.save(report);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document();
        PdfWriter.getInstance(doc, out);
        doc.open();

        // Add header
        doc.add(new Paragraph("EXPEN$IFY Report - " + reportPeriod.toUpperCase()));
        doc.add(new Paragraph("Generated: " + LocalDate.now()));
        doc.add(new Paragraph(" "));

        // Add Transactions Table
        doc.add(new Paragraph("Transactions"));
        PdfPTable tTable = new PdfPTable(4);
        tTable.addCell("Date"); tTable.addCell("Description"); tTable.addCell("Category"); tTable.addCell("Amount");
        for (var tx : txs) {
            tTable.addCell(tx.getDate().toString());  // Get the transaction date as LocalDate
            tTable.addCell(tx.getDescription());
            tTable.addCell(categoryRepository.findById(tx.getCategoryId()).map(c -> c.getName()).orElse(""));
            tTable.addCell(tx.getAmount().toPlainString());
        }
        doc.add(tTable);

        // Add Budgets Table
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Budgets"));
        PdfPTable bTable = new PdfPTable(3);
        bTable.addCell("Category"); bTable.addCell("Limit"); bTable.addCell("Period");
        for (var b : budgets) {
            bTable.addCell(categoryRepository.findById(b.getCategoryId()).map(c -> c.getName()).orElse(""));
            bTable.addCell(b.getLimitAmount().toPlainString());
            bTable.addCell(b.getPeriod());
        }
        doc.add(bTable);

        // Add Bill Payments Table
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Bill Payments"));
        PdfPTable billTable = new PdfPTable(3);
        billTable.addCell("Name"); billTable.addCell("Due Date"); billTable.addCell("Amount");
        for (var b : bills) {
            billTable.addCell(b.getName());
            billTable.addCell(b.getDueDate().toString());  // Assuming 'dueDate' is a LocalDate, you can use it directly
            billTable.addCell(b.getAmount().toPlainString());
        }
        doc.add(billTable);

        doc.close();
        return out;
    }

}