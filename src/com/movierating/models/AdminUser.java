package com.movierating.models;

public class AdminUser extends BaseUser {

    public AdminUser(String username, String password) {
        super(username, password, "Admin");
    }

    public AdminUser(int userId, String username, String password) {
        super(userId, username, password, "Admin");
    }

    @Override
    public String getRoleDescription() {
        return "System Administrator (Full access: Browse, Add Movies, Review, and Manage Users)";
    }

    @Override
    public boolean canManageUsers() {
        return true;
    }

    @Override
    public String getDashboardGreeting() {
        return "Welcome, Administrator " + getUsername() + " [Full Access + User Management]";
    }
}
