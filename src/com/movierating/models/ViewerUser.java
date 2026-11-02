package com.movierating.models;

public class ViewerUser extends BaseUser {

    public ViewerUser(String username, String password) {
        super(username, password, "Viewer");
    }

    public ViewerUser(int userId, String username, String password) {
        super(userId, username, password, "Viewer");
    }

    @Override
    public String getRoleDescription() {
        return "Standard User (Can browse catalog, add new movies, and submit reviews)";
    }

    @Override
    public boolean canManageUsers() {
        return false;
    }

    @Override
    public String getDashboardGreeting() {
        return "Welcome, " + getUsername() + "! Discover, add, and review movies.";
    }
}
