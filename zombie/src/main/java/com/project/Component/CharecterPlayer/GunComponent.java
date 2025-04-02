package com.project.Component.CharecterPlayer;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;

import javafx.scene.image.Image;
import javafx.util.Duration;

public class GunComponent extends Component {
     
    private String currentAnimationState = "idleRight";
    private final AnimatedTexture texture;
    private final AnimationChannel shootRight;  // แถวบน: ยิงจากซ้ายไปขวา
    private final AnimationChannel shootLeft;   // แถวล่าง: ยิงจากขวาไปซ้าย
    private final AnimationChannel walkRight;   // แถวบน: เดินหันขวา
    private final AnimationChannel walkLeft;    // แถวล่าง: เดินหันซ้าย
    private AnimationComponent playerAnimation; // อ้างอิงแอนิเมชันของผู้เล่น
    private boolean isShooting = false; // เพิ่มตัวแปรเก็บสถานะการยิง

    public GunComponent() {
        Image image = FXGL.image("gun.png");

        int columns = 5;  // 5 คอลัมน์
        int rows = 2;

        int frameW = (int) image.getWidth() / columns;
        int frameH = (int) image.getHeight() / rows;

        // แถวบน: ยิงจากซ้ายไปขวา (0-4)
        shootRight = new AnimationChannel(image, columns, frameW, frameH, Duration.seconds(0.2), 0, 4);
        
        // แถวล่าง: ยิงจากขวาไปซ้าย (5-9)
        shootLeft = new AnimationChannel(image, columns, frameW, frameH, Duration.seconds(0.2), 5, 9);

        // แถวบน: เดินหันขวา (เฟรมแรก)
        walkRight = new AnimationChannel(image, columns, frameW, frameH, Duration.seconds(0.1), 4, 4);
        
        // แถวล่าง: เดินหันซ้าย (เฟรมแรก)
        walkLeft = new AnimationChannel(image, columns, frameW, frameH, Duration.seconds(0.1), 5, 5);

        texture = new AnimatedTexture(walkRight);
        texture.loopAnimationChannel(walkRight);

        // ปรับขนาดของภาพปืนให้เล็กลง
        texture.setScaleX(0.5);
        texture.setScaleY(0.5);

        // ปรับตำแหน่งของภาพให้อยู่ตรงกลาง Hitbox
        texture.setTranslateX(-6);
        texture.setTranslateY(-3.5);
    }

    @Override
    public void onAdded() {
        // ดึง AnimationComponent ของผู้เล่น
        playerAnimation = entity.getComponent(AnimationComponent.class);
        
        // เพิ่ม texture ของปืนเข้าไปใน entity
        entity.getViewComponent().addChild(texture);

        // เชื่อมต่อกับ ControllerComponent
        ControllerComponent controller = entity.getComponent(ControllerComponent.class);
        if (controller != null) {
            controller.setGunComponent(this);
        }
    }

    public AnimatedTexture getTexture() {
        return texture;
    }

    public void shoot() {
        isShooting = true;
        // ตรวจสอบทิศทางปัจจุบันของผู้เล่น
        String playerDirection = playerAnimation.checkwalk();
        System.out.println("Current direction: " + playerDirection); // Debug log
        
        if (playerDirection.contains("Right") || playerDirection.contains("right") || 
            (currentAnimationState.contains("Right") && !playerDirection.contains("Left"))) {
            // ถ้าผู้เล่นหันขวา หรือครั้งสุดท้ายหันขวา ให้ยิงจากซ้ายไปขวา
            texture.playAnimationChannel(shootRight);
            currentAnimationState = "shootRight";
            // ปรับตำแหน่งปืนเมื่อหันขวา
            texture.setTranslateX(6);
        } else {
            // ถ้าผู้เล่นหันซ้าย หรือกรณีอื่นๆ ให้ยิงจากขวาไปซ้าย
            texture.playAnimationChannel(shootLeft);
            currentAnimationState = "shootLeft";
            // ปรับตำแหน่งปืนเมื่อหันซ้าย
            texture.setTranslateX(-6);
        }

        // หลังจากยิงเสร็จ รอให้แอนิเมชันจบแล้วกลับไปท่าเดิม
        FXGL.runOnce(() -> {
            isShooting = false;
            if (!playerAnimation.checkwalk().equals("idle")) {
                // ถ้ากำลังเดินอยู่ ให้กลับไปแอนิเมชันเดิน
                walk(playerAnimation.checkwalk());
            } else {
                // ถ้าไม่ได้เดิน ให้กลับไปท่าปกติ
                idle();
            }
        }, Duration.seconds(0.5));
    }

    public void walk(String direction) {
        if (isShooting) return; // ถ้ากำลังยิงอยู่ ไม่ต้องเปลี่ยนแอนิเมชัน

        if (direction.contains("Right") || direction.contains("right")) {
            texture.loopAnimationChannel(walkRight);
            currentAnimationState = "walkRight";
            texture.setTranslateX(6);
        } else if (direction.contains("Left") || direction.contains("left")) {
            texture.loopAnimationChannel(walkLeft);
            currentAnimationState = "walkLeft";
            texture.setTranslateX(-6);
        }
    }

    public void idle() {
        if (isShooting) return; // ถ้ากำลังยิงอยู่ ไม่ต้องเปลี่ยนแอนิเมชัน

        // กลับไปยังแอนิเมชันเริ่มต้นตามทิศทางปัจจุบัน
        if (currentAnimationState.contains("Right")) {
            texture.loopAnimationChannel(walkRight);
            texture.setTranslateX(6);
        } else if (currentAnimationState.contains("Left")) {
            texture.loopAnimationChannel(walkLeft);
            texture.setTranslateX(-6);
        }
    }
} 