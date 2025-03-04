package Project;

public class PlayerInventory {
    private static int scrap = 0;
    private static int firstAidKits = 0;
    
    public static int getScrap() {
        return scrap;
    }
    
    public static void addScrap(int amount) {
        scrap += amount;
    }
    
    public static boolean useScrap(int amount) {
        if (scrap >= amount) {
            scrap -= amount;
            return true;
        }
        return false;
    }
    
    public static int getFirstAidKits() {
        return firstAidKits;
    }
    
    public static void addFirstAidKit() {
        firstAidKits++;
    }
    
    public static boolean craftFirstAidKit() {
        if (useScrap(3)) {
            addFirstAidKit();
            return true;
        }
        return false;
    }
    
    public static boolean useFirstAidKit() {
        if (firstAidKits > 0) {
            firstAidKits--;
            return true;
        }
        return false;
    }
}
