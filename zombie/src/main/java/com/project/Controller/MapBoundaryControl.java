package com.project.Controller;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.project.model.MapManager;
import javafx.util.Duration;

/**
 * ควบคุมการเปลี่ยนแผนที่เมื่อผู้เล่นชนขอบของแผนที่ปัจจุบัน
 */
public class MapBoundaryControl extends Component {

    private boolean mapChanged = false; // ตัวแปรตรวจสอบว่ามีการเปลี่ยนแผนที่แล้วหรือไม่

    /**
     * อัปเดตทุกเฟรมเพื่อตรวจสอบว่าผู้เล่นชนขอบแผนที่หรือไม่
     * 
     * @param tpf (Time per frame) เวลาต่อเฟรม เพื่อให้การทำงานเป็นไปอย่างราบรื่น
     */
    @Override
    public void onUpdate(double tpf) {
        if (!mapChanged) {
            String direction = null; // ตัวแปรเก็บทิศทางที่ผู้เล่นชนขอบแผนที่

            // ตรวจสอบว่าผู้เล่นชนขอบแผนที่ด้านไหน
            if (entity.getX() <= 0) {
                direction = "LEFT"; // ชนขอบซ้าย
            } else if (entity.getX() >= 1280 - entity.getWidth()) {
                direction = "RIGHT"; // ชนขอบขวา
            } else if (entity.getY() <= 0) {
                direction = "UP"; // ชนขอบบน
            } else if (entity.getY() >= 720 - entity.getHeight()) {
                direction = "DOWN"; // ชนขอบล่าง
            }

            // หากผู้เล่นชนขอบแผนที่
            if (direction != null) {
                mapChanged = true; // ตั้งค่าป้องกันการเปลี่ยนแผนที่ซ้ำ

                // เรียกใช้ฟังก์ชันแสดงคัทซีนและเปลี่ยนแผนที่
                MapManager.showCutsceneAndChangeMap(entity, direction);

                // หน่วงเวลา 2 วินาทีเพื่อป้องกันการเปลี่ยนแผนที่ซ้ำ ๆ
                FXGL.runOnce(() -> mapChanged = false, Duration.seconds(2));
            }
        }
    }
}