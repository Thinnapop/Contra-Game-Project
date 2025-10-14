package se233.contra.model.entity;

public class DefenseWallBoss extends Boss {
    private int attackPattern; // 0, 1, 2 for different patterns

    public DefenseWallBoss(double x, double y) {
        this.x = x;
        this.y = y;
        this.health = 100;
        this.maxHealth = 100;
        this.scoreValue = 2;
    }

    @Override
    public void attack() {
        // Shoot bullets in patterns
    }

    @Override
    public void move() {
        // Boss 1 doesn't move much, just slightly
    }

    @Override
    public void takeDamage(int damage) {
        health -= damage;
        GameLogger.info("Boss 1 took damage: " + damage);
    }
}