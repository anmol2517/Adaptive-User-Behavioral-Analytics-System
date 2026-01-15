package com.auis.dao;

import com.auis.model.UserActivity;
import com.auis.util.DBConnection;
import java.sql.*;

import java.time.*;
import java.util.*;


/*

  *   ActivityDAO - Data Access Object for User Activity operations
  *   Enhanced with Transaction Handling and Time-based Analysis
  *   All queries use PreparedStatement (prevents SQL injection)
  *   Transaction integrity with commit/rollback

*/


public class ActivityDAO {


    /*
        *  Save user activity with TRANSACTION support
        *  Ensures atomic operation - either all succeed or all rollback
    */


    public boolean saveActivity(UserActivity activity) {
        String sql = "INSERT INTO user_activity (user_id, action) VALUES (?, ?)";
        Connection conn = null;


        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, activity.getUserId());
                pstmt.setString(2, activity.getAction());
                int rowsInserted = pstmt.executeUpdate();
                
                if (rowsInserted > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("[ERROR] Transaction failed, rolled back : " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to save activity : " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    DBConnection.returnConnection(conn);
                } catch (SQLException e) {
                    System.err.println("[ERROR] Failed to reset connection : " + e.getMessage());
                }
            }
        }
    }



     //  Get action frequency for a user (count of each action)



    public Map<String, Integer> getActionFrequency(int userId) {
        String sql = "SELECT action, COUNT(*) as frequency FROM user_activity WHERE user_id = ? GROUP BY action ORDER BY frequency DESC";
        Map<String, Integer> frequencyMap = new LinkedHashMap<>();
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        frequencyMap.put(rs.getString("action"), rs.getInt("frequency"));
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get action frequency : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return frequencyMap;
    }


     //   Get peak activity hour for a user (when most active)

     //   Returns hour of day (0-23) when user is most active


    public int getPeakActivityHour(int userId) {
        String sql = "SELECT HOUR(activity_time) as hour, COUNT(*) as count FROM user_activity WHERE user_id = ? GROUP BY HOUR(activity_time) ORDER BY count DESC LIMIT 1";
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("hour");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get peak activity hour : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return -1;
    }


     //   Get activity count for specific hour of day

     //   Used to analyze time-based patterns


    public int getActivityCountByHour(int userId, int hour) {
        String sql = "SELECT COUNT(*) as count FROM user_activity WHERE user_id = ? AND HOUR(activity_time) = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                pstmt.setInt(2, hour);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("count");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get activity count by hour: " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return 0;
    }



     //   Get activity trend (last 24 hours, 7 days, etc.)


    public Map<String, Integer> getActivityTrendLastNHours(int userId, int hours) {
        String sql = "SELECT DATE_FORMAT(activity_time, '%Y-%m-%d %H:00') as hour_slot, COUNT(*) as count FROM user_activity WHERE user_id = ? AND activity_time >= DATE_SUB(NOW(), INTERVAL ? HOUR) GROUP BY DATE_FORMAT(activity_time, '%Y-%m-%d %H:00') ORDER BY hour_slot DESC";

        Map<String, Integer> trend = new LinkedHashMap<>();
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                pstmt.setInt(2, hours);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        trend.put(rs.getString("hour_slot"), rs.getInt("count"));
                    }
                }
            }


        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get activity trend : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return trend;
    }



     //  Get most frequent action for a user



    public String getMostFrequentAction(int userId) {
        String sql = "SELECT action, COUNT(*) as frequency FROM user_activity WHERE user_id = ? GROUP BY action ORDER BY frequency DESC LIMIT 1";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("action");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get most frequent action : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return null;
    }



     //   Get last activity time for a user


    public Timestamp getLastActivityTime(int userId) {
        String sql = "SELECT activity_time FROM user_activity WHERE user_id = ? ORDER BY activity_time DESC LIMIT 1";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getTimestamp("activity_time");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get last activity time : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return null;
    }



     //   Get total activity count for a user


    public int getTotalActivityCount(int userId) {
        String sql = "SELECT COUNT(*) as total FROM user_activity WHERE user_id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("total");
                    }
                }
            }


        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get activity count : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return 0;
    }



     //  Get all activities for a user



    public List<UserActivity> getAllActivities(int userId) {
        String sql = "SELECT * FROM user_activity WHERE user_id = ? ORDER BY activity_time DESC";
        List<UserActivity> activities = new ArrayList<>();
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        activities.add(new UserActivity(
                                rs.getInt("activity_id"),
                                rs.getInt("user_id"),
                                rs.getString("action"),
                                rs.getTimestamp("activity_time").toLocalDateTime()
                        ));
                    }
                }
            }


        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get all activities : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return activities;
    }



     //   Get all activities for a user (with missing getUserActivities method)



    public List<UserActivity> getUserActivities(int userId) {
        String sql = "SELECT * FROM user_activity WHERE user_id = ? ORDER BY activity_time DESC";
        List<UserActivity> activities = new ArrayList<>();
        Connection conn = null;


        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        activities.add(new UserActivity(
                                rs.getInt("activity_id"),
                                rs.getInt("user_id"),
                                rs.getString("action"),
                                rs.getTimestamp("activity_time").toLocalDateTime()
                        ));
                    }
                }
            }


        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get user activities : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return activities;
    }
}


