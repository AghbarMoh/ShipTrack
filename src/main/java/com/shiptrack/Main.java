package com.shiptrack;

import com.shiptrack.database.DatabaseManager;
import com.shiptrack.menus.AdminMenu;
import com.shiptrack.menus.CustomerMenu;
import com.shiptrack.menus.DispatcherMenu;
import com.shiptrack.menus.DeliveryMenu;
import com.shiptrack.models.User;
import com.shiptrack.services.AuthService;

import java.util.Scanner;

public final class Main {

    private Main() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void main(String[] args) {

        DatabaseManager.initializeDatabase();

        AuthService authService = new AuthService();

        int choice = 0;

        try (Scanner scanner = new Scanner(System.in)) {
            while (choice != 3) {
            System.out.println("\n===== Welcome to ShipTrack =====");
            System.out.println("1. Login");
            System.out.println("2. Register as Customer");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            choice = Integer.parseInt(scanner.nextLine());

            if (choice == 1) {
                System.out.print("Username: ");
                String username = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();

                User user = authService.login(username, password);

                if (user != null) {
                    System.out.println("\nWelcome, " + user.getUsername() + "! Role: " + user.getRole());

                    if ("admin".equals(user.getRole())) {
                        new AdminMenu(scanner).show(user);
                    } else if ("customer".equals(user.getRole())) {
                        new CustomerMenu(scanner).show(user);
                    } else if ("dispatcher".equals(user.getRole())) {
                        new DispatcherMenu(scanner).show(user);
                    } else if ("delivery".equals(user.getRole())) {
                        new DeliveryMenu(scanner).show(user);
                    }
                }

            } else if (choice == 2) {
                System.out.println("\n--- Register as Customer ---");
                System.out.print("Username: ");
                String username = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();
                System.out.print("Full Name: ");
                String fullName = scanner.nextLine();
                System.out.print("ID Number: ");
                String idNumber = scanner.nextLine();
                System.out.print("Contact Number: ");
                String contactNumber = scanner.nextLine();

                authService.registerCustomer(username, password, fullName, idNumber, contactNumber);

            } else if (choice == 3) {
                System.out.println("Goodbye!");
            } else {
                System.out.println("Invalid choice.");
            }
            } 
        } 
    }
}