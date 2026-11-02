package com.movierating;

import com.movierating.database.DBManager;
import com.movierating.database.DatabaseOperations;
import com.movierating.ui.LoginFrame;

import javax.swing.*;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not apply system Look and Feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseOperations dbManager = new DBManager();
                dbManager.initializeDatabase();

                LoginFrame loginFrame = new LoginFrame(dbManager);
                loginFrame.setVisible(true);

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,
                        "Failed to initialize database: " + e.getMessage(),
                        "Database Initialization Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
