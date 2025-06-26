package group3.expensify.service;

import group3.expensify.model.Transaction;
import group3.expensify.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public void updateTransaction(Transaction transaction) {
        transactionRepository.save(transaction);  // Same as save, but this will update if the entity exists
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);  // Delete the transaction by ID
    }
}
