package spring.dacn.mercury.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Long id;
    private LocalDateTime reservationTime;
    private int numberOfPeople;
    private String specialRequest;
    private String status;
    private UserDTO user;  // Thay vì trả về trực tiếp User entity, ta trả về UserDTO
}
