package com.project;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class UIManager {

    public static void showMainMenu() {
        FXGL.getGameScene().clearUINodes();

        Text title = new Text("Zombie Shooter Game");
        title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-fill: white;");

        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-font-size: 20px;");
        startButton.setOnAction(event -> showNameInputScreen());

        VBox menuLayout = new VBox(20, title, startButton);
        menuLayout.setTranslateX(300);
        menuLayout.setTranslateY(200);

        
        FXGL.getGameScene().addUINodes(menuLayout);
    }

    public static void showNameInputScreen() {
        FXGL.getGameScene().clearUINodes();

        Text prompt = new Text("Enter Your Name:");
        prompt.setStyle("-fx-font-size: 24px; -fx-fill: white;");

        TextField nameField = new TextField();
        nameField.setMaxWidth(200);

        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-font-size: 20px;");
        confirmButton.setOnAction(event -> {
            ((ZombieShooterGame) FXGL.getApp()).startGame();
        });

        VBox nameInputLayout = new VBox(10, prompt, nameField, confirmButton);
        nameInputLayout.setTranslateX(300);
        nameInputLayout.setTranslateY(200);

        FXGL.getGameScene().addUINodes(nameInputLayout);
    }
}
