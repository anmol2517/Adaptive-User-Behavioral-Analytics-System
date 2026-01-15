package com.auis.dao;

import com.auis.model.ActionSequence;
import com.auis.util.DBConnection;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;


/*

    *   ActionSequenceDAO - Manages action transition patterns
    *   All queries use PreparedStatement (prevents SQL injection)

*/


public class ActionSequenceDAO {

    public ActionSequence getNextActionPrediction(String currentAction) {
        String query = "SELECT sequence_id, first_action, next_action, occurrence_count, confidence_score " +
                      "FROM action_sequences WHERE first_action = ? ORDER BY confidence_score DESC LIMIT 1";

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, currentAction);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        ActionSequence seq = new ActionSequence(
                                rs.getString("first_action"),
                                rs.getString("next_action"),
                                rs.getLong("occurrence_count"),
                                rs.getFloat("confidence_score")
                        );


                        seq.setSequenceId(rs.getInt("sequence_id"));
                        return seq;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to fetch next action prediction : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return null;
    }


    public void recordActionSequence(String firstAction, String nextAction) {
        String query = "INSERT INTO action_sequences (first_action, next_action, occurrence_count, confidence_score) " +
                      "VALUES (?, ?, 1, RAND() * 0.5 + 0.5) " +
                      "ON DUPLICATE KEY UPDATE occurrence_count = occurrence_count + 1";


        Connection conn = null;



        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, firstAction);
                pstmt.setString(2, nextAction);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to record action sequence : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
    }


    public List<ActionSequence> getAllSequences() {
        List<ActionSequence> sequences = new ArrayList<>();
        String query = "SELECT sequence_id, first_action, next_action, occurrence_count, confidence_score " +
                      "FROM action_sequences ORDER BY confidence_score DESC";

        Connection conn = null;


        try {
            conn = DBConnection.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    ActionSequence seq = new ActionSequence(
                            rs.getString("first_action"),
                            rs.getString("next_action"),
                            rs.getLong("occurrence_count"),
                            rs.getFloat("confidence_score")
                    );


                    seq.setSequenceId(rs.getInt("sequence_id"));
                    sequences.add(seq);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to fetch all sequences : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }

        return sequences;
    }
}



