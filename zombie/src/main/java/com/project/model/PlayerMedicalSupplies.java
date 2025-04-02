package com.project.model;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.text.Text;

public class PlayerMedicalSupplies extends Component {

    // ตัวแปรสำหรับเก็บจำนวนวัสดุที่ผู้เล่นเก็บได้
    private int supplies = 0;
    // จำนวนวัสดุที่ต้องเก็บครบก่อนที่จะสามารถคราฟชุดประถมพยาบาลได้
    private static final int SUPPLIES_REQUIRED = 3; // ต้องเก็บครบ 3 ชิ้นถึงจะคราฟได้
    // Text node สำหรับแสดงผลจำนวนวัสดุบนหน้าจอ
    private Text suppliesDisplay;

    /**
     * Constructor สำหรับสร้าง PlayerMedicalSupplies และตั้งค่า Text node สำหรับแสดงผล
     */
    public PlayerMedicalSupplies() {
        // สร้าง Text node สำหรับแสดงผลวัสดุ
        suppliesDisplay = new Text();
        // กำหนดรูปแบบของข้อความ (ขนาดและสี)
        suppliesDisplay.setStyle("-fx-font-size: 20px; -fx-fill: white;");
        // กำหนดตำแหน่งที่จะแสดงบนหน้าจอ
        suppliesDisplay.setTranslateX(10);
        suppliesDisplay.setTranslateY(120);
        // อัปเดตข้อความให้ตรงกับจำนวนวัสดุปัจจุบัน
        updateDisplay();
        // เพิ่ม Text node ลงใน UI ของเกม
        FXGL.getGameScene().addUINode(suppliesDisplay);
    }

    /**
     * เมธอดสำหรับเพิ่มจำนวนวัสดุที่ผู้เล่นเก็บได้
     *
     * @param amount จำนวนวัสดุที่จะเพิ่ม
     */
    public void addSupply(int amount) {
        supplies += amount;
        updateDisplay();
    }

    /**
     * พยายามใช้ Supplies เพื่อคราฟชุดประถมพยาบาล
     *
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

    /**
     * อัปเดตข้อความแสดงผลบนหน้าจอให้ตรงกับจำนวนวัสดุปัจจุบัน
     */
    private void updateDisplay() {
        suppliesDisplay.setText("Supplies: " + supplies);
    }

    /**
     * เมธอดสำหรับดึงค่าจำนวนวัสดุปัจจุบัน
     *
     * @return จำนวนวัสดุที่ผู้เล่นมี
     */
    public int getSupplies() {
        return supplies;
    }

    /**
     * เมธอดสำหรับตั้งค่าจำนวนวัสดุใหม่และอัปเดตการแสดงผล
     *
     * @param supplies จำนวนวัสดุใหม่ที่ต้องการตั้งค่า
     */
    public void setSupplies(int supplies) {
        this.supplies = supplies;
        updateDisplay();
    }
}
