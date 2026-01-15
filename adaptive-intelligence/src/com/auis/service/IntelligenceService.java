package com.auis.service;

import com.auis.dao.ActivityDAO;
import com.auis.dao.RuleDAO;
import com.auis.model.Rule;

import com.auis.model.UserActivity;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import java.util.*;


/*

  *  IntelligenceService - Core business logic for AI-inspired behavior tracking
  *  Enhanced with time-based pattern analysis and advanced intelligence

*/

public class IntelligenceService {
    private ActivityDAO activityDAO;
    private RuleDAO ruleDAO;

    public IntelligenceService() {
        this.activityDAO = new ActivityDAO();
        this.ruleDAO = new RuleDAO();
    }


    // Track user activity - Duplicate print removed (DAO handles the success message)


    public boolean trackUserActivity(int userId, String action) {
        UserActivity activity = new UserActivity(userId, action);

        // We only return result here; the [TRACKED] print is handled inside activityDAO.saveActivity

        return activityDAO.saveActivity(activity);
    }


    // Get peak activity hour with intelligent interpretation


    public String getPeakActivityHourAnalysis(int userId) {
        int peakHour = activityDAO.getPeakActivityHour(userId);
        if (peakHour == -1) {
            return "No activity data available";
        }

        String timeOfDay;
        if (peakHour >= 6 && peakHour < 12) {
            timeOfDay = "Morning (6 AM - 12 PM)";
        } else if (peakHour >= 12 && peakHour < 18) {
            timeOfDay = "Afternoon (12 PM - 6 PM)";
        } else if (peakHour >= 18 && peakHour < 24) {
            timeOfDay = "Evening (6 PM - 12 AM)";
        } else {
            timeOfDay = "Night (12 AM - 6 AM)";
        }

        int peakCount = activityDAO.getActivityCountByHour(userId, peakHour);
        return String.format("Peak Activity: %s (Hour %d with %d actions)", timeOfDay, peakHour, peakCount);
    }

    public String getMostFrequentAction(int userId) {
        return activityDAO.getMostFrequentAction(userId);
    }

    public Map<String, Integer> getActionFrequencyMap(int userId) {
        return activityDAO.getActionFrequency(userId);
    }


    public String evaluateRules(int userId) {
        int totalActivities = activityDAO.getTotalActivityCount(userId);


        if (totalActivities == 0) {
            return "\n========== AI INTELLIGENCE SUITE ==========\n" +
                    "[SYSTEM] No behavioral patterns detected yet.\n" +
                    "💡 TIP: Perform some actions (e.g., Search, Browse, Gaming)\n" +
                    "so the AI can learn your habits and suggest patterns!\n" +
                    "==========================================\n";
        }

        String mostFrequentAction = getMostFrequentAction(userId);
        Timestamp lastActivity = activityDAO.getLastActivityTime(userId);
        int peakHour = activityDAO.getPeakActivityHour(userId);

        StringBuilder suggestion = new StringBuilder();
        suggestion.append("\n=== ADVANCED INTELLIGENCE REPORT ===\n");

        if (mostFrequentAction != null && totalActivities >= 3) {
            Rule repeatRule = ruleDAO.getRuleByCondition("REPEAT_ACTION");


            if (repeatRule != null) {
                suggestion.append("Pattern Detected: REPEAT_ACTION\n");
                suggestion.append("Most Frequent: ").append(mostFrequentAction).append(" (").append(
                        activityDAO.getActionFrequency(userId).get(mostFrequentAction)).append(" times)\n");
                suggestion.append("Suggestion: ").append(repeatRule.getSuggestionText()).append("\n");
                suggestion.append("Time-Based Insight: ").append(getPeakActivityHourAnalysis(userId)).append("\n");
            }
        }

        if (lastActivity != null) {
            LocalDateTime lastTime = lastActivity.toLocalDateTime();
            LocalDateTime now = LocalDateTime.now();
            long minutesSinceLastActivity = java.time.temporal.ChronoUnit.MINUTES.between(lastTime, now);

            if (minutesSinceLastActivity > 30) {
                Rule inactiveRule = ruleDAO.getRuleByCondition("INACTIVE_USER");


                if (inactiveRule != null) {
                    suggestion.append("Pattern Detected: INACTIVE_USER\n");
                    suggestion.append("Last Activity: ").append(minutesSinceLastActivity).append(" minutes ago\n");
                    suggestion.append("Suggestion: ").append(inactiveRule.getSuggestionText()).append("\n");
                    if (peakHour != -1) {
                        suggestion.append("Re-engagement Tip: User is most active around ").append(peakHour).append(":00. Try engaging then!\n");
                    }
                }
            }
        }

        if (totalActivities >= 5) {
            Rule highFreqRule = ruleDAO.getRuleByCondition("HIGH_FREQUENCY");
            if (highFreqRule != null) {
                suggestion.append("Pattern Detected: HIGH_FREQUENCY\n");


                suggestion.append("Total Actions: ").append(totalActivities).append("\n");
                suggestion.append("Suggestion: ").append(highFreqRule.getSuggestionText()).append("\n");
                Map<String, Integer> trend = activityDAO.getActivityTrendLastNHours(userId, 24);
                if (!trend.isEmpty()) {
                    suggestion.append("Trend (Last 24h): ").append(trend.size()).append(" active hours\n");
                }
            }
        }

        if (peakHour != -1) {
            suggestion.append("Activity Distribution:\n");
            suggestion.append("  Peak Hour: ").append(peakHour).append(":00\n");
            suggestion.append("  Peak Insight: ").append(getPeakActivityHourAnalysis(userId)).append("\n");
        }

        suggestion.append("===================================\n");
        return suggestion.toString();
    }

    public String generateSuggestion(int userId) {
        return evaluateRules(userId);
    }

    public String getActivityReport(int userId) {
        int totalCount = activityDAO.getTotalActivityCount(userId);


        if (totalCount == 0) {
            return "\n[REPORT] No activities found for this user yet.\n";
        }

        List<UserActivity> activities = activityDAO.getAllActivities(userId);
        Map<String, Integer> frequency = activityDAO.getActionFrequency(userId);
        String peakAnalysis = getPeakActivityHourAnalysis(userId);

        StringBuilder report = new StringBuilder();
        report.append("\n=== ADVANCED ACTIVITY REPORT ===\n");
        report.append("Total Activities: ").append(totalCount).append("\n");
        report.append("Unique Actions: ").append(frequency.size()).append("\n");
        report.append("Peak Activity: ").append(peakAnalysis).append("\n");
        report.append("\nAction Breakdown:\n");

        frequency.forEach((action, count) -> {
            double percentage = (count * 100.0) / totalCount;
            report.append(String.format("  - %s: %d times (%.1f%%)\n", action, count, percentage));
        });

        report.append("\nRecent Activities:\n");
        activities.stream().limit(5).forEach(activity -> {
            report.append("  - ").append(activity.getAction()).append(" at ").append(activity.getActivityTime()).append("\n");
        });

        if (!activities.isEmpty()) {
            report.append("\nLast Activity: ").append(activities.get(0).getActivityTime()).append("\n");
        }

        report.append("=================================\n");
        return report.toString();
    }
}


