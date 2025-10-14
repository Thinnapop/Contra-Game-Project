package se233.contra.model.entity;

public class JavaBoss extends Boss {
    private double moveSpeed;
    private int attackCooldown;

    public JavaBoss(double x, double y) {
        this.x = x;
        this.y = y;
        this.health = 150;
        this.maxHealth = 150;
        this.scoreValue = 3;
        this.moveSpeed = 2.0;
    }

    @Override
    public void attack() {
        // Java boss attack pattern (from mouth)
    }

    @Override
    public void move() {
        // Moves up and down
    }
}