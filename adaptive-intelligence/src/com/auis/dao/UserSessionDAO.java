package com.auis.dao;

import com.auis.model.UserSession;
import com.auis.util.DBConnection;

import com.auis.util.SystemLogger;
import java.sql.*;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;


  //  Handles session tracking - when users login/logout and duration


public class UserSessionDAO {


     //  Create new session when user logs in


    public boolean createSession(UserSession session) {
        String sql = "INSERT INTO user_sessions (session_id, user_id, session_start_time, total_actions) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, session.getSessionId());

                pstmt.setInt(2, session.getUserId());
                pstmt.setTimestamp(3, Timestamp.valueOf(session.getSessionStartTime()));
                pstmt.setInt(4, 0);
                
                int result = pstmt.executeUpdate();
                SystemLogger.info("Session created : " + session.getSessionId() + " for user : " + session.getUserId());
                return result > 0;
            }


        } catch (SQLException e) {
            SystemLogger.error("Failed to create session : " + e.getMessage());
            return false;
        } finally {
            if (conn != null) DBConnection.returnConnection(conn);
        }
    }


     //  End session when user logs out


    public boolean endSession(String sessionId) {
        String sql = "UPDATE user_sessions SET session_end_time = ?, session_duration_minutes = TIMESTAMPDIFF(MINUTE, session_start_time, NOW()) WHERE session_id = ?";
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setString(2, sessionId);
                
                int result = pstmt.executeUpdate();
                SystemLogger.info("Session ended : " + sessionId);
                return result > 0;
            }
        } catch (SQLException e) {
            SystemLogger.error("Failed to end session : " + e.getMessage());
            return false;
        } finally {
            if (conn != null) DBConnection.returnConnection(conn);
        }
    }



     //  Increment action count for session


    public boolean incrementActionCount(String sessionId) {
        String sql = "UPDATE user_sessions SET total_actions = total_actions + 1 WHERE session_id = ?";
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, sessionId);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            SystemLogger.error("Failed to increment action count : " + e.getMessage());
            return false;
        } finally {
            if (conn != null) DBConnection.returnConnection(conn);
        }
    }



     //  Get average session duration for a user (FEATURE #3: User Habit Analysis)


    public int getAverageSessionDuration(int userId) {
        String sql = "SELECT AVG(session_duration_minutes) as avg_duration FROM user_sessions WHERE user_id = ? AND session_end_time IS NOT NULL";
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("avg_duration");
                    }
                }
            }
        } catch (SQLException e) {
            SystemLogger.error("Failed to get average session duration : " + e.getMessage());
        } finally {
            if (conn != null) DBConnection.returnConnection(conn);
        }
        return 0;
    }


     //  Get session by ID


    public UserSession getSession(String sessionId) {
        String sql = "SELECT * FROM user_sessions WHERE session_id = ?";
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, sessionId);


                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        UserSession session = new UserSession(rs.getString("session_id"), rs.getInt("user_id"));
                        session.setSessionStartTime(rs.getTimestamp("session_start_time").toLocalDateTime());
                        if (rs.getTimestamp("session_end_time") != null) {
                            session.setSessionEndTime(rs.getTimestamp("session_end_time").toLocalDateTime());
                        }


                        session.setTotalActions(rs.getInt("total_actions"));
                        return session;
                    }
                }
            }


        } catch (SQLException e) {
            SystemLogger.error("Failed to get session : " + e.getMessage());
        } finally {
            if (conn != null) DBConnection.returnConnection(conn);
        }
        return null;
    }
}


