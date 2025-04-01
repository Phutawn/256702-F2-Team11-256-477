package com.project.model;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.entity.component.Component;
import javafx.util.Duration;

public class ZombieAnimationControl extends Component {

    private AnimatedTexture texture;
    private AnimationChannel idleChannel;
    private AnimationChannel runChannel;
    private double lastX;
    private double lastY;

    @Override
    public void onAdded() {
        // บันทึกตำแหน่งเริ่มต้น
        lastX = entity.getX();
        lastY = entity.getY();
        
        // สร้าง AnimationChannel สำหรับ idle และ run
        idleChannel = new AnimationChannel(FXGL.image("Zombie.png"), 4, 40, 40, Duration.seconds(1), 0, 3);
        runChannel = new AnimationChannel(FXGL.image("Zombie.png"), 4, 40, 40, Duration.seconds(0.8), 0, 3);
        
        // สร้าง AnimatedTexture โดยเริ่มต้นด้วย idleChannel
        texture = new AnimatedTexture(idleChannel);
        
        // เปลี่ยน view ของ entity โดยลบ view เดิมออก
        entity.getViewComponent().clearChildren();
        entity.getViewComponent().addChild(texture);
    }

    @Override
    public void onUpdate(double tpf) {
        double currentX = entity.getX();
        double currentY = entity.getY();
        double dx = currentX - lastX;
        double dy = currentY - lastY;
        lastX = currentX;
        lastY = currentY;

        // ถ้าไม่เคลื่อนที่ (นิ่ง) ให้เล่น idle animation
        if (Math.abs(dx) < 0.1 && Math.abs(dy) < 0.1) {
            if (texture.getAnimationChannel() != idleChannel) {
                texture.playAnimationChannel(idleChannel);
            }
        } else {
            // หากมีการเคลื่อนที่ ให้เล่น run animation
            if (texture.getAnimationChannel() != runChannel) {
                texture.playAnimationChannel(runChannel);
            }
        }
    }
}
