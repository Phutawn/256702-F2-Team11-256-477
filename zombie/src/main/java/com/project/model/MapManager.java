package com.project.model;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.project.Controller.ZombieShooterGame;
import com.project.Controller.ZombieShooterGame.EntityType;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Random;

public class MapManager {

    // สร้างตัวแปร Random สำหรับใช้งานสุ่มค่าในคลาสนี้
    private static final Random random = new Random();

    /**
     * เปลี่ยนแปลงแมพ (Map) โดยการลบ Entity ทั้งหมดที่ไม่ใช่ผู้เล่นออกจากโลกเกม
     * และคำนวณตำแหน่งใหม่ของผู้เล่นให้ทะลุออกไปยังอีกฝั่งของแมพ
     *
     * @param player    Entity ของผู้เล่น
     * @param direction ทิศทางที่ผู้เล่นออกจากแมพ ("UP", "DOWN", "LEFT", "RIGHT")
     */
    public static void changeMap(Entity player, String direction) {
        // สร้างสำเนาของรายการ Entity ก่อนลบ เพื่อป้องกัน ConcurrentModificationException
        ArrayList<Entity> entitiesCopy = new ArrayList<>(FXGL.getGameWorld().getEntities());
        for (Entity entity : entitiesCopy) {
            // ตรวจสอบว่าถ้า Entity ไม่ใช่ผู้เล่น ให้ลบออกจากโลกเกม
            if (entity.getType() != ZombieShooterGame.EntityType.PLAYER) {
                entity.removeFromWorld();
            }
        }

        // คำนวณตำแหน่งใหม่ของผู้เล่นให้ทะลุไปยังอีกฝั่งของแมพ
        double newX = player.getX();
        double newY = player.getY();
        double screenWidth = 1280;  // ปรับตามขนาดหน้าจอใหม่
        double screenHeight = 720;  // ปรับตามขนาดหน้าจอใหม่
        double offset = 12;         // ระยะห่างจากขอบแมพ

        // เลือกตำแหน่งใหม่ตามทิศทางที่ระบุไว้
        switch (direction) {
            case "UP":
                newY = screenHeight - player.getHeight() - offset; // ถ้าชนขอบบน -> โผล่จากขอบล่าง
                break;
            case "DOWN":
                newY = offset; // ถ้าชนขอบล่าง -> โผล่จากขอบบน
                break;
            case "LEFT":
                newX = screenWidth - player.getWidth() - offset; // ถ้าชนขอบซ้าย -> โผล่จากขอบขวา
                break;
            case "RIGHT":
                newX = offset; // ถ้าชนขอบขวา -> โผล่จากขอบซ้าย
                break;
        }

        // ตั้งค่าตำแหน่งใหม่ให้กับผู้เล่น
        player.setPosition(newX, newY);
    }

    /**
     * แสดงฉาก Cutscene ด้วยเอฟเฟกต์ Fade Transition ก่อนที่จะเปลี่ยนแมพ
     *
     * @param player    Entity ของผู้เล่น
     * @param direction ทิศทางที่ต้องการเปลี่ยนแมพ
     */
    public static void showCutsceneAndChangeMap(Entity player, String direction) {
        // สร้างเลเยอร์สีดำเพื่อใช้สำหรับเอฟเฟกต์ fade
        Node fadeLayer = new javafx.scene.shape.Rectangle(
                FXGL.getAppWidth(),
                FXGL.getAppHeight(),
                Color.BLACK
        );
        fadeLayer.setOpacity(0); // เริ่มต้นด้วยความโปร่งใส
        FXGL.getGameScene().addUINode(fadeLayer);

        // สร้างเอฟเฟกต์ fade out (ค่อยๆ ดำ)
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), fadeLayer);
        fadeOut.setFromValue(0); // เริ่มจากโปร่งใส
        fadeOut.setToValue(1);   // ไปจนถึงทึบ (ดำสนิท)

        // เมื่อ fade out เสร็จ ให้เปลี่ยนแมพ
        fadeOut.setOnFinished(e -> {
            // เปลี่ยนตำแหน่งของผู้เล่นตามทิศทางที่ระบุ
            changeMap(player, direction);

            // เปลี่ยนสีพื้นหลังของแมพใหม่ (ถ้าต้องการ) โดยสุ่มสีใหม่
            Color newBg = Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
            FXGL.getGameScene().setBackgroundColor(newBg);

            // สร้างเอฟเฟกต์ fade in (ค่อยๆ จาง) เพื่อให้ภาพกลับมาเห็นได้ชัดเจนอีกครั้ง
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), fadeLayer);
            fadeIn.setFromValue(1); // เริ่มจากทึบ (ดำสนิท)
            fadeIn.setToValue(0);   // ไปจนถึงโปร่งใส
            fadeIn.setOnFinished(ev -> FXGL.getGameScene().removeUINode(fadeLayer)); // ลบเลเยอร์สีดำเมื่อจางหมด
            fadeIn.play();
        });

        // เริ่มต้นการทำงานของเอฟเฟกต์ fade out
        fadeOut.play();
    }
}
