package com.ashu.typingapp.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class ProfileManager {
    // Ye file aapke project folder me banegi jisme score save hoga
    private static final String FILE_PATH = "commander_stats.properties";
    private static Properties stats = new Properties();

    static {
        loadStats();
    }

    public static void loadStats() {
        try (FileInputStream in = new FileInputStream(FILE_PATH)) {
            stats.load(in);
        } catch (Exception e) {
            // Agar pehli baar app khul raha hai toh default 0 set kar do
            stats.setProperty("zenHighWPM", "0");
            stats.setProperty("meteorHighScore", "0");
            stats.setProperty("totalKeystrokes", "0");
        }
    }

    public static void saveStats() {
        try (FileOutputStream out = new FileOutputStream(FILE_PATH)) {
            stats.store(out, "TypingMaster Pro - Commander Stats");
        } catch (Exception e) {
            System.out.println("⚠️ Warning: Stats save nahi hue!");
        }
    }

    public static int getInt(String key) {
        return Integer.parseInt(stats.getProperty(key, "0"));
    }

    // Yeh function check karega ki naya score purane record se bada hai ya nahi
    public static void updateIfHigher(String key, int newValue) {
        int current = getInt(key);
        if (newValue > current) {
            stats.setProperty(key, String.valueOf(newValue));
            saveStats();
        }
    }

    public static void addKeystrokes(int amount) {
        int current = getInt("totalKeystrokes");
        stats.setProperty("totalKeystrokes", String.valueOf(current + amount));
        saveStats();
    }
}