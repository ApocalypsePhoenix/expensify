package group3.expensify.service;

import group3.expensify.model.Receipt;
import group3.expensify.model.Transaction;
import group3.expensify.model.User;
import group3.expensify.repository.ReceiptRepository;
import group3.expensify.repository.TransactionRepository;
import group3.expensify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import net.sourceforge.tess4j.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    // Method to get all receipts for a specific user
    public List<Receipt> getReceiptsByUser(Long userId) {
        return receiptRepository.findByUserId(userId);  // Get receipts by userId
    }

    // Method to create a new receipt
    public Receipt createReceipt(Receipt receipt) {
        return receiptRepository.save(receipt);  // Save the receipt in the database
    }

    // Method to process receipt OCR and create transaction
    public void processReceiptWithOCR(MultipartFile file, Long userId) {
        try {
            // Step 1: Save the file temporarily
            Path tempPath = Files.createTempFile("receipt_", ".png");
            file.transferTo(tempPath.toFile());

            // Step 2: Perform OCR using Tesseract
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath("C:\\Users\\ISAC\\AppData\\Local\\Programs\\Tesseract-OCR\\tessdata"); // Ensure correct path to tessdata
            String extractedText = tesseract.doOCR(tempPath.toFile());

            // Step 3: Extract amount from OCR text using regex
            BigDecimal amount = extractAmountFromText(extractedText);

            // Step 4: Save the transaction extracted from OCR
            Transaction transaction = new Transaction();
            transaction.setUserId(userId);
            transaction.setDescription("OCR: " + extractedText.substring(0, Math.min(50, extractedText.length())));  // Shortened description
            transaction.setAmount(amount);  // Set the extracted amount
            transaction.setTransactionType("Expense");
            transaction.setDate(java.time.LocalDate.now());
            transaction.setCategoryId(6L);  // Default category ID for receipts
            transaction.setCurrencyId(1L);  // Default currency ID
            transactionRepository.save(transaction);

            // Step 5: Save the receipt record
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            Receipt receipt = new Receipt();
            receipt.setUser(user);  // Set the user object
            receipt.setImagePath(tempPath.toString());  // Save image path
            receipt.setExtractedData(extractedText);  // Store OCR extracted data
            receiptRepository.save(receipt);

            // Step 6: Optionally, delete the temporary file after processing
            Files.deleteIfExists(tempPath);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to process OCR: " + e.getMessage());
        }
    }

    // Helper method to extract amount from OCR text using regex
    private BigDecimal extractAmountFromText(String text) {
        // Regex to match a dollar amount (e.g., $11.50, $100)
        String regex = "\\$\\s?\\d+(?:,\\d{1,3})*(?:\\.\\d{2})?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String amountStr = matcher.group(0).replaceAll("[^\\d.]", ""); // Remove $ and commas
            return new BigDecimal(amountStr);
        }
        return BigDecimal.ZERO;  // Default to 0 if no amount is found
    }

}
