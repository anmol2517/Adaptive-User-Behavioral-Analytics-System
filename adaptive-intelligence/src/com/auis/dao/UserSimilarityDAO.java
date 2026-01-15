package com.auis.dao;

import com.auis.model.UserSimilarity;
import com.auis.util.DBConnection;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;


/*

  *  UserSimilarityDAO - Manages collaborative filtering recommendations
  *  All queries use PreparedStatement (prevents SQL injection)

*/


public class UserSimilarityDAO {

    public void recordSimilarity(UserSimilarity similarity) {
        String query = "INSERT INTO user_similarity (user_a, user_b, similarity_score, shared_actions) " +
                      "VALUES (?, ?, ?, ?) " +
                      "ON DUPLICATE KEY UPDATE similarity_score = ?, shared_actions = ?, last_calculated = NOW()";
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, similarity.getUserA());
                pstmt.setInt(2, similarity.getUserB());


                pstmt.setFloat(3, similarity.getSimilarityScore());
                pstmt.setInt(4, similarity.getSharedActions());
                pstmt.setFloat(5, similarity.getSimilarityScore());


                pstmt.setInt(6, similarity.getSharedActions());
                pstmt.executeUpdate();
            }


        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to record user similarity : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
    }


    public List<UserSimilarity> getSimilarUsers(int userId, float minSimilarityThreshold) {
        List<UserSimilarity> similarUsers = new ArrayList<>();
        String query = "SELECT similarity_id, user_a, user_b, similarity_score, shared_actions " +
                      "FROM user_similarity " +
                      "WHERE (user_a = ? OR user_b = ?) AND similarity_score >= ? " +
                      "ORDER BY similarity_score DESC";
        Connection conn = null;


        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                pstmt.setInt(2, userId);
                pstmt.setFloat(3, minSimilarityThreshold);


                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        UserSimilarity similarity = new UserSimilarity(
                                rs.getInt("user_a"),
                                rs.getInt("user_b"),
                                rs.getFloat("similarity_score"),
                                rs.getInt("shared_actions")
                        );
                        similarity.setSimilarityId(rs.getInt("similarity_id"));
                        similarUsers.add(similarity);
                    }
                }
            }


        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to fetch similar users : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return similarUsers;
    }
}


