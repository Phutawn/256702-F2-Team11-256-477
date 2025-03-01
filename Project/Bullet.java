package Project;

import java.awt.event.KeyEvent;

public class Bullet {
    private int x, y;
    private final int speed = 10;
    private int direction;

    public Bullet(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public void move() {
        switch (direction) {
            case KeyEvent.VK_W:
                y -= speed;
                break;
            case KeyEvent.VK_S:
                y += speed;
                break;
            case KeyEvent.VK_A:
                x -= speed;
                break;
            case KeyEvent.VK_D:
                x += speed;
                break;
        }
    }

    public boolean isOutOfBounds(int width, int height) {
        return x < 0 || x > width || y < 0 || y > height;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
