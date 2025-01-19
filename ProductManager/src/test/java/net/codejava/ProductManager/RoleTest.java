package net.codejava.ProductManager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testGetAndSetId() {
        // Arrange
        Role role = new Role();
        Integer expectedId = 1;

        // Act
        role.setId(expectedId);
        Integer actualId = role.getId();

        // Assert
        assertEquals(expectedId, actualId, "The ID should match the set value.");
    }

    @Test
    void testGetAndSetName() {
        // Arrange
        Role role = new Role();
        String expectedName = "ADMIN";

        // Act
        role.setName(expectedName);
        String actualName = role.getName();

        // Assert
        assertEquals(expectedName, actualName, "The name should match the set value.");
    }

    @Test
    void testDefaultConstructor() {
        // Act
        Role role = new Role();

        // Assert
        assertNull(role.getId(), "The ID should be null by default.");
        assertNull(role.getName(), "The name should be null by default.");
    }



    @Test
    void testNotEquals() {
        // Arrange
        Role role1 = new Role();
        Role role2 = new Role();

        role1.setId(1);
        role1.setName("USER");

        role2.setId(2);
        role2.setName("ADMIN");

        // Assert
        assertNotEquals(role1, role2, "Roles with different IDs or names should not be equal.");
    }
}
