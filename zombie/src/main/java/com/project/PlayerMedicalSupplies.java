package com.project;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.text.Text;

public class PlayerMedicalSupplies extends Component {

    private int supplies = 0;
    private static final int SUPPLIES_REQUIRED = 3; // ต้องเก็บครบ 3 ชิ้นถึงจะคราฟได้
    private Text suppliesDisplay;

    public PlayerMedicalSupplies() {
        suppliesDisplay = new Text();
        suppliesDisplay.setStyle("-fx-font-size: 20px; -fx-fill: green;");
        suppliesDisplay.setTranslateX(10);
        suppliesDisplay.setTranslateY(70);
        updateDisplay();
        FXGL.getGameScene().addUINode(suppliesDisplay);
    }

    public void addSupply(int amount) {
        supplies += amount;
        updateDisplay();
    }

    /**
     * พยายามใช้ Supplies เพื่อคราฟชุดประถมพยาบาล
     * @return true หากใช้ได้ (คราฟสำเร็จ) หรือ false หาก Supplies ไม่เพียงพอ
     */
    public boolean useSuppliesForFirstAid() {
        if (supplies >= SUPPLIES_REQUIRED) {
            supplies -= SUPPLIES_REQUIRED;
            updateDisplay();
            return true;
        }
        return false;
    }

    private void updateDisplay() {
        suppliesDisplay.setText("Supplies: " + supplies);
    }
}
