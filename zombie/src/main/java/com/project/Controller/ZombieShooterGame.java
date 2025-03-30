package com.project.Controller;

// นำเข้าไลบรารีและคลาสที่ใช้ในโปรเจค
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.entity.level.tiled.TMXLevelLoader;
import com.almasb.fxgl.physics.CollisionHandler;
import com.project.View.UIManager;
import com.project.model.PlayerAmmo;
import com.project.model.PlayerHealth;
import com.project.model.PlayerMedicalSupplies;
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

    // ประกาศ Enum สำหรับกำหนดประเภทของ Entity ที่ใช้ในเกม
    public enum EntityType {
        PLAYER, BULLET, ZOMBIE, MAGAZINE, MEDICAL_SUPPLY, WALL
    }

    // ตัวแปรสำหรับเก็บข้อมูลผู้เล่นและค่าคงที่ต่าง ๆ
    private Entity player;
    private static final double SPEED = 1;
    private static final double BULLET_SPEED = 600;
    private static final double ZOMBIE_SPEED = 2;
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
    }

    
    // เมธอดสำหรับเริ่มต้นและตั้งค่าองค์ประกอบหลักของเกม
    public void startGame() {
        // ตั้งค่าสีพื้นหลังและล้าง UI nodes เดิม
        getGameScene().setBackgroundColor(Color.BLACK);
        getGameScene().clearUINodes();
        FXGL.getGameWorld().addEntityFactory(new BackgroundFactory());
        
        // สุ่มเลือกแผนที่
        String[] maps = {"scene1.tmx", "scene2.tmx"};
        String selectedMap = maps[random.nextInt(maps.length)];

        // โหลด Level map จากไฟล์ TMX
        map = FXGL.getAssetLoader().loadLevel(selectedMap, new TMXLevelLoader());
        FXGL.setLevelFromMap(selectedMap);

        // ตั้งค่า UI สำหรับ Timer และ High Score
        setupUI();

        // รีเซ็ตสถานะและค่าตัวแปรสำหรับรอบเกมใหม่
        resetGameState();

        // สร้าง Entity ของผู้เล่น
        player = entityBuilder()
                .at(400, 300)
                .viewWithBBox(new Rectangle(40, 40, Color.BLUE))
                .with(new PlayerHealth())
                .with(new PlayerAmmo(10))
                .with(new PlayerMedicalSupplies())
                .with(new CollidableComponent(true))
                .type(EntityType.PLAYER)
                .buildAndAttach();

        // ตั้งค่ากล้องให้ติดตามตัวละคร
        setupCamera();

        // เริ่ม spawn ซอมบี้ครั้งแรก
        spawnZombieOutsideScreen();
        // เริ่มต้นรับค่า Input จากผู้เล่น
        initInput();

        // ตั้งเวลาให้ทุก 10 วินาที spawn ซอมบี้เพิ่มจำนวนตาม zombieSpawnMultiplier 
        setupTimers();
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

    private void setupCamera() {
        FXGL.getGameScene().getViewport().bindToEntity(player, getSettings().getWidth() / 2.0, getSettings().getHeight() / 2.0);
        FXGL.getGameScene().getViewport().setBounds(0, 0, (int) map.getWidth(), (int) map.getHeight());
        FXGL.getGameScene().getViewport().setZoom(2.5); // ค่า 2.5 คือระดับการซูม (ปรับได้ตามต้องการ)
    }

    // เมธอดสำหรับรับค่า Input จากคีย์บอร์ด (เคลื่อนที่, ยิง, คราฟ First Aid Kit)
    @Override
    protected void initInput() {
        // ตรวจสอบว่า Input ได้ถูกกำหนดไว้แล้วหรือไม่
        if (inputInitialized)
            return;
        inputInitialized = true;

        // กำหนดการเคลื่อนที่ของผู้เล่นด้วยคีย์ A, D, W, S
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

        // กำหนดการยิงกระสุน เมื่อกด SPACE
        onKeyDown(KeyCode.SPACE, "Shoot Bullet", this::shootBullet);

        // กำหนดการคราฟ First Aid Kit เมื่อกด H (ถ้ามีวัสดุเพียงพอ)
        onKeyDown(KeyCode.H, "Craft First Aid Kit", this::craftFirstAidKit);
    }

    private void craftFirstAidKit() {
        boolean crafted = player.getComponent(PlayerMedicalSupplies.class).useSuppliesForFirstAid();
        if (crafted) {
            player.getComponent(PlayerHealth.class).heal(10);
        } else {
            FXGL.showMessage("ไม่พบวัสดุสำหรับคราฟชุดประถมพยาบาลเพียงพอ!");
        }
    }

    // เมธอดสำหรับตั้งค่า Physics และการชนของ Entity ต่าง ๆ
    @Override
    protected void initPhysics() {
        // เมื่อผู้เล่นชนกับ Magazine ให้เพิ่ม Ammo 5 นัด
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.MAGAZINE) {
            @Override
            protected void onCollisionBegin(Entity player, Entity magazine) {
                magazine.removeFromWorld();
                player.getComponent(PlayerAmmo.class).addAmmo(5);
            }
        });

        // เมื่อผู้เล่นชนกับ Medical Supply ให้เพิ่มวัสดุสำหรับคราฟ
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.MEDICAL_SUPPLY) {
            @Override
            protected void onCollisionBegin(Entity player, Entity supply) {
                supply.removeFromWorld();
                player.getComponent(PlayerMedicalSupplies.class).addSupply(1);
            }
        });

        // เมื่อกระสุนชนกับซอมบี้ ให้ลบทั้งกระสุนและซอมบี้ พร้อมเพิ่มค่า zombieKillCount
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.ZOMBIE) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity zombie) {
                bullet.removeFromWorld();
                zombie.removeFromWorld();
                zombieKillCount++;
            }
        });

        // เมื่อผู้เล่นชนกับซอมบี้ ให้ผู้เล่นเสียเลือด
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.ZOMBIE) {
            @Override
            protected void onCollisionBegin(Entity player, Entity zombie) {
                player.getComponent(PlayerHealth.class).takeDamage(10);
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

        // สร้าง Entity ของกระสุน
        Entity bullet = entityBuilder()
                .at(startX, startY)
                .type(EntityType.BULLET)
                .viewWithBBox(new Circle(5, Color.YELLOW))
                .with(new CollidableComponent(true))
                .buildAndAttach();

        // เพิ่มคอนโทรลสำหรับการเคลื่อนที่ของกระสุน
        bullet.addComponent(new BulletControl(dirX, dirY, BULLET_SPEED));

        // กำหนดเวลาที่กระสุนสามารถยิงได้ครั้งต่อไป (Delay 1 วินาที)
        getGameTimer().runOnceAfter(() -> canShoot = true, Duration.seconds(1));
    }

    // เมธอดสำหรับ spawn Magazine ในตำแหน่งสุ่มบนหน้าจอ
    private void spawnMagazine() {
        entityBuilder()
                .type(EntityType.MAGAZINE)
                .at(random.nextInt(1280), random.nextInt(720))
                .viewWithBBox(new Circle(15, Color.YELLOW))
                .with(new CollidableComponent(true))
                .buildAndAttach();
    }

    // เมธอดสำหรับ spawn Medical Supply ในตำแหน่งสุ่มบนหน้าจอ
    private void spawnMedicalSupply() {
        entityBuilder()
                .type(EntityType.MEDICAL_SUPPLY)
                .at(random.nextInt(1280), random.nextInt(720))
                .viewWithBBox(new Circle(10, Color.LIGHTGREEN))
                .with(new CollidableComponent(true))
                .buildAndAttach();
    }

    // เมธอดสำหรับ spawn ซอมบี้ให้ออกนอกขอบจอ
    private void spawnZombieOutsideScreen() {
        for (int i = 0; i < zombieSpawnMultiplier; i++) {
            int screenWidth = getSettings().getWidth();
            int screenHeight = getSettings().getHeight();
            double spawnX, spawnY;
            // เลือกสุ่มด้านข้างของหน้าจอที่จะ spawn ซอมบี้
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

            // สร้าง Entity ของซอมบี้
            Entity zombie = entityBuilder()
                    .at(spawnX, spawnY)
                    .type(EntityType.ZOMBIE)
                    .viewWithBBox(new Rectangle(40, 40, Color.RED))
                    .with(new CollidableComponent(true))
                    .buildAndAttach();

            // เพิ่มคอนโทรลสำหรับการโจมตีของซอมบี้
            zombie.addComponent(new ZombieAttackControl());
            // เริ่มติดตามการเคลื่อนที่ของซอมบี้ไปยังผู้เล่น
            trackZombieMovement(zombie);
        }
    }

    // เมธอดสำหรับติดตามการเคลื่อนที่ของซอมบี้ให้ตามผู้เล่น
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

    private void setupTimers() {
        getGameTimer().runAtInterval(() -> {
            spawnZombieOutsideScreen();
            zombieSpawnMultiplier *= 2;
        }, Duration.seconds(10));

        FXGL.runOnce(() -> {
            getGameTimer().runAtInterval(this::spawnMagazine, Duration.seconds(10));
            getGameTimer().runAtInterval(this::spawnMedicalSupply, Duration.seconds(7));
        }, Duration.seconds(3));

        run(() -> {
            timeSurvived += 1.0 / 60;
            currentSurvivalTime = timeSurvived;
            timerDisplay.setText("Time: " + (int) timeSurvived);
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
