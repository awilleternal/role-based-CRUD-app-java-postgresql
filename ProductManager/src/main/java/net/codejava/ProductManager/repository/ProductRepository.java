package net.codejava.ProductManager.repository;

import net.codejava.ProductManager.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingOrBrandContainingOrMadeinContaining(String name, String brand, String madein);
}
