package group3.expensify.service;

import group3.expensify.model.Bill;
import group3.expensify.model.User;
import group3.expensify.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    // Get all bills for a user
    public List<Bill> getBillsByUser(Long userId) {
        User user = new User();  // Ideally, fetch user from database or security context
        user.setId(userId);
        return billRepository.findByUser(user);
    }

    // Create a new bill
    public Bill createBill(Bill bill) {
        return billRepository.save(bill);
    }
}
