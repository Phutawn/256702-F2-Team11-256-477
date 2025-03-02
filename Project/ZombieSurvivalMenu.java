package Project;

import javax.swing.*;
import java.awt.*;

public class ZombieSurvivalMenu {
    private static float brightness = 1.0f; 
    private static JFrame frame; 
    private Image backgroundImage; 

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Zombie Survival"); 
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
            frame.setSize(1024, 768); 
            frame.setLocationRelativeTo(null); 
            frame.setResizable(false); 

            ZombieSurvivalMenu menu = new ZombieSurvivalMenu(); 
            menu.loadBackgroundImage(); 
            menu.showMenu(); 

            frame.setVisible(true); 
        });
    }

    private void loadBackgroundImage() {
        try {
            backgroundImage = new ImageIcon(getClass().getResource("/Project/image/p1.jpg")).getImage();
            if (backgroundImage == null) {
                throw new NullPointerException("Background image not found.");
            }
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
        }
    }

    private void showMenu() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("ZOMBIE SURVIVAL");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 50)); 
        titleLabel.setForeground(Color.RED); 
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 
        panel.add(Box.createRigidArea(new Dimension(0, 120))); 
        panel.add(titleLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 40)));

        JButton startButton = createButton(" -3- Start Game");
        JButton optionsButton = createButton(" ;) Options");
        JButton exitButton = createButton(" ' Exit ' ");

        panel.add(startButton);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(optionsButton);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(exitButton);

        startButton.addActionListener(e -> startGame());
        optionsButton.addActionListener(e -> showOptions());
        exitButton.addActionListener(e -> System.exit(0));

        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }

    private static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT); 
        button.setFont(new Font("Arial", Font.BOLD, 18)); 
        button.setBackground(Color.DARK_GRAY); 
        button.setForeground(Color.WHITE); 
        button.setFocusPainted(false); 
        return button;
    }

    private static void showOptions() {
        JDialog optionsDialog = new JDialog(frame, "Options", true);
        optionsDialog.setSize(300, 200); 
        optionsDialog.setLayout(new FlowLayout()); 
        optionsDialog.setLocationRelativeTo(frame); 

        JLabel brightnessLabel = new JLabel("Brightness: " + (int) (brightness * 100) + "%");
        JSlider brightnessSlider = new JSlider(0, 100, (int) (brightness * 100));
        brightnessSlider.addChangeListener(e -> {
            brightness = brightnessSlider.getValue() / 100.0f; 
            brightnessLabel.setText("Brightness: " + brightnessSlider.getValue() + "%"); 
        });

        optionsDialog.add(brightnessLabel);
        optionsDialog.add(brightnessSlider);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> optionsDialog.dispose()); 
        optionsDialog.add(closeButton);

        optionsDialog.setVisible(true);
    }

    private static void startGame() {
        if (frame.getContentPane() instanceof GamePanel) return; 

        GamePanel gamePanel = new GamePanel(); 
        frame.setContentPane(gamePanel);
        frame.revalidate(); 
        frame.repaint(); 
        gamePanel.requestFocusInWindow(); 
    }
}
