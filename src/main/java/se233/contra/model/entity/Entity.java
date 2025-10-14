package se233.contra.model.entity;

import javafx.scene.canvas.GraphicsContext;

public abstract class Entity {
    protected double x, y;
    protected double width, height;
    protected double velocityX, velocityY;
    protected boolean active;

    // Abstract methods that all entities must implement
    public abstract void update();
    public abstract void render(GraphicsContext gc);

    // Collision detection
    public boolean intersects(Entity other) {
        return x < other.x + other.width &&
                x + width > other.x &&
                y < other.y + other.height &&
                y + height > other.y;
    }

    // Getters and Setters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public boolean isActive() { return active; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setActive(boolean active) { this.active = active; }
}