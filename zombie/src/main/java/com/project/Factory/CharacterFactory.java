package com.project.Factory;

import static com.almasb.fxgl.dsl.FXGL.texture;

import com.project.Component.CharecterPlayer.AnimationComponent;
import com.project.Component.CharecterPlayer.ControllerComponent;
import com.project.Component.CharecterZombie.ZombieAnimationComponent;
import com.project.Type.SceneType;
//import com.Type.Enemy.EnemyType;
import com.project.Type.Player.PlayerType;
import com.project.Type.Enemy.ZombieType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.project.Controller.BulletControl;
import com.project.model.PlayerAmmo;
import com.project.Controller.ZombieShooterGame.EntityType;
import com.project.Component.CharecterZombie.ZombieAttackControl;
import com.project.model.PlayerHealth;
import com.project.model.PlayerMedicalSupplies;
import com.project.Component.ItemAnimationComponent;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class CharacterFactory implements EntityFactory {
    @Spawns("player")
    public Entity newPlayerCharacter(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        
        // สร้าง Entity หลัก
        Entity player = FXGL.entityBuilder(data)
                .type(EntityType.PLAYER)
                .bbox(new HitBox(BoundingShape.box(20, 28)))           
                .with(physics)
                .with(new CollidableComponent(true))
                .with(new ControllerComponent())
                .with(new AnimationComponent("Player.png"))
                .with(new PlayerAmmo(10)) // เพิ่มระบบกระสุน
                .with(new PlayerHealth()) // เพิ่มระบบเลือด
                .with(new PlayerMedicalSupplies()) // เพิ่มระบบ medical supplies
                .build();

        // ตั้งค่าเลือดเริ่มต้น
        PlayerHealth healthComponent = player.getComponent(PlayerHealth.class);
        healthComponent.setHealth(FXGL.geti("playerHealth"));

        // เพิ่มชื่อผู้เล่นให้แสดงบนหัว
        Text playerNameText = new Text(FXGL.gets("playerName")); // ใช้ชื่อจากตัวแปรเกม
        playerNameText.setStyle("-fx-font-size: 8px; -fx-fill: white;");
        // ตั้งค่าตำแหน่งให้อยู่ตรงกลาง
        double textWidth = playerNameText.getLayoutBounds().getWidth();
        double playerWidth = 20; // ความกว้างของตัวละคร
        playerNameText.setTranslateX((playerWidth - textWidth) / 2);
        playerNameText.setTranslateY(-5);
        player.getViewComponent().addChild(playerNameText);

        return player;
    }

    @Spawns("zombie")
    public Entity newZombie(SpawnData data) {
        // สร้าง Entity ของซอมบี้
        Entity zombie = FXGL.entityBuilder(data)
                .type(EntityType.ZOMBIE)
                .bbox(new HitBox(BoundingShape.box(22, 28)))           
                .with(new CollidableComponent(true))
                .with(new ZombieAttackControl())
                .with(new ZombieAnimationComponent())
                .build();

        // เพิ่มชื่อซอมบี้ให้แสดงบนหัว
        
        return zombie;
    }


    @Spawns("bullet")
    public Entity newBullet(SpawnData data) {
        // สร้าง Entity ของกระสุน
        Entity bullet = FXGL.entityBuilder(data)
                .type(EntityType.BULLET)
                .viewWithBBox(new Circle(2, Color.YELLOW))
                .with(new CollidableComponent(true))
                .build();

        // เพิ่มคอนโทรลสำหรับการเคลื่อนที่ของกระสุน
        bullet.addComponent(new BulletControl(data.get("dirX"), data.get("dirY"), 300));

        return bullet;
    }

    @Spawns("magazine")
    public Entity newMagazine(SpawnData data) {
        // สร้าง Entity ของนิตยสารกระสุน
        Entity magazine = FXGL.entityBuilder(data)
                .type(EntityType.MAGAZINE)
                .with(new CollidableComponent(true))
                .with(new ItemAnimationComponent("ammo.png", 16, 16, 7, Duration.seconds(0.1)))
                .build();

        // เพิ่ม HitBox ที่ชัดเจน
        magazine.getBoundingBoxComponent().addHitBox(new HitBox(BoundingShape.box(16, 16)));
        System.out.println("Created magazine with collision at: " + data.getX() + ", " + data.getY());
        return magazine;
    }

    @Spawns("medical_supply")
    public Entity newMedicalSupply(SpawnData data) {
        // สร้าง Entity ของวัสดุทางการแพทย์
        Entity supply = FXGL.entityBuilder(data)
                .type(EntityType.MEDICAL_SUPPLY)
                .with(new CollidableComponent(true))
                .with(new ItemAnimationComponent("foodStock.png", 16, 16, 7, Duration.seconds(0.1)))
                .build();

        // เพิ่ม HitBox ที่ชัดเจน
        supply.getBoundingBoxComponent().addHitBox(new HitBox(BoundingShape.box(16, 16)));
        System.out.println("Created medical supply with collision at: " + data.getX() + ", " + data.getY());
        return supply;
    }

    @Spawns("medical_kit")
    public Entity newMedicalKit(SpawnData data) {
        // สร้าง Entity ของกล่องยา
        Entity medicalKit = FXGL.entityBuilder(data)
                .type(EntityType.MEDICAL_KIT)
                .with(new CollidableComponent(true))
                .with(new ItemAnimationComponent("medkit.png", 16, 16, 7, Duration.seconds(0.1)))
                .build();

        // เพิ่ม HitBox ที่ชัดเจน
        medicalKit.getBoundingBoxComponent().addHitBox(new HitBox(BoundingShape.box(16, 16)));
        System.out.println("Created medical kit with collision at: " + data.getX() + ", " + data.getY());
        return medicalKit;
    }
}

