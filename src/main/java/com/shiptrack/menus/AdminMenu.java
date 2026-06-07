package com.shiptrack.menus;

import com.shiptrack.models.User;
import com.shiptrack.services.AdminService;
import com.shiptrack.services.AuthService;

import java.util.Scanner;


public class AdminMenu {

    private Scanner scanner;
    private AdminService adminService;
    private AuthService authService;

    public AdminMenu(Scanner scanner) {
        this.scanner = scanner;
        this.adminService = new AdminService();
        this.authService = new AuthService();
    }

    
    public void show(User admin) {
        int choice = 0;

        while (choice != 8) {
            System.out.println("\n===== Admin Menu =====");
            System.out.println("1. Register Dispatcher");
            System.out.println("2. Register Delivery Personnel");
            System.out.println("3. Remove User");
            System.out.println("4. View All Users");
            System.out.println("5. Update Password Policy");
            System.out.println("6. View Password Policy");
            System.out.println("7. Lock / Unlock Account");
            System.out.println("8. Logout");
            System.out.print("Enter choice: ");

            choice = Integer.parseInt(scanner.nextLine());

            if (choice == 1) {
                registerDispatcher();
            } else if (choice == 2) {
                registerDeliveryPersonnel();
            } else if (choice == 3) {
                removeUser();
            } else if (choice == 4) {
                adminService.viewAllUsers();
            } else if (choice == 5) {
                updatePasswordPolicy();
            } else if (choice == 6) {
                adminService.viewPasswordPolicy();
            } else if (choice == 7) {
                lockUnlockAccount();
            } else if (choice == 8) {
                System.out.println("Logged out.");
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    
    private void registerDispatcher() {
        System.out.println("\n--- Register Dispatcher ---");
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

        adminService.registerDispatcher(username, password, fullName, idNumber, contactNumber);
    }

    
    private void registerDeliveryPersonnel() {
        System.out.println("\n--- Register Delivery Personnel ---");
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

        adminService.registerDeliveryPersonnel(username, password, fullName, idNumber, contactNumber);
    }

    
    private void removeUser() {
        System.out.println("\n--- Remove User ---");
        System.out.print("Enter username to remove: ");
        String username = scanner.nextLine();
        adminService.removeUser(username);
    }

    private void updatePasswordPolicy() {
        System.out.println("\n--- Update Password Policy ---");
        System.out.print("Minimum length: ");
        int minLength = Integer.parseInt(scanner.nextLine());
        System.out.print("Minimum uppercase letters: ");
        int minUppercase = Integer.parseInt(scanner.nextLine());
        System.out.print("Minimum lowercase letters: ");
        int minLowercase = Integer.parseInt(scanner.nextLine());
        System.out.print("Minimum digits: ");
        int minDigits = Integer.parseInt(scanner.nextLine());
        System.out.print("Minimum special characters: ");
        int minSpecial = Integer.parseInt(scanner.nextLine());
        System.out.print("Maximum login attempts: ");
        int maxAttempts = Integer.parseInt(scanner.nextLine());

        adminService.updatePasswordPolicy(minLength, minUppercase, minLowercase,
                                          minDigits, minSpecial, maxAttempts);
    }

    
    private void lockUnlockAccount() {
        System.out.println("\n--- Lock / Unlock Account ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter action (lock / unlock): ");
        String action = scanner.nextLine();

        if ("lock".equals(action)) {
            authService.lockAccount(username);
            System.out.println("Account locked.");
        } else if ("unlock".equals(action)) {
            authService.unlockAccount(username);
        } else {
            System.out.println("Invalid action.");
        }
    }
}