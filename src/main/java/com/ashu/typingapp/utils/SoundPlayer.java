package com.ashu.typingapp.utils;

import javafx.scene.media.AudioClip;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundPlayer {
    
    private static Map<String, AudioClip> clickSounds = new HashMap<>();
    private static AudioClip errorSound;
    private static String currentTheme = "Mech"; // Default theme

    static {
        try {
            // Saari sounds .mp3 format me load kar rahe hain
            loadSound("Mech", "/assets/sounds/mech.mp3");
            loadSound("Typewriter", "/assets/sounds/typewriter.mp3");
            loadSound("Sci-Fi", "/assets/sounds/laser.mp3");

            URL errorUrl = SoundPlayer.class.getResource("/assets/sounds/error.mp3");
            if (errorUrl != null) {
                errorSound = new AudioClip(errorUrl.toExternalForm());
                errorSound.setVolume(0.4);
            }
        } catch (Exception e) {
            System.out.println("⚠️ Sound load error. Path check karo.");
        }
    }

    private static void loadSound(String name, String path) {
        URL url = SoundPlayer.class.getResource(path);
        if (url != null) {
            AudioClip clip = new AudioClip(url.toExternalForm());
            clip.setVolume(0.7); // Volume thoda loud rakha hai
            clickSounds.put(name, clip);
        }
    }

    // Yeh function UI se call hoga jab user Dropdown change karega
    public static void setTheme(String theme) {
        currentTheme = theme;
        System.out.println("🎵 Sound Theme changed to: " + theme);
    }

    public static void playClickSound() {
        if (currentTheme.equals("Mute")) return; // Agar mute hai toh aawaz mat karo

        AudioClip clip = clickSounds.get(currentTheme);
        if (clip != null) {
            clip.play();
        }
    }

    public static void playErrorSound() {
        if (currentTheme.equals("Mute")) return;

        if (errorSound != null) {
            errorSound.play();
        }
    }
}