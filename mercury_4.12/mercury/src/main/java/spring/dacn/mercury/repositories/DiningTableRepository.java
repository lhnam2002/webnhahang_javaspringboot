package spring.dacn.mercury.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.dacn.mercury.entities.DiningTable;

import java.util.List;

public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {
}
