package com.project.model;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class PlayerAmmo extends Component {

    // ตัวแปรสำหรับเก็บจำนวนกระสุนปัจจุบันของผู้เล่น
    private int ammo;
    // ตัวแปรสำหรับแสดงผลจำนวนกระสุนบนหน้าจอ
    private Label ammoLabel;
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
        setupAmmoLabel();
    }

    private void setupAmmoLabel() {
        ammoLabel = new Label("Ammo: " + ammo);
        ammoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        ammoLabel.setTranslateX(10);
        ammoLabel.setTranslateY(40);
        FXGL.getGameScene().addUINode(ammoLabel);
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
    public boolean useAmmo() {
        if (ammo > 0) {
            ammo--;
            updateAmmoLabel();
            System.out.println("Used 1 ammo. Remaining: " + ammo);
            return true;
        }
        return false;
    }

    /**
     * เมธอดสำหรับเพิ่มจำนวนกระสุนที่ผู้เล่นเก็บได้
     *
     * @param amount จำนวนกระสุนที่ต้องการเพิ่ม
     */
    public void addAmmo(int amount) {
        ammo += amount;
        updateAmmoLabel();
        System.out.println("Added " + amount + " ammo. Current ammo: " + ammo);
    }

    /**
     * เมธอดสำหรับอัปเดตข้อความแสดงผลของจำนวนกระสุนบนหน้าจอ
     */
    private void updateAmmoLabel() {
        ammoLabel.setText("Ammo: " + ammo);
    }
    
    /**
     * เมธอดสำหรับตั้งค่าจำนวนกระสุนใหม่ และอัปเดตการแสดงผล
     *
     * @param ammo จำนวนกระสุนที่ต้องการตั้งค่าใหม่
     */
    public void setAmmo(int ammo) {
        this.ammo = ammo;
        updateAmmoLabel();
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
