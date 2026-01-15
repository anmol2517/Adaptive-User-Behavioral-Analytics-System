package com.auis.dao;

import com.auis.model.Anomaly;
import com.auis.util.DBConnection;
import com.auis.util.SystemLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;



/*

 *  AnomalyDAO - Manages anomaly detection records

 *  All queries use PreparedStatement (prevents SQL injection)

 *  Enhanced with severity scoring and top anomalies retrieval

*/


public class AnomalyDAO {

    public void recordAnomaly(Anomaly anomaly) {
        String query = "INSERT INTO anomalies (user_id, anomaly_type, action_count, time_window_seconds, severity_level, anomaly_score) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;


        try {
            conn = DBConnection.getConnection();

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, anomaly.getUserId());
                pstmt.setString(2, anomaly.getAnomalyType());
                pstmt.setInt(3, anomaly.getActionCount());
                pstmt.setInt(4, anomaly.getTimeWindowSeconds());
                pstmt.setString(5, anomaly.getSeverityLevel());


                int anomalyScore = calculateAnomalyScore(anomaly.getActionCount(), anomaly.getSeverityLevel());
                pstmt.setInt(6, anomalyScore);
                pstmt.executeUpdate();
                SystemLogger.warn("Anomaly recorded : " + anomaly.getAnomalyType() + " for user : " + anomaly.getUserId());
            }
        } catch (SQLException e) {
            SystemLogger.error("Failed to record anomaly : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
    }



    private int calculateAnomalyScore(int actionCount, String severityLevel) {
        int baseSeverity = switch(severityLevel) {
            case "LOW" -> 10;
            case "MEDIUM" -> 25;
            case "HIGH" -> 50;
            case "CRITICAL" -> 100;
            default -> 5;
        };
        return baseSeverity + (actionCount * 5);
    }


    public List<Anomaly> getRecentAnomalies(int userId, int limitMinutes) {
        List<Anomaly> anomalies = new ArrayList<>();
        String query = "SELECT anomaly_id, user_id, anomaly_type, action_count, time_window_seconds, severity_level, detected_at " +
                      "FROM anomalies WHERE user_id = ? AND detected_at >= DATE_SUB(NOW(), INTERVAL ? MINUTE) " +
                      "ORDER BY detected_at DESC";


        Connection conn = null;


        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                pstmt.setInt(2, limitMinutes);


                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Anomaly anomaly = new Anomaly(
                                rs.getInt("user_id"),
                                rs.getString("anomaly_type"),
                                rs.getInt("action_count"),
                                rs.getInt("time_window_seconds"),
                                rs.getString("severity_level")
                        );
                        anomaly.setAnomalyId(rs.getInt("anomaly_id"));
                        anomalies.add(anomaly);
                    }
                }
            }

        } catch (SQLException e) {
            SystemLogger.error("Failed to fetch anomalies : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return anomalies;
    }


    public List<Integer> getTopSuspiciousUsers(int limit) {
        List<Integer> suspiciousUsers = new ArrayList<>();
        String query = "SELECT user_id, COUNT(*) as anomaly_count FROM anomalies " +
                      "WHERE is_resolved = FALSE GROUP BY user_id ORDER BY anomaly_count DESC LIMIT ?";

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, limit);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        suspiciousUsers.add(rs.getInt("user_id"));
                    }
                }
            }


            SystemLogger.info("Retrieved top " + limit + " suspicious users");
        } catch (SQLException e) {
            SystemLogger.error("Failed to get suspicious users : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return suspiciousUsers;
    }

    public List<Anomaly> getCriticalAnomalies(int limit) {
        List<Anomaly> anomalies = new ArrayList<>();
        String query = "SELECT anomaly_id, user_id, anomaly_type, action_count, time_window_seconds, severity_level, detected_at " +
                      "FROM anomalies WHERE severity_level IN ('HIGH', 'CRITICAL') AND is_resolved = FALSE " +
                      "ORDER BY detected_at DESC LIMIT ?";
        Connection conn = null;


        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, limit);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Anomaly anomaly = new Anomaly(
                                rs.getInt("user_id"),
                                rs.getString("anomaly_type"),
                                rs.getInt("action_count"),
                                rs.getInt("time_window_seconds"),
                                rs.getString("severity_level")
                        );
                        anomaly.setAnomalyId(rs.getInt("anomaly_id"));
                        anomalies.add(anomaly);
                    }
                }
            }


        } catch (SQLException e) {
            SystemLogger.error("Failed to fetch critical anomalies : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return anomalies;
    }
}

