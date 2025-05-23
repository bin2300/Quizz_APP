package com.ghost.quizzgame.model;

import java.util.List;

public class Question {
    private String questionText;
    private List<String> options;
    private int correctIndex;
    private int timeLimitSeconds; // 🕒 Nouveau champ

    public Question() {
        // Nécessaire pour Jackson
    }

    public Question(String questionText, List<String> options, int correctIndex, int timeLimitSeconds) {
        this.questionText = questionText;
        this.options = options;
        this.correctIndex = correctIndex;
        this.timeLimitSeconds = timeLimitSeconds;
    }

    // Getters et setters
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public void setCorrectIndex(int correctIndex) {
        this.correctIndex = correctIndex;
    }

    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public void setTimeLimitSeconds(int timeLimitSeconds) {
        this.timeLimitSeconds = timeLimitSeconds;
    }
}
