package com.auis.dao;

import com.auis.model.Rule;
import com.auis.util.DBConnection;
import java.sql.*;
import java.util.*;


/*

 * RuleDAO - Data Access Object for Rule operations

 * Enhanced with proper try-with-resources for all ResultSets

*/

public class RuleDAO {


     //  Get rule by condition text & Try-with-resources for ResultSet


    public Rule getRuleByCondition(String condition) {
        String sql = "SELECT * FROM ai_rules WHERE condition_text = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, condition);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Rule(
                            rs.getInt("rule_id"),
                            rs.getString("condition_text"),
                            rs.getString("suggestion_text")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get rule by condition : " + e.getMessage());
        }
        return null;
    }


     //  Get all rules from database & Try-with-resources for both Statement and ResultSet


    public List<Rule> getAllRules() {
        String sql = "SELECT * FROM ai_rules";
        List<Rule> rules = new ArrayList<>();


        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {


            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    rules.add(new Rule(
                            rs.getInt("rule_id"),
                            rs.getString("condition_text"),
                            rs.getString("suggestion_text")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get all rules : " + e.getMessage());
        }
        return rules;
    }


     //  Add new rule to database


    public boolean addRule(Rule rule) {
        String sql = "INSERT INTO ai_rules (condition_text, suggestion_text) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rule.getConditionText());
            pstmt.setString(2, rule.getSuggestionText());
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;


        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to add rule : " + e.getMessage());
            return false;
        }
    }
}

