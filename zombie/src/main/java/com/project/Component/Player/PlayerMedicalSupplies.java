package com.project.Component.Player;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

public class PlayerMedicalSupplies extends Component {
    private int medicalKits = 0;
    private Text medicalKitsText;
    private VBox medicalKitsContainer;

    @Override
    public void onAdded() {
        // สร้างพื้นหลังสีดำโปร่งแสง
        Pane background = new Pane();
        background.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-background-radius: 5px;");
        background.setPrefSize(100, 30);
        background.setTranslateX(10);
        background.setTranslateY(10);

        // สร้าง container สำหรับข้อความ
        medicalKitsContainer = new VBox(5);
        medicalKitsContainer.setAlignment(Pos.CENTER);
        medicalKitsContainer.setTranslateX(10);
        medicalKitsContainer.setTranslateY(10);

        // สร้างข้อความแสดงจำนวน medical kits
        medicalKitsText = new Text("Medical Kits: 0");
        medicalKitsText.setFont(Font.font("Arial", 14));
        medicalKitsText.setFill(Color.WHITE);

        // เพิ่มข้อความลงใน container
        medicalKitsContainer.getChildren().add(medicalKitsText);

        // เพิ่ม container และพื้นหลังลงใน scene
        FXGL.getGameScene().addUINodes(background, medicalKitsContainer);
    }

    public void addMedicalKit() {
        medicalKits++;
        updateMedicalKitsText();
    }

    public void useMedicalKit() {
        if (medicalKits > 0) {
            medicalKits--;
            updateMedicalKitsText();
        }
    }

    public int getMedicalKits() {
        return medicalKits;
    }

    private void updateMedicalKitsText() {
        medicalKitsText.setText("Medical Kits: " + medicalKits);
    }

    @Override
    public void onRemoved() {
        // ลบ UI elements เมื่อ component ถูกลบ
        if (medicalKitsContainer != null) {
            FXGL.getGameScene().removeUINode(medicalKitsContainer);
        }
    }
} 