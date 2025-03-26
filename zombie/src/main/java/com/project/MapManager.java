package com.project;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

public class MapManager {

    private static final Random random = new Random();

    public static void changeMap(Entity player, String direction) {
        // สร้างสำเนาของรายการ Entity ก่อนลบ เพื่อป้องกัน ConcurrentModificationException
        ArrayList<Entity> entitiesCopy = new ArrayList<>(FXGL.getGameWorld().getEntities());
        for (Entity entity : entitiesCopy) {
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
            changeMap(player, direction);

            // เปลี่ยนสีพื้นหลังของแมพใหม่ (ถ้าต้องการ)
            Color newBg = Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
            FXGL.getGameScene().setBackgroundColor(newBg);

            // สร้างเอฟเฟกต์ fade in (ค่อยๆ จาง)
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), fadeLayer);
            fadeIn.setFromValue(1); // เริ่มจากทึบ (ดำสนิท)
            fadeIn.setToValue(0);   // ไปจนถึงโปร่งใส
            fadeIn.setOnFinished(ev -> FXGL.getGameScene().removeUINode(fadeLayer)); // ลบเลเยอร์สีดำเมื่อจางหมด
            fadeIn.play();
        });

        // เริ่มต้น fade out
        fadeOut.play();
    }
}
