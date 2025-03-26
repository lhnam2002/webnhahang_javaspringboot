package spring.dacn.mercury.controllers;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import spring.dacn.mercury.services.BillPaymentService;
import spring.dacn.mercury.services.BillService;
import spring.dacn.mercury.services.PaymentService;

import spring.dacn.mercury.entities.Bill;
import spring.dacn.mercury.entities.Payment;

import java.util.List;

@Controller
public class BillPaymentController {

    private final BillService billService;
    private final PaymentService paymentService;
    private final BillPaymentService billPaymentService;


    @Autowired
    public BillPaymentController(BillService billService, PaymentService paymentService, BillPaymentService billPaymentService) {
        this.billService = billService;
        this.paymentService = paymentService;
        this.billPaymentService = billPaymentService;
    }

    // Hiển thị danh sách Bill
    @GetMapping("/admin/bills")
    public String showBillList(Model model) {
        List<Bill> bills = billService.getAllBills();
        model.addAttribute("bills", bills);  // Thêm danh sách Bill vào model
        return "admin/bill-list";  // Tên view (template) để hiển thị danh sách Bill
    }

    // Hiển thị danh sách Payment
    @GetMapping("/admin/payments")
    public String showPaymentList(Model model) {
        List<Payment> payments = paymentService.getAllPayments();
        model.addAttribute("payments", payments);  // Thêm danh sách Payment vào model
        return "admin/payment-list";  // Tên view (template) để hiển thị danh sách Payment
    }

    @GetMapping("/payment/search")
    public String searchHoaDon(
            @NotNull Model model,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {
        model.addAttribute("payments", billPaymentService.searchHoaDons(keyword));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",
                billPaymentService.getAllHoaDons(pageNo , pageSize, sortBy).size() / pageSize);
        return "admin/payment-list";
    }

}
