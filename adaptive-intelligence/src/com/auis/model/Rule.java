package com.auis.model;


 //  Rule Model Class - Represents AI decision rules


public class Rule {
    private int ruleId;
    private String conditionText;
    private String suggestionText;


    // Constructor


    public Rule(int ruleId, String conditionText, String suggestionText) {
        this.ruleId = ruleId;
        this.conditionText = conditionText;
        this.suggestionText = suggestionText;
    }

    public Rule(String conditionText, String suggestionText) {
        this.conditionText = conditionText;
        this.suggestionText = suggestionText;
    }


    // Getters And Setters


    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public String getConditionText() {
        return conditionText;
    }

    public void setConditionText(String conditionText) {
        this.conditionText = conditionText;
    }



    public String getSuggestionText() {
        return suggestionText;
    }

    public void setSuggestionText(String suggestionText) {
        this.suggestionText = suggestionText;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "ruleId=" + ruleId +
                ", conditionText='" + conditionText + '\'' +
                ", suggestionText='" + suggestionText + '\'' +
                '}';
    }
}


