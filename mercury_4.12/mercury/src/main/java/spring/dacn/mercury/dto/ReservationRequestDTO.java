package spring.dacn.mercury.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDTO {
    private String userId;                  // ID người dùng

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm") // Định dạng ngày tháng năm và giờ phút
    private LocalDateTime reservationTime;  // Thời gian đặt bàn

    private int numberOfPeople;             // Số lượng người
    private String specialRequest;          // Lời nhắn
    private List<Long> tableIds;            // Danh sách ID bàn
    private List<Long> menuIds;             // Danh sách ID menu
}
