package com.project.model;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;
import javafx.geometry.Point2D;

public class ZombieAnimationComponent extends Component {
    private AnimatedTexture texture;
    private AnimationChannel idleAnimation;
    private AnimationChannel runAnimation;
    private Vec2 moveDirection = new Vec2();

    public ZombieAnimationComponent() {
        // สร้าง Animation Channel สำหรับท่ายืน (Idle)
        idleAnimation = new AnimationChannel(
            FXGL.image("Zombie.png"),
            4, // จำนวนเฟรมทั้งหมด
            32, // ความกว้างของแต่ละเฟรม
            32, // ความสูงของแต่ละเฟรม
            Duration.seconds(1), // ระยะเวลาทั้งหมดของแอนิเมชัน
            0, // เฟรมเริ่มต้น
            3  // เฟรมสุดท้าย
        );

        // สร้าง Animation Channel สำหรับท่าวิ่ง (Run)
        runAnimation = new AnimationChannel(
            FXGL.image("Zombie.png"),
            8, // จำนวนเฟรมทั้งหมด
            32, // ความกว้างของแต่ละเฟรม
            32, // ความสูงของแต่ละเฟรม
            Duration.seconds(0.8), // ระยะเวลาทั้งหมดของแอนิเมชัน
            0, // เฟรมเริ่มต้น
            7  // เฟรมสุดท้าย
        );

        texture = new AnimatedTexture(idleAnimation);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
    }

    public void moveTowards(Point2D target) {
        Point2D position = entity.getPosition();
        moveDirection.set((float)(target.getX() - position.getX()), 
                         (float)(target.getY() - position.getY()));
        
        // ถ้ากำลังเคลื่อนที่ ให้เล่นแอนิเมชันวิ่ง
        if (moveDirection.length() > 0) {
            if (texture.getAnimationChannel() != runAnimation) {
                texture.loopAnimationChannel(runAnimation);
            }
            // พลิกภาพตามทิศทางการเคลื่อนที่
            texture.setScaleX(moveDirection.x > 0 ? 1 : -1);
        } else {
            // ถ้าไม่ได้เคลื่อนที่ ให้เล่นแอนิเมชันยืน
            if (texture.getAnimationChannel() != idleAnimation) {
                texture.loopAnimationChannel(idleAnimation);
            }
        }
    }
} 