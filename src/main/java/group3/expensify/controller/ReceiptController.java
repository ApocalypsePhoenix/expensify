package group3.expensify.controller;

import group3.expensify.model.Receipt;
import group3.expensify.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
}