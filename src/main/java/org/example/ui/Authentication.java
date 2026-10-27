package org.example.ui;

import org.example.dao.UserDao;
import org.example.model.Role;
import org.example.model.User;

import java.sql.SQLException;
import java.util.Scanner;

/** Handles the start screen, registration, login and role-based routing. */
public class Authentication {

    private final Scanner scanner;
    private final UserDao userDao;

    public Authentication(Scanner scanner, UserDao userDao) {
        this.scanner = scanner;
        this.userDao = userDao;
    }

    public void start() {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("       CLINIC APPOINTMENT SYSTEM");
            System.out.println("========================================");
            System.out.println("[1] Register Account");
            System.out.println("[2] Login");
            System.out.println("[3] Exit");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> registerAccount();
                    case "2" -> loginAccount();
                    case "3" -> {
                        System.out.println("Application closed. Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    private void registerAccount() throws SQLException {
        System.out.println("\n--- REGISTER CLINIC ACCOUNT ---");

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (username.isBlank() || password.isBlank()) {
            System.out.println("Username and password are required.");
            return;
        }

        Role role = selectRole();
        if (role == null) {
            System.out.println("Invalid role selection.");
            return;
        }

        if (userDao.register(username, password, role)) {
            System.out.println("\nAccount registered successfully!");
            System.out.println("Username: " + username);
            System.out.println("Role: " + role);
            System.out.println("Status: PENDING");
            System.out.println("Your account is awaiting approval.");
        } else {
            System.out.println("Username already exists.");
        }
    }

    /** Lists every role in the Role enum and returns the chosen one, or null if invalid. */
    private Role selectRole() {
        Role[] roles = Role.values();

        System.out.println("\nSelect role:");
        for (int i = 0; i < roles.length; i++) {
            System.out.println("[" + (i + 1) + "] " + roles[i]);
        }
        System.out.print("Enter choice: ");

        try {
            int index = Integer.parseInt(scanner.nextLine().trim());
            if (index >= 1 && index <= roles.length) {
                return roles[index - 1];
            }
        } catch (NumberFormatException ignored) {
            // falls through to return null
        }

        return null;
    }

    private void loginAccount() throws SQLException {
        System.out.println("\n--- CLINIC SYSTEM LOGIN ---");

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = userDao.login(username, password);

        if (user == null) {
            System.out.println("Invalid username or password.");
            return;
        }

        if (!"APPROVED".equals(user.status())) {
            System.out.println("Your account is " + user.status() + ".");
            System.out.println("Please contact the clinic administrator.");
            return;
        }

        System.out.println("\nLogin successful!");
        System.out.println("Welcome, " + user.username());
        System.out.println("Role: " + user.role());

        routeToMenu(user);
    }

    private void routeToMenu(User user) {
        RoleMenu menu;

        switch (Role.valueOf(user.role())) {
            case ADMIN -> menu = new AdminMenu(scanner, userDao);
            case RECEPTIONIST -> menu = new ReceptionistMenu(scanner, userDao);
            case DOCTOR -> menu = new DoctorMenu(scanner, userDao);
            default -> {
                System.out.println("Unknown role.");
                return;
            }
        }

        menu.show(user);
    }
}
