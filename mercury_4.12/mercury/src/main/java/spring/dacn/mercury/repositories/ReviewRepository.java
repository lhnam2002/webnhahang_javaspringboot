package spring.dacn.mercury.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.dacn.mercury.entities.MenuSet;
import spring.dacn.mercury.entities.Review;
import spring.dacn.mercury.entities.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.menuSet.id = :menuSetId")
    Double findAverageRatingByMenuSetId(@Param("menuSetId") Long menuSetId);

    int countAllByMenuSet(MenuSet menuSet);
}