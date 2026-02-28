package com.ashu.typingapp.service;

import com.ashu.typingapp.model.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalyticsEngine {

    // User ke sabse weak keys (jinme sabse zyada galti hui) nikalne ka logic
    public List<Character> getTopWeakKeys(UserProfile profile, int limit) {
        Map<Character, Integer> weakKeysMap = profile.getWeakKeys();
        
        // Agar user ne abhi tak koi galti nahi ki hai
        if (weakKeysMap == null || weakKeysMap.isEmpty()) {
            return new ArrayList<>();
        }

        // Map ko sort kar rahe hain errors ke hisaab se (Highest errors pehle)
        List<Map.Entry<Character, Integer>> sortedList = new ArrayList<>(weakKeysMap.entrySet());
        sortedList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        List<Character> topWeakKeys = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, sortedList.size()); i++) {
            topWeakKeys.add(sortedList.get(i).getKey());
        }

        return topWeakKeys;
    }
    
    // UI me virtual keyboard ke keys ka color decide karne ke liye
    public String getKeyColorCode(char key, UserProfile profile) {
        Map<Character, Integer> weakKeysMap = profile.getWeakKeys();
        
        if (weakKeysMap == null) return "#00ff00"; // Default Green
        
        int errorCount = weakKeysMap.getOrDefault(Character.toLowerCase(key), 0);
        
        if (errorCount == 0) {
            return "#00ff00"; // Green (Ekdum Perfect)
        } else if (errorCount < 5) {
            return "#ffcc00"; // Yellow (Thodi mistakes hain, warning)
        } else {
            return "#ff0000"; // Red (Bohot mistakes hain, danger zone)
        }
    }
}