package Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel {
            private int playerX = 100;
            private int playerY = 100;
            private final int playerSpeed = 5;

    public GamePanel() {
        setBackground(Color.GRAY);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        playerY -= playerSpeed;
                        break;
                    case KeyEvent.VK_S:
                        playerY += playerSpeed;
                        break;
                    case KeyEvent.VK_A:
                        playerX -= playerSpeed;
                        break;
                    case KeyEvent.VK_D:
                        playerX += playerSpeed;
                        break;
                }
                repaint();  
            }
        });
    }

    public void startGame() {
        
    }

    @Override
       protected void paintComponent(Graphics g) {
          super.paintComponent(g);

           g.setColor(Color.BLUE);  
           g.fillRect(playerX, playerY, 50, 50);  
    }
}
