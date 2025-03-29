package com.project;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.text.Text;

public class PlayerAmmo extends Component {

    private int ammo;
    private Text ammoDisplay;
    private static final int MAX_AMMO = 10; // จำนวนกระสุนสูงสุดที่สามารถเก็บได้

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
        // เพิ่มกระสุน แต่ไม่เกินจำนวนสูงสุด MAX_AMMO
        ammo = Math.min(ammo + amount, MAX_AMMO);
        updateAmmoDisplay();
    }

    private void updateAmmoDisplay() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ammo; i++) {
            sb.append("-");
        }
        ammoDisplay.setText(sb.toString());
    }
    public void setAmmo(int ammo) {
        this.ammo = ammo;
        updateAmmoDisplay();
    }
    
}
