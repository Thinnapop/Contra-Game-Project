package se233.contra.model.entity;

public class CustomBoss extends Boss {
    // Your custom design!
    // Could be a flying boss, multi-part boss, etc.

    public CustomBoss(double x, double y) {
        this.x = x;
        this.y = y;
        this.health = 200;
        this.maxHealth = 200;
        this.scoreValue = 5;
    }
}