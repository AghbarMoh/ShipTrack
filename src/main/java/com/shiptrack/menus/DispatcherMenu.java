package com.shiptrack.menus;

import com.shiptrack.database.DatabaseManager;
import com.shiptrack.models.User;
import com.shiptrack.services.ShipmentService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

// DispatcherMenu displays the dispatcher console and handles dispatcher actions
public class DispatcherMenu {

    private Scanner scanner;
    private ShipmentService shipmentService;

    public DispatcherMenu(Scanner scanner) {
        this.scanner = scanner;
        this.shipmentService = new ShipmentService();
    }

    // Shows the dispatcher menu and handles user input
    public void show(User dispatcher) {
        int choice = 0;

        while (choice != 6) {
            System.out.println("\n===== Dispatcher Menu =====");
            System.out.println("1. View All Shipments");
            System.out.println("2. Assign Delivery to Driver");
            System.out.println("3. Update Delivery Status");
            System.out.println("4. View My Personal Info");
            System.out.println("5. Update My Personal Info");
            System.out.println("6. Logout");
            System.out.print("Enter choice: ");

            choice = Integer.parseInt(scanner.nextLine());

            if (choice == 1) {
                shipmentService.viewAllShipments();
            } else if (choice == 2) {
                assignDelivery();
            } else if (choice == 3) {
                updateDeliveryStatus();
            } else if (choice == 4) {
                viewPersonalInfo(dispatcher);
            } else if (choice == 5) {
                updatePersonalInfo(dispatcher);
            } else if (choice == 6) {
                System.out.println("Logged out.");
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    // Assigns a shipment to a delivery person
    private void assignDelivery() {
        System.out.println("\n--- Assign Delivery ---");
        System.out.print("Enter Shipment ID: ");
        int shipmentId = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Delivery Personnel ID: ");
        int deliveryPersonnelId = Integer.parseInt(scanner.nextLine());

        shipmentService.assignDelivery(shipmentId, deliveryPersonnelId);
    }

    // Updates the status of a shipment
    private void updateDeliveryStatus() {
        System.out.println("\n--- Update Delivery Status ---");
        System.out.print("Enter Shipment ID: ");
        int shipmentId = Integer.parseInt(scanner.nextLine());
        System.out.println("Select new status:");
        System.out.println("1. pending");
        System.out.println("2. in transit");
        System.out.println("3. delivered");
        System.out.print("Enter choice: ");
        int statusChoice = Integer.parseInt(scanner.nextLine());

        String status = "";
        if (statusChoice == 1) {
            status = "pending";
        } else if (statusChoice == 2) {
            status = "in transit";
        } else if (statusChoice == 3) {
            status = "delivered";
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        shipmentService.updateShipmentStatus(shipmentId, status);
    }

    // Displays the dispatcher's personal info
    private void viewPersonalInfo(User dispatcher) {
        String sql = "SELECT * FROM dispatchers WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dispatcher.getId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n--- Your Personal Info ---");
                    System.out.println("Username:       " + dispatcher.getUsername());
                    System.out.println("Full Name:      " + rs.getString("full_name"));
                    System.out.println("ID Number:      " + rs.getString("id_number"));
                    System.out.println("Contact Number: " + rs.getString("contact_number"));
                    System.out.println("--------------------------");
                } else {
                    System.out.println("No personal info found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error viewing info: " + e.getMessage());
        }
    }

    // Updates the dispatcher's personal info
    private void updatePersonalInfo(User dispatcher) {
        System.out.println("\n--- Update Personal Info ---");
        System.out.print("New Full Name: ");
        String fullName = scanner.nextLine();
        System.out.print("New Contact Number: ");
        String contactNumber = scanner.nextLine();

        String sql = "UPDATE dispatchers SET full_name = ?, contact_number = ? WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fullName);
            stmt.setString(2, contactNumber);
            stmt.setInt(3, dispatcher.getId());
            stmt.executeUpdate();

            System.out.println("Personal info updated successfully.");

        } catch (SQLException e) {
            System.out.println("Error updating info: " + e.getMessage());
        }
    }
}