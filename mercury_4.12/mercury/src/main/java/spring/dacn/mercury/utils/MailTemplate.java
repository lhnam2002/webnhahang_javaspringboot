package spring.dacn.mercury.utils;

import spring.dacn.mercury.entities.Reservation;
import spring.dacn.mercury.entities.ReservationItem;
import spring.dacn.mercury.entities.ReservationTable;
import spring.dacn.mercury.entities.User;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class MailTemplate {

    private Reservation reservation;

    public MailTemplate(Reservation reservation){
        this.reservation = reservation;
    }

    public String getMail() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        DecimalFormat decimalFormat = new DecimalFormat("#,### VND");
        int totalPrice = reservation.getReservationItems().stream()
                .mapToInt(item -> item.getPrice() * item.getQuantity())
                .sum();
        User user = reservation.getUser();
        StringBuilder html = new StringBuilder();
        html.append("<div style=\"font-family: Pacifico, cursive; background-color: #f9f9f9; margin: 0; padding: 0; color: #333; max-width: 600px; margin: 0 auto; padding: 30px; background-color: #fff; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); border-radius: 10px;\">\n");
        html.append("<h1 style=\"text-align: center; color: #007bff; font-size: 28px; margin-bottom: 20px; font-family: 'Pacifico', cursive;\">Đặt Bàn Thành Công!</h1>\n");

        html.append("<h2 style=\"font-size: 20px; color: #333; border-bottom: 2px solid #007bff; padding-bottom: 10px; margin-bottom: 15px; font-weight: bold; font-family: 'Pacifico', cursive; display: inline;\">Thông tin người dùng</h2>\n");
        html.append("<p style=\"font-size: 16px; color: #555; margin-bottom: 10px;\"><strong>Họ và tên:</strong> <span>").append(user.getFullName()).append("</span></p>\n");
        html.append("<p style=\"font-size: 16px; color: #555; margin-bottom: 10px;\"><strong>Email:</strong> <span>").append(user.getEmail()).append("</span></p>\n");
        html.append("<p style=\"font-size: 16px; color: #555; margin-bottom: 10px;\"><strong>Số điện thoại:</strong> <span>").append(user.getPhone()).append("</span></p>\n");

        html.append("<h2 style=\"font-size: 20px; color: #333; border-bottom: 2px solid #007bff; padding-bottom: 10px; margin-bottom: 15px; font-weight: bold; font-family: 'Pacifico', cursive; display: inline;\">Thông tin đặt bàn</h2>\n");
        html.append("<p style=\"font-size: 16px; color: #555; margin-bottom: 10px;\"><strong>Mã đặt bàn:</strong> <span>").append(reservation.getId()).append("</span></p>\n");
        html.append("<p style=\"font-size: 16px; color: #555; margin-bottom: 10px;\"><strong>Thời gian đặt:</strong> <span>").append(reservation.getReservationTime().format(formatter)).append("</span></p>\n");
        html.append("<p style=\"font-size: 16px; color: #555; margin-bottom: 10px;\"><strong>Số người:</strong> <span>").append(reservation.getNumberOfPeople()).append("</span></p>\n");
        html.append("<p style=\"font-size: 16px; color: #555; margin-bottom: 10px;\"><strong>Yêu cầu đặc biệt:</strong> <span>").append(reservation.getSpecialRequest()).append("</span></p>\n");
        html.append("<p style=\"font-size: 16px; color: #555; margin-bottom: 10px;\"><strong>Đặt món trước:</strong> <span>").append(reservation.isPreOrderFood() ? "Có" : "Không").append("</span></p>\n");

        html.append("<h2 style=\"font-size: 20px; color: #333; border-bottom: 2px solid #007bff; padding-bottom: 10px; margin-bottom: 15px; font-weight: bold; font-family: 'Pacifico', cursive; display: inline;\">Danh sách bàn đã chọn</h2>\n");
        html.append("<ul style=\"list-style-type: none; padding-left: 0; font-size: 16px; color: #555;\">\n");
        for (ReservationTable rt : reservation.getReservationTables()) {
            html.append("<li style=\"background-color: #f9f9f9; padding: 10px; margin-bottom: 5px; border-radius: 5px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);\">Bàn số ").append(rt.getDiningTable().getTableNumber())
                    .append(" (Thời gian đến: ").append(rt.getStartTime().format(formatter)).append(")</li>\n");
        }
        html.append("</ul>\n");

        html.append("<h2 style=\"font-size: 20px; color: #333; border-bottom: 2px solid #007bff; padding-bottom: 10px; margin-bottom: 15px; font-weight: bold; font-family: 'Pacifico', cursive; display: inline;\">Danh sách món ăn đã chọn</h2>\n");
        html.append("<ul style=\"list-style-type: none; padding-left: 0; font-size: 16px; color: #555;\">\n");
        for (ReservationItem it : reservation.getReservationItems()) {
            html.append("<li style=\"background-color: #f9f9f9; padding: 10px; margin-bottom: 5px; border-radius: 5px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);\">")
                    .append(it.getMenuSet().getName()).append(" - Số lượng: ").append(it.getQuantity()).append(" - Giá: ")
                    .append(decimalFormat.format((long) it.getQuantity() * it.getPrice())).append("</li>\n");
        }
        html.append("</ul>\n");

        html.append("<h2 style=\"font-size: 20px; color: #333; border-bottom: 2px solid #007bff; padding-bottom: 10px; margin-bottom: 15px; font-weight: bold; font-family: 'Pacifico', cursive; display: inline;\">Tổng tiền</h2>\n");
        html.append("<p style=\"font-size: 1.5em; font-weight: bold; color: #e74c3c; margin-bottom: 20px;\">Tổng tiền: ").append(decimalFormat.format(totalPrice)).append("</p>\n");

        html.append("<div style=\"text-align: center; margin-top: 30px; font-size: 14px; color: #777;\">\n");
        html.append("<p style=\"margin-bottom: 10px;\">Xin cảm ơn quý khách đã sử dụng dịch vụ của chúng tôi. Chúng tôi rất hân hạnh được phục vụ và hy vọng quý khách sẽ có một trải nghiệm tuyệt vời.</p>\n");
        html.append("<p style=\"margin-bottom: 10px;\">Nếu có bất kỳ thắc mắc hoặc yêu cầu bổ sung, xin vui lòng liên hệ với chúng tôi qua email hoặc số điện thoại đã cung cấp.</p>\n");
        html.append("<p style=\"margin-bottom: 10px;\">Gửi email cho chúng tôi | Gọi điện thoại</p>\n");
        html.append("</div>\n");

        html.append("</div>\n");
        return html.toString();
    }

}
