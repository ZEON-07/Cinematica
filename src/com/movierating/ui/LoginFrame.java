package com.movierating.ui;

import com.movierating.database.DatabaseOperations;
import com.movierating.exceptions.EmptyFieldException;
import com.movierating.models.AdminUser;
import com.movierating.models.BaseUser;
import com.movierating.models.ViewerUser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private final DatabaseOperations dbManager;

    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel loginCardPanel;
    private JPanel regCardPanel;
    private JTabbedPane tabbedPane;

    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private JButton loginBtn;

    private JTextField regUsernameField;
    private JPasswordField regPasswordField;
    private JComboBox<String> regRoleComboBox;
    private JButton registerBtn;

    public LoginFrame(DatabaseOperations dbManager) {
        this.dbManager = dbManager;
        initUI();
    }

    private void initUI() {
        setTitle("Cinematica - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 430);
        setLocationRelativeTo(null);
        setResizable(false);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIHelper.getBgColor());

        headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(UIHelper.getHeaderBgColor());
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBlock.setOpaque(false);

        JLabel titleLabel = new JLabel("Cinematica");
        titleLabel.setFont(UIHelper.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Movie Rating & Review System | OOP Project");
        subtitleLabel.setFont(UIHelper.FONT_REGULAR);
        subtitleLabel.setForeground(new Color(200, 215, 230));

        titleBlock.add(titleLabel);
        titleBlock.add(subtitleLabel);
        headerPanel.add(titleBlock, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIHelper.FONT_BOLD);
        tabbedPane.addTab("  Sign In  ", createLoginPanel());
        tabbedPane.addTab("  Register New User  ", createRegisterPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);

        getRootPane().setDefaultButton(loginBtn);
    }

    private JPanel createLoginPanel() {
        loginCardPanel = new JPanel(new GridBagLayout());
        loginCardPanel.setBorder(new EmptyBorder(25, 35, 25, 35));
        loginCardPanel.setBackground(UIHelper.getCardBgColor());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblLoginUser = new JLabel("Username:");
        lblLoginUser.setFont(UIHelper.FONT_BOLD);
        lblLoginUser.setForeground(UIHelper.getTextColor());
        loginCardPanel.add(lblLoginUser, gbc);

        gbc.gridx = 1;
        loginUsernameField = new JTextField(15);
        loginUsernameField.setFont(UIHelper.FONT_REGULAR);
        loginUsernameField.setPreferredSize(new Dimension(200, 32));
        loginUsernameField.setBackground(UIHelper.getInputBgColor());
        loginUsernameField.setForeground(UIHelper.getInputTextColor());
        loginCardPanel.add(loginUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblLoginPass = new JLabel("Password:");
        lblLoginPass.setFont(UIHelper.FONT_BOLD);
        lblLoginPass.setForeground(UIHelper.getTextColor());
        loginCardPanel.add(lblLoginPass, gbc);

        gbc.gridx = 1;
        loginPasswordField = new JPasswordField(15);
        loginPasswordField.setFont(UIHelper.FONT_REGULAR);
        loginPasswordField.setPreferredSize(new Dimension(200, 32));
        loginPasswordField.setBackground(UIHelper.getInputBgColor());
        loginPasswordField.setForeground(UIHelper.getInputTextColor());
        loginCardPanel.add(loginPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginBtn = UIHelper.createCustomButton("Sign In", UIHelper.COLOR_PRIMARY, Color.WHITE);
        loginBtn.setPreferredSize(new Dimension(220, 38));
        loginBtn.addActionListener(e -> handleLogin());
        loginCardPanel.add(loginBtn, gbc);

        UIHelper.linkEnterKeyToButton(loginUsernameField, loginBtn);
        UIHelper.linkEnterKeyToButton(loginPasswordField, loginBtn);

        return loginCardPanel;
    }

    private JPanel createRegisterPanel() {
        regCardPanel = new JPanel(new GridBagLayout());
        regCardPanel.setBorder(new EmptyBorder(20, 35, 20, 35));
        regCardPanel.setBackground(UIHelper.getCardBgColor());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblRegUser = new JLabel("New Username:");
        lblRegUser.setFont(UIHelper.FONT_BOLD);
        lblRegUser.setForeground(UIHelper.getTextColor());
        regCardPanel.add(lblRegUser, gbc);

        gbc.gridx = 1;
        regUsernameField = new JTextField(15);
        regUsernameField.setFont(UIHelper.FONT_REGULAR);
        regUsernameField.setPreferredSize(new Dimension(200, 32));
        regUsernameField.setBackground(UIHelper.getInputBgColor());
        regUsernameField.setForeground(UIHelper.getInputTextColor());
        regCardPanel.add(regUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblRegPass = new JLabel("New Password:");
        lblRegPass.setFont(UIHelper.FONT_BOLD);
        lblRegPass.setForeground(UIHelper.getTextColor());
        regCardPanel.add(lblRegPass, gbc);

        gbc.gridx = 1;
        regPasswordField = new JPasswordField(15);
        regPasswordField.setFont(UIHelper.FONT_REGULAR);
        regPasswordField.setPreferredSize(new Dimension(200, 32));
        regPasswordField.setBackground(UIHelper.getInputBgColor());
        regPasswordField.setForeground(UIHelper.getInputTextColor());
        regCardPanel.add(regPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblRegRole = new JLabel("Account Role:");
        lblRegRole.setFont(UIHelper.FONT_BOLD);
        lblRegRole.setForeground(UIHelper.getTextColor());
        regCardPanel.add(lblRegRole, gbc);

        gbc.gridx = 1;
        regRoleComboBox = new JComboBox<>(new String[]{"Viewer", "Admin"});
        regRoleComboBox.setFont(UIHelper.FONT_REGULAR);
        regRoleComboBox.setPreferredSize(new Dimension(200, 32));
        regRoleComboBox.setBackground(UIHelper.getInputBgColor());
        regRoleComboBox.setForeground(UIHelper.getInputTextColor());
        regCardPanel.add(regRoleComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        registerBtn = UIHelper.createCustomButton("Register Account", UIHelper.COLOR_SUCCESS, Color.WHITE);
        registerBtn.setPreferredSize(new Dimension(220, 38));
        registerBtn.addActionListener(e -> handleRegistration());
        regCardPanel.add(registerBtn, gbc);

        UIHelper.linkEnterKeyToButton(regUsernameField, registerBtn);
        UIHelper.linkEnterKeyToButton(regPasswordField, registerBtn);

        return regCardPanel;
    }

    private void handleLogin() {
        String username = loginUsernameField.getText();
        String password = new String(loginPasswordField.getPassword());

        try {
            BaseUser authenticatedUser = dbManager.authenticateUser(username, password);

            if (authenticatedUser != null) {
                JOptionPane.showMessageDialog(this,
                        "Login successful!\n" + authenticatedUser.getRoleDescription(),
                        "Welcome", JOptionPane.INFORMATION_MESSAGE);

                new MainDashboardFrame(authenticatedUser, dbManager).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password. Please verify and try again.",
                        "Authentication Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (EmptyFieldException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Required", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void handleRegistration() {
        String username = regUsernameField.getText();
        String password = new String(regPasswordField.getPassword());
        String selectedRole = (String) regRoleComboBox.getSelectedItem();

        try {
            if (username == null || username.trim().isEmpty()) {
                throw new EmptyFieldException("Please enter a username for registration.");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new EmptyFieldException("Please enter a password for registration.");
            }

            BaseUser newUser = "Admin".equalsIgnoreCase(selectedRole)
                    ? new AdminUser(username.trim(), password)
                    : new ViewerUser(username.trim(), password);

            boolean success = dbManager.registerUser(newUser);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Registration successful! You can now log in as " + selectedRole + ".",
                        "Registration Complete", JOptionPane.INFORMATION_MESSAGE);
                regUsernameField.setText("");
                regPasswordField.setText("");
                tabbedPane.setSelectedIndex(0);
                loginUsernameField.setText(username.trim());
                loginPasswordField.requestFocus();
            }
        } catch (EmptyFieldException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Required", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            if (ex.getMessage().contains("UNIQUE constraint failed")) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different username.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
