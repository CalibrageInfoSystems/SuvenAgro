package com.cis.palm360.palmgrow.SuvenAgro.areaextension;

public class Question {
    private String questionText;
    private boolean isEnabled;
    private Integer selectedAnswer = null;
    private String selectedDate;

    public Question(String questionText, boolean isEnabled) {
        this.questionText = questionText;
        this.isEnabled = isEnabled;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public Integer getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(Integer selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
