@Spawns("spawn point")
public Entity newPlayerCharacter(SpawnData data) {
    PhysicsComponent physics = new PhysicsComponent();
    physics.setBodyType(BodyType.DYNAMIC);
    
    // สร้าง Entity หลัก
    Entity player = FXGL.entityBuilder(data)
            .type(EntityType.PLAYER)  // เปลี่ยนจาก PlayerType.PLAYER เป็น EntityType.PLAYER
            .bbox(new HitBox(BoundingShape.box(20, 28)))           
            .with(physics)
            .with(new CollidableComponent(true))
            .with(new ControllerComponent())
            .with(new AnimationComponent("Player.png"))
            .with(new StatusComponent("Player", 100, 100, 100, 100))
            .with(new PlayerAmmo(10))
            .with(new PlayerHealth())
            .build();

    // ... existing code ...
    return player;
}

@Spawns("zombie")
public Entity newZombie(SpawnData data) {
    return FXGL.entityBuilder(data)
            .type(EntityType.ZOMBIE)
            .bbox(new HitBox(BoundingShape.box(20, 28)))           
            .with(new CollidableComponent(true))
            .with(new ZombieAttackControl())
            .with(new ZombieAnimationComponent())
            .build();
}

@Spawns("magazine")
public Entity newMagazine(SpawnData data) {
    Entity magazine = FXGL.entityBuilder(data)
            .type(EntityType.MAGAZINE)
            .viewWithBBox(new Rectangle(15, 15, Color.YELLOW))
            .with(new CollidableComponent(true))
            .bbox(new HitBox(BoundingShape.box(15, 15)))
            .build();
    
    System.out.println("Created magazine with collision at: " + data.getX() + ", " + data.getY());
    return magazine;
}

@Spawns("medical_supply")
public Entity newMedicalSupply(SpawnData data) {
    Entity supply = FXGL.entityBuilder(data)
            .type(EntityType.MEDICAL_SUPPLY)
            .viewWithBBox(new Rectangle(10, 10, Color.LIGHTGREEN))
            .with(new CollidableComponent(true))
            .bbox(new HitBox(BoundingShape.box(10, 10)))
            .build();
    
    System.out.println("Created medical supply with collision at: " + data.getX() + ", " + data.getY());
    return supply;
} 