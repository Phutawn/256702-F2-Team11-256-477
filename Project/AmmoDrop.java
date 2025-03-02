package Project;

import java.awt.*;

public class AmmoDrop {
    private int x, y;
    private static final int SIZE = 20; 
    private boolean isPickedUp = false;

    public AmmoDrop(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        if (!isPickedUp) {
            g.setColor(Color.RED);
            g.fillRect(x, y, SIZE, SIZE); 
        }
    }

    public boolean isPickedUp(int playerX, int playerY) { 
        if (playerX + 50 > x && playerX < x + SIZE && playerY + 50 > y && playerY < y + SIZE) {
            isPickedUp = true; 
            return true;
        }
        return false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
