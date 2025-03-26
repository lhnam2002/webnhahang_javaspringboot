package spring.dacn.mercury.repositories;

import spring.dacn.mercury.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
}
