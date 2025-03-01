package Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends JPanel {
    private int playerX = 100;
    private int playerY = 100;
    private final int playerSpeed = 5;
    private int playerDirection = KeyEvent.VK_D;
    private int ammo = 10;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<AmmoDrop> ammoDrops = new ArrayList<>();
    private Random rand = new Random();
    private boolean shooting = false;
    private long lastAmmoDropTime = System.currentTimeMillis();  

    public GamePanel() {
        setBackground(Color.GRAY);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        playerY -= playerSpeed;
                        playerDirection = KeyEvent.VK_W;
                        break;
                    case KeyEvent.VK_S:
                        playerY += playerSpeed;
                        playerDirection = KeyEvent.VK_S;
                        break;
                    case KeyEvent.VK_A:
                        playerX -= playerSpeed;
                        playerDirection = KeyEvent.VK_A;
                        break;
                    case KeyEvent.VK_D:
                        playerX += playerSpeed;
                        playerDirection = KeyEvent.VK_D;
                        break;
                    case KeyEvent.VK_SPACE:
                        if (!shooting) {  
                            shootBullet();
                            shooting = true;
                        }
                        break;
                }
                repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    shooting = false;  
                }
            }
        });

        Timer timer = new Timer(20, e -> updateGame());
        timer.start();
    }

    public void startGame() {
        playerX = 100;
        playerY = 100;
        ammo = 10;
        bullets.clear();
        ammoDrops.clear();
    }

    private void shootBullet() {
        if (ammo > 0) {
            bullets.add(new Bullet(playerX + 25, playerY + 25, playerDirection));
            ammo--;
            repaint();
        }
    }

    private void updateGame() {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.move();
            if (bullet.isOutOfBounds(getWidth(), getHeight())) {
                iterator.remove();
            }
        }

        Iterator<AmmoDrop> ammoIterator = ammoDrops.iterator();
        while (ammoIterator.hasNext()) {
            AmmoDrop ammoDrop = ammoIterator.next();
            if (ammoDrop.isPickedUp(playerX, playerY)) {
                ammo += 5; 
                ammoIterator.remove(); 
            }
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAmmoDropTime >= 15000) {  
            generateAmmoDrop();
            lastAmmoDropTime = currentTime; 
        }

        repaint();
    }

    private void generateAmmoDrop() {
        int x = rand.nextInt(getWidth() - 20);
        int y = rand.nextInt(getHeight() - 20);
        ammoDrops.add(new AmmoDrop(x, y));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLUE);
        g.fillRect(playerX, playerY, 50, 50);

        g.setColor(Color.YELLOW);
        for (Bullet bullet : bullets) {
            g.fillOval(bullet.getX(), bullet.getY(), 10, 10);
        }

        for (AmmoDrop ammoDrop : ammoDrops) {
            ammoDrop.draw(g);
        }

        g.setColor(Color.WHITE);
        g.drawString("Ammo: " + ammo, 10, 20);
    }
}
