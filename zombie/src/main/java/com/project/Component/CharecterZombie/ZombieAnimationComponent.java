package com.project.Component.CharecterZombie;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class ZombieAnimationComponent extends Component {
    private AnimatedTexture texture;
    private AnimationChannel walkLeft;
    private AnimationChannel walkRight;
    private AnimationChannel hitLeft;
    private AnimationChannel hitRight;
    private String currentAnimationState = "walkLeft";
    private String lastHorizontalDirection = "left";

    public ZombieAnimationComponent() {
        Image image = FXGL.image("Zombie.png");

        // กำหนดจำนวนแถวและคอลัมน์
        int columns = 8;
        int rows = 4;

        int frameW = (int) image.getWidth() / columns;  // 256 / 8 = 32
        int frameH = (int) image.getHeight() / rows;   // 128 / 4 = 32

        // สร้าง Animation Channel สำหรับแต่ละการเคลื่อนไหว
        walkLeft = new AnimationChannel(image, columns, frameW, frameH, Duration.seconds(0.8), 0, 7);    // แถวที่ 1 (0-7) เดินซ้าย
        walkRight = new AnimationChannel(image, columns, frameW, frameH, Duration.seconds(0.8), 8, 15);  // แถวที่ 2 (8-15) เดินขวา
        hitLeft = new AnimationChannel(image, columns, frameW, frameH, Duration.seconds(0.8), 16, 23);   // แถวที่ 3 (16-23) โดนยิงซ้าย
        hitRight = new AnimationChannel(image, columns, frameW, frameH, Duration.seconds(0.8), 24, 31);  // แถวที่ 4 (24-31) โดนยิงขวา

        // เริ่มต้นด้วยท่าเดินซ้าย
        texture = new AnimatedTexture(walkLeft);
        
        // ตั้งค่าการแสดงผล
        texture.setTranslateX(1);  // เลื่อนภาพไปทางขวา 1 pixel
        texture.setTranslateY(1);  // เลื่อนภาพลงล่าง 1 pixel
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
    }

    public void walkUp() {
        // ใช้แอนิเมชันตามทิศทางล่าสุด
        if (lastHorizontalDirection.equals("left")) {
            if (texture.getAnimationChannel() != walkLeft) {
                texture.loopAnimationChannel(walkLeft);
            }
        } else {
            if (texture.getAnimationChannel() != walkRight) {
                texture.loopAnimationChannel(walkRight);
            }
        }
        currentAnimationState = "walkUp";
    }

    public void walkDown() {
        if (lastHorizontalDirection.equals("left")) {
            if (texture.getAnimationChannel() != walkLeft) {
                texture.loopAnimationChannel(walkLeft);
            }
        } else {
            if (texture.getAnimationChannel() != walkRight) {
                texture.loopAnimationChannel(walkRight);
            }
        }
        currentAnimationState = "walkDown";
    }

    public void walkLeft() {
        if (texture.getAnimationChannel() != walkLeft) {
            texture.loopAnimationChannel(walkLeft);
        }
        currentAnimationState = "walkLeft";
        lastHorizontalDirection = "left";
    }

    public void walkRight() {
        if (texture.getAnimationChannel() != walkRight) {
            texture.loopAnimationChannel(walkRight);
        }
        currentAnimationState = "walkRight";
        lastHorizontalDirection = "right";
    }

    public void idle() {
        // ใช้เฟรมสุดท้ายของการเดินในทิศทางล่าสุด
        if (lastHorizontalDirection.equals("left")) {
            if (texture.getAnimationChannel() != walkLeft) {
                texture.loopAnimationChannel(walkLeft);
            }
        } else {
            if (texture.getAnimationChannel() != walkRight) {
                texture.loopAnimationChannel(walkRight);
            }
        }
        currentAnimationState = "idle";
    }

    public void hit() {
        // ตั้งค่าสถานะแอนิเมชันโดนยิง
        entity.getComponent(ZombieAttackControl.class).setHitAnimation(true);
        
        // เล่นแอนิเมชันโดนยิงตามทิศทางล่าสุด
        if (lastHorizontalDirection.equals("left")) {
            texture.playAnimationChannel(hitLeft);
        } else {
            texture.playAnimationChannel(hitRight);
        }
        currentAnimationState = "hit";
        
        // รอให้แอนิเมชันจบก่อนที่จะลบซอมบี้ออก
        FXGL.runOnce(() -> {
            entity.getComponent(ZombieAttackControl.class).setHitAnimation(false);
        }, Duration.seconds(1));
    }

    public String checkWalk() {
        return currentAnimationState;
    }

    public void moveTowards(Point2D target) {
        Point2D position = entity.getPosition();
        double dx = target.getX() - position.getX();
        double dy = target.getY() - position.getY();
        
        // ถ้ากำลังเคลื่อนที่ ให้เล่นแอนิเมชันเดิน
        if (Math.abs(dx) > 0.1 || Math.abs(dy) > 0.1) {
            if (Math.abs(dx) > Math.abs(dy)) {
                // เคลื่อนที่ในแนวนอนมากกว่า
                if (dx > 0) {
                    walkRight();
                } else {
                    walkLeft();
                }
            } else {
                // เคลื่อนที่ในแนวตั้งมากกว่า
                if (dy > 0) {
                    walkDown();
                } else {
                    walkUp();
                }
            }
        } else {
            // ถ้าไม่ได้เคลื่อนที่ ให้เล่นแอนิเมชันยืน
            idle();
        }
    }
} 