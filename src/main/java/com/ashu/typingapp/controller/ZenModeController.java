package com.ashu.typingapp.controller;

import com.ashu.typingapp.model.TypingSession;
import com.ashu.typingapp.service.WordGenerator;
import com.ashu.typingapp.utils.SoundPlayer;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ZenModeController {

    @FXML private Label wpmLabel, accuracyLabel, targetWordLabel, userInputLabel;
    @FXML private ComboBox<String> difficultyCombo, soundCombo;
    @FXML private VBox keyboardPane; 
    
    // Timer UI Elements
    @FXML private Label timerDisplayLabel;
    @FXML private TextField timerInputField;

    private WordGenerator wordGenerator;
    private TypingSession session;
    private String currentTarget = ""; 
    private String typedText = "";
    private Map<String, Label> keyMap = new HashMap<>();
    private int sessionKeystrokes = 0;

    // Timer Logic Variables
    private int selectedTimeInSeconds = 0; // 0 matlab Not Set (Infinite)
    private int timeRemaining = 0;
    private Timeline countdownTimeline;
    private boolean isTimerRunning = false;
    private boolean isSessionOver = false;


    @FXML
    public void initialize() {
        wordGenerator = new WordGenerator();
        session = new TypingSession();
        
        difficultyCombo.getItems().addAll("Easy", "Medium", "Hard", "Auto");
        difficultyCombo.setValue("Easy");
        difficultyCombo.setOnAction(e -> resetGameAndLoadNext());

        soundCombo.getItems().addAll("Mech", "Typewriter", "Sci-Fi", "Mute");
        soundCombo.setValue("Mech");
        soundCombo.setOnAction(e -> SoundPlayer.setTheme(soundCombo.getValue()));

        setupTimerInputBehavior();
        buildKeyboard(); 
        resetGameAndLoadNext();
        
        Platform.runLater(() -> {
            targetWordLabel.getScene().setOnKeyTyped(this::handleKeyPress);
            targetWordLabel.getScene().getRoot().requestFocus(); 
        });
    }

    // --- TIMER UI & LOGIC ---
    private void setupTimerInputBehavior() {
        // Sirf numbers allow karega (letters block)
        timerInputField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                timerInputField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        // Agar user box ke bahar click kare toh automatically save ho jaye
        timerInputField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                processAndSaveTimer();
            }
        });
    }

    @FXML
    public void enableTimerEdit() {
        if (isTimerRunning) return; // Game chalte waqt timer edit nahi kar sakte
        
        timerDisplayLabel.setVisible(false);
        timerInputField.setVisible(true);
        timerInputField.setText(selectedTimeInSeconds > 0 ? String.valueOf(selectedTimeInSeconds) : "");
        timerInputField.requestFocus();
    }

    @FXML
    public void saveTimer(ActionEvent event) {
        processAndSaveTimer();
        targetWordLabel.getScene().getRoot().requestFocus(); // Wapas typing area pe focus
    }

    private void processAndSaveTimer() {
        timerInputField.setVisible(false);
        timerDisplayLabel.setVisible(true);
        
        String input = timerInputField.getText();
        if (input.isEmpty()) {
            selectedTimeInSeconds = 0;
        } else {
            try {
                selectedTimeInSeconds = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                selectedTimeInSeconds = 0;
            }
        }
        
        timeRemaining = selectedTimeInSeconds;
        updateTimerLabel(selectedTimeInSeconds);
    }

    private void updateTimerLabel(int seconds) {
        if (seconds <= 0) {
            timerDisplayLabel.setText("Not Set");
            return;
        }
        
        // Asli Math yahan hai: 61 sec -> 1 min 1 sec
        if (seconds < 60) {
            timerDisplayLabel.setText(seconds + " sec");
        } else {
            int mins = seconds / 60;
            int secs = seconds % 60;
            if (secs == 0) {
                timerDisplayLabel.setText(mins + " min");
            } else {
                timerDisplayLabel.setText(mins + " min " + secs + " sec");
            }
        }
    }

    private void startCountdownTimer() {
        if (selectedTimeInSeconds > 0 && !isTimerRunning) {
            isTimerRunning = true;
            timeRemaining = selectedTimeInSeconds;
            
            countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                timeRemaining--;
                updateTimerLabel(timeRemaining);
                
                if (timeRemaining <= 0) {
                    endSession();
                }
            }));
            countdownTimeline.setCycleCount(Timeline.INDEFINITE);
            countdownTimeline.play();
        }
    }

    private void endSession() {
        if (countdownTimeline != null) countdownTimeline.stop();
        isTimerRunning = false;
        isSessionOver = true;
        
        targetWordLabel.setText("Time's Up!");
        targetWordLabel.setStyle("-fx-font-size: 40px; -fx-text-fill: #ff3333; -fx-font-weight: bold;");
        
        int finalWPM = session.calculateWPM();
        userInputLabel.setText("Final WPM: " + finalWPM + " | Acc: " + session.calculateAccuracy() + "%");
        userInputLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: #00ffcc;");
        
        SoundPlayer.playErrorSound(); 

        // --- NAYA CODE: Timer end hone par WPM aur Keys save karega ---
        com.ashu.typingapp.utils.ProfileManager.updateIfHigher("zenHighWPM", finalWPM);
        if (sessionKeystrokes > 0) {
            com.ashu.typingapp.utils.ProfileManager.addKeystrokes(sessionKeystrokes);
            sessionKeystrokes = 0; 
        }
    }
    // -------------------------

    private void resetGameAndLoadNext() {
        if (countdownTimeline != null) countdownTimeline.stop();
        isTimerRunning = false;
        isSessionOver = false;
        timeRemaining = selectedTimeInSeconds;
        updateTimerLabel(selectedTimeInSeconds);
        session = new TypingSession(); // Stats reset
        updateStats();

        targetWordLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
        String mode = difficultyCombo.getValue();
        currentTarget = wordGenerator.getSentence(mode, 100, 8);
        targetWordLabel.setText(currentTarget);
        typedText = "";
        userInputLabel.setText("");
        userInputLabel.setStyle("-fx-text-fill: #a6accd;"); 
    }

    private void loadNextSentence() {
        String mode = difficultyCombo.getValue();
        int currentAcc = session.calculateAccuracy() == 0 ? 100 : session.calculateAccuracy();
        currentTarget = wordGenerator.getSentence(mode, currentAcc, 8);
        targetWordLabel.setText(currentTarget);
        typedText = "";
        userInputLabel.setText("");
        userInputLabel.setStyle("-fx-text-fill: #a6accd;"); 
    }

    private void handleKeyPress(KeyEvent event) {
        if (isSessionOver) return; 

        if (!isTimerRunning && selectedTimeInSeconds > 0) {
            startCountdownTimer();
        }

        String character = event.getCharacter();

        if (character.equals("\b")) { 
            if (!typedText.isEmpty()) typedText = typedText.substring(0, typedText.length() - 1);
        } else if (character.matches("[a-zA-Z0-9 ]")) { 
            typedText += character;
            sessionKeystrokes++; // <--- NAYA CODE: Har key dabne par count badhega
            checkTypingStatus(character);
        }
        userInputLabel.setText(typedText);
    }

    private void checkTypingStatus(String lastTypedChar) {
        if (currentTarget.equals(typedText)) {
            session.addKeystroke(true);
            animateKey(lastTypedChar, true);
            SoundPlayer.playClickSound(); 
            updateStats();
            loadNextSentence(); 
            
        } else if (currentTarget.startsWith(typedText)) {
            session.addKeystroke(true);
            animateKey(lastTypedChar, true); 
            SoundPlayer.playClickSound(); 
            userInputLabel.setStyle("-fx-text-fill: #a6accd;"); 
        } else {
            session.addKeystroke(false);
            animateKey(lastTypedChar, false); 
            SoundPlayer.playErrorSound(); 
            userInputLabel.setStyle("-fx-text-fill: #ff3333;"); 
        }
    }

    private void updateStats() {
        wpmLabel.setText("WPM: " + session.calculateWPM());
        accuracyLabel.setText("Accuracy: " + session.calculateAccuracy() + "%");
    }

    private void buildKeyboard() {
        String[] row1 = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"};
        String[] row2 = {"A", "S", "D", "F", "G", "H", "J", "K", "L"};
        String[] row3 = {"Z", "X", "C", "V", "B", "N", "M"};

        keyboardPane.getChildren().add(createRow(row1));
        keyboardPane.getChildren().add(createRow(row2));
        keyboardPane.getChildren().add(createRow(row3));
        
        HBox spaceRow = new HBox(8);
        spaceRow.setAlignment(Pos.CENTER);
        Label spaceLabel = new Label("SPACE");
        spaceLabel.setStyle("-fx-background-color: #2e2e3e; -fx-text-fill: white; -fx-padding: 10 100; -fx-background-radius: 6; -fx-font-weight: bold; -fx-font-size: 16px;");
        keyMap.put(" ", spaceLabel);
        spaceRow.getChildren().add(spaceLabel);
        keyboardPane.getChildren().add(spaceRow);
    }

    private HBox createRow(String[] keys) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER);
        for (String k : keys) {
            Label keyLabel = new Label(k);
            keyLabel.setStyle("-fx-background-color: #2e2e3e; -fx-text-fill: white; -fx-padding: 10 18; -fx-background-radius: 6; -fx-font-weight: bold; -fx-font-size: 16px;");
            keyMap.put(k.toLowerCase(), keyLabel);
            row.getChildren().add(keyLabel);
        }
        return row;
    }

    private void animateKey(String key, boolean isCorrect) {
        Label lbl = keyMap.get(key.toLowerCase());
        if (lbl != null) {
            String glowColor = isCorrect ? "#00ffcc" : "#ff3333"; 
            String textColor = isCorrect ? "#000000" : "#ffffff";
            String padding = key.equals(" ") ? "10 100" : "10 18"; 
            
            lbl.setStyle("-fx-background-color: " + glowColor + "; -fx-text-fill: " + textColor + "; -fx-padding: " + padding + "; -fx-background-radius: 6; -fx-font-weight: bold; -fx-font-size: 16px;");
            
            PauseTransition pause = new PauseTransition(Duration.millis(150));
            pause.setOnFinished(e -> lbl.setStyle("-fx-background-color: #2e2e3e; -fx-text-fill: white; -fx-padding: " + padding + "; -fx-background-radius: 6; -fx-font-weight: bold; -fx-font-size: 16px;"));
            pause.play();
        }
    }

    
    @FXML
    public void goBack(ActionEvent event) {
        if (countdownTimeline != null) countdownTimeline.stop();
        
        // --- NAYA CODE: Beech me back jane par save karega ---
        com.ashu.typingapp.utils.ProfileManager.updateIfHigher("zenHighWPM", session.calculateWPM());
        if (sessionKeystrokes > 0) {
            com.ashu.typingapp.utils.ProfileManager.addKeystrokes(sessionKeystrokes);
            sessionKeystrokes = 0;
        }

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}