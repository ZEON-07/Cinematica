package com.movierating.models;

import com.movierating.exceptions.EmptyFieldException;

public abstract class BaseUser {

    private int userId;
    private String username;
    private String password;
    private String role;

    public BaseUser() {
    }

    public BaseUser(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public BaseUser(int userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) throws EmptyFieldException {
        if (username == null || username.trim().isEmpty()) {
            throw new EmptyFieldException("Username cannot be empty.");
        }
        this.username = username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws EmptyFieldException {
        if (password == null || password.trim().isEmpty()) {
            throw new EmptyFieldException("Password cannot be empty.");
        }
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean canAddMovies() {
        return true;
    }

    public abstract String getRoleDescription();

    public abstract boolean canManageUsers();

    public abstract String getDashboardGreeting();

    @Override
    public String toString() {
        return "User [ID=" + userId + ", Username=" + username + ", Role=" + role + "]";
    }
}
