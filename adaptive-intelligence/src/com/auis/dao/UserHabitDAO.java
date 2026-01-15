package com.auis.dao;

import com.auis.model.UserHabit;
import com.auis.util.DBConnection;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;


/*

 * UserHabitDAO - Manages time-series user behavioral patterns
 * All queries use PreparedStatement (prevents SQL injection)

*/


public class UserHabitDAO {

    public void recordHabit(UserHabit habit) {
        String query = "INSERT INTO user_habits (user_id, peak_hour, preferred_action, frequency_score) " +
                      "VALUES (?, ?, ?, ?) " +
                      "ON DUPLICATE KEY UPDATE frequency_score = frequency_score + ?, last_detected = NOW()";
        Connection conn = null;


        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, habit.getUserId());
                pstmt.setInt(2, habit.getPeakHour());


                pstmt.setString(3, habit.getPreferredAction());
                pstmt.setInt(4, habit.getFrequencyScore());
                pstmt.setInt(5, habit.getFrequencyScore());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to record user habit : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
    }

    public UserHabit getUserPeakHourHabit(int userId) {
        String query = "SELECT habit_id, user_id, peak_hour, preferred_action, frequency_score " +
                      "FROM user_habits WHERE user_id = ? ORDER BY frequency_score DESC LIMIT 1";
        Connection conn = null;


        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        UserHabit habit = new UserHabit(
                                rs.getInt("user_id"),
                                rs.getInt("peak_hour"),
                                rs.getString("preferred_action"),
                                rs.getInt("frequency_score")
                        );
                        habit.setHabitId(rs.getInt("habit_id"));
                        return habit;
                    }
                }
            }


        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to fetch user peak hour habit : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return null;
    }


    public List<UserHabit> getAllUserHabits(int userId) {
        List<UserHabit> habits = new ArrayList<>();
        String query = "SELECT habit_id, user_id, peak_hour, preferred_action, frequency_score " +
                      "FROM user_habits WHERE user_id = ? ORDER BY frequency_score DESC";
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();


            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        UserHabit habit = new UserHabit(
                                rs.getInt("user_id"),
                                rs.getInt("peak_hour"),
                                rs.getString("preferred_action"),
                                rs.getInt("frequency_score")
                        );
                        habit.setHabitId(rs.getInt("habit_id"));
                        habits.add(habit);
                    }
                }
            }


        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to fetch user habits : " + e.getMessage());
        } finally {
            if (conn != null) {
                DBConnection.returnConnection(conn);
            }
        }
        return habits;
    }
}


