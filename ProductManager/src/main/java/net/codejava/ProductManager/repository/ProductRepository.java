package net.codejava.ProductManager.repository;

import net.codejava.ProductManager.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByBrandContainingIgnoreCase(String brand);
}
