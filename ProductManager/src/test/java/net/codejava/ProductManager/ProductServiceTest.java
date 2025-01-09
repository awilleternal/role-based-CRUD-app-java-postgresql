package net.codejava.ProductManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.NoSuchElementException;

class ProductServiceTest {

    @Mock
    private ProductRepository repo;

    @InjectMocks
    private ProductService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListAll() {
        // Arrange
        Product product1 = new Product(1L, "Product1", 100.0);
        Product product2 = new Product(2L, "Product2", 200.0);
        List<Product> products = Arrays.asList(product1, product2);
        when(repo.findAll()).thenReturn(products);

        // Act
        List<Product> result = service.listAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repo, times(1)).findAll();
    }

    @Test
    void testSave() {
        // Arrange
        Product product = new Product(1L, "Product1", 100.0);

        // Act
        service.save(product);

        // Assert
        verify(repo, times(1)).save(product);
    }

    @Test
    void testGet() {
        // Arrange
        Product product = new Product(1L, "Product1", 100.0);
        when(repo.findById(1L)).thenReturn(Optional.of(product));

        // Act
        Product result = service.get(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Product1", result.getName());
        verify(repo, times(1)).findById(1L);
    }

    @Test
    void testGet_NotFound() {
        // Arrange
        when(repo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> service.get(1L));
        verify(repo, times(1)).findById(1L);
    }

    @Test
    void testDelete() {
        // Act
        service.delete(1L);

        // Assert
        verify(repo, times(1)).deleteById(1L);
    }
}
