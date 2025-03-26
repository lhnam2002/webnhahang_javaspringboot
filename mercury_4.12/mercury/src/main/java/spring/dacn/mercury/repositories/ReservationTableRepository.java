package spring.dacn.mercury.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.dacn.mercury.entities.DiningTable;
import spring.dacn.mercury.entities.ReservationTable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationTableRepository extends JpaRepository<ReservationTable, Long> {
    List<ReservationTable> findByReservationId(Long reservationId); // Khai báo phương thức này

    @Query("SELECT DISTINCT rt.diningTable FROM ReservationTable rt WHERE " +
            "(rt.startTime < :endTime AND rt.endTime > :startTime)")
    List<DiningTable> findConflictingTables(@Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);
}
