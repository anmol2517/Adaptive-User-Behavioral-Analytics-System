package com.auis.model;


//  UserSimilarity Model - Represents behavioral similarity between users


public class UserSimilarity {
    private int similarityId;
    private int userA;


    private int userB;
    private float similarityScore;
    private int sharedActions;

    public UserSimilarity(int userA, int userB, float similarityScore, int sharedActions) {
        this.userA = userA;
        this.userB = userB;
        this.similarityScore = similarityScore;
        this.sharedActions = sharedActions;
    }


    public int getSimilarityId() { return similarityId; }
    public void setSimilarityId(int similarityId) { this.similarityId = similarityId; }

    public int getUserA() { return userA; }
    public void setUserA(int userA) { this.userA = userA; }


    public int getUserB() { return userB; }
    public void setUserB(int userB) { this.userB = userB; }


    public float getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(float similarityScore) { this.similarityScore = similarityScore; }

    public int getSharedActions() { return sharedActions; }
    public void setSharedActions(int sharedActions) { this.sharedActions = sharedActions; }


    @Override
    public String toString() {
        return String.format("User %d <-> User %d : %.2f%% match (%d shared actions)", userA, userB, similarityScore * 100, sharedActions);
    }
}


