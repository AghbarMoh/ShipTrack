package com.shiptrack.services;

import com.shiptrack.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// ShipmentService handles all shipment-related operations
// including creating, tracking, and updating shipments
public class ShipmentService {

    // Creates a new shipment request for a customer
    public boolean createShipment(int customerId, String origin, String destination) {
        String sql = "INSERT INTO shipments (customer_id, origin, destination, status) " +
                     "VALUES (?, ?, ?, 'pending')";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            stmt.setString(2, origin);
            stmt.setString(3, destination);
            stmt.executeUpdate();

            System.out.println("Shipment created successfully.");
            return true;

        } catch (SQLException e) {
            System.out.println("Error creating shipment: " + e.getMessage());
            return false;
        }
    }

    // Shows all shipments belonging to a specific customer
    public void viewCustomerShipments(int customerId) {
        String sql = "SELECT * FROM shipments WHERE customer_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n--- Your Shipments ---");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.println("ID: "          + rs.getInt("id"));
                    System.out.println("From: "        + rs.getString("origin"));
                    System.out.println("To: "          + rs.getString("destination"));
                    System.out.println("Status: "      + rs.getString("status"));
                    System.out.println("Assigned To: " + rs.getInt("delivery_personnel_id"));
                    System.out.println("----------------------");
                }
                if (!found) {
                    System.out.println("No shipments found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error viewing shipments: " + e.getMessage());
        }
    }

    // Shows all shipments assigned to a specific delivery person
    public void viewAssignedDeliveries(int deliveryPersonnelId) {
        String sql = "SELECT * FROM shipments WHERE delivery_personnel_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, deliveryPersonnelId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n--- Your Assigned Deliveries ---");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.println("ID: "     + rs.getInt("id"));
                    System.out.println("From: "   + rs.getString("origin"));
                    System.out.println("To: "     + rs.getString("destination"));
                    System.out.println("Status: " + rs.getString("status"));
                    System.out.println("--------------------------------");
                }
                if (!found) {
                    System.out.println("No deliveries assigned.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error viewing deliveries: " + e.getMessage());
        }
    }

    // Shows all pending shipments — used by dispatcher
    public void viewAllShipments() {
        String sql = "SELECT * FROM shipments";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n--- All Shipments ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("ID: "          + rs.getInt("id"));
                System.out.println("Customer ID: " + rs.getInt("customer_id"));
                System.out.println("From: "        + rs.getString("origin"));
                System.out.println("To: "          + rs.getString("destination"));
                System.out.println("Status: "      + rs.getString("status"));
                System.out.println("Assigned To: " + rs.getInt("delivery_personnel_id"));
                System.out.println("---------------------");
            }
            if (!found) {
                System.out.println("No shipments found.");
            }

        } catch (SQLException e) {
            System.out.println("Error viewing shipments: " + e.getMessage());
        }
    }

    // Assigns a shipment to a delivery person — used by dispatcher
    public boolean assignDelivery(int shipmentId, int deliveryPersonnelId) {
        String sql = "UPDATE shipments SET delivery_personnel_id = ?, status = 'in transit' " +
                     "WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, deliveryPersonnelId);
            stmt.setInt(2, shipmentId);
            stmt.executeUpdate();

            System.out.println("Delivery assigned successfully.");
            return true;

        } catch (SQLException e) {
            System.out.println("Error assigning delivery: " + e.getMessage());
            return false;
        }
    }

    // Updates the status of a shipment
    // Used by both dispatcher and delivery personnel
    public boolean updateShipmentStatus(int shipmentId, String status) {
        String sql = "UPDATE shipments SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, shipmentId);
            stmt.executeUpdate();

            System.out.println("Status updated to: " + status);
            return true;

        } catch (SQLException e) {
            System.out.println("Error updating status: " + e.getMessage());
            return false;
        }
    }
}