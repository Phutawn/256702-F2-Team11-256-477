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
    private int playerDirectionX = 0;
    private int playerDirectionY = 0;
    private int lastDirectionX = 0;
    private int lastDirectionY = -1;
    private int ammo = 10;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<AmmoDrop> ammoDrops = new ArrayList<>();
    private ArrayList<Zombie> zombies = new ArrayList<>();
    private boolean shooting = false;
    private boolean isBlack = true;
    private int currentMap = 0;
    private Random random = new Random();

    public GamePanel(JFrame frame) {
        setBackground(Color.GRAY);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        playerDirectionY = -1;
                        break;
                    case KeyEvent.VK_S:
                        playerDirectionY = 1;
                        break;
                    case KeyEvent.VK_A:
                        playerDirectionX = -1;
                        break;
                    case KeyEvent.VK_D:
                        playerDirectionX = 1;
                        break;
                    case KeyEvent.VK_SPACE:
                        if (!shooting) {
                            shootBullet();
                            shooting = true;
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        goToMainMenu(frame);
                        break;
                }
                movePlayer();
                checkMapTransition();
                repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_S) {
                    playerDirectionY = 0;
                }
                if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_D) {
                    playerDirectionX = 0;
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    shooting = false;
                }
            }
        });

        generateNewMap();

        Timer timer = new Timer(20, e -> updateGame());
        timer.start();

        Timer colorTimer = new Timer(500, e -> {
            isBlack = !isBlack;
            repaint();
        });
        colorTimer.start();

        Timer ammoDropTimer = new Timer(15000, e -> dropAmmo());
        ammoDropTimer.start();

        Timer zombieSpawnTimer = new Timer(5000, e -> spawnZombie());
        zombieSpawnTimer.start();
    }

    private void shootBullet() {
        if (ammo > 0) {
            bullets.add(new Bullet(playerX + 25, playerY + 25, lastDirectionX, lastDirectionY));
            ammo--;
            repaint();
        }
    }

    private void movePlayer() {
        playerX += playerDirectionX * playerSpeed;
        playerY += playerDirectionY * playerSpeed;

        if (playerDirectionX != 0 || playerDirectionY != 0) {
            lastDirectionX = playerDirectionX;
            lastDirectionY = playerDirectionY;
        }
    }

    private void dropAmmo() {
        int x = random.nextInt(getWidth() - 20);
        int y = random.nextInt(getHeight() - 20);
        ammoDrops.add(new AmmoDrop(x, y));
        repaint();
    }

    private void spawnZombie() {
        zombies.add(new Zombie(getWidth(), getHeight()));
        repaint();
    }

    private void updateGame() {
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.move();
            if (bullet.isOutOfBounds(getWidth(), getHeight())) {
                bulletIterator.remove();
            } else {
                Iterator<Zombie> zombieIterator = zombies.iterator();
                while (zombieIterator.hasNext()) {
                    Zombie zombie = zombieIterator.next();
                    if (zombie.isAlive() && zombie.getBounds().intersects(new Rectangle(bullet.getX(), bullet.getY(), 10, 10))) {
                        zombie.kill();
                        bulletIterator.remove();
                        break;
                    }
                }
            }
        }

        Iterator<Zombie> zombieIterator = zombies.iterator();
        while (zombieIterator.hasNext()) {
            Zombie zombie = zombieIterator.next();
            if (zombie.isAlive()) {
                zombie.move(playerX, playerY);
            }
        }

        checkAmmoPickup();
        repaint();
    }

    private void checkAmmoPickup() {
        Iterator<AmmoDrop> iterator = ammoDrops.iterator();
        while (iterator.hasNext()) {
            AmmoDrop drop = iterator.next();
            if (new Rectangle(playerX, playerY, 50, 50).intersects(new Rectangle(drop.getX(), drop.getY(), 20, 20))) {
                ammo += 5;
                iterator.remove();
            }
        }
    }

    private void checkMapTransition() {
        if (playerX < 0) {
            playerX = getWidth() - 50;
            generateNewMap();
            clearBullets(); 
        } else if (playerX > getWidth()) {
            playerX = 0;
            generateNewMap();
            clearBullets(); 
        } else if (playerY < 0) {
            playerY = getHeight() - 50;
            generateNewMap();
            clearBullets(); 
        } else if (playerY > getHeight()) {
            playerY = 0;
            generateNewMap();
            clearBullets();  
        }
    }

    private void generateNewMap() {
        currentMap = random.nextInt(5);
        setBackground(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        zombies.clear();
        ammoDrops.clear();
        repaint();
    }

    private void clearBullets() {
        bullets.clear();  // ลบกระสุนทั้งหมด
    }

    private void goToMainMenu(JFrame frame) {
        ZombieSurvivalMenuTH.showMenu(frame);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Font font = new Font("Tahoma", Font.PLAIN, 16);
        g.setFont(font);

        g.setColor(isBlack ? Color.BLACK : Color.WHITE);
        g.fillRect(playerX, playerY, 50, 50);

        g.setColor(Color.YELLOW);
        for (Bullet bullet : bullets) {
            g.fillOval(bullet.getX(), bullet.getY(), 10, 10);
        }

        for (AmmoDrop ammoDrop : ammoDrops) {
            ammoDrop.draw(g);
        }

        for (Zombie zombie : zombies) {
            zombie.draw(g);
        }

        g.setColor(Color.WHITE);
        g.drawString("Ammo: " + ammo, 10, 20);
        g.drawString("Map: " + currentMap, 10, 40);
        
        g.drawString("กด ESC เพื่อกลับสู่หน้าหลัก", 10, getHeight() - 10);
    }
}
