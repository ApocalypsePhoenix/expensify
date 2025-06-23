package group3.expensify.service;

import group3.expensify.model.Receipt;
import group3.expensify.model.User;
import group3.expensify.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepository;

    // Get all receipts for a user
    public List<Receipt> getReceiptsByUser(Long userId) {
        User user = new User();  // Ideally, fetch user from database or security context
        user.setId(userId);
        return receiptRepository.findByUser(user);
    }

    // Create a new receipt
    public Receipt createReceipt(Receipt receipt) {
        return receiptRepository.save(receipt);
    }
}