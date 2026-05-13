package com.shiptrack.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

// DatabaseManager handles the connection to the SQLite database
// and creates all the tables if they don't already exist
public class DatabaseManager {

    // The database file will be created in the project root folder
    private static final String DB_URL = "jdbc:sqlite:shiptrack.db";

    // Returns a connection to the database
public static Connection getConnection() throws SQLException {
    Connection conn = DriverManager.getConnection(DB_URL);
    // Tells SQLite to wait up to 5 seconds if database is busy
    conn.createStatement().execute("PRAGMA journal_mode=WAL");
    return conn;
}
    // Creates all tables when the application starts
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Users table — stores login info for all user types
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

            // Customers table — stores customer personal info
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

            // Dispatchers table — stores dispatcher personal info
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

            // Delivery personnel table — stores delivery person personal info
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

            // Shipments table — stores all package delivery requests
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

            // Password policy table — admin sets rules for password strength
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

            // Insert default password policy if none exists
            stmt.execute(
                "INSERT OR IGNORE INTO password_policy " +
                "(id, min_length, min_uppercase, min_lowercase, min_digits, min_special, max_login_attempts) " +
                "VALUES (1, 8, 1, 1, 1, 1, 3)"
            );

// Admin account — created only if it doesn't already exist
// Password is hashed at runtime using BCrypt
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