package org.example.ui;

import org.example.dao.UserDao;
import org.example.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/** Dashboard for the ADMIN role: view users and approve pending registrations. */
public class AdminMenu extends RoleMenu {

    public AdminMenu(Scanner scanner, UserDao userDao) {
        super(scanner, userDao);
    }

    @Override
    protected String title() {
        return "ADMIN DASHBOARD";
    }

    @Override
    protected List<String> options() {
        return List.of(
                "View All System Users",
                "View Pending Registrations",
                "Approve User Account"
        );
    }

    @Override
    protected void handleOption(int option, User user) {
        try {
            switch (option) {
                case 1 -> viewAllUsers();
                case 2 -> viewPendingUsers();
                case 3 -> approveUserAccount();
                default -> System.out.println("Invalid choice.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void viewAllUsers() throws SQLException {
        System.out.println("\n--- ALL SYSTEM USERS ---");
        printUserTable(userDao.getAllUsers());
    }

    private void viewPendingUsers() throws SQLException {
        System.out.println("\n--- PENDING REGISTRATIONS ---");
        printUserTable(userDao.getPendingUsers());
    }

    private void approveUserAccount() throws SQLException {
        System.out.println("\n--- APPROVE USER ACCOUNT ---");

        List<User> pending = userDao.getPendingUsers();

        if (pending.isEmpty()) {
            System.out.println("No pending accounts to approve.");
            return;
        }

        printUserTable(pending);

        while (true) {
            System.out.print("Enter User ID to approve (0 to cancel): ");
            String input = scanner.nextLine().trim();

            int userId;
            try {
                userId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            if (userId == 0) {
                System.out.println("Approval cancelled.");
                return;
            }

            User target = findById(pending, userId);

            if (target == null) {
                System.out.println("User ID " + userId + " is not in the pending list. Try again.");
                continue;
            }

            if (userDao.approveUser(userId)) {
                System.out.println("\nAccount approved successfully!");
                System.out.println("User ID : " + target.userId());
                System.out.println("Username: " + target.username());
                System.out.println("Role    : " + target.role());
                System.out.println("Status  : APPROVED");
            } else {
                System.out.println("Could not approve this account. It may have already been updated.");
            }
            return;
        }
    }

    private User findById(List<User> users, int userId) {
        for (User u : users) {
            if (u.userId() == userId) {
                return u;
            }
        }
        return null;
    }

    /** Prints any list of users as an aligned, bordered table. */
    static void printUserTable(List<User> users) {
        String border = "+" + "-".repeat(7) + "+" + "-".repeat(17)
                + "+" + "-".repeat(14) + "+" + "-".repeat(12) + "+";
        String rowFormat = "| %-5.5s | %-15.15s | %-12.12s | %-10.10s |%n";

        System.out.println(border);
        System.out.printf(rowFormat, "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println(border);

        if (users.isEmpty()) {
            System.out.printf("| %-51s |%n", "No users found.");
        }

        for (User u : users) {
            System.out.printf(rowFormat,
                    String.valueOf(u.userId()), u.username(), u.role(), u.status());
        }

        System.out.println(border);
        System.out.println("Total: " + users.size());
    }
}
