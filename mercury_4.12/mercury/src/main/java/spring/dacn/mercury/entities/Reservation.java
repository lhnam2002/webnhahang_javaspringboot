package spring.dacn.mercury.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import spring.dacn.mercury.constants.ReservationStatus;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference  // Tránh tuần tự hóa vòng ngược lại từ User
    private User user;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationTable> reservationTables = new ArrayList<>();  // Khai báo danh sách các bàn

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationItem> reservationItems = new ArrayList<>();  // Khai báo danh sách món ăn

    @Column(name = "reservation_time")
    private LocalDateTime reservationTime;

    @Column(name = "number_of_people")
    private int numberOfPeople;

    @Column(name = "special_request")
    private String specialRequest;

    @Column(name = "pre_order_food")
    private boolean preOrderFood;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(name = "late_fee")
    private Integer lateFee;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "total_price")
    private Long totalPrice;

    // Phương thức thêm bàn vào danh sách
    public void addReservationTable(ReservationTable reservationTable) {
        if (reservationTables == null) {
            reservationTables = new ArrayList<>();
        }
        reservationTables.add(reservationTable);
        reservationTable.setReservation(this);  // Đảm bảo liên kết hai chiều
    }

    public void addReservationItem(ReservationItem reservationItem) {
        if (this.reservationItems == null) {
            this.reservationItems = new ArrayList<>();
        }
        this.reservationItems.add(reservationItem);
        reservationItem.setReservation(this);  // Đảm bảo liên kết hai chiều
    }
}


