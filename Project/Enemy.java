package Project;

import java.awt.*;

public class Enemy {
    public int x, y;
    private final int speed = 2;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update(int playerX, int playerY) {
        if (x < playerX) x += speed;
        if (x > playerX) x -= speed;
        if (y < playerY) y += speed;
        if (y > playerY) y -= speed;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, 20, 20);
    }
}
