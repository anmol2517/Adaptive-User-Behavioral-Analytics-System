package com.auis.model;

import java.time.LocalDateTime;



 //  User Model Class - Updated for Security Features



public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private LocalDateTime createdAt;


    // Constructors


    public User(int userId, String username, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.createdAt = createdAt;
    }

    public User(String username) {
        this.username = username;
    }


    // --- Getters and Setters ---


    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{" + "userId=" + userId + ", username='" + username + '\'' + '}';
    }
}

