package com.auis.dao;

import com.auis.model.User;
import com.auis.util.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/*

 *  UserDAO - Data Access Object for User operations

 *  All queries use PreparedStatement (prevents SQL injection)

*/


public class UserDAO {


     //  Add a new user to database


    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username) VALUES (?)";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, user.getUsername());
                int rowsInserted = pstmt.executeUpdate();
                return rowsInserted > 0;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to add user: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
    }


     //  Get user by username & Try-with-resources for ResultSet


    public User getUserByName(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        Connection conn = null;


        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new User(
                                rs.getInt("user_id"),
                                rs.getString("username"),
                                rs.getTimestamp("created_at").toLocalDateTime()
                        );
                    }
                }
            }


        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get user by name: " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }


        return null;
    }


    //  Get user by ID & Try-with-resources for ResultSet


    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        Connection conn = null;


        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new User(
                                rs.getInt("user_id"),
                                rs.getString("username"),
                                rs.getTimestamp("created_at").toLocalDateTime()
                        );
                    }
                }
            }


        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get user by ID: " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return null;
    }


     //  Get all users (for collaborative filtering and pattern analysis)


    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        users.add(new User(
                                rs.getInt("user_id"),
                                rs.getString("username"),
                                rs.getTimestamp("created_at").toLocalDateTime()
                        ));
                    }
                }
            }


        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get all users: " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return users;
    }

    

     //  Update user password (hashed)


    public boolean updateUserPassword(int userId, String passwordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, passwordHash);
                pstmt.setInt(2, userId);
                int result = pstmt.executeUpdate();
                return result > 0;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to update password : " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
    }


     //  Verify user password


    public boolean verifyPassword(int userId, String passwordHash) {
        String sql = "SELECT password_hash FROM users WHERE user_id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String storedHash = rs.getString("password_hash");
                        return storedHash != null && storedHash.equals(passwordHash);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to verify password : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return false;
    }


     //  Lock user account after max failed attempts


    public boolean lockUserAccount(int userId, long lockDurationMinutes) {
        String sql = "UPDATE users SET is_locked = TRUE, locked_until = DATE_ADD(NOW(), INTERVAL ? MINUTE), failed_attempts = 0 WHERE user_id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, lockDurationMinutes);
                pstmt.setInt(2, userId);
                int result = pstmt.executeUpdate();
                System.out.println("[SECURITY] User " + userId + " account locked for " + lockDurationMinutes + " minutes");
                return result > 0;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to lock account : " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
    }


     //  Unlock user account


    public boolean unlockUserAccount(int userId) {
        String sql = "UPDATE users SET is_locked = FALSE, locked_until = NULL WHERE user_id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                int result = pstmt.executeUpdate();
                System.out.println("[SECURITY] User " + userId + " account unlocked");
                return result > 0;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to unlock account : " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
    }


     //  Check if account is locked and auto-unlock if lock period expired


    public boolean isAccountLocked(int userId) {
        String sql = "SELECT is_locked, locked_until FROM users WHERE user_id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        boolean isLocked = rs.getBoolean("is_locked");
                        Timestamp lockedUntil = rs.getTimestamp("locked_until");
                        
                        if (isLocked && lockedUntil != null) {
                            LocalDateTime lockEndTime = lockedUntil.toLocalDateTime();
                            if (LocalDateTime.now().isAfter(lockEndTime)) {
                                unlockUserAccount(userId);
                                return false;
                            }
                        }
                        return isLocked;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to check account lock : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return false;
    }


     //  Increment failed login attempts


    public int incrementFailedAttempts(int userId) {
        String sql = "UPDATE users SET failed_attempts = failed_attempts + 1 WHERE user_id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
                

                // Check current failed attempts


                return getFailedAttempts(userId);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to increment attempts : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return -1;
    }


     //  Reset failed attempts on successful login


    public boolean resetFailedAttempts(int userId) {
        String sql = "UPDATE users SET failed_attempts = 0 WHERE user_id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to reset attempts : " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
    }


     //  Get current failed attempts count


    public int getFailedAttempts(int userId) {
        String sql = "SELECT failed_attempts FROM users WHERE user_id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("failed_attempts");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get failed attempts : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return 0;
    }
}


