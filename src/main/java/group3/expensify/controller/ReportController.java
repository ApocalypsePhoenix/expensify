package group3.expensify.controller;

import group3.expensify.model.User;
import group3.expensify.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public String showReportPage(@RequestParam(defaultValue = "monthly") String period, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/users/login";

        Map<String, BigDecimal> data = reportService.getCategoryTotalsByPeriod(user.getId(), period);
        model.addAttribute("categoriesJs", data.keySet());
        model.addAttribute("totalsJs", data.values());
        model.addAttribute("selectedPeriod", period);

        return "GenerateReportPage";
    }

    @PostMapping("/generate")
    public void generateReport(@RequestParam String reportPeriod, HttpSession session, HttpServletResponse response) throws Exception {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            response.sendRedirect("/users/login");
            return;
        }

        ByteArrayOutputStream pdf = reportService.generatePdf(user.getId(), reportPeriod);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=expensify-report.pdf");
        response.getOutputStream().write(pdf.toByteArray());
        response.getOutputStream().flush();
    }
}
