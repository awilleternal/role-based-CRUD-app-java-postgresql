package net.codejava.ProductManager;

import net.codejava.ProductManager.service.PasswordEncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncryptionServiceTest {

    private PasswordEncryptionService passwordEncryptionService;

    @BeforeEach
    void setUp() {
        passwordEncryptionService = new PasswordEncryptionService();
    }

    @Test
    void testHashPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Arrange
        String password = "TestPassword123";

        // Act
        String hashedPassword = passwordEncryptionService.hashPassword(password);

        // Assert
        assertNotNull(hashedPassword);
        assertFalse(hashedPassword.isEmpty());
    }

    @Test
    void testVerifyPassword_CorrectPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Arrange
        String password = "CorrectPassword123";
        String hashedPassword = passwordEncryptionService.hashPassword(password);

        // Act
        boolean isVerified = passwordEncryptionService.verifyPassword(password, hashedPassword);

        // Assert
        assertTrue(isVerified, "The password verification should return true for the correct password.");
    }

    @Test
    void testVerifyPassword_IncorrectPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Arrange
        String originalPassword = "OriginalPassword123";
        String incorrectPassword = "IncorrectPassword123";
        String hashedPassword = passwordEncryptionService.hashPassword(originalPassword);

        // Act
        boolean isVerified = passwordEncryptionService.verifyPassword(incorrectPassword, hashedPassword);

        // Assert
        assertFalse(isVerified, "The password verification should return false for an incorrect password.");
    }

    @Test
    void testVerifyPassword_EmptyPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Arrange
        String originalPassword = "SomePassword123";
        String emptyPassword = "";
        String hashedPassword = passwordEncryptionService.hashPassword(originalPassword);

        // Act
        boolean isVerified = passwordEncryptionService.verifyPassword(emptyPassword, hashedPassword);

        // Assert
        assertFalse(isVerified, "The password verification should return false for an empty password.");
    }

    @Test
    void testVerifyPassword_ModifiedHash() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Arrange
        String password = "AnotherPassword123";
        String hashedPassword = passwordEncryptionService.hashPassword(password);

        // Modify the hashed password (tamper with the data)
        String tamperedHash = hashedPassword.substring(0, hashedPassword.length() - 1) + "X";

        // Act
        boolean isVerified = passwordEncryptionService.verifyPassword(password, tamperedHash);

        // Assert
        assertFalse(isVerified, "The password verification should return false for a tampered hash.");
    }
}
