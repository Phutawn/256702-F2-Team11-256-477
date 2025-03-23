package com.project;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.text.Text;

public class PlayerAmmo extends Component {

    private int ammo;
    private Text ammoDisplay;

    public PlayerAmmo(int initialAmmo) {
        this.ammo = initialAmmo;
        ammoDisplay = new Text();
        ammoDisplay.setStyle("-fx-font-size: 20px; -fx-fill: yellow;");
        ammoDisplay.setTranslateX(10);
        ammoDisplay.setTranslateY(40);
        updateAmmoDisplay();
        FXGL.getGameScene().addUINode(ammoDisplay);
    }

    public int getAmmo() {
        return ammo;
    }

    public void useAmmo(int amount) {
        ammo = Math.max(ammo - amount, 0);
        updateAmmoDisplay();
    }

    public void addAmmo(int amount) {
        ammo += amount;
        updateAmmoDisplay();
    }

    private void updateAmmoDisplay() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ammo; i++) {
            sb.append("-");
        }
        ammoDisplay.setText(sb.toString());
    }
}
