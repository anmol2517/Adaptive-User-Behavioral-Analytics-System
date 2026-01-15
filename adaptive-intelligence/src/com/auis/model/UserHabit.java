package com.auis.model;



 //  UserHabit Model - Represents user behavioral patterns over time



public class UserHabit {
    private int habitId;
    private int userId;


    private int peakHour;
    private String preferredAction;
    private int frequencyScore;


    public UserHabit(int userId, int peakHour, String preferredAction, int frequencyScore) {
        this.userId = userId;
        this.peakHour = peakHour;
        this.preferredAction = preferredAction;
        this.frequencyScore = frequencyScore;
    }


    public int getHabitId() { return habitId; }
    public void setHabitId(int habitId) { this.habitId = habitId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }


    public int getPeakHour() { return peakHour; }
    public void setPeakHour(int peakHour) { this.peakHour = peakHour; }

    public String getPreferredAction() { return preferredAction; }
    public void setPreferredAction(String preferredAction) { this.preferredAction = preferredAction; }


    public int getFrequencyScore() { return frequencyScore; }
    public void setFrequencyScore(int frequencyScore) { this.frequencyScore = frequencyScore; }

    @Override
    public String toString() {
        return String.format("Habit : %s at hour %d (Frequency : %d)", preferredAction, peakHour, frequencyScore);
    }
}


