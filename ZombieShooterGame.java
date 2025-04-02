@Override
protected void initPhysics() {
    // เมื่อผู้เล่นชนกับนิตยสารกระสุน
    FXGL.onCollisionBegin(EntityType.PLAYER, EntityType.MAGAZINE, (player, magazine) -> {
        System.out.println("Player collided with magazine!");
        PlayerAmmo ammoComponent = player.getComponent(PlayerAmmo.class);
        if (ammoComponent != null) {
            ammoComponent.addAmmo(10);
            magazine.removeFromWorld();
            FXGL.showMessage("+10 Ammo!");
        }
    });

    // เมื่อผู้เล่นชนกับวัสดุทางการแพทย์
    FXGL.onCollisionBegin(EntityType.PLAYER, EntityType.MEDICAL_SUPPLY, (player, supply) -> {
        System.out.println("Player collided with medical supply!");
        PlayerHealth healthComponent = player.getComponent(PlayerHealth.class);
        if (healthComponent != null) {
            healthComponent.heal(20);
            supply.removeFromWorld();
            FXGL.showMessage("+20 HP!");
        }
    });

    // เมื่อกระสุนชนกับซอมบี้
    FXGL.onCollisionBegin(EntityType.BULLET, EntityType.ZOMBIE, (bullet, zombie) -> {
        bullet.removeFromWorld();
        zombie.getComponent(ZombieAttackControl.class).stopMovement();
        zombie.getComponent(ZombieAnimationComponent.class).hit();
        
        FXGL.runOnce(() -> {
            if (zombie != null && zombie.isActive()) {
                zombie.removeFromWorld();
                zombieKillCount++;
                highScoreDisplay.setText("Longest Survival: " + (int) longestSurvivalTime 
                    + " sec\nMost Kills: " + mostZombieKills);
            }
        }, Duration.seconds(1));
    });

    // เมื่อผู้เล่นชนกับซอมบี้
    FXGL.onCollisionBegin(EntityType.PLAYER, EntityType.ZOMBIE, (player, zombie) -> {
        System.out.println("Player collided with zombie!");
        PlayerHealth healthComponent = player.getComponent(PlayerHealth.class);
        if (healthComponent != null) {
            healthComponent.takeDamage(10);
            System.out.println("Player health reduced to: " + healthComponent.getHealth());
        }
    });
} 