package com.project;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.control.ProgressBar;

public class PlayerHealth extends Component {
    private static final int MAX_HEALTH = 100;
    private int health = MAX_HEALTH;
    private ProgressBar healthBar;

    public PlayerHealth() {
        healthBar = new ProgressBar(1);
        healthBar.setStyle("-fx-accent: red;");
        healthBar.setTranslateX(10);
        healthBar.setTranslateY(10);
        healthBar.setPrefWidth(200);

        FXGL.getGameScene().addUINode(healthBar);
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) health = 0;
        updateHealthBar();

        if (health == 0) {
            FXGL.showMessage("Game Over", () -> FXGL.getGameController().startNewGame());
        }
    }

    public void heal(int amount) {
        health = Math.min(health + amount, MAX_HEALTH);
        updateHealthBar();
    }

    private void updateHealthBar() {
        healthBar.setProgress((double) health / MAX_HEALTH);
    }

    public int getHealth() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHealth'");
    }
}
