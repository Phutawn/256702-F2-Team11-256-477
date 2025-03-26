package com.project;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class UIManager {

    /*public static void showMainMenu() {
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
    }*/

    public static void showNameInputScreen() {
        FXGL.getGameScene().clearUINodes();

        // เพิ่มพื้นหลังรูปภาพ
        javafx.scene.image.ImageView background = new javafx.scene.image.ImageView("assets/levels/p1.jpg");
        background.setFitWidth(FXGL.getAppWidth());
        background.setFitHeight(FXGL.getAppHeight());
        FXGL.getGameScene().addUINode(background);

        // ข้อความแจ้งให้กรอกชื่อ
        Text prompt = new Text("Enter Your Name:");
        prompt.setStyle("-fx-font-size: 24px; -fx-fill: white; -fx-font-weight: bold;");

        // กล่องข้อความสำหรับกรอกชื่อ
        TextField nameField = new TextField();
        nameField.setMaxWidth(300);
        nameField.setStyle("-fx-font-size: 18px; -fx-background-color: lightgray; -fx-border-color: black; -fx-border-width: 2px;");

        // ปุ่มยืนยัน
        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-font-size: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        confirmButton.setOnMouseEntered(e -> confirmButton.setStyle("-fx-font-size: 20px; -fx-background-color: #45a049; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
        confirmButton.setOnMouseExited(e -> confirmButton.setStyle("-fx-font-size: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;"));

        confirmButton.setOnAction(event -> {
            String playerName = nameField.getText();
            if (!playerName.isEmpty()) {
                // บันทึกชื่อผู้เล่นลงในไฟล์
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("player_data.txt", true))) {
                    writer.write(playerName + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // ส่งชื่อไปยัง ZombieShooterGame
                ((ZombieShooterGame) FXGL.getApp()).setPlayerName(playerName);
                ((ZombieShooterGame) FXGL.getApp()).startGame();
            }
        });

        // จัดวาง UI
        VBox nameInputLayout = new VBox(20, prompt, nameField, confirmButton);
        nameInputLayout.setTranslateX(400);
        nameInputLayout.setTranslateY(200);
        nameInputLayout.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 20px; -fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        FXGL.getGameScene().addUINode(nameInputLayout);
    }
}
