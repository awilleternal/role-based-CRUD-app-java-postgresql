package net.codejava.ProductManager;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRolesRepository extends JpaRepository<UserRoles,Integer> {
    public UserRoles findByUserId(Integer userId);
}
