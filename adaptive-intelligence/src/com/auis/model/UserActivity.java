package com.auis.model;

import java.time.LocalDateTime;



  // UserActivity Model Class - Represents user actions/behavior


public class UserActivity {
    private int activityId;
    private int userId;
    private String action;
    private LocalDateTime activityTime;


    // Constructor


    public UserActivity(int activityId, int userId, String action, LocalDateTime activityTime) {
        this.activityId = activityId;
        this.userId = userId;
        this.action = action;
        this.activityTime = activityTime;
    }


    public UserActivity(int userId, String action) {
        this.userId = userId;
        this.action = action;
    }


    // Getters and Setters


    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(LocalDateTime activityTime) {
        this.activityTime = activityTime;
    }

    @Override
    public String toString() {
        return "UserActivity{" +
                "activityId=" + activityId +
                ", userId=" + userId +
                ", action='" + action + '\'' +
                ", activityTime=" + activityTime +
                '}';
    }
}


