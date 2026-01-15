package com.auis.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;


/*

 *  SessionManager - Real-time session tracking with thread safety
 *  Use ConcurrentHashMap instead of HashMap for thread safety
 *  Added session timeout logic to prevent memory leaks

*/


public class SessionManager {
    private static final Map<Integer, UserSession> activeSessions = new ConcurrentHashMap<>();
    private static final long SESSION_TIMEOUT_MS = 30 * 60 * 1000;
    private static long sessionCounter = 0;

    public static class UserSession {
        public int userId;
        public String username;
        public LocalDateTime loginTime;
        public LocalDateTime lastActivity;
        public int actionCount;

        public UserSession(int userId, String username) {
            this.userId = userId;
            this.username = username;
            this.loginTime = LocalDateTime.now();
            this.lastActivity = LocalDateTime.now();
            this.actionCount = 0;
        }

        public long getSessionDurationSeconds() {
            return java.time.temporal.ChronoUnit.SECONDS.between(loginTime, LocalDateTime.now());
        }

        public void recordAction() {
            this.lastActivity = LocalDateTime.now();
            this.actionCount++;
        }

        public boolean isExpired() {
            long inactivityMs = java.time.temporal.ChronoUnit.MILLIS.between(lastActivity, LocalDateTime.now());
            return inactivityMs > SESSION_TIMEOUT_MS;
        }
    }

    public static void createSession(int userId, String username) {
        activeSessions.put(userId, new UserSession(userId, username));
        SystemLogger.info(String.format("Session created for user : %s (ID : %d)", username, userId));
    }

    public static UserSession getSession(int userId) {
        return activeSessions.get(userId);
    }

    public static void recordAction(int userId) {
        UserSession session = activeSessions.get(userId);
        if (session != null) {
            session.recordAction();
        }
    }

    public static void closeSession(int userId) {
        UserSession session = activeSessions.remove(userId);
        if (session != null) {
            SystemLogger.info(String.format("Session closed for user : %s | Duration : %ds | Actions : %d",
                    session.username, session.getSessionDurationSeconds(), session.actionCount));
        }
    }

    public static Map<Integer, UserSession> getActiveSessions() {
        return new HashMap<>(activeSessions);
    }

    public static int getActiveSessionCount() {
        return activeSessions.size();
    }


    /*

      *  New method to clean up expired sessions
      *  Call this periodically from BackgroundWorker to prevent zombie sessions

    */


    public static void cleanupExpiredSessions() {
        List<Integer> expiredSessionIds = new ArrayList<>();

        for (Map.Entry<Integer, UserSession> entry : activeSessions.entrySet()) {
            if (entry.getValue().isExpired()) {
                expiredSessionIds.add(entry.getKey());
            }
        }

        for (Integer userId : expiredSessionIds) {
            UserSession session = activeSessions.remove(userId);
            if (session != null) {
                SystemLogger.warn(String.format("Expired session removed for user : %s (ID : %d) - Inactive for 30+ min",
                        session.username, userId));
            }
        }

        if (!expiredSessionIds.isEmpty()) {
            SystemLogger.info(String.format("Cleaned up %d expired sessions", expiredSessionIds.size()));
        }
    }
}

