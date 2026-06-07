package com.shiptrack.services;

import com.shiptrack.database.DatabaseManager;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminService {

    private AuthService authService = new AuthService();

    public boolean registerDispatcher(String username, String password,
                                      String fullName, String idNumber, String contactNumber) {
        if (!authService.isPasswordValid(password)) {
            return false;
        }

        try (Connection conn = DatabaseManager.getConnection()) {

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            String userSql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'dispatcher')";
            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                userStmt.setString(1, username);
                userStmt.setString(2, hashedPassword);
                userStmt.executeUpdate();

                try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                    int userId = generatedKeys.getInt(1);

                    String dispSql = "INSERT INTO dispatchers (user_id, full_name, id_number, contact_number) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement dispStmt = conn.prepareStatement(dispSql)) {
                        dispStmt.setInt(1, userId);
                        dispStmt.setString(2, fullName);
                        dispStmt.setString(3, idNumber);
                        dispStmt.setString(4, contactNumber);
                        dispStmt.executeUpdate();
                    }
                }
            }

            System.out.println("Dispatcher registered successfully.");
            return true;

        } catch (SQLException e) {
            System.out.println("Error registering dispatcher: " + e.getMessage());
            return false;
        }
    }

    public boolean registerDeliveryPersonnel(String username, String password,
                                             String fullName, String idNumber, String contactNumber) {
        if (!authService.isPasswordValid(password)) {
            return false;
        }

        try (Connection conn = DatabaseManager.getConnection()) {

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            String userSql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'delivery')";
            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                userStmt.setString(1, username);
                userStmt.setString(2, hashedPassword);
                userStmt.executeUpdate();

                try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                    int userId = generatedKeys.getInt(1);

                    String delSql = "INSERT INTO delivery_personnel (user_id, full_name, id_number, contact_number) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement delStmt = conn.prepareStatement(delSql)) {
                        delStmt.setInt(1, userId);
                        delStmt.setString(2, fullName);
                        delStmt.setString(3, idNumber);
                        delStmt.setString(4, contactNumber);
                        delStmt.executeUpdate();
                    }
                }
            }

            System.out.println("Delivery personnel registered successfully.");
            return true;

        } catch (SQLException e) {
            System.out.println("Error registering delivery personnel: " + e.getMessage());
            return false;
        }
    }

    public boolean removeUser(String username) {
        try (Connection conn = DatabaseManager.getConnection()) {

            String selectSql = "SELECT id, role FROM users WHERE username = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                 ResultSet rs = selectStmt.executeQuery()) {
                selectStmt.setString(1, username);

                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String role = rs.getString("role");

                    if ("dispatcher".equals(role)) {
                        try (PreparedStatement del = conn.prepareStatement("DELETE FROM dispatchers WHERE user_id = ?")) {
                            del.setInt(1, userId);
                            del.executeUpdate();
                        }
                    } else if ("delivery".equals(role)) {
                        try (PreparedStatement del = conn.prepareStatement("DELETE FROM delivery_personnel WHERE user_id = ?")) {
                            del.setInt(1, userId);
                            del.executeUpdate();
                        }
                    }

                    try (PreparedStatement delUser = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
                        delUser.setInt(1, userId);
                        delUser.executeUpdate();
                    }

                    System.out.println("User removed successfully.");
                    return true;

                } else {
                    System.out.println("User not found.");
                    return false;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error removing user: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePasswordPolicy(int minLength, int minUppercase, int minLowercase,
                                        int minDigits, int minSpecial, int maxLoginAttempts) {
        try (Connection conn = DatabaseManager.getConnection()) {

            String sql = "UPDATE password_policy SET " +
                         "min_length = ?, min_uppercase = ?, min_lowercase = ?, " +
                         "min_digits = ?, min_special = ?, max_login_attempts = ? " +
                         "WHERE id = 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, minLength);
                stmt.setInt(2, minUppercase);
                stmt.setInt(3, minLowercase);
                stmt.setInt(4, minDigits);
                stmt.setInt(5, minSpecial);
                stmt.setInt(6, maxLoginAttempts);
                stmt.executeUpdate();
            }

            System.out.println("Password policy updated successfully.");
            return true;

        } catch (SQLException e) {
            System.out.println("Error updating policy: " + e.getMessage());
            return false;
        }
    }

    public void viewPasswordPolicy() {
        try (Connection conn = DatabaseManager.getConnection()) {

            String sql = "SELECT * FROM password_policy WHERE id = 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                System.out.println("\n--- Current Password Policy ---");
                System.out.println("Minimum Length:            " + rs.getInt("min_length"));
                System.out.println("Minimum Uppercase Letters: " + rs.getInt("min_uppercase"));
                System.out.println("Minimum Lowercase Letters: " + rs.getInt("min_lowercase"));
                System.out.println("Minimum Digits:            " + rs.getInt("min_digits"));
                System.out.println("Minimum Special Characters:" + rs.getInt("min_special"));
                System.out.println("Max Login Attempts:        " + rs.getInt("max_login_attempts"));
                System.out.println("--------------------------------");
            }
            } 

        } catch (SQLException e) {
            System.out.println("Error viewing policy: " + e.getMessage());
        }
    }

    public void viewAllUsers() {
        try (Connection conn = DatabaseManager.getConnection()) {

            String sql = "SELECT id, username, role, is_locked FROM users";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n--- All Users ---");
            while (rs.next()) {
                System.out.println("ID: "       + rs.getInt("id") +
                                   " | Username: " + rs.getString("username") +
                                   " | Role: "     + rs.getString("role") +
                                   " | Locked: "   + (rs.getInt("is_locked") == 1 ? "Yes" : "No"));
            }
            System.out.println("-----------------");
            } 

        } catch (SQLException e) {
            System.out.println("Error viewing users: " + e.getMessage());
        }
    }
}