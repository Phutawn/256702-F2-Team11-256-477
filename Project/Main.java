package Project;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Zombie Survival Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null); 
            frame.setResizable(false);
            
            ZombieSurvivalMenuTH.showMenu(frame);
            frame.setVisible(true);
        });
    }
}
