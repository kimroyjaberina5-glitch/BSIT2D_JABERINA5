package org.example.ui;

import org.example.dao.UserDao;
import org.example.model.User;

import java.util.List;
import java.util.Scanner;

/**
 * Base class for the role dashboards. Subclasses only supply their option
 * labels and what happens when an option is picked. "Logout" is always the
 * last option. Scanner and UserDao are the shared instances from Main.
 */
public abstract class RoleMenu {

    protected final Scanner scanner;
    protected final UserDao userDao;

    protected RoleMenu(Scanner scanner, UserDao userDao) {
        this.scanner = scanner;
        this.userDao = userDao;
    }

    protected abstract String title();

    protected abstract List<String> options();

    /** Called with the 1-based number of a non-logout option. */
    protected abstract void handleOption(int option, User user);

    public void show(User user) {
        List<String> options = options();
        int logoutNumber = options.size() + 1;

        while (true) {
            System.out.println("\n========================================");
            System.out.println("          " + title());
            System.out.println("========================================");
            System.out.println("User: " + user.username());
            System.out.println("Role: " + user.role());

            for (int i = 0; i < options.size(); i++) {
                System.out.println("[" + (i + 1) + "] " + options.get(i));
            }
            System.out.println("[" + logoutNumber + "] Logout");
            System.out.print("Enter choice: ");

            String input = scanner.nextLine().trim();

            if (input.equals(String.valueOf(logoutNumber))) {
                System.out.println("Logged out successfully.");
                return;
            }

            try {
                int option = Integer.parseInt(input);
                if (option >= 1 && option <= options.size()) {
                    handleOption(option, user);
                    continue;
                }
            } catch (NumberFormatException ignored) {
                // falls through to invalid message
            }

            System.out.println("Invalid choice.");
        }
    }

    protected void notYetImplemented() {
        System.out.println("This feature still needs to be connected to its database operations.");
    }
}
