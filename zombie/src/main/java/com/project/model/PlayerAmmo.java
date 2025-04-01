package com.project.model;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.text.Text;

public class PlayerAmmo extends Component {

    // ตัวแปรสำหรับเก็บจำนวนกระสุนปัจจุบันของผู้เล่น
    private int ammo;
    // ตัวแปรสำหรับแสดงผลจำนวนกระสุนบนหน้าจอ
    private Text ammoDisplay;
    // จำนวนกระสุนสูงสุดที่ผู้เล่นสามารถเก็บได้
    private static final int MAX_AMMO = 10; // จำนวนกระสุนสูงสุดที่สามารถเก็บได้
    private double fireRate = 1.0; // ค่าเริ่มต้นของ delay ระหว่างการยิง
    private double damageMultiplier = 1.0; // ค่าเริ่มต้นของตัวคูณความเสียหาย

    /**
     * Constructor สำหรับสร้าง PlayerAmmo พร้อมกำหนดกระสุนเริ่มต้น
     *
     * @param initialAmmo จำนวนกระสุนเริ่มต้นที่ผู้เล่นมี
     */
    public PlayerAmmo(int initialAmmo) {
        this.ammo = initialAmmo;
        // สร้าง Text node สำหรับแสดงผลกระสุน
        ammoDisplay = new Text();
        // กำหนดรูปแบบตัวอักษรและสีของข้อความ
        ammoDisplay.setStyle("-fx-font-size: 20px; -fx-fill: yellow;");
        // กำหนดตำแหน่งของ Text node บนหน้าจอ
        ammoDisplay.setTranslateX(10);
        ammoDisplay.setTranslateY(40);
        // อัปเดตข้อความแสดงผลให้ตรงกับจำนวนกระสุนปัจจุบัน
        updateAmmoDisplay();
        // เพิ่ม Text node ลงใน UI ของเกมเพื่อให้แสดงบนหน้าจอ
        FXGL.getGameScene().addUINode(ammoDisplay);
    }

    /**
     * เมธอดสำหรับดึงค่าจำนวนกระสุนปัจจุบัน
     *
     * @return จำนวนกระสุนปัจจุบัน
     */
    public int getAmmo() {
        return ammo;
    }

    /**
     * เมธอดสำหรับใช้กระสุนจำนวนที่ระบุ (ลดจำนวนกระสุน)
     *
     * @param amount จำนวนกระสุนที่ต้องการใช้
     */
    public void useAmmo(int amount) {
        // ลดจำนวนกระสุนและตรวจสอบไม่ให้เหลือค่าติดลบ
        ammo = Math.max(ammo - amount, 0);
        // อัปเดตข้อความแสดงผลให้ตรงกับจำนวนกระสุนใหม่
        updateAmmoDisplay();
    }

    /**
     * เมธอดสำหรับเพิ่มจำนวนกระสุนที่ผู้เล่นเก็บได้
     *
     * @param amount จำนวนกระสุนที่ต้องการเพิ่ม
     */
    public void addAmmo(int amount) {
        // เพิ่มกระสุน แต่ไม่เกินจำนวนสูงสุด MAX_AMMO
        ammo = Math.min(ammo + amount, MAX_AMMO);
        // อัปเดตข้อความแสดงผลให้ตรงกับจำนวนกระสุนใหม่
        updateAmmoDisplay();
    }

    /**
     * เมธอดสำหรับอัปเดตข้อความแสดงผลของจำนวนกระสุนบนหน้าจอ
     */
    private void updateAmmoDisplay() {
        // สร้างข้อความโดยใช้เครื่องหมาย "-" แทนแต่ละกระสุน
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ammo; i++) {
            sb.append("-");
        }
        // ตั้งค่าข้อความใน Text node ให้ตรงกับจำนวนกระสุน
        ammoDisplay.setText(sb.toString());
    }
    
    /**
     * เมธอดสำหรับตั้งค่าจำนวนกระสุนใหม่ และอัปเดตการแสดงผล
     *
     * @param ammo จำนวนกระสุนที่ต้องการตั้งค่าใหม่
     */
    public void setAmmo(int ammo) {
        this.ammo = ammo;
        updateAmmoDisplay();
    }

    /**
     * เมธอดสำหรับตั้งค่า delay ระหว่างการยิง
     * @param rate ค่า delay ใหม่ (วินาที)
     */
    public void setFireRate(double rate) {
        this.fireRate = rate;
    }

    /**
     * เมธอดสำหรับตั้งค่าตัวคูณความเสียหาย
     * @param multiplier ค่าตัวคูณความเสียหายใหม่
     */
    public void setDamageMultiplier(double multiplier) {
        this.damageMultiplier = multiplier;
    }

    /**
     * เมธอดสำหรับดึงค่าตัวคูณความเสียหายปัจจุบัน
     * @return ค่าตัวคูณความเสียหาย
     */
    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    /**
     * เมธอดสำหรับดึงค่า delay ระหว่างการยิงปัจจุบัน
     * @return ค่า delay ระหว่างการยิง
     */
    public double getFireRate() {
        return fireRate;
    }
}
