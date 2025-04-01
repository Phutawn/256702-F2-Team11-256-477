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
import com.project.Factory.BackgroundFactory;
import com.project.Factory.CharacterFactory;
import com.project.Type.Player.PlayerType;
import com.project.View.UIManager;
import com.project.model.PlayerAmmo;
import com.project.model.PlayerHealth;
import com.project.model.PlayerMedicalSupplies;
import com.project.model.ZombieAnimationComponent;
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

public class ZombieShooterGame extends GameApplication {

    // ประกาศ Enum สำหรับกำหนดประเภทของ Entity ที่ใช้ในเกม
    public enum EntityType {
        PLAYER, BULLET, ZOMBIE, MAGAZINE, MEDICAL_SUPPLY, WALL, BARRIER
    }

    // ตัวแปรสำหรับเก็บข้อมูลผู้เล่นและค่าคงที่ต่าง ๆ
    private Entity player;
    private static final double BULLET_SPEED = 600;
    private static final double ZOMBIE_SPEED = 1;
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

    // เมธอดสำหรับตั้งค่าชื่อผู้เล่น
    public void setPlayerName(String name) {
        this.playerName = name;
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

        // อ่าน high score จากไฟล์ "highscore.txt" ในรูปแบบ "longestSurvivalTime,mostZombieKills"
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

        // หาผู้เล่นและตั้งค่า viewport
        try {
            player = FXGL.getGameWorld().getEntitiesByType(PlayerType.PLAYER).get(0);
            System.out.println("Player found at: " + player.getX() + ", " + player.getY());
        } catch (Exception e) {
            System.err.println("Error finding player: " + e.getMessage());
            e.printStackTrace();
        }

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
    }

    private void resetGameState() {
        timeSurvived = 0;
        currentSurvivalTime = 0;
        zombieKillCount = 0;
        zombieSpawnMultiplier = 1;
    }

    private void startSpawningSystem() {
        System.out.println("Starting spawning system...");
        
        // เริ่ม spawn ซอมบี้ครั้งแรกทันที
        FXGL.runOnce(() -> {
            System.out.println("Spawning initial wave of zombies...");
            spawnZombieOutsideScreen();
        }, Duration.seconds(2));

        // ตั้งเวลาให้ทุก 5 วินาที spawn ซอมบี้เพิ่มจำนวน
        FXGL.getGameTimer().runAtInterval(() -> {
            System.out.println("Spawning new wave of zombies. Multiplier: " + zombieSpawnMultiplier);
            spawnZombieOutsideScreen();
            zombieSpawnMultiplier++;
        }, Duration.seconds(5));
    }

    private void startTimerSystem() {
        run(() -> {
            timeSurvived += 1.0 / 60;
            currentSurvivalTime = timeSurvived;
            timerDisplay.setText("Time: " + (int) timeSurvived);
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
        // ตรวจสอบว่า input ถูก initialize แล้วหรือยัง
        if (inputInitialized) {
            return;
        }

        // ล้าง input bindings เก่า
        FXGL.getInput().clearAll();

        // เพิ่มการผูกปุ่ม Space สำหรับการยิง
        FXGL.onKeyDown(KeyCode.SPACE, () -> {
            if (player != null && player.getComponent(PlayerAmmo.class).getAmmo() > 0) {
                shootBullet();
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
        // เมื่อผู้เล่นชนกับนิตยสารกระสุน ให้เพิ่มกระสุน
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.MAGAZINE) {
            @Override
            protected void onCollisionBegin(Entity player, Entity magazine) {
                player.getComponent(PlayerAmmo.class).addAmmo(10);
                magazine.removeFromWorld();
            }
        });

        // เมื่อผู้เล่นชนกับวัสดุทางการแพทย์ ให้เพิ่มเลือด
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.MEDICAL_SUPPLY) {
            @Override
            protected void onCollisionBegin(Entity player, Entity supply) {
                player.getComponent(PlayerHealth.class).heal(20);
                supply.removeFromWorld();
            }
        });

        // เมื่อกระสุนชนกับซอมบี้ ให้ซอมบี้ตาย
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.ZOMBIE) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity zombie) {
                bullet.removeFromWorld();
                zombie.removeFromWorld();
                zombieKillCount++;
                highScoreDisplay.setText("Longest Survival: " + (int) longestSurvivalTime 
                    + " sec\nMost Kills: " + mostZombieKills);
            }
        });

        // เมื่อผู้เล่นชนกับซอมบี้ ให้ผู้เล่นเสียเลือด
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.ZOMBIE) {
            @Override
            protected void onCollisionBegin(Entity player, Entity zombie) {
                System.out.println("Zombie hit player! Ending game...");
                player.getComponent(PlayerHealth.class).takeDamage(10);
                endGame(); // จบเกมทันทีเมื่อซอมบี้ชนกับผู้เล่น
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

        // ใช้ Ammo 1 นัดสำหรับยิงกระสุน
        player.getComponent(PlayerAmmo.class).useAmmo(1);

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
        FXGL.spawn("magazine", random.nextInt(1280), random.nextInt(720));
    }

    // เมธอดสำหรับ spawn Medical Supply ในตำแหน่งสุ่มบนหน้าจอ
    private void spawnMedicalSupply() {
        FXGL.spawn("medical_supply", random.nextInt(1280), random.nextInt(720));
    }

    // เมธอดสำหรับ spawn ซอมบี้ให้ออกนอกขอบจอ
    private void spawnZombieOutsideScreen() {
        System.out.println("Attempting to spawn " + zombieSpawnMultiplier + " zombies");
        for (int i = 0; i < zombieSpawnMultiplier; i++) {
            try {
                // กำหนดขนาดแผนที่เป็น 800x800
                double mapWidth = 800;
                double mapHeight = 800;
                double spawnX = 0;
                double spawnY = 0;
                boolean validPosition = false;
                
                // พยายามหาตำแหน่งที่ถูกต้องจนกว่าจะเจอ
                while (!validPosition) {
                    // เลือกสุ่มด้านข้างของแผนที่ที่จะ spawn ซอมบี้
                    int edge = random.nextInt(4);
                    switch (edge) {
                        case 0: // ซ้าย
                            spawnX = 50; // เริ่มจากขอบซ้ายของแผนที่
                            spawnY = random.nextDouble() * (mapHeight - 100) + 50; // อยู่ระหว่าง 50 ถึง 750
                            break;
                        case 1: // ขวา
                            spawnX = mapWidth - 50; // เริ่มจากขอบขวาของแผนที่
                            spawnY = random.nextDouble() * (mapHeight - 100) + 50;
                            break;
                        case 2: // บน
                            spawnX = random.nextDouble() * (mapWidth - 100) + 50; // อยู่ระหว่าง 50 ถึง 750
                            spawnY = 50; // เริ่มจากขอบบนของแผนที่
                            break;
                        default: // ล่าง
                            spawnX = random.nextDouble() * (mapWidth - 100) + 50;
                            spawnY = mapHeight - 50; // เริ่มจากขอบล่างของแผนที่
                            break;
                    }

                    // ตรวจสอบระยะห่างจากผู้เล่น
                    if (player != null) {
                        double playerX = player.getX();
                        double playerY = player.getY();
                        double distance = Math.sqrt(
                            Math.pow(spawnX - playerX, 2) + 
                            Math.pow(spawnY - playerY, 2)
                        );
                        
                        // ถ้าอยู่ใกล้ผู้เล่นเกินไป ให้เลื่อนตำแหน่งออกไป
                        if (distance < 200) {
                            double angle = Math.atan2(spawnY - playerY, spawnX - playerX);
                            spawnX = playerX + Math.cos(angle) * 200;
                            spawnY = playerY + Math.sin(angle) * 200;
                            
                            // ตรวจสอบว่าตำแหน่งใหม่ยังอยู่ในขอบเขตแผนที่
                            spawnX = Math.max(50, Math.min(mapWidth - 50, spawnX));
                            spawnY = Math.max(50, Math.min(mapHeight - 50, spawnY));
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
            } catch (Exception e) {
                System.err.println("Error spawning zombie " + (i + 1) + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    

    // เมธอดสำหรับติดตามการเคลื่อนที่ของซอมบี้ให้ตามผู้เล่น
    private void trackZombieMovement(Entity zombie) {
        run(() -> {
            if (player != null && zombie.isActive()) {
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

                // อัปเดตสถานะของผู้เล่นและตัวแปรเกมให้ตรงกับค่าที่โหลดมา
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

    // เมธอดสำหรับสิ้นสุดเกมและออกจากเกม
    public void endGame() {
        FXGL.getGameController().exit();
    }

    // เมธอด main สำหรับรันโปรแกรมเกม
    public static void main(String[] args) {
        launch(args);
    }
}
