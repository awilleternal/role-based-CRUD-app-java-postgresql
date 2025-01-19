package net.codejava.ProductManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import net.codejava.ProductManager.entity.Product;
import net.codejava.ProductManager.repository.ProductRepository;
import net.codejava.ProductManager.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListAll() {
        // Arrange
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setBrand("Brand A");
        product1.setMadein("USA");
        product1.setPrice(100.0f);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setBrand("Brand B");
        product2.setMadein("Canada");
        product2.setPrice(200.0f);

        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // Act
        List<Product> productList = productService.listAll();

        // Assert
        assertNotNull(productList);
        assertEquals(2, productList.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testSave() {
        // Arrange
        Product product = new Product();
        product.setName("New Product");
        product.setBrand("New Brand");
        product.setMadein("India");
        product.setPrice(150.0f);

        // Act
        productService.save(product);

        // Assert
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testGet() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setBrand("Test Brand");
        product.setMadein("Germany");
        product.setPrice(250.0f);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        Product result = productService.get(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals("Test Brand", result.getBrand());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testDelete() {
        // Arrange
        Long productId = 1L;

        // Act
        productService.delete(productId);

        // Assert
        verify(productRepository, times(1)).deleteById(productId);
    }
}
