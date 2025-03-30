package com.project;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.entity.level.tiled.TMXLevelLoader;
import com.almasb.fxgl.physics.CollisionHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
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
    private boolean inputInitialized = false;
    private double lastDirX = 0;
    private double lastDirY = -1;
    protected Random random = new Random();
    private boolean canShoot = true;
    private String playerName = "Player"; // ชื่อเริ่มต้น
    private Level map;

    // ฟิลด์สำหรับ Timer และสถานะเกมในรอบนี้
    public static double currentSurvivalTime = 0;
    private double timeSurvived = 0;
    private Text timerDisplay;

    // ฟิลด์สำหรับ high score (Longest Survival Time และ Most Zombie Kills)
    public static double longestSurvivalTime = 0;
    public static int mostZombieKills = 0;
    private Text highScoreDisplay;

    // ตัวแปรสำหรับนับจำนวนซอมบี้ที่ฆ่าได้ในรอบนี้
    public static int zombieKillCount = 0;

    // ตัวแปรสำหรับ spawn ซอมบี้แบบเพิ่มจำนวน
    private int zombieSpawnMultiplier = 1;

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Zombie Shooter");
        settings.setVersion("1.0");
        settings.setCloseConfirmation(true);
        settings.setMainMenuEnabled(true);
        settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(true);
    }

    @Override
    protected void initGame() {
        getGameScene().clearUINodes();
        UIManager.showNameInputScreen();

        // อ่าน high score จากไฟล์ (ถ้ามี) ในรูปแบบ "longestSurvivalTime,mostZombieKills"
        try (BufferedReader reader = new BufferedReader(new FileReader("highscore.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    longestSurvivalTime = Double.parseDouble(parts[0].trim());
                    mostZombieKills = Integer.parseInt(parts[1].trim());
                }
            }
        } catch (IOException e) {
            longestSurvivalTime = 0;
            mostZombieKills = 0;
        }
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {


        vars.put("map1", "scene1.tmx");
        
       
    }

    public void startGame() {
        getGameScene().setBackgroundColor(Color.BLACK);
        getGameScene().clearUINodes();
        map = FXGL.getAssetLoader().loadLevel(FXGL.gets("map1"), new TMXLevelLoader());
        FXGL.setLevelFromMap("scene1.tmx");
        // ตั้งค่า UI สำหรับ Timer (ด้านซ้าย) และ High Score (มุมขวาเลื่อนลงให้ชัด)
        timerDisplay = new Text("Time: 0");
        timerDisplay.setStyle("-fx-font-size: 20px; -fx-fill: white;");
        timerDisplay.setTranslateX(10);
        timerDisplay.setTranslateY(100);
        getGameScene().addUINode(timerDisplay);

        highScoreDisplay = new Text("Longest Survival: " + (int) longestSurvivalTime 
                + " sec\nMost Kills: " + mostZombieKills);
        highScoreDisplay.setStyle("-fx-font-size: 20px; -fx-fill: gold;");
        highScoreDisplay.setTranslateX(getSettings().getWidth() - 250);
        highScoreDisplay.setTranslateY(40);
        getGameScene().addUINode(highScoreDisplay);

        // รีเซ็ตสถานะเกม
        timeSurvived = 0;
        currentSurvivalTime = 0;
        zombieKillCount = 0;
        zombieSpawnMultiplier = 1;

        player = entityBuilder()
                .at(400, 300)
                .viewWithBBox(new Rectangle(40, 40, Color.BLUE))
                .with(new PlayerHealth())
                .with(new PlayerAmmo(10))
                .with(new PlayerMedicalSupplies())
                .with(new CollidableComponent(true))
                .with(new MapBoundaryControl())
                .type(EntityType.PLAYER)
                .buildAndAttach();

        // เพิ่มชื่อผู้เล่นบนหัว
        Text playerNameText = new Text(playerName);
        playerNameText.setStyle("-fx-font-size: 18px; -fx-fill: white;");
        playerNameText.setTranslateY(-20);
        player.getViewComponent().addChild(playerNameText);

        // เริ่ม spawn ซอมบี้ครั้งแรก
        spawnZombieOutsideScreen();
        initInput();

        // ทุก 10 วินาที spawn ซอมบี้จำนวน zombieSpawnMultiplier ครั้ง แล้วคูณ multiplier ด้วย 2
        FXGL.getGameTimer().runAtInterval(() -> {
            spawnZombieOutsideScreen();
            zombieSpawnMultiplier *= 2;
        }, Duration.seconds(10));

        FXGL.runOnce(() -> {
            FXGL.getGameTimer().runAtInterval(() -> spawnMagazine(), Duration.seconds(10));
            FXGL.getGameTimer().runAtInterval(() -> spawnMedicalSupply(), Duration.seconds(7));
        }, Duration.seconds(3));

        // ตัวจับเวลาที่อัปเดตทุก frame
        run(() -> {
            timeSurvived += 1.0 / 60;
            currentSurvivalTime = timeSurvived;
            timerDisplay.setText("Time: " + (int) timeSurvived);
        }, Duration.seconds(1.0 / 60));

        // สร้างปุ่ม Save และ Load ที่ด้านล่างตรงกลางจอ
        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-font-size: 16px;");
        saveButton.setFocusTraversable(false); // ไม่ให้รับ key event จาก spacebar
        saveButton.setOnAction(e -> saveGame());

        Button loadButton = new Button("Load");
        loadButton.setStyle("-fx-font-size: 16px;");
        loadButton.setFocusTraversable(false); // ไม่ให้รับ key event จาก spacebar
        loadButton.setOnAction(e -> loadGame());

        HBox saveLoadBox = new HBox(10, saveButton, loadButton);
        // กำหนดตำแหน่งให้อยู่ตรงกลางด้านล่าง (ปรับค่าตามขนาดจอ)
        saveLoadBox.setTranslateX(getSettings().getWidth() / 2 - 50);
        saveLoadBox.setTranslateY(getSettings().getHeight() - 50);
        getGameScene().addUINode(saveLoadBox);
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

        // เมื่อชนกับ Medical Supply เพิ่มวัสดุในระบบคราฟ
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.MEDICAL_SUPPLY) {
            @Override
            protected void onCollisionBegin(Entity player, Entity supply) {
                supply.removeFromWorld();
                player.getComponent(PlayerMedicalSupplies.class).addSupply(1);
            }
        });

        // เมื่อชนระหว่างกระสุนและซอมบี้ ให้ลบ entity ทั้งคู่ และเพิ่มจำนวนซอมบี้ที่ฆ่าได้
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.ZOMBIE) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity zombie) {
                bullet.removeFromWorld();
                zombie.removeFromWorld();
                zombieKillCount++;
            }
        });

        // เมื่อชนระหว่างผู้เล่นและซอมบี้ ให้ผู้เล่นเสียเลือด
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

        Entity bullet = entityBuilder()
                .at(startX, startY)
                .type(EntityType.BULLET)
                .viewWithBBox(new Circle(5, Color.YELLOW))
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
        for (int i = 0; i < zombieSpawnMultiplier; i++) {
            int screenWidth = getSettings().getWidth();
            int screenHeight = getSettings().getHeight();
            double spawnX, spawnY;
            int edge = random.nextInt(4);
            switch (edge) {
                case 0:
                    spawnX = -50;
                    spawnY = random.nextDouble() * screenHeight;
                    break;
                case 1:
                    spawnX = screenWidth + 50;
                    spawnY = random.nextDouble() * screenHeight;
                    break;
                case 2:
                    spawnX = random.nextDouble() * screenWidth;
                    spawnY = -50;
                    break;
                default:
                    spawnX = random.nextDouble() * screenWidth;
                    spawnY = screenHeight + 50;
                    break;
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

    // เมธอดสำหรับเซฟสถานะเกมไปที่ไฟล์ "savegame.txt"
    private void saveGame() {
        double playerX = player.getX();
        double playerY = player.getY();
        int health = player.getComponent(PlayerHealth.class).getHealth();
        int ammo = player.getComponent(PlayerAmmo.class).getAmmo();
        int supplies = player.getComponent(PlayerMedicalSupplies.class).getSupplies();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("savegame.txt"))) {
            writer.write(playerX + "," + playerY + "," + health + "," + ammo + "," + supplies + "," 
                    + currentSurvivalTime + "," + zombieKillCount + "," + zombieSpawnMultiplier);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FXGL.showMessage("Game Saved!");
    }

    // เมธอดสำหรับโหลดสถานะเกมจากไฟล์ "savegame.txt"
    private void loadGame() {
        try (BufferedReader reader = new BufferedReader(new FileReader("savegame.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                String[] parts = line.split(",");
                double playerX = Double.parseDouble(parts[0]);
                double playerY = Double.parseDouble(parts[1]);
                int health = Integer.parseInt(parts[2]);
                int ammo = Integer.parseInt(parts[3]);
                int supplies = Integer.parseInt(parts[4]);
                double savedTime = Double.parseDouble(parts[5]);
                int savedZombieKills = Integer.parseInt(parts[6]);
                int savedMultiplier = Integer.parseInt(parts[7]);

                player.setPosition(playerX, playerY);
                player.getComponent(PlayerHealth.class).setHealth(health);
                player.getComponent(PlayerAmmo.class).setAmmo(ammo);
                player.getComponent(PlayerMedicalSupplies.class).setSupplies(supplies);
                timeSurvived = savedTime;
                currentSurvivalTime = savedTime;
                zombieKillCount = savedZombieKills;
                zombieSpawnMultiplier = savedMultiplier;

                FXGL.showMessage("Game Loaded!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void endGame() {
        FXGL.getGameController().exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
