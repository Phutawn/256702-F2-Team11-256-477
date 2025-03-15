package com.project;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.*;

public class ZombieShooterGame extends GameApplication {

    private Entity player;
    private String playerName = "Player";

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

        Text title = new Text("Zombie Shooter Game");
        title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-fill: white;");

        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-font-size: 20px;");
        startButton.setOnAction(event -> showNameInputScreen());

        VBox menuLayout = new VBox(20, title, startButton);
        menuLayout.setTranslateX(300);
        menuLayout.setTranslateY(200);

        getGameScene().addUINodes(menuLayout);
    }

    private void showNameInputScreen() {
        getGameScene().clearUINodes();

        Text prompt = new Text("Enter Your Name:");
        prompt.setStyle("-fx-font-size: 24px; -fx-fill: white;");

        TextField nameField = new TextField();
        nameField.setMaxWidth(200);

        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-font-size: 20px;");
        confirmButton.setOnAction(event -> {
            playerName = nameField.getText().isEmpty() ? "Player" : nameField.getText();
            startGame();
        });

        VBox nameInputLayout = new VBox(10, prompt, nameField, confirmButton);
        nameInputLayout.setTranslateX(300);
        nameInputLayout.setTranslateY(200);

        getGameScene().addUINodes(nameInputLayout);
    }

    private void startGame() {
        getGameScene().clearUINodes();
        player = entityBuilder()
                .at(400, 300)
                .view(new Rectangle(40, 40, Color.BLUE))
                .buildAndAttach();

        spawnZombie();
        initInput();
    }

    /*@Override //ปัญหาเกิดจากการเรียกใช้ initInput() ซ้ำ**
    protected void initInput() {
        System.out.println("initInput() ถูกเรียกแล้ว");
        getInput().clearAll(); // ล้างการผูกคีย์ก่อน ตั้งค่าการควบคุมใหม่
        
        onKey(KeyCode.A, "Move Left", () -> player.translateX(-5));
        onKey(KeyCode.D, "Move Right", () -> player.translateX(5));
        onKey(KeyCode.W, "Move Up", () -> player.translateY(-5));
        onKey(KeyCode.S, "Move Down", () -> player.translateY(5));
        onKey(KeyCode.SPACE, "Shoot", this::shootBullet);

    }*/
    

    private void shootBullet() {
        Entity bullet = entityBuilder()
                .at(player.getX() + player.getWidth() / 2, player.getY())
                .view(new Rectangle(10, 5, Color.YELLOW))
                .buildAndAttach();

        run(() -> {
            bullet.translateY(-10);
            if (bullet.getY() < 0) {
                bullet.removeFromWorld();
            }
        }, Duration.seconds(0.05));
    }
    

    private void spawnZombie() {
        Entity zombie = entityBuilder()
                .at(100, 100)
                .view(new Rectangle(40, 40, Color.RED))
                .buildAndAttach();

        run(() -> {
            double dx = player.getX() - zombie.getX();
            double dy = player.getY() - zombie.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 1) {
                zombie.translateX(dx / distance * 2);
                zombie.translateY(dy / distance * 2);
            }
        }, Duration.seconds(0.1));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
