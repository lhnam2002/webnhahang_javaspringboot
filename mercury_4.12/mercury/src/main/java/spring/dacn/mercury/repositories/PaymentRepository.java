package spring.dacn.mercury.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.dacn.mercury.entities.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
