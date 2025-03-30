package com.project.Controller;

import com.almasb.fxgl.entity.component.Component;

public class KeepInBoundsControl extends Component {
    private double minX, minY, maxX, maxY;

    public KeepInBoundsControl(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    @Override
    public void onUpdate(double tpf) {
        if (entity.getX() < minX) entity.setX(minX);
        if (entity.getY() < minY) entity.setY(minY);
        if (entity.getRightX() > maxX) entity.setX(maxX - entity.getWidth());
        if (entity.getBottomY() > maxY) entity.setY(maxY - entity.getHeight());
    }
}