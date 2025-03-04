package Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CraftingMenuTH extends JFrame {
    public CraftingMenuTH() {
        setTitle("ระบบคราฟพื้นฐาน (ประถมพยาบาล)");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setUndecorated(true); 
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.DARK_GRAY);
        
        Font thaiBoldFont = new Font("Angsana New", Font.BOLD, 20);
        Font thaiPlainFont = new Font("Angsana New", Font.PLAIN, 18);
        
        JLabel titleLabel = new JLabel("ระบบคราฟแบบประถมพยาบาล");
        titleLabel.setFont(thaiBoldFont);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JLabel scrapLabel = new JLabel("เศษโลหะ: " + PlayerInventory.getScrap());
        scrapLabel.setFont(thaiPlainFont);
        scrapLabel.setForeground(Color.WHITE);
        scrapLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(scrapLabel);
        
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JButton refreshButton = new JButton("รีเฟรชข้อมูลเศษโลหะ");
        refreshButton.setFont(thaiPlainFont);
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshButton.addActionListener(e -> {
            scrapLabel.setText("เศษโลหะ: " + PlayerInventory.getScrap());
        });
        panel.add(refreshButton);
        
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JLabel recipeLabel = new JLabel("สร้างชุดปฐมพยาบาล (ต้องการเศษโลหะ 3 ชิ้น)");
        recipeLabel.setFont(thaiPlainFont);
        recipeLabel.setForeground(Color.WHITE);
        recipeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(recipeLabel);
        
        JLabel kitCountLabel = new JLabel("จำนวนชุดปฐมพยาบาลที่มีอยู่: " + PlayerInventory.getFirstAidKits());
        kitCountLabel.setFont(thaiPlainFont);
        kitCountLabel.setForeground(Color.WHITE);
        kitCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(kitCountLabel);
        
        JLabel usageLabel = new JLabel("วิธีใช้: ในเกมกด F เพื่อใช้ชุดปฐมพยาบาล");
        usageLabel.setFont(thaiPlainFont);
        usageLabel.setForeground(Color.WHITE);
        usageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(usageLabel);
        
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JButton craftButton = new JButton("สร้างชุดปฐมพยาบาล");
        craftButton.setFont(thaiPlainFont);
        craftButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        craftButton.addActionListener(e -> {
            if (PlayerInventory.craftFirstAidKit()) {
                JOptionPane.showMessageDialog(this, 
                    "สร้างชุดปฐมพยาบาลสำเร็จ!\nคุณมีชุดปฐมพยาบาล: " + PlayerInventory.getFirstAidKits(), 
                    "แจ้งเตือน", JOptionPane.INFORMATION_MESSAGE);
            } else {
                UIManager.put("OptionPane.messageFont", thaiPlainFont);
                JOptionPane.showMessageDialog(this, 
                    "คำเตือน: เศษโลหะไม่เพียงพอ!", 
                    "คำเตือน", JOptionPane.WARNING_MESSAGE);
            }
            kitCountLabel.setText("จำนวนชุดปฐมพยาบาลที่มีอยู่: " + PlayerInventory.getFirstAidKits());
            scrapLabel.setText("เศษโลหะ: " + PlayerInventory.getScrap());
        });
        panel.add(craftButton);
        
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JButton exitButton = new JButton("ออกจากการคราฟ");
        exitButton.setFont(thaiPlainFont);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener(e -> {
            dispose();
        });
        panel.add(exitButton);
        
        add(panel);
    }
}
