package com.ashu.typingapp.controller;

import com.ashu.typingapp.utils.ProfileManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfileController {

    @FXML private Label zenWpmLabel;
    @FXML private Label meteorScoreLabel;
    @FXML private Label keystrokesLabel;

    @FXML
    public void initialize() {
        // App khulte hi File se data load karo aur Labels par chipka do
        zenWpmLabel.setText(ProfileManager.getInt("zenHighWPM") + " WPM");
        meteorScoreLabel.setText(String.valueOf(ProfileManager.getInt("meteorHighScore")));
        keystrokesLabel.setText(String.valueOf(ProfileManager.getInt("totalKeystrokes")));
    }

    @FXML
    public void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}