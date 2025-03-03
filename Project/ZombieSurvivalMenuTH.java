package Project;

import javax.swing.*;
import java.awt.*;

public class ZombieSurvivalMenuTH {
    private static JFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Zombie Survival");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            showMenu(frame);
            frame.setVisible(true);
        });
    }

    public static void showMenu(JFrame frame) {
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
        JButton optionsButton = createButton(" ;) ตัวเลือก");
        JButton exitButton = createButton(" ' ออกจากเกม ' ");

        panel.add(startButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(optionsButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(exitButton);

        startButton.addActionListener(e -> startGame(frame));
        optionsButton.addActionListener(e -> showOptions());
        exitButton.addActionListener(e -> System.exit(0));

        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
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

    private static void showOptions() {
        JDialog optionsDialog = new JDialog(frame, "ตัวเลือก", true);
        optionsDialog.setSize(300, 200);
        optionsDialog.setLayout(new FlowLayout());
        optionsDialog.setLocationRelativeTo(frame);

        JLabel brightnessLabel = new JLabel("ความสว่าง: 100%");
        brightnessLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));

        JSlider brightnessSlider = new JSlider(0, 100, 100);
        brightnessSlider.addChangeListener(e -> brightnessLabel.setText("ความสว่าง: " + brightnessSlider.getValue() + "%"));

        optionsDialog.add(brightnessLabel);
        optionsDialog.add(brightnessSlider);

        JButton closeButton = new JButton("ปิด");
        closeButton.setFont(new Font("Tahoma", Font.PLAIN, 14)); 
        closeButton.addActionListener(e -> optionsDialog.dispose());
        optionsDialog.add(closeButton);

        optionsDialog.setVisible(true);
    }

    private static void startGame(JFrame frame) {
        GamePanel gamePanel = new GamePanel(frame);
        frame.setContentPane(gamePanel);
        frame.revalidate();
        frame.repaint();
        gamePanel.requestFocusInWindow();
    }
}
