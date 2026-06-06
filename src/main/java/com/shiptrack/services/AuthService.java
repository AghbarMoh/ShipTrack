package com.shiptrack.services;

import com.shiptrack.database.DatabaseManager;
import com.shiptrack.models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// AuthService handles all authentication-related operations
// including login, registration, and password validation
public class AuthService {

    // Logs in a user by checking username and password
    // Returns the User object if successful, null if failed
    public User login(String username, String password) {
    try (Connection conn = DatabaseManager.getConnection()) {

        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    boolean isLocked = rs.getInt("is_locked") == 1;
                    int failedAttempts = rs.getInt("failed_attempts");
                    int maxAttempts = getMaxLoginAttempts();

                    if (isLocked) {
                        System.out.println("Account is locked. Please contact the admin.");
                        return null;
                    }

                    String storedHash = rs.getString("password");

                    if (BCrypt.checkpw(password, storedHash)) {

                        resetFailedAttempts(username);

                        return new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("role"),
                                isLocked,
                                failedAttempts
                        );

                    } else {

                        failedAttempts++;
                        updateFailedAttempts(username, failedAttempts);

                        if (failedAttempts >= maxAttempts) {
                            lockAccount(username);
                            System.out.println("Too many failed attempts. Account is now locked.");
                        } else {
                            System.out.println("Incorrect password. Attempts: "
                                    + failedAttempts + "/" + maxAttempts);
                        }

                        return null;
                    }

                } else {

                    System.out.println("Username not found.");
                    return null;

                }
            }
        }

    } catch (SQLException e) {

        System.out.println("Login error: " + e.getMessage());
        return null;

    }
}

    // Registers a new customer in the system
    public boolean registerCustomer(String username, String password,
                                    String fullName, String idNumber, String contactNumber) {
        // Validate password against policy before saving
        if (!isPasswordValid(password)) {
            return false;
        }

        try (Connection conn = DatabaseManager.getConnection()) {

            // Hash the password using BCrypt before storing
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Insert into users table
            String userSql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'customer')";
            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                userStmt.setString(1, username);
                userStmt.setString(2, hashedPassword);
                userStmt.executeUpdate();

                // Get the new user's ID
                try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                    int userId = generatedKeys.getInt(1);

                    // Insert into customers table
                    String custSql = "INSERT INTO customers (user_id, full_name, id_number, contact_number) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement custStmt = conn.prepareStatement(custSql)) {
                        custStmt.setInt(1, userId);
                        custStmt.setString(2, fullName);
                        custStmt.setString(3, idNumber);
                        custStmt.setString(4, contactNumber);
                        custStmt.executeUpdate();
                    }
                }
            }

            System.out.println("Customer registered successfully.");
            return true;

        } catch (SQLException e) {
            System.out.println("Registration error: " + e.getMessage());
            return false;
        }
    }

    // Checks if a password meets the policy requirements
    public boolean isPasswordValid(String password) {
        try (Connection conn = DatabaseManager.getConnection()) {

            String sql = "SELECT * FROM password_policy WHERE id = 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int minLength    = rs.getInt("min_length");
                int minUppercase = rs.getInt("min_uppercase");
                int minLowercase = rs.getInt("min_lowercase");
                int minDigits    = rs.getInt("min_digits");
                int minSpecial   = rs.getInt("min_special");

                // Count each character type in the password
                int uppercase = 0;
                int lowercase = 0;
                int digits = 0;
                int special = 0;
                for (char c : password.toCharArray()) {
                    if (Character.isUpperCase(c)) { uppercase++; }
                    else if (Character.isLowerCase(c)) { lowercase++; }
                    else if (Character.isDigit(c)) { digits++; }
                    else { special++; }
                }

                // Check all conditions
                if (password.length() < minLength) {
                    System.out.println("Password too short. Minimum length: " + minLength);
                    return false;
                }
                if (uppercase < minUppercase) {
                    System.out.println("Password needs at least " + minUppercase + " uppercase letter(s).");
                    return false;
                }
                if (lowercase < minLowercase) {
                    System.out.println("Password needs at least " + minLowercase + " lowercase letter(s).");
                    return false;
                }
                if (digits < minDigits) {
                    System.out.println("Password needs at least " + minDigits + " digit(s).");
                    return false;
                }
                if (special < minSpecial) {
                    System.out.println("Password needs at least " + minSpecial + " special character(s).");
                    return false;
                }
            }

            } // end try-with-resources for stmt and rs
            return true;

        } catch (SQLException e) {
            System.out.println("Policy check error: " + e.getMessage());
            return false;
        }
    }

    // Gets the maximum number of login attempts from the policy
    public int getMaxLoginAttempts() {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT max_login_attempts FROM password_policy WHERE id = 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("max_login_attempts");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting max attempts: " + e.getMessage());
        }
        return 3; // default value
    }

    // Resets failed attempts back to 0 after successful login
    private void resetFailedAttempts(String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "UPDATE users SET failed_attempts = 0 WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error resetting attempts: " + e.getMessage());
        }
    }

    // Updates the failed attempts count for a user
    private void updateFailedAttempts(String username, int attempts) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "UPDATE users SET failed_attempts = ? WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, attempts);
                stmt.setString(2, username);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error updating attempts: " + e.getMessage());
        }
    }

    // Locks a user account
    public void lockAccount(String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "UPDATE users SET is_locked = 1 WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error locking account: " + e.getMessage());
        }
    }

    // Unlocks a user account
    public void unlockAccount(String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "UPDATE users SET is_locked = 0, failed_attempts = 0 WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.executeUpdate();
            }
            System.out.println("Account unlocked successfully.");
        } catch (SQLException e) {
            System.out.println("Error unlocking account: " + e.getMessage());
        }
    }
}