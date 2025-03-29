package com.project;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.control.ProgressBar;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PlayerHealth extends Component {
    private static final int MAX_HEALTH = 100;
    private int health = MAX_HEALTH;
    private ProgressBar healthBar;

    public PlayerHealth() {
        healthBar = new ProgressBar(1);
        healthBar.setStyle("-fx-accent: red;");
        healthBar.setTranslateX(10);
        healthBar.setTranslateY(10);
        healthBar.setPrefWidth(200);
        FXGL.getGameScene().addUINode(healthBar);
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) health = 0;
        updateHealthBar();

        if (health == 0) {
            // ตรวจสอบว่าเวลาที่อยู่รอดในรอบนี้และจำนวนซอมบี้ที่ฆ่ามีค่าสูงกว่าสถิติเดิมหรือไม่
            if (ZombieShooterGame.currentSurvivalTime > ZombieShooterGame.longestSurvivalTime) {
                ZombieShooterGame.longestSurvivalTime = ZombieShooterGame.currentSurvivalTime;
            }
            if (ZombieShooterGame.zombieKillCount > ZombieShooterGame.mostZombieKills) {
                ZombieShooterGame.mostZombieKills = ZombieShooterGame.zombieKillCount;
            }
            // บันทึก high score ลงไฟล์ในรูปแบบ "longestSurvivalTime,mostZombieKills"
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("highscore.txt"))) {
                writer.write(ZombieShooterGame.longestSurvivalTime + "," + ZombieShooterGame.mostZombieKills);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FXGL.showMessage("Game Over\nTime: " + (int) ZombieShooterGame.currentSurvivalTime 
                    + " sec\nZombie Kills: " + ZombieShooterGame.zombieKillCount, () -> FXGL.getGameController().startNewGame());
        }
    }

    public void heal(int amount) {
        health = Math.min(health + amount, MAX_HEALTH);
        updateHealthBar();
    }

    private void updateHealthBar() {
        healthBar.setProgress((double) health / MAX_HEALTH);
    }

    public int getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
        updateHealthBar();
    }
    
}
