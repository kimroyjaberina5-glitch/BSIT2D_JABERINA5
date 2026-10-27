package org.example.ui;

import org.example.dao.UserDao;
import org.example.model.User;

import java.util.List;
import java.util.Scanner;

/** Dashboard for the DOCTOR role. */
public class DoctorMenu extends RoleMenu {

    public DoctorMenu(Scanner scanner, UserDao userDao) {
        super(scanner, userDao);
    }

    @Override
    protected String title() {
        return "DOCTOR DASHBOARD";
    }

    @Override
    protected List<String> options() {
        return List.of("View Appointments", "Consultation / Diagnosis");
    }

    @Override
    protected void handleOption(int option, User user) {
        // TODO: connect each option to its DAO in a later deliverable
        notYetImplemented();
    }
}
