package com.shiptrack;

import com.shiptrack.database.DatabaseManager;
import com.shiptrack.services.AuthService;
import com.shiptrack.models.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    private static AuthService authService;

    @BeforeAll
    public static void setUp() throws Exception {

        java.io.File testDb = new java.io.File("test_shiptrack.db");
        if (testDb.exists()) {
            testDb.delete();
        }

        DatabaseManager.setDbUrl("jdbc:sqlite:test_shiptrack.db");

        DatabaseManager.initializeDatabase();

        try (java.sql.Connection conn = DatabaseManager.getConnection()) {
            String dispHash = org.mindrot.jbcrypt.BCrypt.hashpw("Dispatch@1234", org.mindrot.jbcrypt.BCrypt.gensalt());
            conn.createStatement().execute(
                "INSERT OR IGNORE INTO users (username, password, role, is_locked, failed_attempts) " +
                "VALUES ('testdispatcher', '" + dispHash + "', 'dispatcher', 1, 3)"
            );
        }

        authService = new AuthService();
    }


    // Test 1 — Correct credentials should return a User object
    @Test
    public void testLoginSuccess() {
        User user = authService.login("admin", "Admin@1234");
        assertNotNull(user, "Login should succeed with correct credentials");
    }

    // Test 2 — Wrong password should return null
    @Test
    public void testLoginWrongPassword() {
        User user = authService.login("admin", "wrongpassword");
        assertNull(user, "Login should fail with incorrect password");
    }

    // Test 3 — Non-existing username should return null
    @Test
    public void testLoginUserNotFound() {
        User user = authService.login("nobody", "Admin@1234");
        assertNull(user, "Login should fail for non-existing user");
    }

    // Test 4 — Locked account should not be able to login
    @Test
    public void testLoginLockedAccount() {
        User user = authService.login("testdispatcher", "Dispatch@1234");
        assertNull(user, "Locked account should not be able to login");
    }

    // ==================== Function 2: isPasswordValid() ====================

    // Test 5 — Password meeting all requirements should be valid
    @Test
    public void testPasswordValid() {
        boolean result = authService.isPasswordValid("Secure@1234");
        assertTrue(result, "Password meeting all requirements should be valid");
    }

    // Test 6 — Password that is too short should be invalid
    @Test
    public void testPasswordTooShort() {
        boolean result = authService.isPasswordValid("Ab@1");
        assertFalse(result, "Short password should be invalid");
    }

    // Test 7 — Password with no uppercase should be invalid
    @Test
    public void testPasswordNoUppercase() {
        boolean result = authService.isPasswordValid("secure@1234");
        assertFalse(result, "Password without uppercase should be invalid");
    }

    // Test 8 — Password with no special character should be invalid
    @Test
    public void testPasswordNoSpecialChar() {
        boolean result = authService.isPasswordValid("Secure1234");
        assertFalse(result, "Password without special character should be invalid");
    }

    // Test 9 — Password with no digit should be invalid
    @Test
    public void testPasswordNoDigit() {
        boolean result = authService.isPasswordValid("Secure@abc");
        assertFalse(result, "Password without digit should be invalid");
    }
}