package com.project.model;

import com.almasb.fxgl.dsl.FXGL;
import com.project.Controller.ZombieShooterGame;
import javafx.util.Duration;

public class ZombieAnimationInjector {

    static {
        // ตรวจสอบทุก 1 วินาทีว่ามี zombie entity ที่ยังไม่มี ZombieAnimationControl แล้วเพิ่มเข้าไป
        FXGL.getGameTimer().runAtInterval(() -> {
            FXGL.getGameWorld().getEntitiesByType(ZombieShooterGame.EntityType.ZOMBIE).forEach(zombie -> {
                if (!zombie.hasComponent(ZombieAnimationControl.class)) {
                    zombie.addComponent(new ZombieAnimationControl());
                }
            });
        }, Duration.seconds(1));
    }
}
