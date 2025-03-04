package Project;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.*;
import java.awt.event.*;

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
    private ArrayList<ScrapDrop> scrapDrops = new ArrayList<>(); 
    private boolean shooting = false;
    private boolean isBlack = true;
    private int currentMap = 0;
    private Random random = new Random();
    private int health = 100;
    private boolean isGameOver = false;

    public GamePanel(JFrame frame) {
        setBackground(Color.GRAY);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isGameOver) {
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
                        case KeyEvent.VK_C:
                            new CraftingMenuTH().setVisible(true);
                            break;
                        case KeyEvent.VK_F:
                            if (PlayerInventory.getFirstAidKits() > 0) {
                                int healAmount = 50;
                                health += healAmount;
                                if (health > 100) {
                                    health = 100;
                                }
                                PlayerInventory.useFirstAidKit();
                                JOptionPane.showMessageDialog(frame, "Used first aid kit, healed " + healAmount + " health");
                                repaint();
                            }
                            break;
                    }
                    movePlayer();
                    checkMapTransition();
                    repaint();
                }
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

        Timer scrapDropTimer = new Timer(20000, e -> dropScrap());
        scrapDropTimer.start();
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

    private void dropScrap() {
        int x = random.nextInt(getWidth() - 20);
        int y = random.nextInt(getHeight() - 20);
        scrapDrops.add(new ScrapDrop(x, y));
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
                zombie.checkCollisionWithPlayer(playerX, playerY, this);
            }
        }

        checkAmmoPickup();
        checkScrapPickup();
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

    private void checkScrapPickup() {
        Iterator<ScrapDrop> iterator = scrapDrops.iterator();
        while (iterator.hasNext()) {
            ScrapDrop scrap = iterator.next();
            if (new Rectangle(playerX, playerY, 50, 50).intersects(new Rectangle(scrap.getX(), scrap.getY(), scrap.getSize(), scrap.getSize()))) {
                scrap.pickUp();
                PlayerInventory.addScrap(1);
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
        scrapDrops.clear();
        repaint();
    }

    private void clearBullets() {
        bullets.clear();
    }

    private void goToMainMenu(JFrame frame) {
        ZombieSurvivalMenuTH.showMenu(frame);
    }

    public void decreaseHealth(int amount) {
        health -= amount;
        if (health <= 0) {
            health = 0;
            isGameOver = true;
            repaint();
        }
    }

    private void showGameOverScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        Font gameOverFont = new Font("Tahoma", Font.BOLD, 50);
        g.setFont(gameOverFont);
        g.drawString("คุณตายแล้ว", getWidth() / 2 - 150, getHeight() / 2 - 50);
        Font creditFont = new Font("Tahoma", Font.PLAIN, 30);
        g.setFont(creditFont);
        g.drawString("เครดิต: นายภูตะวัน กุลชาติชัย", getWidth() / 2 - 180, getHeight() / 2);
        g.drawString("รหัสนักศึกษา: 6730300477", getWidth() / 2 - 140, getHeight() / 2 + 40);
        JButton backButton = new JButton("กลับสู่เมนูหลัก");
        backButton.setBounds(getWidth() / 2 - 75, getHeight() / 2 + 80, 150, 40);
        backButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
        backButton.addActionListener(e -> goToMainMenu((JFrame) SwingUtilities.getWindowAncestor(this)));
        this.add(backButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isGameOver) {
            showGameOverScreen(g);
        } else {
            g.setColor(Color.RED);
            g.fillRect(10, 10, 150, 15);
            int currentHealthWidth = Math.min(health * 2, 150);
            g.setColor(Color.GREEN);
            g.fillRect(10, 10, currentHealthWidth, 15);

            g.setColor(isBlack ? Color.BLACK : Color.WHITE);
            g.fillRect(playerX, playerY, 50, 50);

            g.setColor(Color.YELLOW);
            for (Bullet bullet : bullets) {
                g.fillOval(bullet.getX(), bullet.getY(), 10, 10);
            }

            for (AmmoDrop ammoDrop : ammoDrops) {
                ammoDrop.draw(g);
            }

            for (ScrapDrop scrapDrop : scrapDrops) {
                scrapDrop.draw(g);
            }

            for (Zombie zombie : zombies) {
                zombie.draw(g);
            }

            g.setColor(Color.WHITE);
            g.drawString("Ammo: " + ammo, 10, 40);
            g.drawString("Map: " + currentMap, 10, 60);
            Font thaiFont = new Font("Tahoma", Font.PLAIN, 14);
            g.setFont(thaiFont);
            g.drawString("กด ESC เพื่อกลับสู่เมนูหลัก", 10, getHeight() - 10);
            g.drawString("กด C เพื่อเข้าสู่ระบบคราฟ", 10, getHeight() - 30);
        }
    }
}
