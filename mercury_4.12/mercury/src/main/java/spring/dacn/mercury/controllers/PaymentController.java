package spring.dacn.mercury.controllers;


import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import spring.dacn.mercury.entities.Payment;
import spring.dacn.mercury.other.MoMoSecurity;
import spring.dacn.mercury.other.PaymentRequest;
import spring.dacn.mercury.services.BillService;
import spring.dacn.mercury.services.PaymentService;
import spring.dacn.mercury.entities.*;
import org.springframework.web.bind.annotation.*;
import spring.dacn.mercury.services.ReservationService;
import spring.dacn.mercury.services.UserService;


import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class PaymentController {


    @GetMapping("/api/v1/payment/{email}/{reservationId}/{tableId}/{menuId}")
    public Map<String, String> payment(
            @PathVariable String email,  // Chỉnh sửa email từ Long sang String
            @PathVariable Long reservationId,
            @PathVariable Long tableId,
            @PathVariable Long menuId,
            @RequestParam int amount,  // amount nhận từ frontend
            Authentication authentication,
            HttpSession session) throws Exception {

        // Lấy thông tin người dùng
        String username = getCurrentUsername(authentication);
        if (username == null) {
            throw new Exception("Không thể lấy thông tin tài khoản người dùng hiện hành");
        }

        // In giá trị amount nhận được từ frontend để kiểm tra
        System.out.println("Amount received from frontend: " + amount);

        // Lưu thông tin vào session
        session.setAttribute("reservationId", reservationId);
        session.setAttribute("tableId", tableId);
        session.setAttribute("menuId", menuId);
        session.setAttribute("email", email);

        // Các thông tin cần thiết cho việc thanh toán MoMo
        String endpoint = "https://test-payment.momo.vn/gw_payment/transactionProcessor";
        String partnerCode = "MOMOOJOI20210710";
        String accessKey = "iPXneGmrJH0G8FOP";
        String secretKey = "sFcbSGRSJjwGxwhhcEktCHWYUuTuPNDB";
        String orderInfo = "Thanh toán online";
        String returnUrl = "http://localhost:8081/api/v1/payment/callback";
        String notifyUrl = "http://localhost:8081/api/v1/payment/notify";
        String orderId = String.valueOf(new Date().getTime());
        if (orderId.length() > 10) {
            orderId = orderId.substring(orderId.length() - 10); // Giới hạn độ dài orderId
        }
        String requestId = String.valueOf(new Date().getTime());
        String extraData = "";

        try {
            // Tạo chuỗi rawHash để tính toán chữ ký
            String amountStr = String.valueOf(amount);  // Chuyển sang String sau khi ép kiểu về int

            String rawHash = "partnerCode=" + partnerCode +
                    "&accessKey=" + accessKey +
                    "&requestId=" + requestId +
                    "&amount=" + amountStr +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&returnUrl=" + returnUrl +
                    "&notifyUrl=" + notifyUrl +
                    "&extraData=" + extraData;

            // Tính chữ ký
            MoMoSecurity crypto = new MoMoSecurity();
            String signature = crypto.signSHA256(rawHash, secretKey);

            // Tạo đối tượng JSON gửi lên MoMo API
            JSONObject message = new JSONObject();
            message.put("partnerCode", partnerCode);
            message.put("accessKey", accessKey);
            message.put("requestId", requestId);
            message.put("amount", amountStr);
            message.put("orderId", orderId);
            message.put("orderInfo", orderInfo);
            message.put("returnUrl", returnUrl);
            message.put("notifyUrl", notifyUrl);
            message.put("extraData", extraData);
            message.put("requestType", "captureMoMoWallet");
            message.put("signature", signature);

            // Gửi yêu cầu thanh toán đến MoMo
            String response = PaymentRequest.sendPaymentRequest(endpoint, message.toString());
            JSONObject jsonResponse = new JSONObject(response);

            Map<String, String> result = new HashMap<>();
            if (jsonResponse.has("payUrl")) {
                result.put("payUrl", jsonResponse.getString("payUrl"));
            } else {
                result.put("message", "Không nhận được payUrl từ MoMo. Phản hồi: " + response);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error processing payment", e);
        }
    }
    // Callback từ MoMo
    @GetMapping("/api/v1/payment/callback")
    public ModelAndView handlePaymentCallback(@RequestParam Map<String, String> queryParams, Model model, Authentication authentication, HttpSession session) throws Exception {
        // Kiểm tra các tham số trả về từ MoMo
        String amount = queryParams.get("amount");
        String orderId = queryParams.get("orderId");

        String username = getCurrentUsername(authentication);
        Long reservationId = (Long) session.getAttribute("reservationId");

        ModelAndView modelAndView = new ModelAndView();
        if (queryParams.containsKey("signature") && queryParams.get("signature").equals("Bad request")) {
            modelAndView.setViewName("reservation/ThanhToanThatBai.html");
        } else {
            model.addAttribute("orderId", orderId);
            model.addAttribute("amount", amount);
            model.addAttribute("paymentMethod", "MoMo");
            model.addAttribute("paymentDateTime", new Date());
            savePaymentToHoaDon(Integer.parseInt(amount), orderId, username, reservationId);
            modelAndView.setViewName("reservation/ThanhToanThanhCong.html");
        }
        return modelAndView;
    }

    private String getCurrentUsername(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else if (principal instanceof OAuth2User) {
                return ((OAuth2User) principal).getAttribute("email");
            }
        }
        return null;

    }
//===
    @Autowired
    private UserService userService;
    @Autowired
    private BillService billService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private PaymentService paymentService;

    private void savePaymentToHoaDon(int amount, String orderId, String username, Long reservationId) throws Exception {
        // Lấy thông tin người dùng từ UserService
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new Exception("Không tìm thấy tài khoản với tên: " + username));

        Reservation reservation = reservationService.getReservationById(reservationId);

        // Tạo Bill (Hóa đơn)
        Bill bill = Bill.builder()
                .reservation(reservation) // Thay thế bằng thông tin `Reservation` phù hợp nếu cần
                .subtotal(amount) // Tổng tiền chưa tính giảm giá
                .discount(0) // Giả định không có giảm giá
                .tax(0) // Giả định không có thuế
                .total(amount) // Tổng tiền cuối cùng
                .build();

        // Lưu Bill (Hóa đơn) vào cơ sở dữ liệu
        Bill savedBill = billService.createBill(bill);

        // Tạo Payment (Thanh toán)
        Payment payment = Payment.builder()
                .bill(savedBill)
                .amount(new BigDecimal(amount))
                .paymentMethod("MoMo") // Hoặc phương thức thanh toán khác như bạn muốn
                .paymentStatus("SUCCESS") // Trạng thái thanh toán thành công
                .orderId(orderId) // Gán orderId từ tham số
                .paymentDateTime(new Date()) // Gán thời gian thanh toán hiện tại
                .build();

        // Lưu Payment (Thanh toán) vào cơ sở dữ liệu
        paymentService.createPayment(payment);

        // Hiển thị thông tin
        System.out.println("Bill saved: " + savedBill);
        System.out.println("Payment saved: " + payment);
    }

}

