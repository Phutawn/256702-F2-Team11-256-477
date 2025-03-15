package com.project;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.input.KeyCode; // เพิ่มการ import KeyCode

import static com.almasb.fxgl.dsl.FXGL.*;

public class ZombieShooterGame extends GameApplication {

    private Entity player;
    private double bulletSpeed = 10; // ความเร็วของกระสุน
    private double zombieSpeed = 2; // ความเร็วของซอมบี้
    private boolean canShoot = true; // ใช้สำหรับควบคุมการยิงกระสุน

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Zombie Shooter Game");
        settings.setVersion("1.0");
    }

    @Override
    protected void initGame() {
        // สร้างตัวละครผู้เล่น
        player = entityBuilder()
                .at(400, 300) // จุดเริ่มต้น (x=400, y=300)
                .view(new Rectangle(40, 40, Color.BLUE)) // สี่เหลี่ยมสีน้ำเงินเป็นตัวละคร
                .buildAndAttach();
        
        // สร้างซอมบี้
        spawnZombie();
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        // กด A เพื่อเดินซ้าย
        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                player.translateX(-5);
            }
        }, KeyCode.A);

        // กด D เพื่อเดินขวา
        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                player.translateX(5);
            }
        }, KeyCode.D);

        // กด W เพื่อเดินขึ้น
        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                player.translateY(-5);
            }
        }, KeyCode.W);

        // กด S เพื่อเดินลง
        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                player.translateY(5);
            }
        }, KeyCode.S);

        // ยิงกระสุนเมื่อกดเมาส์
        input.addAction(new UserAction("Shoot") {
            @Override
            protected void onAction() {
                if (canShoot) {
                    shootBullet();
                }
            }
        }, KeyCode.SPACE);
    }

    private void shootBullet() {
        // สร้างกระสุน
        Entity bullet = entityBuilder()
                .at(player.getX() + player.getWidth() / 2, player.getY()) // ให้กระสุนเริ่มจากตำแหน่งตรงกลางของผู้เล่น
                .view(new Rectangle(10, 5, Color.YELLOW)) // กระสุน (สี่เหลี่ยมสีเหลือง)
                .buildAndAttach();

        // คำนวณทิศทางการยิง (ให้กระสุนยิงไปตามทิศที่ผู้เล่นหันหน้าไป)
        double angle = Math.toDegrees(Math.atan2(getInput().getMouseYWorld() - player.getY(), getInput().getMouseXWorld() - player.getX())); 
        double radian = Math.toRadians(angle);

        // ความเร็วของกระสุน
        double bulletSpeedX = bulletSpeed * Math.sin(radian);
        double bulletSpeedY = -bulletSpeed * Math.cos(radian);

        // การเคลื่อนที่ของกระสุน
        run(() -> {
            bullet.translateX(bulletSpeedX); // กระสุนเคลื่อนที่ไปทาง X
            bullet.translateY(bulletSpeedY); // กระสุนเคลื่อนที่ไปทาง Y

            // หากกระสุนออกจากหน้าจอให้ลบ
            if (bullet.getX() > getAppWidth() || bullet.getX() < 0 || bullet.getY() > getAppHeight() || bullet.getY() < 0) {
                bullet.removeFromWorld();
            }
        }, Duration.seconds(0.05));

        // จำกัดการยิงใหม่ทุก 0.1 วินาที (ไม่ให้ยิงถี่เกินไป)
        canShoot = false;
        runOnce(() -> canShoot = true, Duration.seconds(0.1));
    }

    private void spawnZombie() {
        // สร้างซอมบี้
        Entity zombie = entityBuilder()
                .at(100, 100) // จุดเริ่มต้นของซอมบี้
                .view(new Rectangle(40, 40, Color.RED)) // สี่เหลี่ยมสีแดงเป็นซอมบี้
                .buildAndAttach();

        // ให้ซอมบี้วิ่งตามผู้เล่น
        run(() -> {
            double dx = player.getX() - zombie.getX();
            double dy = player.getY() - zombie.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            // เคลื่อนที่ซอมบี้ตามทิศทางของผู้เล่น
            if (distance > 1) {
                zombie.translateX(dx / distance * zombieSpeed);
                zombie.translateY(dy / distance * zombieSpeed);
            }
        }, Duration.seconds(0.1));
    }

    @Override
    protected void initUI() {
        // สร้างเมนูเริ่มเกม
        StackPane menuRoot = new StackPane();
        Text title = new Text("Zombie Shooter Game");
        title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-fill: white;");
        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-font-size: 20px;");
        startButton.setOnAction(event -> startGame());
        
        menuRoot.getChildren().addAll(title, startButton);
        startButton.setTranslateY(50);

        // เพิ่ม UI เข้าไปในฉาก
        getGameScene().addUINodes(menuRoot);
    }

    private void startGame() {
        // เริ่มเกมหลังจากกดปุ่ม
        getGameScene().clearUINodes();
        initGame(); // เริ่มเกมใหม่
    }

    public static void main(String[] args) {
        launch(args);
    }
}
