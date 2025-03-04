package Project;

import javax.swing.*;
import java.awt.*;

public class ZombieSurvivalMenuWithCraftingTH {
    private static JFrame frame;
    
    public static void showMenu() {
        frame = new JFrame("Zombie Survival");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);
        
        JLabel titleLabel = new JLabel("ZOMBIE SURVIVAL");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 32));
        titleLabel.setForeground(Color.RED);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        JButton startButton = createButton(" -3- เริ่มเกม");
        JButton craftingButton = createButton(" * Crafting *");
        JButton optionsButton = createButton(" ;) ตัวเลือก");
        JButton exitButton = createButton(" ' ออกจากเกม ' ");
        
        panel.add(startButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(craftingButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(optionsButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(exitButton);
        
        startButton.addActionListener(e -> startGame());
        craftingButton.addActionListener(e -> openCrafting());
        optionsButton.addActionListener(e -> ZombieSurvivalMenuTH.showMenu(frame));
        exitButton.addActionListener(e -> System.exit(0));
        
        frame.setContentPane(panel);
        frame.setVisible(true);
    }
    
    private static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Tahoma", Font.BOLD, 18));
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }
    
    private static void startGame() {
        GamePanel gamePanel = new GamePanel(frame);
        frame.setContentPane(gamePanel);
        frame.revalidate();
        frame.repaint();
        gamePanel.requestFocusInWindow();
    }
    
    private static void openCrafting() {
        new CraftingMenuTH().setVisible(true);
    }
}
