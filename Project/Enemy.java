package Project;

public class Enemy {
    int x, y;
    int dx, dy;

    public Enemy(int x, int y, int targetX, int targetY) {
        this.x = x;
        this.y = y;
        double angle = Math.atan2(targetY - y, targetX - x);
        this.dx = (int) (Math.cos(angle) * 2);
        this.dy = (int) (Math.sin(angle) * 2);
    }

    public void update() {
        x += dx;
        y += dy;
    }

    public boolean checkCollisionWithPlayer(int playerX, int playerY, int playerSize) {
        return x < playerX + playerSize && x + 20 > playerX && y < playerY + playerSize && y + 20 > playerY;
    }
}
