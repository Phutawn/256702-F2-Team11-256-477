package com.project.Factory;

import static com.almasb.fxgl.dsl.FXGL.texture;

import com.project.Component.CharecterHero.AnimationComponent;
import com.project.Component.CharecterHero.ControllerComponent;
import com.project.Component.StatusComponent;
import com.project.Type.SceneType;
//import com.Type.Enemy.EnemyType;
import com.project.Type.Player.PlayerType;
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

import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.geometry.Point2D;

public class CharacterFactory implements EntityFactory {
    @Spawns("spawn point")
    public Entity newPlayerCharacter(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        
        // สร้าง Entity หลัก
        Entity player = FXGL.entityBuilder(data)
                .type(PlayerType.Hero)
                .bbox(new HitBox(BoundingShape.box(40, 50)))           
                .with(physics)
                .with(new CollidableComponent(true))
                .with(new ControllerComponent())
                .with(new AnimationComponent("Actor1.png"))
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

    

    

    
}

