package com.project;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static com.almasb.fxgl.dsl.FXGL.*;

public class ZombieShooterGame extends GameApplication {

    
    public enum EntityType {
        PLAYER, BULLET, ZOMBIE, MAGAZINE, MEDICAL_SUPPLY
    }

    private Entity player;
    private static final double SPEED = 5;
    private static final double BULLET_SPEED = 600;
    private static final double ZOMBIE_SPEED = 2;
    private static final int ZOMBIE_SPAWN_INTERVAL = 10; 
    private boolean inputInitialized = false;
    private double lastDirX = 0;
    private double lastDirY = -1;
    protected Random random = new Random();
    private boolean canShoot = true; 
    private String playerName = "Player"; // ชื่อเริ่มต้น

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);  // ความกว้างของจอภาพ
        settings.setHeight(720);  // ความสูงของจอภาพ
        settings.setTitle("Zombie Shooter");
        settings.setVersion("1.0");
        settings.setCloseConfirmation(true);
        settings.setMainMenuEnabled(true);

        // เปิดใช้งานโหมดเต็มจอ
        settings.setFullScreenAllowed(true); // อนุญาตให้ใช้โหมดเต็มจอ
        settings.setFullScreenFromStart(true); // เริ่มเกมในโหมดเต็มจอ
    }

    @Override
    protected void initGame() {
        getGameScene().clearUINodes();
        UIManager.showNameInputScreen();
    }
    
    public void startGame() {
        getGameScene().setBackgroundColor(Color.BLACK);
        getGameScene().clearUINodes();
        
        player = entityBuilder()
                .at(400, 300)
                .viewWithBBox(new Rectangle(40, 40, Color.BLUE))
                //.view("smily.jpg")
                .with(new PlayerHealth())         
                .with(new PlayerAmmo(10))           
                .with(new PlayerMedicalSupplies())  
                .with(new CollidableComponent(true))
                .with(new MapBoundaryControl())     // ตรวจจับขอบแมพ
                .type(EntityType.PLAYER)
                .buildAndAttach();

        // เพิ่มชื่อผู้เล่นบนหัว
        Text playerNameText = new Text(playerName);
        playerNameText.setStyle("-fx-font-size: 18px; -fx-fill: white;");
        playerNameText.setTranslateY(-20); // วางไว้เหนือผู้เล่น
        player.getViewComponent().addChild(playerNameText);

        spawnZombieOutsideScreen();
        initInput();

        run(() -> spawnZombieOutsideScreen(), Duration.seconds(ZOMBIE_SPAWN_INTERVAL));
        FXGL.runOnce(() -> {
            // ดรอป Magazine ทุก 5 วินาที
            FXGL.getGameTimer().runAtInterval(() -> spawnMagazine(), Duration.seconds(10));
            // ดรอป Medical Supply ทุก 7 วินาที
            FXGL.getGameTimer().runAtInterval(() -> spawnMedicalSupply(), Duration.seconds(7));
        }, Duration.seconds(3));

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

        onKeyDown(KeyCode.SPACE, "Shoot Bullet", this::shootBullet);
    
        // กด H เพื่อคราฟชุดประถมพยาบาล (ถ้ามีวัสดุเพียงพอ)
        onKeyDown(KeyCode.H, "Craft First Aid Kit", () -> {
            boolean crafted = player.getComponent(PlayerMedicalSupplies.class).useSuppliesForFirstAid();
            if (crafted) {
                player.getComponent(PlayerHealth.class).heal(10);
            } else {
                FXGL.showMessage("ไม่พบวัสดุสำหรับคราฟชุดประถมพยาบาลเพียงพอ!");
            }
        });
    }

    @Override
    protected void initPhysics() {
        // เมื่อชนกับ Magazine เพิ่มกระสุน 5 นัด
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.MAGAZINE) {
            @Override
            protected void onCollisionBegin(Entity player, Entity magazine) {
                magazine.removeFromWorld();
                player.getComponent(PlayerAmmo.class).addAmmo(5);
            }
        });
        
        // เมื่อชนกับ Medical Supply ให้เพิ่มวัสดุในระบบคราฟ
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.MEDICAL_SUPPLY) {
            @Override
            protected void onCollisionBegin(Entity player, Entity supply) {
                supply.removeFromWorld();
                player.getComponent(PlayerMedicalSupplies.class).addSupply(1);
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.ZOMBIE) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity zombie) {
                bullet.removeFromWorld();
                zombie.removeFromWorld();
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.ZOMBIE) {
            @Override
            protected void onCollisionBegin(Entity player, Entity zombie) {
                player.getComponent(PlayerHealth.class).takeDamage(10);
            }
        });
    }

    private void shootBullet() {
        if (!canShoot)
            return;
        if (player.getComponent(PlayerAmmo.class).getAmmo() <= 0)
            return;
        canShoot = false;
        
        player.getComponent(PlayerAmmo.class).useAmmo(1);

        double dirX = lastDirX;
        double dirY = lastDirY;
        if (dirX == 0 && dirY == 0) {
            dirY = -1;
        }

        double startX = player.getX() + player.getWidth() / 2;
        double startY = player.getY() + player.getHeight() / 2;

        // เปลี่ยนกระสุนเป็นทรงกลม
        Entity bullet = entityBuilder()
                .at(startX, startY)
                .type(EntityType.BULLET)
                .viewWithBBox(new Circle(5, Color.YELLOW)) // ใช้ Circle แทน Rectangle
                .with(new CollidableComponent(true))
                .buildAndAttach();

        bullet.addComponent(new BulletControl(dirX, dirY, BULLET_SPEED));

        getGameTimer().runOnceAfter(() -> canShoot = true, Duration.seconds(1));
    }

    private void spawnMagazine() {
        entityBuilder()
                .type(EntityType.MAGAZINE)
                .at(random.nextInt(1280), random.nextInt(720))
                .viewWithBBox(new Circle(15, 15, 15, Color.YELLOW))
                .with(new CollidableComponent(true))
                .buildAndAttach();
    }
    
    private void spawnMedicalSupply() {
        entityBuilder()
                .type(EntityType.MEDICAL_SUPPLY)
                .at(random.nextInt(1280), random.nextInt(720))
                .viewWithBBox(new Circle(10, 10, 10, Color.LIGHTGREEN))
                .with(new CollidableComponent(true))
                .buildAndAttach();
    }
    
    private void spawnZombieOutsideScreen() {
        int screenWidth = getSettings().getWidth();
        int screenHeight = getSettings().getHeight();
        double spawnX, spawnY;

        int edge = random.nextInt(4);
        switch (edge) {
            case 0: spawnX = -50; spawnY = random.nextDouble() * screenHeight; break;
            case 1: spawnX = screenWidth + 50; spawnY = random.nextDouble() * screenHeight; break;
            case 2: spawnX = random.nextDouble() * screenWidth; spawnY = -50; break;
            default: spawnX = random.nextDouble() * screenWidth; spawnY = screenHeight + 50; break;
        }

        Entity zombie = entityBuilder()
                .at(spawnX, spawnY)
                .type(EntityType.ZOMBIE)
                .viewWithBBox(new Rectangle(40, 40, Color.RED))
                .with(new CollidableComponent(true))
                .buildAndAttach();

        zombie.addComponent(new ZombieAttackControl());
        trackZombieMovement(zombie);
    }
    
    private void trackZombieMovement(Entity zombie) {
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

    public void endGame() {
        FXGL.getGameController().exit(); // ออกจากเกม
    }

    public static void main(String[] args) {
        launch(args);
    }
}
