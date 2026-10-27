package org.example;

import org.example.dao.DatabaseInitializer;
import org.example.dao.UserDao;
import org.example.ui.Authentication;

import java.sql.SQLException;
import java.util.Scanner;

/** Startup entry point only: init database -> create shared objects -> hand off. */
public class Main {

    public static void main(String[] args) {
        try {
            // 1. Initialize database
            DatabaseInitializer.initialize();

            // 2. Instantiate DAOs & utilities (one shared instance each)
            UserDao userDao = new UserDao();
            Scanner scanner = new Scanner(System.in);

            // 3. Delegate to Authentication
            new Authentication(scanner, userDao).start();

            scanner.close();

        } catch (SQLException e) {
            System.err.println("Could not start the application.");
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
