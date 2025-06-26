package group3.expensify.service;

import group3.expensify.model.Bill;
import group3.expensify.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    public List<Bill> getBillsByUserId(Long userId) {
        return billRepository.findByUserId(userId);
    }

    public void saveBill(Bill bill) {
        billRepository.save(bill);  // Save the bill in the database
    }

    public Bill getBillById(Long id) {
        return billRepository.findById(id).orElseThrow(() -> new RuntimeException("Bill not found"));
    }

    public void updateBill(Bill bill) {
        billRepository.save(bill);  // Save the updated bill
    }

    public void deleteBill(Long id) {
        billRepository.deleteById(id);  // Delete the bill by ID
    }

    public void markBillAsPaid(Long id) {
        Bill bill = billRepository.findById(id).orElseThrow(() -> new RuntimeException("Bill not found"));
        bill.setStatus("Paid");  // Change status to Paid
        billRepository.save(bill);
    }
}
