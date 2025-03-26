package spring.dacn.mercury.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "bill")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", referencedColumnName = "id")
    @ToString.Exclude
    private Reservation reservation;

    @Column(name = "subtotal", precision = 10, scale = 2)
    private Integer subtotal;

    @Column(name = "discount", precision = 10, scale = 2)
    private Integer discount;

    @Column(name = "tax", precision = 10, scale = 2)
    private Integer tax;

    @Column(name = "total", precision = 10, scale = 2)
    private Integer total;

    @OneToOne(mappedBy = "bill", cascade = CascadeType.ALL)
    private Payment payment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return Objects.equals(id, bill.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}