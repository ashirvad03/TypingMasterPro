package com.ashu.typingapp.model;

import java.util.HashMap;
import java.util.Map;

public class UserProfile {
    private String username;
    private int highestWPM;
    private int totalWordsTyped;
    
    // Yeh map track karega ki kis letter pe kitni baar galti hui hai
    private Map<Character, Integer> weakKeys;

    public UserProfile(String username) {
        this.username = username;
        this.highestWPM = 0;
        this.totalWordsTyped = 0;
        this.weakKeys = new HashMap<>();
    }

    // Getters (Data read karne ke liye)
    public String getUsername() { return username; }
    public int getHighestWPM() { return highestWPM; }
    public int getTotalWordsTyped() { return totalWordsTyped; }
    public Map<Character, Integer> getWeakKeys() { return weakKeys; }

    // Setters & Updaters (Data update karne ke liye)
    public void setUsername(String username) { 
        this.username = username; 
    }
    
    // Naya high score set karne ka logic
    public void updateHighestWPM(int currentWPM) {
        if (currentWPM > this.highestWPM) {
            this.highestWPM = currentWPM;
        }
    }

    public void addWordsTyped(int wordsCount) {
        this.totalWordsTyped += wordsCount;
    }

    // Jab bhi user galat button dabayega, yeh function call hoga
    public void recordMistake(char wrongKey) {
        // Character ko lowercase me convert kar rahe hain taaki 'A' aur 'a' ek hi count ho
        char key = Character.toLowerCase(wrongKey); 
        weakKeys.put(key, weakKeys.getOrDefault(key, 0) + 1);
    }
}