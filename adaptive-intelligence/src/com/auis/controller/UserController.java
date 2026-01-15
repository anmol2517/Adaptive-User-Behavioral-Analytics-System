package com.auis.controller;

import com.auis.dao.UserDAO;
import com.auis.model.User;

import com.auis.service.IntelligenceService;
import com.auis.service.PredictiveAnalyticsEngine;

import com.auis.util.SessionManager;
import com.auis.util.SystemLogger;
import java.util.*;


public class UserController {
    private UserDAO userDAO;
    private IntelligenceService intelligenceService;

    private PredictiveAnalyticsEngine analyticsEngine;
    private int currentUserId = -1;


    public UserController() {
        this.userDAO = new UserDAO();

        this.intelligenceService = new IntelligenceService();
        this.analyticsEngine = new PredictiveAnalyticsEngine();
    }


    public boolean loginUser(String username, String password) {
        try {
            User existingUser = userDAO.getUserByName(username);
            if (existingUser != null) {


                if (userDAO.isAccountLocked(existingUser.getUserId())) {
                    System.out.println("[ERROR] Account is locked. Try again after 30 minutes.");
                    return false;
                }


                String hashedPassword = com.auis.util.SecurityManager.hashPassword(password);


                if (!userDAO.verifyPassword(existingUser.getUserId(), hashedPassword)) {
                    int attempts = userDAO.incrementFailedAttempts(existingUser.getUserId());


                    if (attempts >= 3) {
                        userDAO.lockUserAccount(existingUser.getUserId(), 30);
                        System.out.println("[SECURITY] Account locked due to 3 failed login attempts!");
                    }


                    return false;
                }


                userDAO.resetFailedAttempts(existingUser.getUserId());
                this.currentUserId = existingUser.getUserId();
                SessionManager.createSession(currentUserId, username);
                return true;
            } else {
                return handleNewUserRegistration(username, password);
            }
        } catch (Exception e) { return false; }
    }



    private boolean handleNewUserRegistration(String username, String password) {
        User newUser = new User(username);
        if (userDAO.addUser(newUser)) {
            User createdUser = userDAO.getUserByName(username);


            if (createdUser != null) {
                String hashedPassword = com.auis.util.SecurityManager.hashPassword(password);
                userDAO.updateUserPassword(createdUser.getUserId(), hashedPassword);
                this.currentUserId = createdUser.getUserId();
                SessionManager.createSession(currentUserId, username);
                System.out.println("[SIGNUP] New user registered : " + username + "!");
                return true;
            }
        }

        return false;
    }


    public boolean recordUserAction(String action) {
        if (currentUserId == -1) return false;
        long startTime = System.currentTimeMillis();
        boolean result = intelligenceService.trackUserActivity(currentUserId, action);
        if (result) {
            SessionManager.recordAction(currentUserId);
            String prediction = analyticsEngine.predictNextAction(action);
            if (prediction != null) {
                System.out.println("\n[AI INSIGHT] " + prediction);
            }
            System.out.println("[TRACKED] Action '" + action + "' recorded for user ID : " + currentUserId);
        }
        return result;
    }



    // --- Suggestions And Reports

    public void showSuggestion() {
        if (currentUserId == -1) return;
        System.out.println("\n========== AI INTELLIGENCE SUITE ==========");
        System.out.println(intelligenceService.generateSuggestion(currentUserId));
        String habit = analyticsEngine.getHabitBasedSuggestion(currentUserId);
        if (habit != null) System.out.println("[HABIT-BASED] " + habit);
        System.out.println("==========================================\n");
    }


    public void showActivityReport() {
        if (currentUserId == -1) return;
        System.out.println(intelligenceService.getActivityReport(currentUserId));
        List<com.auis.model.UserActivity> recent = new com.auis.dao.ActivityDAO().getUserActivities(currentUserId);
        analyticsEngine.detectBotlikeActivity(currentUserId, recent);
    }


    public int getCurrentUserId() { return currentUserId; }


    public void logout() {
        if (currentUserId != -1) SessionManager.closeSession(currentUserId);
        this.currentUserId = -1;
        System.out.println("[LOGOUT] Goodbye!");
    }
}


