package com.ashu.typingapp.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordGenerator {
    
    // DSA: 3 alag alag buckets difficulty ke hisaab se
    private List<String> easyWords = new ArrayList<>();
    private List<String> mediumWords = new ArrayList<>();
    private List<String> hardWords = new ArrayList<>();
    
    private Random random = new Random();

    public WordGenerator() {
        loadDictionaryFromFile();
    }

    private void loadDictionaryFromFile() {
        try {
            InputStream is = getClass().getResourceAsStream("/data/dictionary.txt");
            if (is == null) {
                loadDefaultWords();
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            
            while ((line = reader.readLine()) != null) {
                String word = line.trim().toLowerCase();
                if (word.isEmpty()) continue;

                // Word ki length ke hisaab se usko sahi bucket (List) me daalna
                if (word.length() <= 5) {
                    easyWords.add(word);
                } else if (word.length() <= 8) {
                    mediumWords.add(word);
                } else {
                    hardWords.add(word);
                }
            }
            System.out.println("✅ DSA Dictionary Loaded! Easy: " + easyWords.size() + ", Med: " + mediumWords.size() + ", Hard: " + hardWords.size());
            
        } catch (Exception e) {
            loadDefaultWords();
        }
    }

    private void loadDefaultWords() {
        easyWords.addAll(List.of("cat", "dog", "run", "fast", "code"));
        mediumWords.addAll(List.of("system", "public", "static", "string", "object"));
        hardWords.addAll(List.of("polymorphism", "encapsulation", "architecture", "developer"));
    }

    // --- MAIN ADAPTIVE ALGORITHM ---
    public String getWord(String mode, int currentAccuracy) {
        // Agar mode "Auto" hai, toh accuracy ke hisaab se decide karo
        if (mode.equalsIgnoreCase("Auto")) {
            if (currentAccuracy >= 90) {
                mode = "Hard"; // 90%+ accuracy pe Hard words
            } else if (currentAccuracy >= 70) {
                mode = "Medium"; // 70-89% pe Medium words
            } else {
                mode = "Easy"; // Kharab accuracy pe wapas Easy
            }
        }

        // Selected mode ke hisaab se list se word nikalo
        switch (mode.toLowerCase()) {
            case "hard":
                return getRandom(hardWords);
            case "medium":
                return getRandom(mediumWords);
            case "easy":
            default:
                return getRandom(easyWords);
        }
    }

    // Helper function taaki list se random word nikal sake
    private String getRandom(List<String> list) {
        if (list.isEmpty()) return "error";
        return list.get(random.nextInt(list.size()));
    }

    // Sentence banane ke liye (Classic mode ke liye)
    public String getSentence(String mode, int accuracy, int length) {
        StringBuilder sentence = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sentence.append(getWord(mode, accuracy)).append(" ");
        }
        return sentence.toString().trim();
    }
}