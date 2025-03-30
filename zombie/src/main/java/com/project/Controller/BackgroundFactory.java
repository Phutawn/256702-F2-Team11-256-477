package com.project.Controller;

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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BackgroundFactory implements EntityFactory {

    @Spawns("Wall")
    public Entity spawnWall(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.STATIC); // กำหนดให้เป็นวัตถุแบบ Static (ไม่เคลื่อนที่)

        return FXGL.entityBuilder(data)
                .at(data.getX(), data.getY()) // กำหนดตำแหน่งจาก SpawnData
                .bbox(new HitBox(BoundingShape.box(data.get("width"), data.get("height")))) // กำหนด HitBox
                .with(physics) // เพิ่ม PhysicsComponent
                .with(new CollidableComponent(true)) // กำหนดให้ชนได้
                .view(new Rectangle(data.get("width"), data.get("height"), Color.GRAY)) // เพิ่มมุมมอง (สีเทา)
                .build();
    }
}
