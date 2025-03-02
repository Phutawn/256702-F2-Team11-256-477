package Project;

public class Bullet {
    private int x, y;
    private final int speed = 10;
    private int directionX, directionY;

    public Bullet(int x, int y, int directionX, int directionY) {
        this.x = x;
        this.y = y;
        this.directionX = directionX;
        this.directionY = directionY;
    }

    public void move() {
        x += directionX * speed;
        y += directionY * speed;
    }

    public boolean isOutOfBounds(int width, int height) {
        return x < 0 || x > width || y < 0 || y > height;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
