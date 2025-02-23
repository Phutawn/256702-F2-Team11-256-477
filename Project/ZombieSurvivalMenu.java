package Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ZombieSurvivalMenu {
    private static float brightness = 1.0f; 

    public static void main(String[] args) {
        JFrame frame = new JFrame("Zombie Survival");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("ZOMBIE SURVIVAL");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.RED);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        JButton startButton = createButton("Start Game");
        JButton optionsButton = createButton("Options");
        JButton exitButton = createButton("Exit");
        
        panel.add(startButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(optionsButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(exitButton);
        
        startButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Starting the Game!"));
        optionsButton.addActionListener(e -> showOptions(frame));
        exitButton.addActionListener(e -> System.exit(0));
        
        frame.add(panel);
        frame.setVisible(true);
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
    
    private static void showOptions(JFrame parentFrame) {
        JDialog optionsDialog = new JDialog(parentFrame, "Options", true);
        optionsDialog.setSize(300, 200);
        optionsDialog.setLayout(new FlowLayout());
        optionsDialog.setLocationRelativeTo(parentFrame);
        
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
}
