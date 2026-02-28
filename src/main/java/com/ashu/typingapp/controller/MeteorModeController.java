package com.ashu.typingapp.controller;

import com.ashu.typingapp.model.WordItem;
import com.ashu.typingapp.service.WordGenerator;
import com.ashu.typingapp.utils.SoundPlayer;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MeteorModeController {

    @FXML private Pane gameArea;
    @FXML private Label scoreLabel, healthLabel, typedWordLabel, accuracyLabel;
    @FXML private ComboBox<String> difficultyCombo;
    @FXML private ComboBox<String> dropTypeCombo; 
    @FXML private ComboBox<String> soundCombo; // Sound Combo Box
    @FXML private Slider speedSlider;
    @FXML private CheckBox autoSpeedCheck;

    private WordGenerator wordGenerator = new WordGenerator();
    private List<WordItem> activeWords = new ArrayList<>();
    private List<Label> wordLabels = new ArrayList<>();
    private AnimationTimer gameLoop;
    private Random random = new Random();

    private String currentTypedText = "";
    private int score = 0;
    private int health = 5;
    private long lastSpawnTime = 0;
    private int totalKeystrokes = 0;
    private int correctKeystrokes = 0;

    @FXML
    public void initialize() {
        difficultyCombo.getItems().addAll("Easy", "Medium", "Hard", "Auto");
        difficultyCombo.setValue("Easy");

        dropTypeCombo.getItems().addAll("Words", "Characters");
        dropTypeCombo.setValue("Words");

        // Sound Setup
        soundCombo.getItems().addAll("Mech", "Typewriter", "Sci-Fi", "Mute");
        soundCombo.setValue("Mech");
        soundCombo.setOnAction(e -> SoundPlayer.setTheme(soundCombo.getValue()));

        autoSpeedCheck.setOnAction(e -> speedSlider.setDisable(autoSpeedCheck.isSelected()));

        Platform.runLater(() -> {
            gameArea.getScene().setOnKeyTyped(this::handleKeyPress);
            gameArea.getScene().getRoot().requestFocus();
        });
        
        startGame();
    }

    private void startGame() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (health <= 0) {
                    stopGame();
                    return;
                }

                double speedMultiplier = speedSlider.getValue() / 20.0;
                long spawnDelay = (long) (2_500_000_000L / Math.max(0.5, speedMultiplier)); 

                if (now - lastSpawnTime > spawnDelay) {
                    spawnWord();
                    lastSpawnTime = now;
                }

                updateWords();
            }
        };
        gameLoop.start();
    }

    private void spawnWord() {
        String text = "";
        if (dropTypeCombo.getValue().equals("Characters")) {
            String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
            text = String.valueOf(chars.charAt(random.nextInt(chars.length())));
        } else {
            int currentAccuracy = calculateAccuracy();
            String selectedDifficulty = difficultyCombo.getValue();
            text = wordGenerator.getWord(selectedDifficulty, currentAccuracy);
        }
        
        double xPos = random.nextInt(600) + 50; 
        WordItem newWord = new WordItem(text, xPos, 0); 
        
        Label wordLabel = new Label(text);
        wordLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 24px; -fx-font-weight: bold;");
        wordLabel.setLayoutX(xPos);
        wordLabel.setLayoutY(0);

        activeWords.add(newWord);
        wordLabels.add(wordLabel);
        gameArea.getChildren().add(wordLabel);
    }

    private void updateWords() {
        double dropSpeed = speedSlider.getValue() / 20.0;

        for (int i = 0; i < activeWords.size(); i++) {
            WordItem word = activeWords.get(i);
            Label label = wordLabels.get(i);

            word.drop(dropSpeed);
            label.setLayoutY(word.getYPosition());

            if (word.getYPosition() > 400) {
                word.destroy();
                health--;
                healthLabel.setText("Health: " + "❤️".repeat(Math.max(0, health)));
                SoundPlayer.playErrorSound(); // ❌ Meteor Girne Par Buzzer
                
                if (autoSpeedCheck.isSelected()) {
                    speedSlider.setValue(Math.max(1, speedSlider.getValue() - 5));
                }
            }
        }
        cleanupDestroyedWords();
    }

    private void handleKeyPress(KeyEvent event) {
        if (health <= 0) return;

        String character = event.getCharacter();
        totalKeystrokes++;

        if (character.equals("\b")) {
            if (!currentTypedText.isEmpty()) {
                currentTypedText = currentTypedText.substring(0, currentTypedText.length() - 1);
                SoundPlayer.playClickSound(); // 🎵 Backspace sound
            }
        } else if (!character.equals("\r") && !character.equals("\n") && !character.equals(" ")) {
            currentTypedText += character;
            SoundPlayer.playClickSound(); // 🎵 Type Sound
        }

        typedWordLabel.setText(currentTypedText);
        checkWordMatch();
        updateAccuracyLabel();
    }

    private void checkWordMatch() {
        for (WordItem word : activeWords) {
            if (word.getText().equals(currentTypedText) && !word.isDestroyed()) {
                word.destroy(); 
                score += 10;
                correctKeystrokes += currentTypedText.length(); 
                
                scoreLabel.setText("Score: " + score);
                currentTypedText = ""; 
                typedWordLabel.setText("");
                
                if (autoSpeedCheck.isSelected()) {
                    speedSlider.setValue(Math.min(100, speedSlider.getValue() + 2));
                }
                
                break;
            }
        }
    }

    private int calculateAccuracy() {
        if (totalKeystrokes == 0) return 100;
        return (correctKeystrokes * 100) / totalKeystrokes;
    }

    private void updateAccuracyLabel() {
        accuracyLabel.setText("Accuracy: " + calculateAccuracy() + "%");
    }

    private void cleanupDestroyedWords() {
        Iterator<WordItem> wordIt = activeWords.iterator();
        Iterator<Label> labelIt = wordLabels.iterator();
        while (wordIt.hasNext()) {
            if (wordIt.next().isDestroyed()) {
                gameArea.getChildren().remove(labelIt.next());
                wordIt.remove();
                labelIt.remove();
            }
        }
    }

    private void stopGame() {
        if (gameLoop != null) gameLoop.stop();
        typedWordLabel.setText("GAME OVER!");
        typedWordLabel.setStyle("-fx-text-fill: #ff0000; -fx-font-size: 30px;");

        com.ashu.typingapp.utils.ProfileManager.updateIfHigher("meteorHighScore", score);
        if (totalKeystrokes > 0) {
            com.ashu.typingapp.utils.ProfileManager.addKeystrokes(totalKeystrokes);
            totalKeystrokes = 0; // Reset kar diya taaki double save na ho
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        stopGame(); 
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}