package com.movierating.ui;

import com.movierating.database.DatabaseOperations;
import com.movierating.models.BaseUser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UserManagementFrame extends JDialog {

    private final BaseUser currentAdmin;
    private final DatabaseOperations dbManager;

    private JPanel mainPanel;
    private JPanel headerPanel;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JLabel totalUsersLabel;
    private JScrollPane scrollPane;

    public UserManagementFrame(JFrame parent, BaseUser currentAdmin, DatabaseOperations dbManager) {
        super(parent, "Cinematica Admin Console - User Management", true);
        this.currentAdmin = currentAdmin;
        this.dbManager = dbManager;

        initUI();
        loadUsersToTable();
    }

    private void initUI() {
        setSize(840, 520);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        mainPanel = new JPanel(new BorderLayout(10, 12));
        mainPanel.setBorder(new EmptyBorder(14, 18, 14, 18));
        mainPanel.setBackground(UIHelper.getBgColor());

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIHelper.getHeaderBgColor());
        headerPanel.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel titleLabel = new JLabel("Cinematica Admin Console: User Management");
        titleLabel.setFont(UIHelper.FONT_HEADER);
        titleLabel.setForeground(Color.WHITE);

        JLabel adminBadge = new JLabel("Logged in as: " + currentAdmin.getUsername() + " (Admin)");
        adminBadge.setFont(UIHelper.FONT_SMALL);
        adminBadge.setForeground(new Color(200, 215, 230));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(adminBadge, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = {"User ID", "Username", "Role", "Privileges"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setRowHeight(32);
        userTable.setFont(UIHelper.FONT_REGULAR);
        userTable.setBackground(UIHelper.getTableBgColor());
        userTable.setForeground(UIHelper.getTableFgColor());
        userTable.setGridColor(UIHelper.getTableGridColor());
        userTable.setSelectionBackground(UIHelper.getTableSelectionBg());
        userTable.setSelectionForeground(UIHelper.getTableSelectionFg());
        userTable.getTableHeader().setFont(UIHelper.FONT_BOLD);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setShowGrid(true);

        userTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        userTable.getColumnModel().getColumn(0).setMaxWidth(90);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        userTable.getColumnModel().getColumn(3).setPreferredWidth(260);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        userTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        userTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        userTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(UIHelper.getTableHeaderBg());
                lbl.setForeground(UIHelper.getTableHeaderFg());
                lbl.setFont(UIHelper.FONT_BOLD);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, UIHelper.getTableGridColor()),
                        BorderFactory.createEmptyBorder(6, 8, 6, 8)
                ));
                return lbl;
            }
        });

        scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIHelper.getBorderColor(), 1));
        scrollPane.getViewport().setBackground(UIHelper.getTableBgColor());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomContainer = new JPanel(new BorderLayout(10, 8));
        bottomContainer.setOpaque(false);

        totalUsersLabel = new JLabel("Registered Accounts: 0");
        totalUsersLabel.setFont(UIHelper.FONT_BOLD);
        totalUsersLabel.setForeground(UIHelper.getTextColor());
        bottomContainer.add(totalUsersLabel, BorderLayout.WEST);

        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonGroup.setOpaque(false);

        JButton toggleRoleBtn = UIHelper.createCustomButton("Toggle Role (Viewer ↔ Admin)", UIHelper.COLOR_PRIMARY, Color.WHITE);
        toggleRoleBtn.addActionListener(e -> handleToggleRole());
        buttonGroup.add(toggleRoleBtn);

        JButton deleteBtn = UIHelper.createCustomButton("Delete User", UIHelper.COLOR_DANGER, Color.WHITE);
        deleteBtn.addActionListener(e -> handleDeleteUser());
        buttonGroup.add(deleteBtn);

        JButton closeBtn = UIHelper.createSecondaryButton("Close");
        closeBtn.addActionListener(e -> dispose());
        buttonGroup.add(closeBtn);

        bottomContainer.add(buttonGroup, BorderLayout.EAST);
        mainPanel.add(bottomContainer, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadUsersToTable() {
        tableModel.setRowCount(0);
        try {
            List<BaseUser> users = dbManager.getAllUsers();
            for (BaseUser u : users) {
                String permissions = u.canManageUsers()
                        ? "Full Access (Catalog + User Management)"
                        : "Standard Access (Catalog & Reviews)";

                String currentTag = (u.getUserId() == currentAdmin.getUserId()) ? " (You)" : "";

                tableModel.addRow(new Object[]{
                        u.getUserId(),
                        u.getUsername() + currentTag,
                        u.getRole(),
                        permissions
                });
            }
            totalUsersLabel.setText("Registered Accounts: " + users.size());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to fetch users: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleToggleRole() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user from the table first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentRole = (String) tableModel.getValueAt(selectedRow, 2);

        if (userId == currentAdmin.getUserId()) {
            JOptionPane.showMessageDialog(this,
                    "You cannot change your own administrator role while currently logged in.",
                    "Action Prohibited", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String newRole = "Admin".equalsIgnoreCase(currentRole) ? "Viewer" : "Admin";

        int confirm = JOptionPane.showConfirmDialog(this,
                "Change user role from '" + currentRole + "' to '" + newRole + "'?",
                "Confirm Role Change", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dbManager.updateUserRole(userId, newRole);
                JOptionPane.showMessageDialog(this,
                        "User role successfully updated to " + newRole + "!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsersToTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to update role: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user from the table to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);

        if (userId == currentAdmin.getUserId()) {
            JOptionPane.showMessageDialog(this,
                    "You cannot delete your own account while currently logged in.",
                    "Action Prohibited", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user '" + username + "'?\nThis will also remove all their associated reviews.",
                "Confirm Account Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dbManager.deleteUser(userId);
                JOptionPane.showMessageDialog(this,
                        "User account has been successfully deleted.",
                        "User Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadUsersToTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to delete user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
