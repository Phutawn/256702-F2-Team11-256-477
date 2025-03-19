package com.project;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.*;

public class ZombieShooterGame extends GameApplication {

    private Entity player;
    private static final double SPEED = 5;
    private static final double BULLET_SPEED = 10;
    private static final double ZOMBIE_SPEED = 2;
    private static final int ZOMBIE_SPAWN_INTERVAL = 10; // วินาที
    private boolean inputInitialized = false;
    private double lastDirX = 0;
    private double lastDirY = -1;
    private Random random = new Random();
    private boolean canShoot = true; // ตรวจสอบการยิงเพื่อให้มี delay

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Zombie Shooter Game");
        settings.setVersion("1.0");
        // ไม่เรียกใช้ setMainLoopFPS เนื่องจาก FXGL จัดการ FPS โดยอัตโนมัติ
    }

    @Override
    protected void initGame() {
        showMainMenu();
    }

    private void showMainMenu() {
        getGameScene().clearUINodes();
        UIManager.showMainMenu();
    }

    public void startGame() {
        getGameScene().clearUINodes();
        player = entityBuilder()
                .at(400, 300)
                .view(new Rectangle(40, 40, Color.BLUE))
                .buildAndAttach();

        spawnZombie();
        initInput();

        // ซอมบี้ใหม่เกิดนอกจอทุกๆ 10 วินาที
        run(() -> spawnZombieOutsideScreen(), Duration.seconds(ZOMBIE_SPAWN_INTERVAL));
    }

    @Override
    protected void initInput() {
        if (inputInitialized)
            return;
        inputInitialized = true;

        onKey(KeyCode.A, "Move Left", () -> {
            lastDirX = -1;
            lastDirY = 0;
            player.translateX(-SPEED);
        });
        onKey(KeyCode.D, "Move Right", () -> {
            lastDirX = 1;
            lastDirY = 0;
            player.translateX(SPEED);
        });
        onKey(KeyCode.W, "Move Up", () -> {
            lastDirX = 0;
            lastDirY = -1;
            player.translateY(-SPEED);
        });
        onKey(KeyCode.S, "Move Down", () -> {
            lastDirX = 0;
            lastDirY = 1;
            player.translateY(SPEED);
        });

        // ยิงกระสุนด้วยปุ่ม SPACE
        onKeyDown(KeyCode.SPACE, "Shoot Bullet", this::shootBullet);
    }

    /*@Override
    protected void initUI() {
        super.initUI();
        
        // โหลดไฟล์ CSS
        getGameScene().getRoot().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }*/


    private void shootBullet() {
        if (!canShoot)
            return;
        canShoot = false;
        // เก็บทิศทางสุดท้ายที่ผู้เล่นเคลื่อนที่ไว้
        final double bulletDirX = lastDirX;
        final double bulletDirY = lastDirY;

        // เริ่มยิงที่ตำแหน่งกึ่งกลางของผู้เล่น
        double startX = player.getX() + player.getWidth() / 2;
        double startY = player.getY() + player.getHeight() / 2;

        Entity bullet = entityBuilder()
                .at(startX, startY)
                .view(new Rectangle(10, 5, Color.YELLOW))
                .buildAndAttach();

        // เคลื่อนที่กระสุนแบบเส้นตรงที่อัตรา 30 FPS
        run(() -> {
            bullet.translate(bulletDirX * BULLET_SPEED, bulletDirY * BULLET_SPEED);
            // เมื่อกระสุนออกนอกขอบจอ ให้ลบออกจากโลกเกม
            if (bullet.getX() < 0 || bullet.getX() > getSettings().getWidth() ||
                bullet.getY() < 0 || bullet.getY() > getSettings().getHeight()) {
                bullet.removeFromWorld();
            }
        }, Duration.seconds(1.0 / 30));

        // ตั้งเวลา delay 1 วินาที เพื่อให้ยิงกระสุนครั้งถัดไปได้
        getGameTimer().runOnceAfter(() -> canShoot = true, Duration.seconds(1));
    }

    private void spawnZombie() {
        Entity zombie = entityBuilder()
                .at(100, 100)
                .view(new Rectangle(40, 40, Color.RED))
                .buildAndAttach();

        trackZombieMovement(zombie);
    }

    private void spawnZombieOutsideScreen() {
        int screenWidth = getSettings().getWidth();
        int screenHeight = getSettings().getHeight();
        double spawnX, spawnY;

        int edge = random.nextInt(4);
        switch (edge) {
            case 0: // ด้านซ้าย
                spawnX = -50;
                spawnY = random.nextDouble() * screenHeight;
                break;
            case 1: // ด้านขวา
                spawnX = screenWidth + 50;
                spawnY = random.nextDouble() * screenHeight;
                break;
            case 2: // ด้านบน
                spawnX = random.nextDouble() * screenWidth;
                spawnY = -50;
                break;
            default: // ด้านล่าง
                spawnX = random.nextDouble() * screenWidth;
                spawnY = screenHeight + 50;
                break;
        }

        Entity zombie = entityBuilder()
                .at(spawnX, spawnY)
                .view(new Rectangle(40, 40, Color.RED))
                .buildAndAttach();

        trackZombieMovement(zombie);
    }

    private void trackZombieMovement(Entity zombie) {
        // อัปเดตการเคลื่อนที่ของซอมบี้ที่อัตรา 30 FPS
        run(() -> {
            if (player != null) {
                double dx = (player.getX() + player.getWidth() / 2) - (zombie.getX() + zombie.getWidth() / 2);
                double dy = (player.getY() + player.getHeight() / 2) - (zombie.getY() + zombie.getHeight() / 2);
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance > 1) {
                    zombie.translateX(dx / distance * ZOMBIE_SPEED);
                    zombie.translateY(dy / distance * ZOMBIE_SPEED);
                }
            }
        }, Duration.seconds(1.0 / 30));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
