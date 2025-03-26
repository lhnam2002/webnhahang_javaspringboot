package spring.dacn.mercury.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.dacn.mercury.entities.MenuSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuSetRepository extends PagingAndSortingRepository<MenuSet, Long>, JpaRepository<MenuSet, Long> {

    default List<MenuSet> findAllMenuSets(Integer pageNo, Integer pageSize, String sortBy) {
        return findAll(PageRequest.of(pageNo, pageSize, Sort.by(sortBy))).getContent();
    }

    @Query("""
            SELECT m FROM MenuSet m
            JOIN ReservationItem ri ON m.id = ri.menuSet.id
            WHERE ri.reservation.id = :reservationId
            """)
    List<MenuSet> findAllByReservationId(@Param("reservationId") Long reservationId);

    @Query("""
            SELECT m FROM MenuSet m
            WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR CAST(m.price AS string) LIKE %:keyword%
            """)
    List<MenuSet> searchMenu(@Param("keyword") String keyword);

    @Query("SELECT m.name FROM MenuSet m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<String> findTitlesByKeyword(@Param("keyword") String keyword);
}
