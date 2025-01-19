package net.codejava.ProductManager.repository;

import net.codejava.ProductManager.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {

}
