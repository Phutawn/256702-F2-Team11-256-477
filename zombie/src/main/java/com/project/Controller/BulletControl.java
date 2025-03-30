package com.project.Controller;

import com.almasb.fxgl.entity.component.Component;

/**
 * ควบคุมพฤติกรรมของกระสุนในเกม
 * กระสุนจะเคลื่อนที่ไปตามทิศทางที่กำหนด และถูกลบออกจากโลกเมื่อออกนอกหน้าจอ
 */
public class BulletControl extends Component {
    
    private double directionX; // ทิศทางการเคลื่อนที่ในแกน X
    private double directionY; // ทิศทางการเคลื่อนที่ในแกน Y
    private double speed;      // ความเร็วของกระสุน

    /**
     * ตัวสร้าง (Constructor) สำหรับกำหนดค่าของกระสุน
     * 
     * @param directionX ทิศทางในแกน X
     * @param directionY ทิศทางในแกน Y
     * @param speed ความเร็วของกระสุน
     */
    public BulletControl(double directionX, double directionY, double speed) {
        this.directionX = directionX;
        this.directionY = directionY;
        this.speed = speed;
    }

    /**
     * อัปเดตการเคลื่อนที่ของกระสุนในทุกเฟรม
     * 
     * @param tpf (Time per frame) เวลาที่ใช้ต่อเฟรมเพื่อให้การเคลื่อนที่เป็นไปอย่างราบรื่น
     */
    @Override
    public void onUpdate(double tpf) {
        // คำนวณตำแหน่งใหม่ของกระสุน
        entity.translateX(directionX * speed * tpf);
        entity.translateY(directionY * speed * tpf);

        // ตรวจสอบว่ากระสุนออกนอกขอบเขตหน้าจอหรือไม่ (ขนาดหน้าจอ 1280x720)
        if (entity.getX() < 0 || entity.getX() > 1280 || entity.getY() < 0 || entity.getY() > 720) {
            entity.removeFromWorld(); // ลบกระสุนออกจากโลกของเกม
        }
    }
}
