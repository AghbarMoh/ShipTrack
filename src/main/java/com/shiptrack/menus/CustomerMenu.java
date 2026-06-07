package com.shiptrack.menus;

import com.shiptrack.database.DatabaseManager;
import com.shiptrack.models.User;
import com.shiptrack.services.ShipmentService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class CustomerMenu {

    private Scanner scanner;
    private ShipmentService shipmentService;

    public CustomerMenu(Scanner scanner) {
        this.scanner = scanner;
        this.shipmentService = new ShipmentService();
    }

    public void show(User customer) {
        int choice = 0;

        while (choice != 5) {
            System.out.println("\n===== Customer Menu =====");
            System.out.println("1. Create Shipment Request");
            System.out.println("2. Track My Shipments");
            System.out.println("3. View My Personal Info");
            System.out.println("4. Update My Personal Info");
            System.out.println("5. Logout");
            System.out.print("Enter choice: ");

            choice = Integer.parseInt(scanner.nextLine());

            if (choice == 1) {
                createShipment(customer);
            } else if (choice == 2) {
                shipmentService.viewCustomerShipments(customer.getId());
            } else if (choice == 3) {
                viewPersonalInfo(customer);
            } else if (choice == 4) {
                updatePersonalInfo(customer);
            } else if (choice == 5) {
                System.out.println("Logged out.");
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private void createShipment(User customer) {
        System.out.println("\n--- Create Shipment ---");
        System.out.print("Origin (pickup location): ");
        String origin = scanner.nextLine();
        System.out.print("Destination: ");
        String destination = scanner.nextLine();

        shipmentService.createShipment(customer.getId(), origin, destination);
    }

    private void viewPersonalInfo(User customer) {
        String sql = "SELECT * FROM customers WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, customer.getId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n--- Your Personal Info ---");
                    System.out.println("Username:       " + customer.getUsername());
                    System.out.println("Full Name:      " + rs.getString("full_name"));
                    System.out.println("ID Number:      " + rs.getString("id_number"));
                    System.out.println("Contact Number: " + rs.getString("contact_number"));
                    System.out.println("--------------------------");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error viewing info: " + e.getMessage());
        }
    }

    private void updatePersonalInfo(User customer) {
        System.out.println("\n--- Update Personal Info ---");
        System.out.print("New Full Name: ");
        String fullName = scanner.nextLine();
        System.out.print("New Contact Number: ");
        String contactNumber = scanner.nextLine();

        String sql = "UPDATE customers SET full_name = ?, contact_number = ? WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fullName);
            stmt.setString(2, contactNumber);
            stmt.setInt(3, customer.getId());
            stmt.executeUpdate();

            System.out.println("Personal info updated successfully.");

        } catch (SQLException e) {
            System.out.println("Error updating info: " + e.getMessage());
        }
    }
}