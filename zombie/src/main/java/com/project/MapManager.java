package com.project;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.paint.Color;

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

        // เปลี่ยนสีพื้นหลังแบบสุ่ม เพื่อบ่งชี้ว่าได้เปลี่ยนแมพแล้ว
        Color newBg = Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
        FXGL.getGameScene().setBackgroundColor(newBg);

        // คำนวณตำแหน่งใหม่ของผู้เล่นให้ทะลุไปยังอีกฝั่งของแมพ
        double newX = player.getX();
        double newY = player.getY();
        double screenWidth = 800;  // ปรับตามขนาดหน้าจอจริง
        double screenHeight = 600; // ปรับตามขนาดหน้าจอจริง

        switch (direction) {
            case "UP":
                newY = screenHeight - player.getHeight(); // ถ้าชนขอบบน -> โผล่จากขอบล่าง
                break;
            case "DOWN":
                newY = 0; // ถ้าชนขอบล่าง -> โผล่จากขอบบน
                break;
            case "LEFT":
                newX = screenWidth - player.getWidth(); // ถ้าชนขอบซ้าย -> โผล่จากขอบขวา
                break;
            case "RIGHT":
                newX = 0; // ถ้าชนขอบขวา -> โผล่จากขอบซ้าย
                break;
        }

        // ตั้งค่าตำแหน่งใหม่ให้กับผู้เล่น
        player.setPosition(newX, newY);
    }
}
