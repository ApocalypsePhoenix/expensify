package group3.expensify.controller;

import group3.expensify.model.Receipt;
import group3.expensify.model.User;
import group3.expensify.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
@RequestMapping("/receipts")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    // Get all receipts for a user
    @GetMapping("/user/{userId}")
    public List<Receipt> getReceiptsByUser(@PathVariable Long userId) {
        return receiptService.getReceiptsByUser(userId);
    }

    // Create a new receipt
    @PostMapping
    public Receipt createReceipt(@RequestBody Receipt receipt) {
        return receiptService.createReceipt(receipt);
    }

    // Process receipt OCR and create transaction
    @PostMapping("/ocr")
    public String processReceiptOCR(@RequestParam("file") MultipartFile file,
                                    HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            System.out.println("User session is null. Redirecting to login.");
            return "redirect:/users/login";
        }

        try {
            receiptService.processReceiptWithOCR(file, user.getId());
        } catch (Exception e) {
            e.printStackTrace();
            // Optionally store error in session to display on front-end
            session.setAttribute("ocrError", e.getMessage());
        }

        return "redirect:/transactions";
    }
}
