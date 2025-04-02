package com.project.Component.CharecterPlayer;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;

import javafx.scene.image.Image;
import javafx.util.Duration;

public class AnimationComponent extends Component {
     
    private String currentAnimationState = "idleDown";
    private String lastHorizontalDirection = "right"; // ค่าเริ่มต้นเป็น "right"
    private final AnimatedTexture texture;
    private final AnimationChannel left;
    private final AnimationChannel right;
    private final AnimationChannel idleLeft;
    private final AnimationChannel idleRight;

    public AnimationComponent(String nameFile) {
        Image image = FXGL.image(nameFile);

        int columns = 6;
        int rows = 4;

        int frameW = (int) image.getWidth() / columns; // 192 / 6 = 32
        int frameH = (int) image.getHeight() / rows;  // 128 / 4 = 32

        left = new AnimationChannel(image, columns, frameW, frameH, Duration.seconds(1), 12, 17);
        right = new AnimationChannel(image, columns, frameW, frameH, Duration.seconds(1), 18, 23);
        idleLeft = new AnimationChannel(image, columns, frameW, frameH, Duration.seconds(1), 0, 5);
        idleRight = new AnimationChannel(image, columns, frameW, frameH, Duration.seconds(1), 6, 11);

        texture = new AnimatedTexture(idleLeft);
        texture.loopAnimationChannel(idleLeft);

        // ปรับตำแหน่งของภาพให้อยู่ตรงกลาง Hitbox
        texture.setTranslateX(-6);
        texture.setTranslateY(-3.5); // Offset Y
    }

    public AnimatedTexture getTexture() {
        return texture;
    }

    public void walkUp() {
        if (lastHorizontalDirection.equals("left")) {
            if (texture.getAnimationChannel() != left) {
                texture.playAnimationChannel(left);
                texture.loopAnimationChannel(left);
            }
        } else if (lastHorizontalDirection.equals("right")) {
            if (texture.getAnimationChannel() != right) {
                texture.playAnimationChannel(right);
                texture.loopAnimationChannel(right);
            }
        }
        currentAnimationState = "walkUp";
    }

    public void walkDown() {
        if (lastHorizontalDirection.equals("left")) {
            if (texture.getAnimationChannel() != left) {
                texture.playAnimationChannel(left);
                texture.loopAnimationChannel(left);
            }
        } else if (lastHorizontalDirection.equals("right")) {
            if (texture.getAnimationChannel() != right) {
                texture.playAnimationChannel(right);
                texture.loopAnimationChannel(right);
            }
        }
        currentAnimationState = "walkDown";
    }

    public void walkLeft() {
        if (texture.getAnimationChannel() != left) {
            texture.playAnimationChannel(left);
            texture.loopAnimationChannel(left);
        }
        currentAnimationState = "walkLeft";
        lastHorizontalDirection = "left"; // บันทึกการเดินซ้าย
    }

    public void walkRight() {
        if (texture.getAnimationChannel() != right) {
            texture.playAnimationChannel(right);
            texture.loopAnimationChannel(right);
        }
        currentAnimationState = "walkRight";
        lastHorizontalDirection = "right"; // บันทึกการเดินขวา
    }

    public String checkwalk(){

        return currentAnimationState;

    }

    public void idleDown() {
        texture.loopAnimationChannel(idleLeft);
    }
    public void idleUp() {
        texture.loopAnimationChannel(idleRight);
    }

    public void idleRight() {
        texture.loopAnimationChannel(idleRight);
    }

    public void idleLeft() {
        texture.loopAnimationChannel(idleLeft);
    }

    

    
}
