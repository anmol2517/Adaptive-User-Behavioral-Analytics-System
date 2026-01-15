package com.auis.model;

import java.time.LocalDateTime;



 //  Anomaly Model - Represents suspicious user behavior


public class Anomaly {
    private int anomalyId;
    private int userId;
    private String anomalyType;


    private int actionCount;
    private int timeWindowSeconds;
    private String severityLevel;
    private LocalDateTime detectedAt;


    public Anomaly(int userId, String anomalyType, int actionCount, int timeWindowSeconds, String severityLevel) {
        this.userId = userId;
        this.anomalyType = anomalyType;
        this.actionCount = actionCount;
        this.timeWindowSeconds = timeWindowSeconds;
        this.severityLevel = severityLevel;
    }


    // Getters and Setters


    public int getAnomalyId() { return anomalyId; }
    public void setAnomalyId(int anomalyId) { this.anomalyId = anomalyId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }


    public String getAnomalyType() { return anomalyType; }
    public void setAnomalyType(String anomalyType) { this.anomalyType = anomalyType; }

    public int getActionCount() { return actionCount; }
    public void setActionCount(int actionCount) { this.actionCount = actionCount; }


    public int getTimeWindowSeconds() { return timeWindowSeconds; }
    public void setTimeWindowSeconds(int timeWindowSeconds) { this.timeWindowSeconds = timeWindowSeconds; }

    public String getSeverityLevel() { return severityLevel; }
    public void setSeverityLevel(String severityLevel) { this.severityLevel = severityLevel; }


    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }

    @Override
    public String toString() {
        return String.format("[ALERT] User %d : %s (Severity : %s, Actions : %d in %ds)",
                userId, anomalyType, severityLevel, actionCount, timeWindowSeconds);
    }
}


