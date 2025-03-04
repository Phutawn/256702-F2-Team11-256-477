package Project;

import java.awt.*;

public class ScrapDrop {
    private int x, y;
    private static final int SIZE = 20;
    private boolean isPickedUp = false;
    
    public ScrapDrop(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void draw(Graphics g) {
        if (!isPickedUp) {
            g.setColor(new Color(184, 134, 11));
            g.fillRect(x, y, SIZE, SIZE);
        }
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getSize() {
        return SIZE;
    }
    
    public void pickUp() {
        isPickedUp = true;
    }
    
    public boolean isPickedUp() {
        return isPickedUp;
    }
}
