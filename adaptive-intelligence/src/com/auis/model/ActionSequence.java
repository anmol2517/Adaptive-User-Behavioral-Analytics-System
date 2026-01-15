package com.auis.model;


/*

  *  ActionSequence Model - Represents action transition patterns
  *  Changed occurrenceCount from int to long to prevent integer overflow

*/



public class ActionSequence {
    private int sequenceId;
    private String firstAction;
    private String nextAction;
    private long occurrenceCount;
    private float confidenceScore;

    public ActionSequence(String firstAction, String nextAction, long occurrenceCount, float confidenceScore) {
        this.firstAction = firstAction;
        this.nextAction = nextAction;
        this.occurrenceCount = occurrenceCount;
        this.confidenceScore = confidenceScore;
    }

    public int getSequenceId() { return sequenceId; }
    public void setSequenceId(int sequenceId) { this.sequenceId = sequenceId; }


    public String getFirstAction() { return firstAction; }
    public void setFirstAction(String firstAction) { this.firstAction = firstAction; }


    public String getNextAction() { return nextAction; }
    public void setNextAction(String nextAction) { this.nextAction = nextAction; }


    public long getOccurrenceCount() { return occurrenceCount; }
    public void setOccurrenceCount(long occurrenceCount) { this.occurrenceCount = occurrenceCount; }

    public float getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(float confidenceScore) { this.confidenceScore = confidenceScore; }


    @Override
    public String toString() {
        return String.format("ActionSequence{%s -> %s (confidence : %.2f%%)}",
                firstAction, nextAction, confidenceScore * 100);
    }
}


