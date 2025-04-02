package com.project.Component.CharecterZombie;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.dsl.FXGL;
import com.project.Controller.ZombieShooterGame;
import com.project.model.PlayerHealth;
import com.project.model.PlayerAmmo;
import javafx.geometry.Point2D;


public class ZombieAttackControl extends Component {

    private double timeSinceLastAttack = 0; // ตัวจับเวลาตั้งแต่การโจมตีครั้งล่าสุด
    private static final double ATTACK_COOLDOWN = 2.0; // เพิ่ม cooldown เป็น 2 วินาที
    private static final double ZOMBIE_SPEED = 20; // ลดความเร็วของซอมบี้ลงจาก 40 เป็น 20 (ช้าลง 50%)
    private static final double ATTACK_DISTANCE = 20; // เพิ่มระยะห่างที่ซอมบี้จะโจมตี
    private boolean movementStopped = false; // เพิ่มตัวแปรสำหรับเช็คว่าซอมบี้หยุดเคลื่อนที่หรือไม่
    private boolean isHitAnimation = false; // เพิ่มตัวแปรสำหรับเช็คว่าซอมบี้กำลังแสดงแอนิเมชันโดนยิงหรือไม่

    /**
     * อัปเดตทุกเฟรมเพื่อตรวจสอบว่าซอมบี้ชนกับผู้เล่นหรือไม่ และทำการโจมตีหากถึงเวลาที่กำหนด
     *
     * @param tpf (Time per frame) เวลาต่อเฟรม เพื่อให้การทำงานเป็นไปอย่างราบรื่น
     */
    @Override
    public void onUpdate(double tpf) {
        // ถ้าซอมบี้หยุดเคลื่อนที่หรือกำลังแสดงแอนิเมชันโดนยิง ไม่ต้องอัพเดทการเคลื่อนที่
        if (movementStopped || isHitAnimation) {
            return;
        }

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
                for (Entity barrier : FXGL.getGameWorld().getEntitiesByType(ZombieShooterGame.EntityType.BARRIER)) {
                    if (newX < barrier.getRightX() && 
                        newX + entity.getWidth() > barrier.getX() && 
                        entity.getY() < barrier.getBottomY() && 
                        entity.getY() + entity.getHeight() > barrier.getY()) {
                        canMoveX = false;
                        break;
                    }
                }

                // ตรวจสอบการชนในแกน Y
                for (Entity barrier : FXGL.getGameWorld().getEntitiesByType(ZombieShooterGame.EntityType.BARRIER)) {
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
                if (distance < ATTACK_DISTANCE && timeSinceLastAttack >= ATTACK_COOLDOWN && !isHitAnimation) {
                    System.out.println("Zombie attacking player! Distance: " + distance + ", Cooldown: " + timeSinceLastAttack);
                    PlayerHealth healthComponent = player.getComponent(PlayerHealth.class);
                    if (healthComponent != null) {
                        int currentHealth = healthComponent.getHealth();
                        healthComponent.takeDamage(10);
                        System.out.println("Player health reduced from " + currentHealth + " to " + healthComponent.getHealth());
                    } else {
                        System.out.println("PlayerHealth component not found!");
                    }
                    timeSinceLastAttack = 0;
                }
            }
        }
    }

    /**
     * หยุดการเคลื่อนที่ของซอมบี้
     */
    public void stopMovement() {
        movementStopped = true;
    }

    /**
     * ตรวจสอบว่าซอมบี้หยุดเคลื่อนที่หรือไม่
     * @return true ถ้าซอมบี้หยุดเคลื่อนที่, false ถ้าซอมบี้ยังเคลื่อนที่อยู่
     */
    public boolean isMovementStopped() {
        return movementStopped;
    }

    /**
     * ตั้งค่าสถานะแอนิเมชันโดนยิง
     * @param isHit true ถ้าซอมบี้กำลังแสดงแอนิเมชันโดนยิง, false ถ้าไม่ใช่
     */
    public void setHitAnimation(boolean isHit) {
        this.isHitAnimation = isHit;
    }

    /**
     * ตรวจสอบว่าซอมบี้กำลังแสดงแอนิเมชันโดนยิงหรือไม่
     * @return true ถ้าซอมบี้กำลังแสดงแอนิเมชันโดนยิง, false ถ้าไม่ใช่
     */
    public boolean isHitAnimation() {
        return isHitAnimation;
    }
} 