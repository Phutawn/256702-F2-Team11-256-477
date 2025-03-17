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

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Zombie Shooter Game");
        settings.setVersion("1.0");
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
        shootBullet();

        // สั่งให้เกิดซอมบี้ใหม่ทุกๆ 10 วินาที
        run(() -> spawnZombieOutsideScreen(), Duration.seconds(ZOMBIE_SPAWN_INTERVAL));
    }

    @Override
    protected void initInput() {
        if (inputInitialized) return;
        inputInitialized = true;

        FXGL.onKey(KeyCode.A, "Move Left", () -> {
            lastDirX = -1;
            lastDirY = 0;
            getPlayer().translateX(-SPEED);
        });
        FXGL.onKey(KeyCode.D, "Move Right", () -> {
            lastDirX = 1;
            lastDirY = 0;
            getPlayer().translateX(SPEED);
        });
        FXGL.onKey(KeyCode.W, "Move Up", () -> {
            lastDirX = 0;
            lastDirY = -1;
            getPlayer().translateY(-SPEED);
        });
        FXGL.onKey(KeyCode.S, "Move Down", () -> {
            lastDirX = 0;
            lastDirY = 1;
            getPlayer().translateY(SPEED);
        });

        FXGL.onKey(KeyCode.SPACE, "Shoot Bullet", this::shootBullet);
    }

    private Entity getPlayer() {
        return player;
    }

    private boolean canShoot = true; // เพิ่มตัวแปรเพื่อตรวจสอบว่าผู้เล่นสามารถยิงได้หรือไม่

private void shootBullet() {
    if (!canShoot) return; // ถ้ายังไม่ครบ 2 วินาที ให้ป้องกันการยิง

    canShoot = false; // ตั้งค่าเป็น false เพื่อป้องกันการยิงซ้ำ
    double bulletDirX = lastDirX;
    double bulletDirY = lastDirY;

    Entity bullet = entityBuilder()
            .at(player.getX() + player.getWidth() / 2, player.getY())
            .view(new Rectangle(10, 5, Color.YELLOW))
            .buildAndAttach();

    run(() -> {
        bullet.translateX(bulletDirX * BULLET_SPEED);
        bullet.translateY(bulletDirY * BULLET_SPEED);
        if (bullet.getX() < 0 || bullet.getX() > getSettings().getWidth() ||
            bullet.getY() < 0 || bullet.getY() > getSettings().getHeight()) {
            bullet.removeFromWorld();
        }
    }, Duration.seconds(0.05));

    // ตั้งค่าให้ยิงได้อีกครั้งหลังจาก 2 วินาที
    FXGL.getGameTimer().runOnceAfter(() -> canShoot = true, Duration.seconds(1));

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
        run(() -> {
            if (player != null) {
                double dx = player.getX() - zombie.getX();
                double dy = player.getY() - zombie.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance > 1) {
                    zombie.translateX(dx / distance * ZOMBIE_SPEED);
                    zombie.translateY(dy / distance * ZOMBIE_SPEED);
                }
            }
        }, Duration.seconds(0.1));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
