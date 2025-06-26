package group3.expensify.controller;

import group3.expensify.model.Bill;
import group3.expensify.model.User;
import group3.expensify.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/bills")
public class BillController {

    @Autowired
    private BillService billService;

    // This handles the GET request to show the Add Bill form
    @GetMapping("/add")
    public String showAddBillForm() {
        return "AddBillReminderPage"; // The page for adding a new bill
    }

    // This handles the POST request to submit the new bill
    @PostMapping("/add")
    public String addBill(@RequestParam Map<String, String> form, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/users/login";

        Bill bill = new Bill();
        bill.setName(form.get("bill-name"));
        bill.setAmount(new BigDecimal(form.get("amount")));
        bill.setDueDate(LocalDate.parse(form.get("due-date")));
        bill.setRecurrence(form.get("recurrence"));
        bill.setCategory(form.get("category"));
        bill.setStatus("Upcoming");  // Default status is "Upcoming"
        bill.setUser(user);

        // Get reminderTime from form and set it
        String reminderTime = form.get("reminder-time");
        if (reminderTime != null && !reminderTime.isEmpty()) {
            bill.setReminderTime(LocalDateTime.parse(reminderTime)); // Convert string to LocalDateTime
        }

        billService.saveBill(bill);
        return "redirect:/bills";
    }

    // Get all bills
    @GetMapping
    public String showBillList(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/users/login";

        model.addAttribute("bills", billService.getBillsByUserId(user.getId()));
        return "BillReminder";
    }

    // Edit bill
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Bill bill = billService.getBillById(id);
        model.addAttribute("bill", bill);
        return "EditBillReminderPage";  // Edit page form
    }

    @PostMapping("/edit/{id}")
    public String editBill(@PathVariable Long id, @RequestParam Map<String, String> form) {
        Bill bill = billService.getBillById(id);
        bill.setName(form.get("bill-name"));
        bill.setAmount(new BigDecimal(form.get("amount")));
        bill.setDueDate(LocalDate.parse(form.get("due-date")));
        bill.setRecurrence(form.get("recurrence"));
        bill.setCategory(form.get("category"));

        // Set reminderTime from form data
        String reminderTime = form.get("reminder-time");
        if (reminderTime != null && !reminderTime.isEmpty()) {
            bill.setReminderTime(LocalDateTime.parse(reminderTime)); // Convert the string to LocalDateTime
        }

        billService.updateBill(bill);
        return "redirect:/bills";
    }

    // Mark bill as paid
    @GetMapping("/mark-paid/{id}")
    public String markBillAsPaid(@PathVariable Long id) {
        billService.markBillAsPaid(id);  // Mark the bill as paid
        return "redirect:/bills";
    }

    // Delete bill
    @PostMapping("/delete/{id}")
    public String deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
        return "redirect:/bills";
    }
}
