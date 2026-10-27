package org.example.ui;

import org.example.dao.UserDao;
import org.example.model.User;

import java.util.List;
import java.util.Scanner;

/** Dashboard for the RECEPTIONIST role. */
public class ReceptionistMenu extends RoleMenu {

    public ReceptionistMenu(Scanner scanner, UserDao userDao) {
        super(scanner, userDao);
    }

    @Override
    protected String title() {
        return "RECEPTIONIST DASHBOARD";
    }

    @Override
    protected List<String> options() {
        return List.of("Register Patient", "Manage Appointments");
    }

    @Override
    protected void handleOption(int option, User user) {
        // TODO: connect each option to its DAO in a later deliverable
        notYetImplemented();
    }
}
