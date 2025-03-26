package spring.dacn.mercury.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter

public class ReservationRequest {
    private LocalDateTime reservationTime;
    private int numberOfPeople;
    private String specialRequest;
    private boolean preOrderFood;
}
