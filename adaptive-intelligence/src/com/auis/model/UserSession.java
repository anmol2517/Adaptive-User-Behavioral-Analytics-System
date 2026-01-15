package com.auis.model;

import java.time.LocalDateTime;


/*

  * UserSession Model - Real-time Session Tracking
  * Tracks when user logs in/out and duration spent

*/


public class UserSession {
    private String sessionId;
    private int userId;
    private LocalDateTime sessionStartTime;

    private LocalDateTime sessionEndTime;
    private int totalActions;
    private int sessionDurationMinutes;

    public UserSession(String sessionId, int userId) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.sessionStartTime = LocalDateTime.now();
        this.totalActions = 0;
    }


    // Getters and Setters


    public String getSessionId() { return sessionId; }
    public int getUserId() { return userId; }
    public LocalDateTime getSessionStartTime() { return sessionStartTime; }
    public void setSessionStartTime(LocalDateTime sessionStartTime) { this.sessionStartTime = sessionStartTime; }
    

    public LocalDateTime getSessionEndTime() { return sessionEndTime; }
    public void setSessionEndTime(LocalDateTime sessionEndTime) { this.sessionEndTime = sessionEndTime; }
    
    public int getTotalActions() { return totalActions; }
    public void setTotalActions(int totalActions) { this.totalActions = totalActions; }
    
    public int getSessionDurationMinutes() { return sessionDurationMinutes; }
    public void setSessionDurationMinutes(int sessionDurationMinutes) { this.sessionDurationMinutes = sessionDurationMinutes; }


    @Override
    public String toString() {
        return String.format("Session : %s | User : %d | Start : %s | Actions : %d",
            sessionId, userId, sessionStartTime, totalActions);
    }
}


