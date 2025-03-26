package spring.dacn.mercury.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.dacn.mercury.entities.Payment;
import spring.dacn.mercury.entities.Reservation;
import spring.dacn.mercury.entities.User;

import java.util.List;
import java.util.Optional;

//@Repository
//public interface ReservationRepository extends JpaRepository<Reservation, Long> {
//    Optional<Reservation> findById(Long id);
//
//    Page<Reservation> findAllByUser(User user, Pageable pageable);
//}
@Repository
public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long>, JpaRepository<Reservation, Long> {
    Optional<Reservation> findById(Long id);

    Page<Reservation> findAllByUser(User user, Pageable pageable);

    default List<Reservation> findAllReservationId(Integer pageNo, Integer pageSize, String sortBy) {
        return findAll(PageRequest.of(pageNo, pageSize, Sort.by(sortBy))).getContent();
    }

    @Query("""
        SELECT m FROM Reservation m
        WHERE CAST(m.id AS string) LIKE CONCAT('%', :keyword, '%')
""")
    List<Reservation> searchReservations(@Param("keyword") String keyword);


}