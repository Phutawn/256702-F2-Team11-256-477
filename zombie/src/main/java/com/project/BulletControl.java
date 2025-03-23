package com.project;

import com.almasb.fxgl.entity.component.Component;

public class BulletControl extends Component {
    private double directionX;
    private double directionY;
    private double speed;

    public BulletControl(double directionX, double directionY, double speed) {
        this.directionX = directionX;
        this.directionY = directionY;
        this.speed = speed;
    }

    @Override
    public void onUpdate(double tpf) {
        entity.translateX(directionX * speed * tpf);
        entity.translateY(directionY * speed * tpf);

        // ลบกระสุนเมื่อออกนอกหน้าจอ
        if (entity.getX() < 0 || entity.getX() > 800 || entity.getY() < 0 || entity.getY() > 600) {
            entity.removeFromWorld();
        }
    }
}
