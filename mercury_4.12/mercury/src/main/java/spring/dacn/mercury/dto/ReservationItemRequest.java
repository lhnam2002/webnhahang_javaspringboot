package spring.dacn.mercury.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationItemRequest {
    private Long menuSetId;
    private int quantity;
}