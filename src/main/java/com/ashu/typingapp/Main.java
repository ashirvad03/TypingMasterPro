package com.ashu.typingapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Yahan humne VBox hata kar 'Parent' kar diya hai, ab kabhi crash nahi hoga chahe UI kaisa bhi ho!
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
        
        primaryStage.setTitle("TypingMaster Pro");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setResizable(false); // Game ki screen fix rakhte hain
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}