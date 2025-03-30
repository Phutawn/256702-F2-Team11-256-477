package com.project.Controller;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.dsl.FXGL;
import com.project.Controller.ZombieShooterGame.EntityType;
import com.project.model.PlayerHealth;

/**
 * ควบคุมพฤติกรรมการโจมตีของซอมบี้
 * ซอมบี้จะทำการโจมตีผู้เล่นเมื่อสัมผัสกับผู้เล่น และมีคูลดาวน์ระหว่างการโจมตีแต่ละครั้ง
 */
public class ZombieAttackControl extends Component {

    private double timeSinceLastAttack = 0; // ตัวจับเวลาตั้งแต่การโจมตีครั้งล่าสุด
    private static final double ATTACK_COOLDOWN = 1.0; // คูลดาวน์ระหว่างการโจมตี (1 วินาที)

    /**
     * อัปเดตทุกเฟรมเพื่อตรวจสอบว่าซอมบี้ชนกับผู้เล่นหรือไม่ และทำการโจมตีหากถึงเวลาที่กำหนด
     *
     * @param tpf (Time per frame) เวลาต่อเฟรม เพื่อให้การทำงานเป็นไปอย่างราบรื่น
     */
    @Override
    public void onUpdate(double tpf) {
        timeSinceLastAttack += tpf; // เพิ่มค่าตัวจับเวลาสำหรับคูลดาวน์การโจมตี

        // ค้นหาผู้เล่นในเกม (สมมติว่ามีเพียงผู้เล่นเดียว)
        Entity player = FXGL.getGameWorld()
                .getEntitiesByType(ZombieShooterGame.EntityType.PLAYER)
                .stream()
                .findFirst()
                .orElse(null);

        // ตรวจสอบว่าผู้เล่นมีอยู่ในเกมหรือไม่
        if (player != null) {
            // ตรวจสอบว่าซอมบี้ชนกับผู้เล่นหรือไม่
            if (entity.getBoundingBoxComponent().isCollidingWith(player.getBoundingBoxComponent())) {
                // ตรวจสอบว่าครบคูลดาวน์การโจมตีหรือยัง
                if (timeSinceLastAttack >= ATTACK_COOLDOWN) {
                    player.getComponent(PlayerHealth.class).takeDamage(10); // ลดพลังชีวิตผู้เล่น 10 หน่วย
                    timeSinceLastAttack = 0; // รีเซ็ตตัวจับเวลาการโจมตี
                }
            }
        }
    }
}