package com.project;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.dsl.FXGL;

public class ZombieAttackControl extends Component {

    private double timeSinceLastAttack = 0;
    private static final double ATTACK_COOLDOWN = 1.0; // หน่วงเวลาการโจมตี 1 วินาที

    @Override
    public void onUpdate(double tpf) {
        timeSinceLastAttack += tpf;
        // ค้นหาผู้เล่นในเกม (สมมติว่ามีแค่ผู้เล่นเดียว)
        Entity player = FXGL.getGameWorld().getEntitiesByType(ZombieShooterGame.EntityType.PLAYER)
                .stream().findFirst().orElse(null);
        if (player != null) {
            if (entity.getBoundingBoxComponent().isCollidingWith(player.getBoundingBoxComponent())) {
                if (timeSinceLastAttack >= ATTACK_COOLDOWN) {
                    player.getComponent(PlayerHealth.class).takeDamage(10);
                    timeSinceLastAttack = 0;
                }
            }
        }
    }
}