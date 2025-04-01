package com.project.Factory;

import static com.almasb.fxgl.dsl.FXGL.texture;

import com.project.Component.StatusComponent;
import com.project.Component.CharecterPlayer.AnimationComponent;
import com.project.Component.CharecterPlayer.ControllerComponent;
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
import com.project.model.ZombieAnimationComponent;
import com.project.Controller.ZombieAttackControl;

import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

public class CharacterFactory implements EntityFactory {
    @Spawns("spawn point")
    public Entity newPlayerCharacter(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        
        // สร้าง Entity หลัก
        Entity player = FXGL.entityBuilder(data)
                .type(PlayerType.PLAYER)
                .bbox(new HitBox(BoundingShape.box(20, 28)))           
                .with(physics)
                .with(new CollidableComponent(true))
                .with(new ControllerComponent())
                .with(new AnimationComponent("Player.png"))
                .with(new StatusComponent("Player", 100, 100, 100, 100)) // เพิ่ม Status Component
                .with(new PlayerAmmo(10)) // เพิ่มระบบกระสุน
                .build();

        // เพิ่มชื่อผู้เล่นให้แสดงบนหัว
        Text playerNameText = new Text("Player");
        playerNameText.setStyle("-fx-font-size: 18px; -fx-fill: white;");
        playerNameText.setTranslateY(-20);
        player.getViewComponent().addChild(playerNameText);

        return player;
    }

    @Spawns("zombie")
    public Entity newZombie(SpawnData data) {
        // สร้าง Entity ของซอมบี้
        Entity zombie = FXGL.entityBuilder(data)
                .type(EntityType.ZOMBIE)
                .bbox(new HitBox(BoundingShape.box(20, 28)))           
                .with(new CollidableComponent(true))
                .with(new ZombieAttackControl())
                .with(new ZombieAnimationComponent())
                .build();

        // เพิ่มชื่อซอมบี้ให้แสดงบนหัว
        System.out.println("Created zombie at: " + data.getX() + ", " + data.getY());
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
                .viewWithBBox(new Rectangle(15, 15, Color.YELLOW))
                .with(new CollidableComponent(true))
                .build();

        return magazine;
    }

    @Spawns("medical_supply")
    public Entity newMedicalSupply(SpawnData data) {
        // สร้าง Entity ของวัสดุทางการแพทย์
        Entity supply = FXGL.entityBuilder(data)
                .type(EntityType.MEDICAL_SUPPLY)
                .viewWithBBox(new Rectangle(10, 10, Color.LIGHTGREEN))
                .with(new CollidableComponent(true))
                .build();

        return supply;
    }
}

