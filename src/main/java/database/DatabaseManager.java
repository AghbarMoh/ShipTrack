package com.shiptrack.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public final class DatabaseManager {

    // Private constructor to prevent instantiation (Utility Class)
    private DatabaseManager() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Default database — can be overridden for testing
    private static String DB_URL = "jdbc:sqlite:shiptrack.db";

    // Allows tests to set a different database URL
    public static void setDbUrl(String url) {
        DB_URL = url;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "role TEXT NOT NULL," +
                "is_locked INTEGER DEFAULT 0," +
                "failed_attempts INTEGER DEFAULT 0" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS customers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "full_name TEXT NOT NULL," +
                "id_number TEXT NOT NULL," +
                "contact_number TEXT NOT NULL," +
                "FOREIGN KEY (user_id) REFERENCES users(id)" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS dispatchers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "full_name TEXT NOT NULL," +
                "id_number TEXT NOT NULL," +
                "contact_number TEXT NOT NULL," +
                "FOREIGN KEY (user_id) REFERENCES users(id)" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS delivery_personnel (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "full_name TEXT NOT NULL," +
                "id_number TEXT NOT NULL," +
                "contact_number TEXT NOT NULL," +
                "FOREIGN KEY (user_id) REFERENCES users(id)" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS shipments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "customer_id INTEGER NOT NULL," +
                "delivery_personnel_id INTEGER," +
                "origin TEXT NOT NULL," +
                "destination TEXT NOT NULL," +
                "status TEXT DEFAULT 'pending'," +
                "FOREIGN KEY (customer_id) REFERENCES users(id)," +
                "FOREIGN KEY (delivery_personnel_id) REFERENCES users(id)" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS password_policy (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "min_length INTEGER DEFAULT 8," +
                "min_uppercase INTEGER DEFAULT 1," +
                "min_lowercase INTEGER DEFAULT 1," +
                "min_digits INTEGER DEFAULT 1," +
                "min_special INTEGER DEFAULT 1," +
                "max_login_attempts INTEGER DEFAULT 3" +
                ")"
            );

            stmt.execute(
                "INSERT OR IGNORE INTO password_policy " +
                "(id, min_length, min_uppercase, min_lowercase, min_digits, min_special, max_login_attempts) " +
                "VALUES (1, 8, 1, 1, 1, 1, 3)"
            );

            String adminHash = org.mindrot.jbcrypt.BCrypt.hashpw("Admin@1234", org.mindrot.jbcrypt.BCrypt.gensalt());
            stmt.execute(
                "INSERT OR IGNORE INTO users (id, username, password, role, is_locked, failed_attempts) " +
                "VALUES (1, 'admin', '" + adminHash + "', 'admin', 0, 0)"
            );

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}