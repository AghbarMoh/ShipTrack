package com.shiptrack;

import com.shiptrack.database.DatabaseManager;
import com.shiptrack.services.AuthService;
import com.shiptrack.models.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// FuzzTest tests login() and isPasswordValid() with random/unexpected inputs
// The goal is to make sure the application never crashes on bad input
public class FuzzTest {

    private static AuthService authService;

    @BeforeAll
    public static void setUp() throws Exception {
        // Use the same test database
        DatabaseManager.setDbUrl("jdbc:sqlite:test_shiptrack.db");
        authService = new AuthService();
    }

    // ==================== Fuzz: login() ====================

    // Fuzz Test 1 — Empty username and password
    @Test
    public void fuzzLoginEmptyInputs() {
        User user = authService.login("", "");
        assertNull(user, "Empty credentials should not login");
    }

    // Fuzz Test 2 — Very long username
    @Test
    public void fuzzLoginLongUsername() {
        String longUsername = "a".repeat(10000);
        User user = authService.login(longUsername, "Admin@1234");
        assertNull(user, "Very long username should not crash the app");
    }

    // Fuzz Test 3 — Very long password
    @Test
    public void fuzzLoginLongPassword() {
        String longPassword = "A@1".repeat(5000);
        User user = authService.login("admin", longPassword);
        assertNull(user, "Very long password should not crash the app");
    }

    // Fuzz Test 4 — SQL injection attempt in username
    @Test
    public void fuzzLoginSQLInjectionUsername() {
        User user = authService.login("' OR '1'='1", "Admin@1234");
        assertNull(user, "SQL injection in username should not work");
    }

    // Fuzz Test 5 — SQL injection attempt in password
    @Test
    public void fuzzLoginSQLInjectionPassword() {
        User user = authService.login("admin", "' OR '1'='1");
        assertNull(user, "SQL injection in password should not work");
    }

    // Fuzz Test 6 — Special characters in username
    @Test
    public void fuzzLoginSpecialCharsUsername() {
        User user = authService.login("!@#$%^&*()", "Admin@1234");
        assertNull(user, "Special characters in username should not crash the app");
    }

    // Fuzz Test 7 — Null-like string inputs
    @Test
    public void fuzzLoginNullString() {
        User user = authService.login("null", "null");
        assertNull(user, "String 'null' should not login");
    }

    // Fuzz Test 8 — Unicode characters in username
    @Test
    public void fuzzLoginUnicodeUsername() {
        User user = authService.login("用户名测试", "Admin@1234");
        assertNull(user, "Unicode characters in username should not crash the app");
    }

    // ==================== Fuzz: isPasswordValid() ====================

    // Fuzz Test 9 — Empty password
    @Test
    public void fuzzPasswordEmpty() {
        boolean result = authService.isPasswordValid("");
        assertFalse(result, "Empty password should be invalid");
    }

    // Fuzz Test 10 — Very long password
    @Test
    public void fuzzPasswordVeryLong() {
        String longPassword = "Aa@1".repeat(10000);
        // Should not crash — just return true or false
        assertDoesNotThrow(() -> authService.isPasswordValid(longPassword));
    }

    // Fuzz Test 11 — Password with only spaces
    @Test
    public void fuzzPasswordOnlySpaces() {
        boolean result = authService.isPasswordValid("        ");
        assertFalse(result, "Password with only spaces should be invalid");
    }

    // Fuzz Test 12 — Password with SQL injection
    @Test
    public void fuzzPasswordSQLInjection() {
        boolean result = authService.isPasswordValid("' OR '1'='1' --");
        // Should not crash — just validate normally
        assertDoesNotThrow(() -> authService.isPasswordValid("' OR '1'='1' --"));
    }

    // Fuzz Test 13 — Password with unicode characters
    @Test
    public void fuzzPasswordUnicode() {
        assertDoesNotThrow(() -> authService.isPasswordValid("密码@Test1234"));
    }

    // Fuzz Test 14 — Password with newline characters
    @Test
    public void fuzzPasswordNewline() {
        boolean result = authService.isPasswordValid("Secure@1234\n\t");
        assertDoesNotThrow(() -> authService.isPasswordValid("Secure@1234\n\t"));
    }
}