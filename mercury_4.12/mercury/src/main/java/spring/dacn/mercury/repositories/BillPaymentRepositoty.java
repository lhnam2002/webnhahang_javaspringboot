package spring.dacn.mercury.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.dacn.mercury.entities.MenuSet;
import spring.dacn.mercury.entities.Payment;

import java.util.List;

import static org.antlr.v4.runtime.tree.xpath.XPath.findAll;

@Repository
public interface BillPaymentRepositoty extends PagingAndSortingRepository<Payment, Long>, JpaRepository<Payment, Long> {

    default List<Payment> findAllHoaHons(Integer pageNo, Integer pageSize, String sortBy) {
        return findAll(PageRequest.of(pageNo, pageSize, Sort.by(sortBy))).getContent();
    }

    @Query("""
    SELECT m FROM Payment m
    WHERE LOWER(m.orderId) LIKE LOWER(CONCAT('%', :keyword, '%')) 
""")
    List<Payment> searchHoaDons(@Param("keyword") String keyword);


}