package org.OwlsGame.backend.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        // Initialize User object before test
        user = new User();
        user.setId(1L);
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("Secure123!");
        user.setLocked(false);
    }

    //-------------------------
    // basic entity test
    //-------------------------
    @Test
    void testGettersAndSetters() {
        assertEquals(1L, user.getId());
        assertEquals("John", user.getFirstname());
        assertEquals("Doe", user.getLastname());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("Secure123!", user.getPassword());
        assertFalse(user.isLocked());
    }

    @Test
    void testLockUser() {
        // Given: Initial is not locked
        assertFalse(user.isLocked());

        // When: execute lock
        user.setLocked(true);

        // Then: verify the state changes
        assertTrue(user.isLocked());
    }

    //-------------------------
    // business logical test
    //-------------------------
    @Test
    void testFullNameConcatenation() {
        // assume User has added getFullName()
        // user.setFirstname("John");
        // user.setLastname("Doe");
        // assertEquals("John Doe", user.getFullName());
    }

    @Test
    void testEmailFormatValidation() {
        // assume User has added isValidEmail()
        // assertTrue(user.isValidEmail());
        // user.setEmail("invalid-email");
        // assertFalse(user.isValidEmail());
    }
}