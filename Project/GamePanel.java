package Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel {
    private int playerX = 100, playerY = 100;
    private final int playerSpeed = 5;
    private int playerDirection = KeyEvent.VK_D;
    private int ammo = 100;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<AmmoDrop> ammoDrops = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private Random rand = new Random();
    private boolean shooting = false;
    private long lastAmmoDropTime = System.currentTimeMillis();  
    private boolean gameOver = false;

    public GamePanel() {
        setBackground(Color.GRAY);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameOver && e.getKeyCode() == KeyEvent.VK_R) {
                    resetGame();
                    return;
                }
                
                if (!gameOver) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_W:
                        case KeyEvent.VK_UP:
                            playerY -= playerSpeed;
                            playerDirection = KeyEvent.VK_W;
                            break;
                        case KeyEvent.VK_S:
                        case KeyEvent.VK_DOWN:
                            playerY += playerSpeed;
                            playerDirection = KeyEvent.VK_S;
                            break;
                        case KeyEvent.VK_A:
                        case KeyEvent.VK_LEFT:
                            playerX -= playerSpeed;
                            playerDirection = KeyEvent.VK_A;
                            break;
                        case KeyEvent.VK_D:
                        case KeyEvent.VK_RIGHT:
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
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    shooting = false;  
                }
            }
        });

        Timer timer = new Timer(2, e -> updateGame());
        timer.start();
    }

    public void resetGame() {
        playerX = 100;
        playerY = 100;
        ammo = 10;
        bullets.clear();
        ammoDrops.clear();
        enemies.clear();
        gameOver = false;
        spawnEnemy();
        repaint();
    }

    private void shootBullet() {
        if (ammo > 0) {
            bullets.add(new Bullet(playerX + 25, playerY + 25, playerDirection));
            ammo--;
            repaint();
        }
    }

    private void updateGame() {
        if (gameOver) return;

        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.move();
            if (bullet.isOutOfBounds(getWidth(), getHeight())) {
                iterator.remove();
            }

            for (int i = 0; i < enemies.size(); i++) {
                Enemy enemy = enemies.get(i);
                if (bullet.getX() < enemy.x + 20 && bullet.getX() + 10 > enemy.x && bullet.getY() < enemy.y + 20 && bullet.getY() + 10 > enemy.y) {
                    enemies.remove(i);
                    iterator.remove();
                    spawnEnemy();
                    break;
                }
            }
        }

        for (Enemy enemy : enemies) {
            enemy.update();
            if (checkCollisionWithPlayer(enemy)) {
                gameOver = true;
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

    private boolean checkCollisionWithPlayer(Enemy enemy) {
        return playerX < enemy.x + 20 && playerX + 50 > enemy.x && playerY < enemy.y + 20 && playerY + 50 > enemy.y;
    }

    private void spawnEnemy() {
        int spawnX = rand.nextInt(getWidth());
        int spawnY = rand.nextInt(getHeight());
        enemies.add(new Enemy(spawnX, spawnY, playerX, playerY));
    }

    private void generateAmmoDrop() {
        int x = rand.nextInt(getWidth() - 20);
        int y = rand.nextInt(getHeight() - 20);
        ammoDrops.add(new AmmoDrop(x, y));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameOver) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("GAME OVER", getWidth() / 2 - 150, getHeight() / 2);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press R to Restart", getWidth() / 2 - 90, getHeight() / 2 + 40);
            return;
        }

        g.setColor(Color.BLUE);
        g.fillRect(playerX, playerY, 50, 50);

        g.setColor(Color.YELLOW);
        for (Bullet bullet : bullets) {
            g.fillOval(bullet.getX(), bullet.getY(), 10, 10);
        }

        g.setColor(Color.GREEN);
        for (Enemy enemy : enemies) {
            g.fillRect(enemy.x, enemy.y, 20, 20);
        }

        for (AmmoDrop ammoDrop : ammoDrops) {
            ammoDrop.draw(g);
        }

        g.setColor(Color.WHITE);
        g.drawString("Ammo: " + ammo, 10, 20);
    }
}