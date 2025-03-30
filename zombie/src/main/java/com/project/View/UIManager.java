package com.project.View;

import com.almasb.fxgl.dsl.FXGL;
import com.project.Controller.ZombieShooterGame;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class UIManager {

    /**
     * เมธอดนี้ถูกคอมเมนต์ออกไปเนื่องจากไม่ใช้ในตอนนี้
     * แสดงหน้าจอเมนูหลักของเกม
     */
    /*public static void showMainMenu() {
        FXGL.getGameScene().clearUINodes();

        // สร้างข้อความที่แสดงชื่อเกม
        Text title = new Text("Zombie Shooter Game");
        title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-fill: white;");

        // สร้างปุ่มเริ่มเกม
        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-font-size: 20px;");
        startButton.setOnAction(event -> showNameInputScreen());

        // จัดเรียง UI ในแนวตั้ง
        VBox menuLayout = new VBox(20, title, startButton);
        menuLayout.setTranslateX(300);
        menuLayout.setTranslateY(200);

        // เพิ่ม layout ลงใน UI ของเกม
        FXGL.getGameScene().addUINodes(menuLayout);
    }*/

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
        VBox nameInputLayout = new VBox(20, prompt, nameField, confirmButton);
        nameInputLayout.setTranslateX(400);
        nameInputLayout.setTranslateY(200);
        nameInputLayout.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 20px; -fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        // เพิ่ม layout ลงใน UI ของเกม
        FXGL.getGameScene().addUINode(nameInputLayout);
    }
}
