package Project;

import java.awt.*;

public class Zombie {
    private int x, y;
    private final int speed = 2;
    private int direction;
    private boolean isAlive = true;
    private boolean isGreen = true;  

    public Zombie(int mapWidth, int mapHeight) {
        int edge = (int) (Math.random() * 4);
        switch (edge) {
            case 0: 
                x = (int) (Math.random() * mapWidth);
                y = 0;
                break;
            case 1: 
                x = (int) (Math.random() * mapWidth);
                y = mapHeight;
                break;
            case 2: 
                x = 0;
                y = (int) (Math.random() * mapHeight);
                break;
            case 3: 
                x = mapWidth;
                y = (int) (Math.random() * mapHeight);
                break;
        }
    }

    public void move(int playerX, int playerY) {
        if (x < playerX) {
            x += speed;
        } else if (x > playerX) {
            x -= speed;
        }

        if (y < playerY) {
            y += speed;
        } else if (y > playerY) {
            y -= speed;
        }

        isGreen = !isGreen;
    }

    public void draw(Graphics g) {
        if (isAlive) {
            if (isGreen) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.RED);
            }
            g.fillRect(x, y, 40, 40); 
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 40, 40);
    }

    public void kill() {
        isAlive = false;
    }

    public boolean isAlive() {
        return isAlive;
    }
}
