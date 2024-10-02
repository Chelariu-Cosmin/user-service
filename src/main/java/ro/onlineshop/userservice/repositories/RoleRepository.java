package ro.onlineshop.userservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.onlineshop.api.beans.ERole;
import ro.onlineshop.userservice.entities.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(ERole name);
}
