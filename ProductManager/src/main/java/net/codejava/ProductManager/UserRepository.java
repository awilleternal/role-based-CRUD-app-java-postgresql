package net.codejava.ProductManager;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Lookup user by email, returning an Optional
    Optional<User> findByEmail(String email);
}
