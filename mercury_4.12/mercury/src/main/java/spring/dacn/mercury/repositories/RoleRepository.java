package spring.dacn.mercury.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.dacn.mercury.entities.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
    Role findRoleById(Long id);
}