package se233.contra.model.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bullet extends Entity {
    private double speed;
    private int damage;

    public Bullet(double x, double y, double directionX, double directionY) {
        this.x = x;
        this.y = y;
        this.width = 8;  // Bullet size
        this.height = 8;
        this.speed = 10;
        this.damage = 1;
        this.active = true;
        this.velocityX = directionX * speed;
        this.velocityY = directionY * speed;
    }

    @Override
    public void update() {
        x += velocityX;
        y += velocityY;

        // Deactivate if out of bounds
        if (x < 0 || x > 800 || y < 0 || y > 600) {
            active = false;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (active) {
            // Draw bullet as a yellow circle
            gc.setFill(Color.YELLOW);
            gc.fillOval(x, y, width, height);

            // Optional: Add a white outline for better visibility
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1);
            gc.strokeOval(x, y, width, height);
        }
    }

    // Getter for damage (needed for collision detection)
    public int getDamage() {
        return damage;
    }

    // Check if bullet is still active
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}