package com.project.View;

import com.almasb.fxgl.dsl.FXGL;
import com.project.Controller.ZombieShooterGame;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;

public class UIManager {

    /**
     * แสดงหน้าจอเมนูหลักของเกม
     */
    

    /**
     * เมธอดนี้ใช้แสดงหน้าจอสำหรับให้ผู้เล่นกรอกชื่อ
     */
    public static void showNameInputScreen() {
        // ล้าง UI เดิมทั้งหมดออก
        FXGL.getGameScene().clearUINodes();

        // เพิ่มพื้นหลังรูปภาพ
        javafx.scene.image.ImageView background = new javafx.scene.image.ImageView("assets/levels/p1.jpg");
        background.setFitWidth(FXGL.getAppWidth());
        background.setFitHeight(FXGL.getAppHeight());
        FXGL.getGameScene().addUINode(background);

        // ข้อความแจ้งให้ผู้เล่นกรอกชื่อ
        Text prompt = new Text("Enter Your Name:");
        prompt.setStyle("-fx-font-size: 24px; -fx-fill: white; -fx-font-weight: bold;");

        // กล่องข้อความสำหรับให้ผู้เล่นกรอกชื่อ
        TextField nameField = new TextField();
        nameField.setMaxWidth(300);
        nameField.setStyle("-fx-font-size: 18px; -fx-background-color: lightgray; -fx-border-color: black; -fx-border-width: 2px;");

        // ปุ่มยืนยันการกรอกชื่อ
        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-font-size: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        
        // เปลี่ยนสีของปุ่มเมื่อ mouse hover
        confirmButton.setOnMouseEntered(e -> confirmButton.setStyle("-fx-font-size: 20px; -fx-background-color: #45a049; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
        confirmButton.setOnMouseExited(e -> confirmButton.setStyle("-fx-font-size: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;"));

        // สร้างปุ่มดูคะแนนสูงสุด
        Button highScoreButton = new Button("High Scores");
        highScoreButton.setStyle("-fx-font-size: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        highScoreButton.setOnAction(event -> showHighScoreScreen());

        // เมธอดที่ทำงานเมื่อผู้เล่นกดปุ่มยืนยัน
        confirmButton.setOnAction(event -> {
            // ดึงชื่อผู้เล่นจาก TextField
            String playerName = nameField.getText();
            if (!playerName.isEmpty()) {
                // บันทึกชื่อผู้เล่นลงในไฟล์
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("player_data.txt", true))) {
                    writer.write(playerName + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // ส่งชื่อผู้เล่นไปยัง ZombieShooterGame และเริ่มเกม
                ((ZombieShooterGame) FXGL.getApp()).setPlayerName(playerName);
                ((ZombieShooterGame) FXGL.getApp()).startGame();
            }
        });

        // จัดวาง UI สำหรับกรอกชื่อ
        VBox nameInputLayout = new VBox(20, prompt, nameField, confirmButton, highScoreButton);
        nameInputLayout.setTranslateX(400);
        nameInputLayout.setTranslateY(200);
        nameInputLayout.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 20px; -fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        // เพิ่ม layout ลงใน UI ของเกม
        FXGL.getGameScene().addUINode(nameInputLayout);
    }

    /**
     * แสดงหน้าจอคะแนนสูงสุด
     */
    public static void showHighScoreScreen() {
        // ล้าง UI nodes ทั้งหมด
        FXGL.getGameScene().clearUINodes();

        // สร้างพื้นหลังสีดำ
        javafx.scene.layout.Pane background = new javafx.scene.layout.Pane();
        background.setStyle("-fx-background-color: black;");
        background.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());
        FXGL.getGameScene().addUINode(background);

        // สร้าง VBox สำหรับจัดวางองค์ประกอบ UI
        VBox content = new VBox(5);
        content.setAlignment(Pos.CENTER);
        content.setPrefWidth(400); // กำหนดความกว้างคงที่
        content.setTranslateX(FXGL.getAppWidth() / 2 - 200);
        content.setTranslateY(FXGL.getAppHeight() / 2 - 250);
        content.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 20px; -fx-border-color: #4CAF50; -fx-border-width: 2px; -fx-border-radius: 15px; -fx-background-radius: 15px;");

        // เพิ่มหัวข้อ
        Text title = new Text("Top 3 Players");
        title.setStyle("-fx-font-size: 36px; -fx-fill: #4CAF50; -fx-font-weight: bold;");
        content.getChildren().add(title);

        // อ่านและแสดงคะแนนสูงสุด
        List<ScoreEntry> scores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("highscore.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    try {
                        // ตรวจสอบว่าข้อมูลเป็นตัวเลขที่ถูกต้อง
                        if (parts[1].trim().matches("\\d+(\\.\\d+)?")) {
                            double survivalTime = Double.parseDouble(parts[1].trim());
                            if (parts[2].trim().matches("\\d+")) {
                                int kills = Integer.parseInt(parts[2].trim());
                                scores.add(new ScoreEntry(parts[0].trim(), survivalTime, kills));
                            }
                        }
                    } catch (NumberFormatException e) {
                        // ข้ามข้อมูลที่ไม่ถูกต้อง
                        continue;
                    }
                }
            }
        } catch (IOException e) {
            // กรณีที่ไม่มีไฟล์หรือเกิดข้อผิดพลาด
            Text errorText = new Text("No high scores yet!");
            errorText.setStyle("-fx-font-size: 24px; -fx-fill: #4CAF50;");
            content.getChildren().add(errorText);
        }

        // เรียงลำดับคะแนนตามเวลารอดชีวิต
        scores.sort((a, b) -> Double.compare(b.survivalTime, a.survivalTime));

        // แสดงคะแนนสูงสุด 3 อันดับแรก
        for (int i = 0; i < Math.min(3, scores.size()); i++) {
            ScoreEntry entry = scores.get(i);
            VBox playerScore = new VBox(0);
            playerScore.setAlignment(Pos.CENTER);
            playerScore.setPrefWidth(350); // กำหนดความกว้างคงที่
            playerScore.setStyle("-fx-background-color: rgba(76, 175, 80, 0.1); -fx-padding: 10px; -fx-border-color: #4CAF50; -fx-border-width: 1px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

            Text rank = new Text((i + 1) + ".");
            rank.setStyle("-fx-font-size: 24px; -fx-fill: #FFD700; -fx-font-weight: bold;");
            playerScore.getChildren().add(rank);

            Text name = new Text(entry.playerName);
            name.setStyle("-fx-font-size: 20px; -fx-fill: white; -fx-font-weight: bold;");
            playerScore.getChildren().add(name);

            Text survival = new Text(String.format("Survival: %.1f seconds", entry.survivalTime));
            survival.setStyle("-fx-font-size: 18px; -fx-fill: #4CAF50;");
            playerScore.getChildren().add(survival);

            Text kills = new Text("Kills: " + entry.kills);
            kills.setStyle("-fx-font-size: 18px; -fx-fill: #4CAF50;");
            playerScore.getChildren().add(kills);

            content.getChildren().add(playerScore);
        }

        // เพิ่มปุ่มกลับ
        Button backButton = new Button("Back to Home");
        backButton.setStyle("-fx-font-size: 18px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 8px 20px;");
        backButton.setOnAction(e -> showNameInputScreen());
        content.getChildren().add(backButton);

        // เพิ่ม content ลงใน scene
        FXGL.getGameScene().addUINode(content);
    }

    // คลาสสำหรับเก็บข้อมูลคะแนน
    private static class ScoreEntry {
        String playerName;
        double survivalTime;
        int kills;

        ScoreEntry(String playerName, double survivalTime, int kills) {
            this.playerName = playerName;
            this.survivalTime = survivalTime;
            this.kills = kills;
        }
    }

    /**
     * อัพเดท UI ในเกมให้มีปุ่มดูคะแนนสูงสุด
     */
    public static void updateGameUI() {
        // สร้างปุ่มดูคะแนนสูงสุด
        Button highScoreButton = new Button("High Scores");
        highScoreButton.setStyle("-fx-font-size: 16px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        highScoreButton.setOnAction(event -> showHighScoreScreen());

        // จัดวางปุ่มในมุมขวาบน
        highScoreButton.setTranslateX(FXGL.getAppWidth() - 120);
        highScoreButton.setTranslateY(5);

        // เพิ่มปุ่มลงใน UI ของเกม
        FXGL.getGameScene().addUINode(highScoreButton);
    }
}
