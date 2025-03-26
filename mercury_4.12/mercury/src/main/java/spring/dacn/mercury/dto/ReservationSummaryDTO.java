package spring.dacn.mercury.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.dacn.mercury.entities.DiningTable;
import spring.dacn.mercury.entities.MenuSet;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSummaryDTO {
    private ReservationDTO reservation;     // Thay vì Reservation entity, ta dùng ReservationDTO
    private UserDTO user;                   // Thay vì User entity, ta dùng UserDTO
    private List<DiningTable> tables;       // Bàn ăn liên quan, không bị ảnh hưởng bởi recursion
    private List<MenuSet> menus;            // Menu liên quan, cũng không bị ảnh hưởng
}
