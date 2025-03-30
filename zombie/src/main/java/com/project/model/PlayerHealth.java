package com.project.model;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.project.Controller.ZombieShooterGame;
import javafx.scene.control.ProgressBar;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PlayerHealth extends Component {

    // จำนวนสุขภาพสูงสุดของผู้เล่น
    private static final int MAX_HEALTH = 100;
    // ตัวแปรสำหรับเก็บค่าสุขภาพปัจจุบันของผู้เล่น (เริ่มต้นที่ค่าสูงสุด)
    private int health = MAX_HEALTH;
    // ProgressBar สำหรับแสดงสุขภาพบนหน้าจอ
    private ProgressBar healthBar;

    /**
     * Constructor สำหรับสร้าง PlayerHealth และกำหนด ProgressBar สำหรับแสดงสุขภาพ
     */
    public PlayerHealth() {
        // สร้าง ProgressBar โดยเริ่มต้นที่ค่าเต็ม (1)
        healthBar = new ProgressBar(1);
        // กำหนดสีของ ProgressBar ให้เป็นสีแดง
        healthBar.setStyle("-fx-accent: red;");
        // กำหนดตำแหน่งของ ProgressBar บนหน้าจอ
        healthBar.setTranslateX(10);
        healthBar.setTranslateY(10);
        // กำหนดความกว้างของ ProgressBar
        healthBar.setPrefWidth(200);
        // เพิ่ม ProgressBar ลงใน UI ของเกม
        FXGL.getGameScene().addUINode(healthBar);
    }

    /**
     * เมธอดสำหรับลดจำนวนสุขภาพของผู้เล่นเมื่อได้รับความเสียหาย
     *
     * @param amount จำนวนความเสียหายที่จะถูกหักออกจากสุขภาพ
     */
    public void takeDamage(int amount) {
        // ลดสุขภาพตามจำนวนที่ได้รับความเสียหาย
        health -= amount;
        // ตรวจสอบไม่ให้สุขภาพต่ำกว่า 0
        if (health < 0) health = 0;
        // อัปเดตการแสดงผลของ ProgressBar ให้ตรงกับค่าสุขภาพใหม่
        updateHealthBar();

        // ถ้าสุขภาพของผู้เล่นเหลือ 0 ให้ทำการประมวลผลเกมจบ
        if (health == 0) {
            // ตรวจสอบและอัปเดตสถิติ longestSurvivalTime หากเวลาปัจจุบันสูงกว่า
            if (ZombieShooterGame.currentSurvivalTime > ZombieShooterGame.longestSurvivalTime) {
                ZombieShooterGame.longestSurvivalTime = ZombieShooterGame.currentSurvivalTime;
            }
            // ตรวจสอบและอัปเดตสถิติ mostZombieKills หากจำนวนซอมบี้ที่ฆ่าได้ในรอบนี้สูงกว่า
            if (ZombieShooterGame.zombieKillCount > ZombieShooterGame.mostZombieKills) {
                ZombieShooterGame.mostZombieKills = ZombieShooterGame.zombieKillCount;
            }
            // บันทึก high score ลงไฟล์ "highscore.txt" ในรูปแบบ "longestSurvivalTime,mostZombieKills"
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("highscore.txt"))) {
                writer.write(ZombieShooterGame.longestSurvivalTime + "," + ZombieShooterGame.mostZombieKills);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // แสดงข้อความ "Game Over" พร้อมข้อมูลเวลาที่รอดและจำนวนซอมบี้ที่ฆ่า จากนั้นเริ่มเกมใหม่
            FXGL.showMessage("Game Over\nTime: " + (int) ZombieShooterGame.currentSurvivalTime 
                    + " sec\nZombie Kills: " + ZombieShooterGame.zombieKillCount, 
                    () -> FXGL.getGameController().startNewGame());
        }
    }

    /**
     * เมธอดสำหรับเพิ่มสุขภาพให้กับผู้เล่น
     *
     * @param amount จำนวนสุขภาพที่จะเพิ่ม (ไม่เกิน MAX_HEALTH)
     */
    public void heal(int amount) {
        // เพิ่มสุขภาพโดยไม่ให้เกินค่าสูงสุด MAX_HEALTH
        health = Math.min(health + amount, MAX_HEALTH);
        // อัปเดตการแสดงผลของ ProgressBar
        updateHealthBar();
    }

    /**
     * เมธอดสำหรับอัปเดต ProgressBar ให้แสดงเปอร์เซ็นต์ของสุขภาพที่เหลืออยู่
     */
    private void updateHealthBar() {
        // คำนวณเปอร์เซ็นต์ของสุขภาพ (health / MAX_HEALTH)
        healthBar.setProgress((double) health / MAX_HEALTH);
    }

    /**
     * เมธอดสำหรับดึงค่าปัจจุบันของสุขภาพผู้เล่น
     *
     * @return ค่าสุขภาพปัจจุบัน
     */
    public int getHealth() {
        return health;
    }

    /**
     * เมธอดสำหรับตั้งค่าสุขภาพของผู้เล่นใหม่และอัปเดต ProgressBar
     *
     * @param health ค่าสุขภาพใหม่ที่ต้องการตั้งค่า
     */
    public void setHealth(int health) {
        this.health = health;
        updateHealthBar();
    }
}
