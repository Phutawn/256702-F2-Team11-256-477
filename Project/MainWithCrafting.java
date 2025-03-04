package Project;

import javax.swing.SwingUtilities;

public class MainWithCrafting {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ZombieSurvivalMenuWithCraftingTH.showMenu();
        });
    }
}
