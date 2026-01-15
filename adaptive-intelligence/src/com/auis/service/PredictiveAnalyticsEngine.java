package com.auis.service;

import com.auis.dao.*;
import com.auis.model.*;
import java.time.LocalDateTime;
import java.util.*;

public class PredictiveAnalyticsEngine {
    private ActionSequenceDAO sequenceDAO;
    private AnomalyDAO anomalyDAO;
    private UserHabitDAO habitDAO;


    private UserSimilarityDAO similarityDAO;
    private ActivityDAO activityDAO;
    private UserDAO userDAO;

    public PredictiveAnalyticsEngine() {
        this.sequenceDAO = new ActionSequenceDAO();
        this.anomalyDAO = new AnomalyDAO();
        this.habitDAO = new UserHabitDAO();
        this.similarityDAO = new UserSimilarityDAO();
        this.activityDAO = new ActivityDAO();
        this.userDAO = new UserDAO();
    }


    // ==================== PHASE 1: PREDICTION ====================



    public String predictNextAction(String currentAction) {
        ActionSequence prediction = sequenceDAO.getNextActionPrediction(currentAction);
        if (prediction != null && prediction.getConfidenceScore() > 0.6) {
            return String.format("[AI INSIGHT] You might want to: %s (%.0f%% users do this)",
                    prediction.getNextAction(), prediction.getConfidenceScore() * 100);
        }
        return null;
    }

    public void recordActionForLearning(String currentAction, String nextAction) {
        sequenceDAO.recordActionSequence(currentAction, nextAction);
    }


    // ==================== PHASE 1 : ANOMALY DETECTION ====================


    public void detectBotlikeActivity(int userId, List<UserActivity> recentActivities) {
        if (recentActivities == null || recentActivities.size() < 10) return;

        recentActivities.sort(Comparator.comparing(UserActivity::getActivityTime));
        LocalDateTime firstAction = recentActivities.get(0).getActivityTime();
        LocalDateTime lastAction = recentActivities.get(Math.min(9, recentActivities.size() - 1)).getActivityTime();

        long secondsElapsed = Math.abs(java.time.temporal.ChronoUnit.SECONDS.between(firstAction, lastAction));

        if (secondsElapsed < 10 && secondsElapsed > 0) {
            Anomaly anomaly = new Anomaly(userId, "BOT_LIKE_ACTIVITY", 10, 10, "HIGH");
            anomalyDAO.recordAnomaly(anomaly);
            System.out.println("[SECURITY ALERT] Rapid behavior detected for User ID: " + userId);
        }
    }


    // ==================== PHASE 1 : TIME-SERIES HABITS ====================


    public void analyzeUserHabits(int userId) {
        List<UserActivity> allActivities = activityDAO.getUserActivities(userId);
        if (allActivities == null || allActivities.isEmpty()) return;

        Map<Integer, Map<String, Integer>> hourlyActionCounts = new HashMap<>();
        for (UserActivity activity : allActivities) {
            int hour = activity.getActivityTime().getHour();
            String action = activity.getAction();
            hourlyActionCounts.computeIfAbsent(hour, k -> new HashMap<>()).merge(action, 1, Integer::sum);
        }

        int peakHour = hourlyActionCounts.entrySet().stream()
                .max(Comparator.comparingInt(e -> e.getValue().values().stream().mapToInt(Integer::intValue).sum()))
                .map(Map.Entry::getKey)
                .orElse(-1);

        if (peakHour != -1) {
            String preferredAction = hourlyActionCounts.get(peakHour).entrySet().stream()
                    .max(Comparator.comparingInt(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .orElse("UNKNOWN");

            UserHabit habit = new UserHabit(userId, peakHour, preferredAction, 1);
            habitDAO.recordHabit(habit);
        }
    }

    public String getHabitBasedSuggestion(int userId) {
        UserHabit habit = habitDAO.getUserPeakHourHabit(userId);

        if (habit == null) {
            return "[AI] I'm still learning your patterns. Keep using the system for habit tips!";
        }

        int currentHour = LocalDateTime.now().getHour();
        if (currentHour == habit.getPeakHour()) {
            return String.format("It's your peak hour! Consider: %s (your usual action at %d:00)",
                    habit.getPreferredAction(), habit.getPeakHour());
        } else if (currentHour > habit.getPeakHour()) {
            return String.format("[AI REMINDER] You typically do %s at %d:00. Want to do it now?",
                    habit.getPreferredAction(), habit.getPeakHour());
        }
        return null;
    }


    // ==================== PHASE 1: COLLABORATIVE FILTERING ====================


    public float calculateUserSimilarity(int userA, int userB) {
        List<UserActivity> activitiesA = activityDAO.getUserActivities(userA);
        List<UserActivity> activitiesB = activityDAO.getUserActivities(userB);

        if (activitiesA == null || activitiesB == null || activitiesA.isEmpty() || activitiesB.isEmpty()) return 0.0f;

        Set<String> actionsA = new HashSet<>();
        for (UserActivity ua : activitiesA) actionsA.add(ua.getAction());
        Set<String> actionsB = new HashSet<>();
        for (UserActivity ub : activitiesB) actionsB.add(ub.getAction());

        Set<String> intersection = new HashSet<>(actionsA);
        intersection.retainAll(actionsB);
        Set<String> union = new HashSet<>(actionsA);
        union.addAll(actionsB);

        if (union.isEmpty()) return 0.0f;
        float jaccardSimilarity = (float) intersection.size() / (float) union.size();

        similarityDAO.recordSimilarity(new UserSimilarity(userA, userB, jaccardSimilarity, intersection.size()));
        return jaccardSimilarity;
    }

    public List<String> getCollaborativeRecommendations(int userId) {
        List<UserSimilarity> similarUsers = similarityDAO.getSimilarUsers(userId, 0.7f);
        if (similarUsers == null || similarUsers.isEmpty()) return null;

        List<String> recommendations = new ArrayList<>();
        for (UserSimilarity sim : similarUsers) {
            int similarUserId = (sim.getUserA() == userId) ? sim.getUserB() : sim.getUserA();
            List<UserActivity> similarUserActivities = activityDAO.getUserActivities(similarUserId);
            List<UserActivity> currentUserActivities = activityDAO.getUserActivities(userId);

            Set<String> currentUserActions = new HashSet<>();
            for (UserActivity ua : currentUserActivities) currentUserActions.add(ua.getAction());

            for (UserActivity ua : similarUserActivities) {
                if (!currentUserActions.contains(ua.getAction())) {
                    recommendations.add(ua.getAction());
                }
            }
        }
        return recommendations.isEmpty() ? null : recommendations;
    }
}

