package com.ashu.typingapp.model;

public class TypingSession {
    private int totalKeystrokes = 0;
    private int correctKeystrokes = 0;
    private long startTime = 0;
    private boolean isActive = false;

    // Jab user pehli key dabayega tab timer start hoga
    public void startSession() {
        startTime = System.currentTimeMillis();
        isActive = true;
        totalKeystrokes = 0;
        correctKeystrokes = 0;
    }

    // Har key press par yeh record karega ki sahi tha ya galat
    public void addKeystroke(boolean isCorrect) {
        if (!isActive) startSession();
        
        totalKeystrokes++;
        if (isCorrect) {
            correctKeystrokes++;
        }
    }

    // WPM = (Total Characters / 5) / Time in Minutes
    public int calculateWPM() {
        if (startTime == 0 || totalKeystrokes == 0) return 0;
        
        long currentTime = System.currentTimeMillis();
        double minutesElapsed = (currentTime - startTime) / 60000.0;
        
        if (minutesElapsed <= 0) return 0;

        double wordsTyped = totalKeystrokes / 5.0; // 5 characters = 1 standard word
        return (int) (wordsTyped / minutesElapsed);
    }

    // Accuracy % calculate karne ke liye
    public int calculateAccuracy() {
        if (totalKeystrokes == 0) return 100;
        return (int) (((double) correctKeystrokes / totalKeystrokes) * 100);
    }

    // Session restart karne ke liye
    public void reset() {
        isActive = false;
        totalKeystrokes = 0;
        correctKeystrokes = 0;
        startTime = 0;
    }
}