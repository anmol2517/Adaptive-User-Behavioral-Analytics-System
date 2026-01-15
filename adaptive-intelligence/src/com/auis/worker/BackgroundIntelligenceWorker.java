package com.auis.worker;

import com.auis.dao.*;
import com.auis.service.PredictiveAnalyticsEngine;
import com.auis.util.SystemLogger;


import com.auis.util.SessionManager;
import com.auis.model.User;
import java.util.List;


import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/*

  * BackgroundIntelligenceWorker - Multithreaded pattern mining and rule generation
  * Use ScheduledExecutorService with fixed delay to prevent race conditions
  * Added session cleanup logic

*/



public class BackgroundIntelligenceWorker implements Runnable {
    private volatile boolean running = true;
    private final int SCAN_INTERVAL_MS = 30000;
    private UserDAO userDAO;
    private PredictiveAnalyticsEngine analyticsEngine;
    private ScheduledExecutorService scheduler;

    public BackgroundIntelligenceWorker() {
        this.userDAO = new UserDAO();
        this.analyticsEngine = new PredictiveAnalyticsEngine();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void run() {
        SystemLogger.info("BackgroundIntelligenceWorker started in separate thread");

        scheduler.scheduleWithFixedDelay(() -> {
            try {
                performPatternMining();
                cleanupSessions();
            } catch (Exception e) {
                SystemLogger.error("BackgroundWorker exception : " + e.getMessage());
            }
        }, 0, 30, TimeUnit.SECONDS);

        while (running) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        SystemLogger.info("BackgroundIntelligenceWorker stopped");
    }

    private void performPatternMining() {
        long startTime = System.currentTimeMillis();

        try {
            List<User> allUsers = userDAO.getAllUsers();

            for (User user : allUsers) {
                analyticsEngine.analyzeUserHabits(user.getUserId());
            }

            if (allUsers.size() > 1) {
                for (int i = 0; i < allUsers.size(); i++) {
                    for (int j = i + 1; j < allUsers.size(); j++) {
                        analyticsEngine.calculateUserSimilarity(
                                allUsers.get(i).getUserId(),
                                allUsers.get(j).getUserId()
                        );
                    }
                }
            }

            long duration = System.currentTimeMillis() - startTime;
            SystemLogger.performance("Pattern Mining Cycle", duration);

        } catch (Exception e) {
            SystemLogger.error("Pattern mining failed : " + e.getMessage());
        }
    }


       //    New method to clean up expired sessions



    private void cleanupSessions() {
        try {
            SessionManager.cleanupExpiredSessions();
        } catch (Exception e) {
            SystemLogger.error("Session cleanup failed : " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        SystemLogger.info("BackgroundIntelligenceWorker stop signal sent");
    }
}


