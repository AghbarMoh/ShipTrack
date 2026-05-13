package com.shiptrack.menus;

import com.shiptrack.database.DatabaseManager;
import com.shiptrack.models.User;
import com.shiptrack.services.ShipmentService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

// DeliveryMenu displays the delivery personnel console and handles their actions
public class DeliveryMenu {

    private Scanner scanner;
    private ShipmentService shipmentService;

    public DeliveryMenu(Scanner scanner) {
        this.scanner = scanner;
        this.shipmentService = new ShipmentService();
    }

    // Shows the delivery personnel menu and handles user input
    public void show(User deliveryPersonnel) {
        int choice = 0;

        while (choice != 4) {
            System.out.println("\n===== Delivery Personnel Menu =====");
            System.out.println("1. View My Assigned Deliveries");
            System.out.println("2. Update Delivery Status");
            System.out.println("3. View My Personal Info");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");

            choice = Integer.parseInt(scanner.nextLine());

            if (choice == 1) {
                shipmentService.viewAssignedDeliveries(deliveryPersonnel.getId());
            } else if (choice == 2) {
                updateDeliveryStatus();
            } else if (choice == 3) {
                viewPersonalInfo(deliveryPersonnel);
            } else if (choice == 4) {
                System.out.println("Logged out.");
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    // Updates the status of an assigned delivery
    private void updateDeliveryStatus() {
        System.out.println("\n--- Update Delivery Status ---");
        System.out.print("Enter Shipment ID: ");
        int shipmentId = Integer.parseInt(scanner.nextLine());
        System.out.println("Select new status:");
        System.out.println("1. picked up");
        System.out.println("2. in transit");
        System.out.println("3. delivered");
        System.out.print("Enter choice: ");
        int statusChoice = Integer.parseInt(scanner.nextLine());

        String status = "";
        if (statusChoice == 1) {
            status = "picked up";
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

    // Displays the delivery personnel's personal info
    private void viewPersonalInfo(User deliveryPersonnel) {
        try (Connection conn = DatabaseManager.getConnection()) {

            String sql = "SELECT * FROM delivery_personnel WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, deliveryPersonnel.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("\n--- Your Personal Info ---");
                System.out.println("Username:       " + deliveryPersonnel.getUsername());
                System.out.println("Full Name:      " + rs.getString("full_name"));
                System.out.println("ID Number:      " + rs.getString("id_number"));
                System.out.println("Contact Number: " + rs.getString("contact_number"));
                System.out.println("--------------------------");
            } else {
                System.out.println("No personal info found.");
            }

        } catch (SQLException e) {
            System.out.println("Error viewing info: " + e.getMessage());
        }
    }
}