package com.project.Controller;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.dsl.FXGL;
import com.project.Controller.ZombieShooterGame.EntityType;
import com.project.model.PlayerHealth;
import com.project.model.PlayerAmmo;
import javafx.geometry.Point2D;

/**
 * ควบคุมพฤติกรรมการโจมตีของซอมบี้
 * ซอมบี้จะทำการโจมตีผู้เล่นเมื่อสัมผัสกับผู้เล่น และมีคูลดาวน์ระหว่างการโจมตีแต่ละครั้ง
 */
public class ZombieAttackControl extends Component {

    private double timeSinceLastAttack = 0; // ตัวจับเวลาตั้งแต่การโจมตีครั้งล่าสุด
    private static final double ATTACK_COOLDOWN = 0.5; // ลดคูลดาวน์ลงเหลือ 0.5 วินาที
    private static final double ZOMBIE_SPEED = 100; // ความเร็วของซอมบี้

    /**
     * อัปเดตทุกเฟรมเพื่อตรวจสอบว่าซอมบี้ชนกับผู้เล่นหรือไม่ และทำการโจมตีหากถึงเวลาที่กำหนด
     *
     * @param tpf (Time per frame) เวลาต่อเฟรม เพื่อให้การทำงานเป็นไปอย่างราบรื่น
     */
    @Override
    public void onUpdate(double tpf) {
        timeSinceLastAttack += tpf; // เพิ่มค่าตัวจับเวลาสำหรับคูลดาวน์การโจมตี

        // ค้นหาผู้เล่น
        Entity player = FXGL.getGameWorld()
                .getEntitiesByType(ZombieShooterGame.EntityType.PLAYER)
                .stream()
                .findFirst()
                .orElse(null);

        if (player != null) {
            // คำนวณทิศทางไปยังผู้เล่น
            double dx = player.getX() - entity.getX();
            double dy = player.getY() - entity.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
                // ปรับความเร็วตามระยะห่าง
                double speed = Math.min(ZOMBIE_SPEED * tpf, distance);
                
                // คำนวณตำแหน่งใหม่
                double newX = entity.getX() + (dx / distance) * speed;
                double newY = entity.getY() + (dy / distance) * speed;

                // ตรวจสอบการชนกับ barrier
                boolean canMoveX = true;
                boolean canMoveY = true;

                // ตรวจสอบการชนในแกน X
                for (Entity barrier : FXGL.getGameWorld().getEntitiesByType(EntityType.BARRIER)) {
                    if (newX < barrier.getRightX() && 
                        newX + entity.getWidth() > barrier.getX() && 
                        entity.getY() < barrier.getBottomY() && 
                        entity.getY() + entity.getHeight() > barrier.getY()) {
                        canMoveX = false;
                        break;
                    }
                }

                // ตรวจสอบการชนในแกน Y
                for (Entity barrier : FXGL.getGameWorld().getEntitiesByType(EntityType.BARRIER)) {
                    if (entity.getX() < barrier.getRightX() && 
                        entity.getX() + entity.getWidth() > barrier.getX() && 
                        newY < barrier.getBottomY() && 
                        newY + entity.getHeight() > barrier.getY()) {
                        canMoveY = false;
                        break;
                    }
                }

                // อัพเดทตำแหน่งถ้าไม่ชนกำแพง
                if (canMoveX) {
                    entity.setX(newX);
                }
                if (canMoveY) {
                    entity.setY(newY);
                }

                // ตรวจสอบการชนกับผู้เล่น
                if (distance < 40 && timeSinceLastAttack >= ATTACK_COOLDOWN) {
                    System.out.println("Zombie attacking player!");
                    player.getComponent(PlayerHealth.class).takeDamage(10);
                    timeSinceLastAttack = 0;
                }
            }
        }
    }

    
}