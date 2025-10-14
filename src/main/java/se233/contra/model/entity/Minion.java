package se233.contra.model.entity;

public class Minion extends Entity {
    private int health;
    private int scoreValue;
    private double moveSpeed;

    public Minion(double x, double y, int type) {
        this.x = x;
        this.y = y;
        this.health = type; // type 1 = 1 health, type 2 = 2 health
        this.scoreValue = type;
        this.moveSpeed = 1.5;
    }

    @Override
    public void update() {
        // Move toward player or in patterns
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            active = false;
        }
    }
}