package com.project;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.util.Duration;

public class MapBoundaryControl extends Component {

    private boolean mapChanged = false;

    @Override
    public void onUpdate(double tpf) {
        if (!mapChanged) {
            String direction = null;

            // ตรวจสอบว่าผู้เล่นชนขอบแมพด้านไหน
            if (entity.getX() <= 0) {
                direction = "LEFT";
            } else if (entity.getX() >= 1280 - entity.getWidth()) {
                direction = "RIGHT";
            } else if (entity.getY() <= 0) {
                direction = "UP";
            } else if (entity.getY() >= 720 - entity.getHeight()) {
                direction = "DOWN";
            }

            // ถ้าชนขอบแมพจริง ให้เปลี่ยนแมพ
            if (direction != null) {
                mapChanged = true;

                // เรียกใช้ฟังก์ชันแสดงคัทซีนและเปลี่ยนแมพ
                MapManager.showCutsceneAndChangeMap(entity, direction);

                // หน่วงเวลา 2 วินาทีเพื่อป้องกันการเปลี่ยนแมพซ้ำๆ
                FXGL.runOnce(() -> mapChanged = false, Duration.seconds(2));
            }
        }
    }
}
