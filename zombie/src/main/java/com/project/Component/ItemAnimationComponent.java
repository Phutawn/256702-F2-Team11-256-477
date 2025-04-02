package com.project.Component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class ItemAnimationComponent extends Component {
    private ImageView view;
    private Image spriteSheet;
    private int frameWidth;
    private int frameHeight;
    private int currentFrame = 0;
    private int totalFrames;
    private Duration frameDuration;

    public ItemAnimationComponent(String spriteSheetPath, int frameWidth, int frameHeight, int totalFrames, Duration frameDuration) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.totalFrames = totalFrames;
        this.frameDuration = frameDuration;
        
        // โหลดสไปรท์ชีท
        spriteSheet = FXGL.image(spriteSheetPath);
        view = new ImageView(spriteSheet);
        
        // ตั้งค่าขนาดของเฟรม
        view.setFitWidth(frameWidth);
        view.setFitHeight(frameHeight);
        
        // เริ่มอนิเมชั่น
        startAnimation();
    }

    private void startAnimation() {
        // รีเซ็ตเฟรมปัจจุบันเป็น 0
        currentFrame = 0;
        updateFrame();
        
        FXGL.getGameTimer().runAtInterval(() -> {
            if (entity != null && entity.isActive()) {
                currentFrame = (currentFrame + 1) % totalFrames;
                updateFrame();
            }
        }, frameDuration);
    }

    private void updateFrame() {
        view.setViewport(new javafx.geometry.Rectangle2D(
            currentFrame * frameWidth, 0, frameWidth, frameHeight
        ));
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(view);
        // รีเซ็ตเฟรมเมื่อเพิ่มคอมโพเนนต์
        currentFrame = 0;
        updateFrame();
    }

    @Override
    public void onRemoved() {
        entity.getViewComponent().removeChild(view);
    }
} 