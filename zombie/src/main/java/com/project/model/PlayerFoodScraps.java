package com.project.model;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.text.Text;

public class PlayerFoodScraps extends Component {

    // ตัวแปรสำหรับเก็บจำนวนวัสดุที่ผู้เล่นเก็บได้
    private int scraps = 0;
    // จำนวนวัสดุที่ต้องเก็บครบก่อนที่จะสามารถคราฟชุดประถมพยาบาลได้
    private static final int SCRAPS_REQUIRED = 3; // ต้องเก็บครบ 3 ชิ้นถึงจะคราฟได้
    // Text node สำหรับแสดงผลจำนวนวัสดุบนหน้าจอ
    private Text scrapsDisplay;

    /**
     * Constructor สำหรับสร้าง PlayerFoodScraps และตั้งค่า Text node สำหรับแสดงผล
     */
    public PlayerFoodScraps() {
        // สร้าง Text node สำหรับแสดงผลวัสดุ
        scrapsDisplay = new Text();
        // กำหนดรูปแบบของข้อความ (ขนาดและสี)
        scrapsDisplay.setStyle("-fx-font-size: 20px; -fx-fill: green;");
        // กำหนดตำแหน่งที่จะแสดงบนหน้าจอ
        scrapsDisplay.setTranslateX(10);
        scrapsDisplay.setTranslateY(70);
        // อัปเดตข้อความให้ตรงกับจำนวนวัสดุปัจจุบัน
        updateDisplay();
        // เพิ่ม Text node ลงใน UI ของเกม
        FXGL.getGameScene().addUINode(scrapsDisplay);
    }

    /**
     * เมธอดสำหรับเพิ่มจำนวนวัสดุที่ผู้เล่นเก็บได้
     *
     * @param amount จำนวนวัสดุที่จะเพิ่ม
     */
    public void addScrap(int amount) {
        scraps += amount;
        updateDisplay();
    }

    /**
     * พยายามใช้ Scraps เพื่อคราฟชุดประถมพยาบาล
     *
     * @return true หากใช้ได้ (คราฟสำเร็จ) หรือ false หาก Scraps ไม่เพียงพอ
     */
    public boolean useScrapsForFirstAid() {
        if (scraps >= SCRAPS_REQUIRED) {
            scraps -= SCRAPS_REQUIRED;
            updateDisplay();
            return true;
        }
        return false;
    }

    /**
     * อัปเดตข้อความแสดงผลบนหน้าจอให้ตรงกับจำนวนวัสดุปัจจุบัน
     */
    private void updateDisplay() {
        scrapsDisplay.setText("Food Scraps: " + scraps);
    }

    /**
     * เมธอดสำหรับดึงค่าจำนวนวัสดุปัจจุบัน
     *
     * @return จำนวนวัสดุที่ผู้เล่นมี
     */
    public int getScraps() {
        return scraps;
    }

    /**
     * เมธอดสำหรับตั้งค่าจำนวนวัสดุใหม่และอัปเดตการแสดงผล
     *
     * @param scraps จำนวนวัสดุใหม่ที่ต้องการตั้งค่า
     */
    public void setScraps(int scraps) {
        this.scraps = scraps;
        updateDisplay();
    }
}
