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
    private final ArrayList<Bullet> bullets = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private boolean shooting = false;
    private boolean gameOver = false;
    private final Timer gameTimer;
    private final Random rand = new Random();

    public GamePanel() {
        setBackground(Color.DARK_GRAY);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameOver && e.getKeyCode() == KeyEvent.VK_R) {
                    resetGame();
                    return;
                }
                if (!gameOver) {
                    movePlayer(e.getKeyCode());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    shooting = false;
                }
            }
        });

        gameTimer = new Timer(16, e -> updateGame());
        gameTimer.start();
        spawnEnemy();
    }

    private void movePlayer(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> playerY -= playerSpeed;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> playerY += playerSpeed;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> playerX -= playerSpeed;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> playerX += playerSpeed;
            case KeyEvent.VK_SPACE -> {
                if (!shooting) {
                    shootBullet();
                    shooting = true;
                }
            }
        }
        playerDirection = keyCode;
        repaint();
    }

    private void resetGame() {
        playerX = 100;
        playerY = 100;
        ammo = 100;
        bullets.clear();
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

        bullets.removeIf(bullet -> {
            bullet.move();
            return bullet.isOutOfBounds(getWidth(), getHeight());
        });

        checkBulletEnemyCollision();
        updateEnemies();
        repaint();
    }

    private void checkBulletEnemyCollision() {
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            for (Iterator<Enemy> enemyIterator = enemies.iterator(); enemyIterator.hasNext(); ) {
                Enemy enemy = enemyIterator.next();
                if (bullet.getX() < enemy.x + 20 && bullet.getX() + 10 > enemy.x && bullet.getY() < enemy.y + 20 && bullet.getY() + 10 > enemy.y) {
                    enemyIterator.remove();
                    bulletIterator.remove();
                    spawnEnemy();
                    break;
                }
            }
        }
    }

    private void updateEnemies() {
        for (Enemy enemy : enemies) {
            enemy.update();
            if (checkCollisionWithPlayer(enemy)) {
                gameOver = true;
            }
        }
    }

    private boolean checkCollisionWithPlayer(Enemy enemy) {
        return playerX < enemy.x + 20 && playerX + 50 > enemy.x && playerY < enemy.y + 20 && playerY + 50 > enemy.y;
    }

    private void spawnEnemy() {
        int spawnX = rand.nextInt(getWidth() - 20);
        int spawnY = rand.nextInt(getHeight() - 20);
        enemies.add(new Enemy(spawnX, spawnY, playerX, playerY));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameOver) {
            drawGameOverScreen(g);
            return;
        }

        drawPlayer(g);
        drawBullets(g);
        drawEnemies(g);
        drawAmmoCount(g);
    }

    private void drawGameOverScreen(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("GAME OVER", getWidth() / 2 - 150, getHeight() / 2);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Press R to Restart", getWidth() / 2 - 90, getHeight() / 2 + 40);
    }

    private void drawPlayer(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(playerX, playerY, 50, 50);
    }

    private void drawBullets(Graphics g) {
        g.setColor(Color.YELLOW);
        for (Bullet bullet : bullets) {
            g.fillOval(bullet.getX(), bullet.getY(), 10, 10);
        }
    }

    private void drawEnemies(Graphics g) {
        g.setColor(Color.GREEN);
        for (Enemy enemy : enemies) {
            g.fillRect(enemy.x, enemy.y, 20, 20);
        }
    }

    private void drawAmmoCount(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString("Ammo: " + ammo, 10, 20);
    }
}
