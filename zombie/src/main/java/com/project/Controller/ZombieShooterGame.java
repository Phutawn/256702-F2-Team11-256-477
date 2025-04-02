package com.project.Controller;

// นำเข้าไลบรารีและคลาสที่ใช้ในโปรเจค
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.entity.level.tiled.TMXLevelLoader;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.BoundingShape;
import com.project.Component.CharecterPlayer.ControllerComponent;
import com.project.Component.CharecterZombie.ZombieAnimationComponent;
import com.project.Factory.BackgroundFactory;
import com.project.Factory.CharacterFactory;
import com.project.Type.Player.PlayerType;
import com.project.View.UIManager;
import com.project.model.PlayerAmmo;
import com.project.model.PlayerHealth;
import com.project.model.PlayerMedicalSupplies;

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
import javafx.geometry.Point2D;
import com.almasb.fxgl.entity.SpawnData;
import com.project.Component.CharecterZombie.ZombieAttackControl;
import com.project.Component.CharecterPlayer.GunComponent;

public class ZombieShooterGame extends GameApplication {

    // ประกาศ Enum สำหรับกำหนดประเภทของ Entity ที่ใช้ในเกม
    public enum EntityType {
        PLAYER, BULLET, ZOMBIE, MAGAZINE, MEDICAL_SUPPLY, MEDICAL_KIT, WALL, BARRIER
    }

    // ตัวแปรสำหรับเก็บข้อมูลผู้เล่นและค่าคงที่ต่าง ๆ
    private Entity player;
    private static final double BULLET_SPEED = 600;
    private static final double ZOMBIE_SPEED = 0.5; // ลดความเร็วของซอมบี้ลงจาก 1 เป็น 0.5
    private boolean inputInitialized = false;
    private double lastDirX = 0;
    private double lastDirY = -1;
    protected Random random = new Random();
    private boolean canShoot = true;
    private String playerName = "Player"; // ชื่อเริ่มต้นของผู้เล่น
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

    // เพิ่มตัวแปรสำหรับแถบเลือด
    private Rectangle healthBar;
    private Rectangle healthBarBackground;
    private Text healthText;

    // เพิ่มตัวแปรสำหรับระบบ wave
    private int currentWave = 1;
    private boolean waveInProgress = false;
    private int zombiesRemaining = 0;
    private Text waveDisplay;

    // เพิ่มตัวแปรสำหรับเก็บ wave ก่อนหน้า
    private int previousWave = 0;

    // เพิ่มเมธอดสำหรับแสดง popup message
    private void showPopupMessage(String message) {
        Text popupText = new Text(message);
        popupText.setStyle("-fx-font-size: 20px; -fx-fill: white;");
        popupText.setTranslateX(getSettings().getWidth() / 2 - popupText.getLayoutBounds().getWidth() / 2);
        popupText.setTranslateY(50);
        getGameScene().addUINode(popupText);

        // หายไปหลังจาก 2 วินาที
        FXGL.runOnce(() -> {
            getGameScene().removeUINode(popupText);
        }, Duration.seconds(2));
    }

    // เมธอดสำหรับตั้งค่าชื่อผู้เล่น
    public void setPlayerName(String name) {
        this.playerName = name;
        FXGL.set("playerName", name); // เก็บชื่อในตัวแปรเกม
    }

    // เมธอดสำหรับกำหนดค่าเริ่มต้นของ Game Settings
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Zombie Shooter");
        settings.setVersion("1.0");
        settings.setCloseConfirmation(true);
        settings.setMainMenuEnabled(true);
        settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(false);
    }

    // เมธอดสำหรับเริ่มต้นเกมและการโหลด high score จากไฟล์
    @Override
    protected void initGame() {
        // ล้าง UI ที่มีอยู่และแสดงหน้าจอให้ผู้เล่นกรอกชื่อ
        getGameScene().clearUINodes();
        UIManager.showNameInputScreen();

        // อ่าน high score จากไฟล์ "highscore.txt" ในรูปแบบ "playerName,longestSurvivalTime,mostZombieKills"
        try (BufferedReader reader = new BufferedReader(new FileReader("highscore.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    try {
                        // ตรวจสอบว่าข้อมูลเป็นตัวเลขที่ถูกต้อง
                        if (parts[1].trim().matches("\\d+(\\.\\d+)?")) {
                            longestSurvivalTime = Double.parseDouble(parts[1].trim());
                        }
                        if (parts[2].trim().matches("\\d+")) {
                            mostZombieKills = Integer.parseInt(parts[2].trim());
                        }
                    } catch (NumberFormatException e) {
                        // ถ้าแปลงตัวเลขไม่สำเร็จ ให้ใช้ค่าเริ่มต้น
                        longestSurvivalTime = 0;
                        mostZombieKills = 0;
                    }
                }
            }
        } catch (IOException e) {
            // กรณีที่ไม่มีไฟล์หรือเกิดข้อผิดพลาด กำหนดค่าเริ่มต้นเป็น 0
            longestSurvivalTime = 0;
            mostZombieKills = 0;
        }
    }

    // เมธอดสำหรับกำหนดตัวแปรในเกม (Game Variables)
    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("map1", "scene1.tmx");
        vars.put("map2", "scene2.tmx");
        vars.put("Phase", true);
        vars.put("playerHealth", 100);    // พลังชีวิตเริ่มต้น
        vars.put("playerMaxHealth", 100); // พลังชีวิตสูงสุด
        vars.put("playerAmmo", 10);       // กระสุนเริ่มต้น
        vars.put("playerMaxAmmo", 100);   // กระสุนสูงสุด
        vars.put("playerSupplies", 0);    // ซัพพลายยาเริ่มต้น
        vars.put("playerMaxSupplies", 100); // ซัพพลายยาสูงสุด
        vars.put("playerName", "Player");  // ชื่อผู้เล่นเริ่มต้น
        vars.put("currentWave", 1); // เริ่มที่ wave 1
        vars.put("zombieKillCount", 0);
        vars.put("timeSurvived", 0.0);
        vars.put("currentSurvivalTime", 0.0);
        vars.put("highScore", 0.0);
        vars.put("highScoreWave", 1); // เริ่มที่ wave 1
    }

    // เมธอดสำหรับเริ่มต้นและตั้งค่าองค์ประกอบหลักของเกม
    public void startGame() {
        // ตั้งค่าสีพื้นหลังและล้าง UI nodes เดิม
        getGameScene().setBackgroundColor(Color.BLACK);
        getGameScene().clearUINodes();

        // เพิ่ม Entity Factories
        FXGL.getGameWorld().addEntityFactory(new BackgroundFactory());
        FXGL.getGameWorld().addEntityFactory(new CharacterFactory());
       
        // โหลด map
        String[] maps = {"scene1.tmx", "scene2.tmx"};
        String selectedMap = maps[random.nextInt(maps.length)];
        System.out.println("Loading map: " + selectedMap);
        
        try {
            map = FXGL.getAssetLoader().loadLevel(selectedMap, new TMXLevelLoader());
            FXGL.setLevelFromMap(selectedMap);
            System.out.println("Map loaded successfully. Width: " + map.getWidth() + ", Height: " + map.getHeight());
        } catch (Exception e) {
            System.err.println("Error loading map: " + e.getMessage());
            e.printStackTrace();
        }

        // ตั้งค่าแรงโน้มถ่วงของโลกฟิสิกส์เป็น 0
        getPhysicsWorld().setGravity(0, 0);

        // สร้างผู้เล่นในตำแหน่งสุ่ม
        double playerX = random.nextDouble(100, 700); // สุ่มตำแหน่ง X ระหว่าง 100-700
        double playerY = random.nextDouble(100, 700); // สุ่มตำแหน่ง Y ระหว่าง 100-700
        
        // ตรวจสอบว่าตำแหน่งสุ่มไม่อยู่ในกำแพง
        boolean validPosition = false;
        while (!validPosition) {
            validPosition = true;
            for (Entity barrier : getGameWorld().getEntitiesByType(EntityType.BARRIER)) {
                if (playerX < barrier.getRightX() && 
                    playerX + 20 > barrier.getX() && 
                    playerY < barrier.getBottomY() && 
                    playerY + 28 > barrier.getY()) {
                    validPosition = false;
                    playerX = random.nextDouble(100, 700);
                    playerY = random.nextDouble(100, 700);
                    break;
                }
            }
        }

        // สร้างผู้เล่นในตำแหน่งที่สุ่มได้
        SpawnData playerData = new SpawnData(playerX, playerY);
        player = FXGL.spawn("player", playerData);
        System.out.println("Player spawned at: " + playerX + ", " + playerY);

        FXGL.getGameScene().getViewport().bindToEntity(player, FXGL.getAppWidth()/2, FXGL.getAppHeight()/2);
        FXGL.getGameScene().getViewport().setZoom(2.8);
        FXGL.getGameScene().getViewport().setBounds(0, 0, 800, 800);
        FXGL.getGameScene().getViewport().setLazy(true);

        // ตั้งค่า UI
        setupUI();

        // รีเซ็ตสถานะเกม
        resetGameState();

        // เริ่มระบบ spawn
        startSpawningSystem();

        // เริ่มระบบจับเวลา
        startTimerSystem();

        // ตั้งค่าปุ่ม Save/Load
        setupSaveLoadButtons();

        // เริ่มระบบ Input และ Physics
        initInput();
        initPhysics();

        // อัพเดทแถบเลือดทุก 0.1 วินาที
        run(() -> updateHealthBar(), Duration.seconds(0.1));
    }

    private void setupUI() {
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

        // สร้างแถบเลือด
        healthBarBackground = new Rectangle(200, 20, Color.RED);
        healthBarBackground.setTranslateX(10);
        healthBarBackground.setTranslateY(40);
        getGameScene().addUINode(healthBarBackground);

        healthBar = new Rectangle(200, 20, Color.GREEN);
        healthBar.setTranslateX(10);
        healthBar.setTranslateY(40);
        getGameScene().addUINode(healthBar);

        healthText = new Text("HP: 100/100");
        healthText.setStyle("-fx-font-size: 16px; -fx-fill: white;");
        healthText.setTranslateX(10);
        healthText.setTranslateY(35);
        getGameScene().addUINode(healthText);

        // เพิ่มการแสดงผล wave
        waveDisplay = new Text("Wave: 1");
        waveDisplay.setStyle("-fx-font-size: 24px; -fx-fill: yellow;");
        waveDisplay.setTranslateX(getSettings().getWidth() / 2 - 50);
        waveDisplay.setTranslateY(40);
        getGameScene().addUINode(waveDisplay);
    }

    private void resetGameState() {
        timeSurvived = 0;
        currentSurvivalTime = 0;
        zombieKillCount = 0;
        zombieSpawnMultiplier = 1;
    }

    private void startSpawningSystem() {
        System.out.println("Starting wave system...");
        
        // เริ่ม wave แรกทันที
        FXGL.runOnce(() -> {
            startNewWave();
        }, Duration.seconds(2));

        // ตั้งเวลา spawn กระสุนทุก 10 วินาที
        FXGL.getGameTimer().runAtInterval(() -> {
            spawnMagazine();
        }, Duration.seconds(7));

        // ตั้งเวลา spawn ยาทุก 15 วินาที
        FXGL.getGameTimer().runAtInterval(() -> {
            spawnMedicalSupply();
        }, Duration.seconds(7));

        // ตั้งเวลา spawn กล่องยาทุก 30 วินาที
        FXGL.getGameTimer().runAtInterval(() -> {
            spawnMedicalKit();
        }, Duration.seconds(30));
    }

    private void startTimerSystem() {
        run(() -> {
            timeSurvived += 1.0 / 60;
            currentSurvivalTime = timeSurvived;
            timerDisplay.setText("Time: " + (int) timeSurvived);
            
            // อัพเดทคะแนนสูงสุดทุกวินาที
            updateHighScore();
        }, Duration.seconds(1.0 / 60));
    }

    private void setupSaveLoadButtons() {
        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-font-size: 16px;");
        saveButton.setFocusTraversable(false);
        saveButton.setOnAction(e -> saveGame());

        Button loadButton = new Button("Load");
        loadButton.setStyle("-fx-font-size: 16px;");
        loadButton.setFocusTraversable(false);
        loadButton.setOnAction(e -> loadGame());

        HBox saveLoadBox = new HBox(10, saveButton, loadButton);
        saveLoadBox.setTranslateX(getSettings().getWidth() / 2 - 50);
        saveLoadBox.setTranslateY(getSettings().getHeight() - 50);
        getGameScene().addUINode(saveLoadBox);
    }

    // เมธอดสำหรับรับค่า Input จากคีย์บอร์ด (เคลื่อนที่, ยิง, คราฟ First Aid Kit)
    @Override
    protected void initInput() {
        if (inputInitialized) return;
        FXGL.getInput().clearAll();

        // เพิ่มการผูกปุ่ม Space สำหรับการยิง
        FXGL.onKeyDown(KeyCode.SPACE, () -> {
            PlayerAmmo ammoComponent = player.getComponent(PlayerAmmo.class);
            if (ammoComponent != null && ammoComponent.useAmmo()) {
                // เรียกใช้แอนิเมชันยิงปืน
                GunComponent gunComponent = player.getComponent(GunComponent.class);
                if (gunComponent != null) {
                    gunComponent.shoot();
                }
                shootBullet();
            }
        });

        // เพิ่มการผูกปุ่ม H สำหรับใช้ฮีล
        FXGL.onKeyDown(KeyCode.H, () -> {
            PlayerMedicalSupplies suppliesComponent = player.getComponent(PlayerMedicalSupplies.class);
            PlayerHealth healthComponent = player.getComponent(PlayerHealth.class);
            if (suppliesComponent != null && healthComponent != null) {
                if (suppliesComponent.useSuppliesForFirstAid()) {
                    healthComponent.heal(20);
                    FXGL.showMessage("Healed +30 HP!");
                } else {
                    FXGL.showMessage("Not enough Food scraps! Need 3.");
                }
            }
        });

        // อัปเดตทิศทางล่าสุดเมื่อผู้เล่นเคลื่อนที่
        FXGL.getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                if (FXGL.getb("Phase") == true) {
                    player.getComponent(ControllerComponent.class).moveRight();
                    lastDirX = 1;
                    lastDirY = 0;
                }
            }

            @Override
            protected void onActionEnd() {
                if (FXGL.getb("Phase") == true) {
                    player.getComponent(ControllerComponent.class).stop();
                }
            }
        }, KeyCode.D);

        FXGL.getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                if (FXGL.getb("Phase") == true) {
                    player.getComponent(ControllerComponent.class).moveLeft();
                    lastDirX = -1;
                    lastDirY = 0;
                }
            }

            @Override
            protected void onActionEnd() {
                if (FXGL.getb("Phase") == true) {
                    player.getComponent(ControllerComponent.class).stop();
                }
            }
        }, KeyCode.A);

        FXGL.getInput().addAction(new UserAction("Up") {
            @Override
            protected void onAction() {
                if (FXGL.getb("Phase") == true) {
                    player.getComponent(ControllerComponent.class).moveUp();
                    lastDirX = 0;
                    lastDirY = -1;
                }
            }

            @Override
            protected void onActionEnd() {
                if (FXGL.getb("Phase") == true) {
                    player.getComponent(ControllerComponent.class).stop();
                }
            }
        }, KeyCode.W);

        FXGL.getInput().addAction(new UserAction("Down") {
            @Override
            protected void onAction() {
                if (FXGL.getb("Phase") == true) {
                    player.getComponent(ControllerComponent.class).moveDown();
                    lastDirX = 0;
                    lastDirY = 1;
                }
            }

            @Override
            protected void onActionEnd() {
                if (FXGL.getb("Phase") == true) {
                    player.getComponent(ControllerComponent.class).stop();
                }
            }
        }, KeyCode.S);

        // ตั้งค่า input ว่าได้ถูก initialize แล้ว
        inputInitialized = true;
    }

    // เมธอดสำหรับตั้งค่า Physics และการชนของ Entity ต่าง ๆ
    @Override
    protected void initPhysics() {
        // เมื่อผู้เล่นชนกับนิตยสารกระสุน
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.MAGAZINE) {
            @Override
            protected void onCollisionBegin(Entity player, Entity magazine) {
                PlayerAmmo ammoComponent = player.getComponent(PlayerAmmo.class);
                if (ammoComponent != null) {
                    ammoComponent.addAmmo(10);
                    showPopupMessage("+10 Ammo");
                }
                magazine.removeFromWorld();
            }
        });

        // เมื่อผู้เล่นชนกับวัสดุทางการแพทย์
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.MEDICAL_SUPPLY) {
            @Override
            protected void onCollisionBegin(Entity player, Entity supply) {
                PlayerMedicalSupplies suppliesComponent = player.getComponent(PlayerMedicalSupplies.class);
                if (suppliesComponent != null) {
                    suppliesComponent.addSupply(1);
                    showPopupMessage("+1 Food scraps get 3 for use");
                }
                supply.removeFromWorld();
            }
        });

        // เมื่อผู้เล่นชนกับกล่องยา
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.MEDICAL_KIT) {
            @Override
            protected void onCollisionBegin(Entity player, Entity medicalKit) {
                PlayerHealth healthComponent = player.getComponent(PlayerHealth.class);
                if (healthComponent != null) {
                    healthComponent.heal(100);
                    showPopupMessage("+100 HP");
                }
                medicalKit.removeFromWorld();
            }
        });

        // เมื่อกระสุนชนกับซอมบี้
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.ZOMBIE) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity zombie) {
                bullet.removeFromWorld();
                zombie.getComponent(ZombieAttackControl.class).stopMovement();
                zombie.getComponent(ZombieAnimationComponent.class).hit();
                
                FXGL.runOnce(() -> {
                    if (zombie != null && zombie.isActive()) {
                        zombie.removeFromWorld();
                        zombieKillCount++;
                        highScoreDisplay.setText("Longest Survival: " + (int) longestSurvivalTime 
                            + " sec\nMost Kills: " + mostZombieKills);
                    }
                }, Duration.seconds(1));
            }
        });

        // เมื่อผู้เล่นชนกับซอมบี้
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.ZOMBIE) {
            @Override
            protected void onCollisionBegin(Entity player, Entity zombie) {
                PlayerHealth healthComponent = player.getComponent(PlayerHealth.class);
                if (healthComponent != null) {
                    int currentHealth = healthComponent.getHealth();
                    healthComponent.takeDamage(10);
                    showPopupMessage("-10 HP");
                }
            }
        });
    }

    // เมธอดสำหรับยิงกระสุน
    private void shootBullet() {
        // ตรวจสอบสถานะการยิงและจำนวน Ammo ก่อนยิง
        if (!canShoot)
            return;
        if (player.getComponent(PlayerAmmo.class).getAmmo() <= 0)
            return;
        canShoot = false;

        double dirX = lastDirX;
        double dirY = lastDirY;
        // กำหนดทิศทางเริ่มต้นของกระสุน หากไม่มีการเคลื่อนที่ให้ยิงขึ้นด้านบน
        if (dirX == 0 && dirY == 0) {
            dirY = -1;
        }

        // คำนวณตำแหน่งเริ่มต้นของกระสุนให้ตรงกับศูนย์กลางของผู้เล่น
        double startX = player.getX() + player.getWidth() / 2;
        double startY = player.getY() + player.getHeight() / 2;

        // สร้าง Entity ของกระสุนโดยใช้ Factory
        SpawnData data = new SpawnData(startX, startY);
        data.put("dirX", dirX);
        data.put("dirY", dirY);
        FXGL.spawn("bullet", data);

        // กำหนดเวลาที่กระสุนสามารถยิงได้ครั้งต่อไป (Delay 1 วินาที)
        getGameTimer().runOnceAfter(() -> canShoot = true, Duration.seconds(1));
    }

    // เมธอดสำหรับ spawn Magazine ในตำแหน่งสุ่มบนหน้าจอ
    private void spawnMagazine() {
        // สร้างตำแหน่งสุ่มสำหรับนิตยสารกระสุน
        double x = FXGL.random(50, 750);
        double y = FXGL.random(50, 750);
        
        // ตรวจสอบระยะห่างจากผู้เล่น
        Entity player = FXGL.getGameWorld().getSingleton(EntityType.PLAYER);
        if (player != null) {
            Point2D playerPos = player.getPosition();
            while (playerPos.distance(x, y) < 100) {
                x = FXGL.random(50, 750);
                y = FXGL.random(50, 750);
            }
        }

        // ตรวจสอบการชนกับสิ่งกีดขวาง
        boolean validPosition = false;
        while (!validPosition) {
            validPosition = true;
            for (Entity barrier : FXGL.getGameWorld().getEntitiesByType(EntityType.BARRIER)) {
                if (x < barrier.getRightX() && 
                    x + 15 > barrier.getX() && 
                    y < barrier.getBottomY() && 
                    y + 15 > barrier.getY()) {
                    validPosition = false;
                    x = FXGL.random(50, 750);
                    y = FXGL.random(50, 750);
                    break;
                }
            }
        }

        // สร้างนิตยสารกระสุน
        Entity magazine = FXGL.spawn("magazine", x, y);
    }

    // เมธอดสำหรับ spawn Medical Supply ในตำแหน่งสุ่มบนหน้าจอ
    private void spawnMedicalSupply() {
        // สร้างตำแหน่งสุ่มสำหรับวัสดุทางการแพทย์
        double x = FXGL.random(50, 750);
        double y = FXGL.random(50, 750);
        
        // ตรวจสอบระยะห่างจากผู้เล่น
        Entity player = FXGL.getGameWorld().getSingleton(EntityType.PLAYER);
        if (player != null) {
            Point2D playerPos = player.getPosition();
            while (playerPos.distance(x, y) < 100) {
                x = FXGL.random(50, 750);
                y = FXGL.random(50, 750);
            }
        }

        // ตรวจสอบการชนกับสิ่งกีดขวาง
        boolean validPosition = false;
        while (!validPosition) {
            validPosition = true;
            for (Entity barrier : FXGL.getGameWorld().getEntitiesByType(EntityType.BARRIER)) {
                if (x < barrier.getRightX() && 
                    x + 10 > barrier.getX() && 
                    y < barrier.getBottomY() && 
                    y + 10 > barrier.getY()) {
                    validPosition = false;
                    x = FXGL.random(50, 750);
                    y = FXGL.random(50, 750);
                    break;
                }
            }
        }

        // สร้างวัสดุทางการแพทย์
        Entity supply = FXGL.spawn("medical_supply", x, y);
        
    }

    // เมธอดสำหรับ spawn กล่องยาในตำแหน่งสุ่มบนหน้าจอ
    private void spawnMedicalKit() {
        // สร้างตำแหน่งสุ่มสำหรับกล่องยา
        double x = FXGL.random(50, 750);
        double y = FXGL.random(50, 750);
        
        // ตรวจสอบระยะห่างจากผู้เล่น
        Entity player = FXGL.getGameWorld().getSingleton(EntityType.PLAYER);
        if (player != null) {
            Point2D playerPos = player.getPosition();
            while (playerPos.distance(x, y) < 100) {
                x = FXGL.random(50, 750);
                y = FXGL.random(50, 750);
            }
        }

        // ตรวจสอบการชนกับสิ่งกีดขวาง
        boolean validPosition = false;
        while (!validPosition) {
            validPosition = true;
            for (Entity barrier : FXGL.getGameWorld().getEntitiesByType(EntityType.BARRIER)) {
                if (x < barrier.getRightX() && 
                    x + 16 > barrier.getX() && 
                    y < barrier.getBottomY() && 
                    y + 16 > barrier.getY()) {
                    validPosition = false;
                    x = FXGL.random(50, 750);
                    y = FXGL.random(50, 750);
                    break;
                }
            }
        }

        // สร้างกล่องยา
        Entity medicalKit = FXGL.spawn("medical_kit", x, y);
    }

    // เมธอดสำหรับ spawn ซอมบี้ให้ออกนอกขอบจอ
    private void spawnZombieOutsideScreen() {
        System.out.println("Spawning zombies for wave " + currentWave);
        int zombiesToSpawn = Math.min(3, zombiesRemaining); // spawn ครั้งละ 3 ตัว
        
        for (int i = 0; i < zombiesToSpawn; i++) {
            try {
                // กำหนดขนาดแผนที่เป็น 800x800
                double mapWidth = 800;
                double mapHeight = 800;
                double spawnX = 0;
                double spawnY = 0;
                boolean validPosition = false;
                
                // พยายามหาตำแหน่งที่ถูกต้องจนกว่าจะเจอ
                while (!validPosition) {
                    // สุ่มตำแหน่งรอบๆ แผนที่
                    double angle = random.nextDouble() * 2 * Math.PI; // สุ่มมุม 0-360 องศา
                    double radius = 250 + random.nextDouble() * 50; // ระยะห่างจากกลางแผนที่ 250-300 หน่วย
                    
                    // หาจุดกึ่งกลางแผนที่
                    double centerX = mapWidth / 2;
                    double centerY = mapHeight / 2;
                    
                    // คำนวณตำแหน่ง spawn จากมุมและระยะทาง
                    spawnX = centerX + Math.cos(angle) * radius;
                    spawnY = centerY + Math.sin(angle) * radius;
                    
                    // ปรับตำแหน่งให้อยู่ในขอบเขตแผนที่
                    spawnX = Math.max(50, Math.min(mapWidth - 50, spawnX));
                    spawnY = Math.max(50, Math.min(mapHeight - 50, spawnY));

                    // ตรวจสอบระยะห่างจากผู้เล่น
                    if (player != null) {
                        double playerX = player.getX();
                        double playerY = player.getY();
                        double distance = Math.sqrt(
                            Math.pow(spawnX - playerX, 2) + 
                            Math.pow(spawnY - playerY, 2)
                        );
                        
                        // ถ้าอยู่ใกล้ผู้เล่นเกินไป ให้หาตำแหน่งใหม่
                        if (distance < 200) {
                            continue;
                        }
                    }

                    // ตรวจสอบว่าตำแหน่งที่จะ spawn ไม่อยู่ในกำแพง
                    validPosition = true;
                    for (Entity barrier : getGameWorld().getEntitiesByType(EntityType.BARRIER)) {
                        if (spawnX < barrier.getRightX() && 
                            spawnX + 40 > barrier.getX() && 
                            spawnY < barrier.getBottomY() && 
                            spawnY + 40 > barrier.getY()) {
                            validPosition = false;
                            break;
                        }
                    }
                }
        
                // สร้าง Entity ของซอมบี้โดยใช้ Factory
                SpawnData data = new SpawnData(spawnX, spawnY);
                Entity zombie = FXGL.spawn("zombie", data);
                if (zombie != null) {
                    // เริ่มติดตามการเคลื่อนที่ของซอมบี้ไปยังผู้เล่น
                    trackZombieMovement(zombie);
                    System.out.println("Successfully spawned zombie " + (i + 1) + " at: " + spawnX + ", " + spawnY);
                } else {
                    System.err.println("Failed to spawn zombie " + (i + 1));
                }
                
                // ลดจำนวนซอมบี้ที่เหลือ
                zombiesRemaining--;
                
                // ถ้ายังมีซอมบี้ที่ต้อง spawn ให้ spawn ตัวถัดไปหลังจาก 2 วินาที
                if (zombiesRemaining > 0) {
                    FXGL.runOnce(() -> spawnZombieOutsideScreen(), Duration.seconds(2));
                }
            } catch (Exception e) {
                System.err.println("Error spawning zombie: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    

    // เมธอดสำหรับติดตามการเคลื่อนที่ของซอมบี้ให้ตามผู้เล่น
    private void trackZombieMovement(Entity zombie) {
        run(() -> {
            if (player != null && zombie.isActive()) {
                // ตรวจสอบว่าซอมบี้ไม่ได้อยู่ในสถานะโดนยิง
                ZombieAttackControl zombieControl = zombie.getComponent(ZombieAttackControl.class);
                if (zombieControl.isMovementStopped()) {
                    return;
                }

                Point2D playerCenter = new Point2D(
                    player.getX() + player.getWidth() / 2,
                    player.getY() + player.getHeight() / 2
                );
                
                // คำนวณทิศทางและระยะทาง
                double dx = playerCenter.getX() - (zombie.getX() + zombie.getWidth() / 2);
                double dy = playerCenter.getY() - (zombie.getY() + zombie.getHeight() / 2);
                double distance = Math.sqrt(dx * dx + dy * dy);
                
                if (distance > 1) {
                    // เพิ่มความเร็วของซอมบี้
                    double speed = ZOMBIE_SPEED;
                    // คำนวณตำแหน่งใหม่
                    double newX = zombie.getX() + (dx / distance * speed);
                    double newY = zombie.getY() + (dy / distance * speed);
                    
                    // ตรวจสอบขอบเขตของแผนที่ (800x800)
                    if (newX >= 0 && newX <= 800 - zombie.getWidth()) {
                        // ตรวจสอบการชนกับ barrier
                        boolean canMoveX = true;
                        for (Entity barrier : getGameWorld().getEntitiesByType(EntityType.BARRIER)) {
                            if (newX < barrier.getRightX() && 
                                newX + zombie.getWidth() > barrier.getX() && 
                                zombie.getY() < barrier.getBottomY() && 
                                zombie.getY() + zombie.getHeight() > barrier.getY()) {
                                canMoveX = false;
                                break;
                            }
                        }
                        if (canMoveX) {
                            zombie.setX(newX);
                            // อัพเดทแอนิเมชันตามทิศทาง
                            ZombieAnimationComponent animation = zombie.getComponent(ZombieAnimationComponent.class);
                            if (dx > 0) {
                                animation.walkRight();
                            } else {
                                animation.walkLeft();
                            }
                        }
                    }
                    
                    if (newY >= 0 && newY <= 800 - zombie.getHeight()) {
                        // ตรวจสอบการชนกับ barrier
                        boolean canMoveY = true;
                        for (Entity barrier : getGameWorld().getEntitiesByType(EntityType.BARRIER)) {
                            if (zombie.getX() < barrier.getRightX() && 
                                zombie.getX() + zombie.getWidth() > barrier.getX() && 
                                newY < barrier.getBottomY() && 
                                newY + zombie.getHeight() > barrier.getY()) {
                                canMoveY = false;
                                break;
                            }
                        }
                        if (canMoveY) {
                            zombie.setY(newY);
                        }
                    }
                }
            }
        }, Duration.seconds(1.0 / 60));
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
                    + currentSurvivalTime + "," + zombieKillCount + "," + currentWave);
        } catch (IOException e) {
            e.printStackTrace();
        }
        showPopupMessage("Game Saved!");
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
                currentWave = Integer.parseInt(parts[7]); // โหลดค่า wave จากไฟล์
                previousWave = currentWave - 1; // ตั้งค่า wave ก่อนหน้า

                // อัปเดตสถานะของผู้เล่นและตัวแปรเกม
                player.setPosition(playerX, playerY);
                player.getComponent(PlayerHealth.class).setHealth(health);
                player.getComponent(PlayerAmmo.class).setAmmo(ammo);
                player.getComponent(PlayerMedicalSupplies.class).setSupplies(supplies);
                timeSurvived = savedTime;
                currentSurvivalTime = savedTime;
                zombieKillCount = savedZombieKills;
                FXGL.set("currentWave", currentWave); // อัพเดทค่าใน game vars
                waveDisplay.setText("Wave: " + currentWave);

                showPopupMessage("Game Loaded!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // เมธอดสำหรับสิ้นสุดเกมและออกจากเกม
    public void endGame() {
        FXGL.getGameController().exit();
    }

    // เมธอดสำหรับอัพเดทแถบเลือด
    private void updateHealthBar() {
        if (player != null) {
            PlayerHealth healthComponent = player.getComponent(PlayerHealth.class);
            int currentHealth = healthComponent.getHealth();
            int maxHealth = FXGL.geti("playerMaxHealth"); // ใช้ค่าสูงสุดจากตัวแปรเกม
            
            // คำนวณความกว้างของแถบเลือด
            double healthPercentage = (double) currentHealth / maxHealth;
            healthBar.setWidth(200 * healthPercentage);
            
            // อัพเดทข้อความแสดงเลือด
            healthText.setText("HP: " + currentHealth + "/" + maxHealth);
            
            // เปลี่ยนสีแถบเลือดตามจำนวนเลือดที่เหลือ
            if (healthPercentage > 0.6) {
                healthBar.setFill(Color.GREEN);
            } else if (healthPercentage > 0.3) {
                healthBar.setFill(Color.YELLOW);
            } else {
                healthBar.setFill(Color.RED);
            }
        }
    }

    // เมธอดสำหรับเริ่มระบบ wave
    private void startNewWave() {
        if (waveInProgress) return;
        
        // ตรวจสอบว่า wave เพิ่มขึ้นทีละ 1
        if (previousWave > 0 && currentWave != previousWave + 1) {
            currentWave = previousWave + 1;
            FXGL.set("currentWave", currentWave);
        }
        
        waveInProgress = true;
        zombiesRemaining = 5 + (currentWave * 2);
        waveDisplay.setText("Wave: " + currentWave);
        
        // แสดง popup message
        showPopupMessage("Wave " + currentWave + " Started!\nZombies: " + zombiesRemaining);
        
        spawnZombieOutsideScreen();
        
        FXGL.getGameTimer().runAtInterval(() -> {
            int currentZombies = FXGL.getGameWorld().getEntitiesByType(EntityType.ZOMBIE).size();
            if (currentZombies == 0) {
                waveInProgress = false;
                previousWave = currentWave; // เก็บค่า wave ก่อนหน้า
                currentWave++; // เพิ่ม wave ขึ้นทีละ 1
                FXGL.set("currentWave", currentWave); // อัพเดทค่าใน game vars
                FXGL.runOnce(() -> startNewWave(), Duration.seconds(5));
            }
        }, Duration.seconds(1));
    }

    // เมธอดสำหรับกลับไปยังเกมหลังจากดูคะแนนสูงสุด
    public void resumeGame() {
        // ล้าง UI nodes ทั้งหมด
        getGameScene().clearUINodes();
        
        // อัพเดท UI ของเกม
        UIManager.updateGameUI();
        
        // อัพเดทการแสดงผลคะแนนสูงสุด
        updateHighScoreDisplay();
    }

    // เมธอดสำหรับอัพเดทคะแนนสูงสุด
    private void updateHighScore() {
        // อัพเดทคะแนนสูงสุดถ้าเวลารอดชีวิตนานกว่าเดิม
        if (timeSurvived > longestSurvivalTime) {
            longestSurvivalTime = timeSurvived;
        }
        
        // อัพเดทคะแนนสูงสุดถ้าฆ่าซอมบี้ได้มากกว่าเดิม
        if (zombieKillCount > mostZombieKills) {
            mostZombieKills = zombieKillCount;
        }
        
        // บันทึกคะแนนสูงสุดลงไฟล์พร้อมชื่อผู้เล่น
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("highscore.txt", true))) {
            writer.write(String.format("%s,%.1f,%d\n", playerName, timeSurvived, zombieKillCount));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // เมธอดสำหรับอัพเดทการแสดงผลคะแนนสูงสุด
    private void updateHighScoreDisplay() {
        if (highScoreDisplay != null) {
            highScoreDisplay.setText("Longest Survival: " + (int) longestSurvivalTime 
                + " sec\nMost Kills: " + mostZombieKills);
        }
    }

    // เมธอด main สำหรับรันโปรแกรมเกม
    public static void main(String[] args) {
        launch(args);
    }
}
