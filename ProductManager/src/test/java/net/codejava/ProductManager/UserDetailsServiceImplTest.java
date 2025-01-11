package net.codejava.ProductManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_UserExists() {
        // Arrange
        String email = "test@example.com";
        User mockUser = new User();
        mockUser.setID(1);
        mockUser.setUsername("testuser");
        mockUser.setEmail(email);
        mockUser.setPassword("password");
        mockUser.setEnabled(true);
        mockUser.setRole("ROLE_USER");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        // Act
        User result = userDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(email, result.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userDetailsService.loadUserByUsername(email));
        assertEquals("Could not find user with email: " + email, exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
    }
}
