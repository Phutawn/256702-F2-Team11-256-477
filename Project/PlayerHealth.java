package Project;

public class PlayerHealth {
    private int health;
    private final int maxHealth;
    private int currentVisibleHealth;

    public PlayerHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        this.health = maxHealth; 
        this.currentVisibleHealth = maxHealth; 
    }

    public int getHealth() {
        return health;
    }

    public int getVisibleHealth() {
        return currentVisibleHealth;
    }

    public void damage(int amount) {
        health -= amount;
        if (health < 0) {
            health = 0;
        }
        
        currentVisibleHealth = health; 
        
        updateHealthBar(currentVisibleHealth);
    }

    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) {
            health = maxHealth;
        }
        
        currentVisibleHealth = health;
        updateHealthBar(currentVisibleHealth);
    }

    public boolean isAlive() {
        return health > 0;
    }

    private void updateHealthBar(int visibleHealth) {
    }
}
