package spring.dacn.mercury.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.dacn.mercury.entities.DiningTable;
import spring.dacn.mercury.services.DiningTableService;
import spring.dacn.mercury.services.EmailService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {
    private final DiningTableService diningTableService;
    private final EmailService emailService;

    @GetMapping("/table")
    public ResponseEntity<?> getAllTableByTime(@RequestParam LocalDateTime time){
        List<DiningTable> diningTables = diningTableService.getAllTableByTime(time);
        return ResponseEntity.ok().body(diningTables);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(){
        String a = "<div marginwidth=\"0\" marginheight=\"0\"\n" +
                "        style=\"font-size:16px;line-height:24px!important;font-family:arial;background:#f9f9f9;padding:50px; color: #606060;\">\n" +
                "        <div style=\"max-width: 600px; margin: auto; margin-left: auto;\">\n" +
                "            <h1 style=\"text-align: center; color: #007bff;\">Đặt Bàn Thành Công!</h1>\n" +
                "\n" +
                "            <h2 style=\"border-bottom: 2px solid #ddd; padding-bottom: 5px;\">Thông tin người dùng</h2>\n" +
                "            <p><strong>Họ và tên:</strong> <span>Nguyễn Văn A</span></p>\n" +
                "            <p><strong>Email:</strong> <span>daocongdanh48@gmail.com</span></p>\n" +
                "            <p><strong>Số điện thoại:</strong> <span>0123456789</span></p>\n" +
                "\n" +
                "            <h2 style=\"border-bottom: 2px solid #ddd; padding-bottom: 5px;\">Thông tin đặt bàn</h2>\n" +
                "            <p><strong>Mã đặt bàn:</strong> <span>83</span></p>\n" +
                "            <p><strong>Thời gian đặt:</strong> <span>2024-12-06T17:19:21.731129</span></p>\n" +
                "            <p><strong>Số người:</strong> <span>2</span></p>\n" +
                "            <p><strong>Yêu cầu đặc biệt:</strong> <span>haha</span></p>\n" +
                "            <p><strong>Đặt món trước:</strong> <span>Có</span></p>\n" +
                "\n" +
                "            <h2 style=\"border-bottom: 2px solid #ddd; padding-bottom: 5px;\">Danh sách bàn đã chọn</h2>\n" +
                "            <ul>\n" +
                "                <li>Bàn số 1 (Thời gian đến: 2024-12-06T20:19)</li>\n" +
                "                <li>Bàn số 3 (Thời gian đến: 2024-12-06T20:19)</li>\n" +
                "            </ul>\n" +
                "\n" +
                "            <h2 style=\"border-bottom: 2px solid #ddd; padding-bottom: 5px;\">Danh sách món ăn đã chọn</h2>\n" +
                "            <ul>\n" +
                "                <li>set4 - Số lượng: 4 - Giá: 12000000 VND</li>\n" +
                "                <li>ttt - Số lượng: 2 - Giá: 3000000 VND</li>\n" +
                "            </ul>\n" +
                "\n" +
                "            <h2 style=\"border-bottom: 2px solid #ddd; padding-bottom: 5px;\">Tổng tiền</h2>\n" +
                "            <p><strong style=\"font-size: 1.2em;\">Tổng tiền: 15000000 VND</strong></p>\n" +
                "\n" +
                "            <p style=\"margin-top: 20px; text-align: center; font-size: 1em; color: #555;\">\n" +
                "                Xin cảm ơn quý khách đã sử dụng dịch vụ của chúng tôi. Chúng tôi rất hân hạnh được phục vụ và hy vọng\n" +
                "                quý khách sẽ có một trải nghiệm tuyệt vời.\n" +
                "            </p>\n" +
                "            <p style=\"text-align: center; font-size: 1em; color: #555;\">\n" +
                "                Nếu có bất kỳ thắc mắc hoặc yêu cầu bổ sung, xin vui lòng liên hệ với chúng tôi qua email hoặc số điện\n" +
                "                thoại đã cung cấp.\n" +
                "            </p>\n" +
                "        </div>\n" +
                "    </div>";
        emailService.sendEmail("mercuryrestaurant8386@gmail.com", "Đặt bàn thành công",a);
        return ResponseEntity.ok().body("ok");
    }
}