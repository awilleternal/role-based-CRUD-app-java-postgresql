package net.codejava.ProductManager;


import net.codejava.ProductManager.entity.User;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testUserGettersAndSetters() {
        // Create a User instance
        User user = new User();

        // Test setting and getting ID
        user.setID(1);
        assertEquals(1, user.getID());

        // Test setting and getting username
        user.setUsername("johndoe");
        assertEquals("johndoe", user.getUsername());

        // Test setting and getting password
        user.setPassword("password123");
        assertEquals("password123", user.getPassword());

        // Test setting and getting fullName
        user.setFullName("John Doe");
        assertEquals("John Doe", user.getFullName());

        // Test setting and getting email
        user.setEmail("johndoe@example.com");
        assertEquals("johndoe@example.com", user.getEmail());

        // Test setting and getting enabled
        user.setEnabled(true);
        assertTrue(user.isEnabled());

        // Test setting and getting roles


        // Test setting and getting Role (string field)
        user.setRole("Admin");
        assertEquals("Admin", user.getRole());
    }
}
