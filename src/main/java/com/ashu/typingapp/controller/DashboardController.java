package com.ashu.typingapp.controller;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;

public class DashboardController {

    @FXML private Pane particlePane;
    @FXML private Label titleLabel;
    @FXML private Button zenButton;
    @FXML private Button meteorButton;
    @FXML private Button profileButton;

    private Random random = new Random();

    @FXML
    public void initialize() {
        // 1. Button Styling & Hover Animations setup karna
        // Zen Button: Cyan theme
        setupButtonAnimation(zenButton, "#66fcf1", "#0b0c10");
        // Meteor Button: Pink/Red theme
        setupButtonAnimation(meteorButton, "#ff3366", "#0b0c10");

        setupButtonAnimation(profileButton, "#ffcc00", "#0b0c10");

        // 2. Title Pulsating Effect (Dheere dheere fade in-out hoga)
        FadeTransition ft = new FadeTransition(Duration.seconds(2), titleLabel);
        ft.setFromValue(0.7);
        ft.setToValue(1.0);
        ft.setCycleCount(Animation.INDEFINITE);
        ft.setAutoReverse(true);
        ft.play();

        // 3. GIF jaisa Particle Background Effect start karna
        createParticles();
    }

    // --- CYBERPUNK BUTTON HOVER LOGIC ---
    private void setupButtonAnimation(Button btn, String colorHex, String bgColorHex) {
        // Default: Khali (Hollow) button sirf border ke sath
        String defaultStyle = "-fx-background-color: transparent; -fx-border-color: " + colorHex + "; -fx-border-width: 2; -fx-border-radius: 5; -fx-text-fill: " + colorHex + "; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 12 40; -fx-cursor: hand; -fx-letter-spacing: 2px;";
        
        // Hover: Pura button us color se bhar jayega aur text dark ho jayega
        String hoverStyle = "-fx-background-color: " + colorHex + "; -fx-border-color: " + colorHex + "; -fx-border-width: 2; -fx-border-radius: 5; -fx-text-fill: " + bgColorHex + "; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 12 40; -fx-cursor: hand; -fx-letter-spacing: 2px;";

        btn.setStyle(defaultStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(defaultStyle));
    }

    // --- PARTICLE ANIMATION ENGINE (Lightweight GIF alternative) ---
    private void createParticles() {
        // 40 chote-chote circles banayenge
        for (int i = 0; i < 40; i++) {
            // Random size (1px to 3px) aur halka transparency
            Circle particle = new Circle(random.nextDouble() * 2 + 1, Color.web("#45a29e", 0.5));
            
            // Random X position pe set karo
            particle.setLayoutX(random.nextDouble() * 800);
            
            // Screen ke neeche se start karo
            particle.setLayoutY(random.nextDouble() * 200 + 600); 

            particlePane.getChildren().add(particle);

            // Upar ki taraf udne ka animation
            TranslateTransition tt = new TranslateTransition(Duration.seconds(random.nextInt(15) + 5), particle);
            tt.setByY(-800); // Screen ke upar tak jayega
            tt.setCycleCount(Animation.INDEFINITE); // Infinite chalta rahega
            tt.setDelay(Duration.seconds(random.nextDouble() * 5)); // Random time pe start hoga
            tt.play();
        }
    }

    // --- NAVIGATION LOGIC ---
    @FXML
    public void startZenMode(ActionEvent event) {
        switchScene(event, "/fxml/ZenMode.fxml");
    }

    @FXML
    public void startMeteorMode(ActionEvent event) {
        switchScene(event, "/fxml/MeteorMode.fxml");
    }

    @FXML
public void openProfile(ActionEvent event) {
    switchScene(event, "/fxml/Profile.fxml");
}

    private void switchScene(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}