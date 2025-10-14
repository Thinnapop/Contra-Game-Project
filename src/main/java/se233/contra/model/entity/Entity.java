package se233.contra.model.entity;

public abstract class Entity {
    protected double x, y;
    protected double width, height;
    protected double velocityX, velocityY;
    protected boolean active;

    // Getters, setters, abstract methods
    public abstract void update();
    public abstract void render();

    // Collision detection
    public boolean intersects(Entity other) {
        return x < other.x + other.width &&
                x + width > other.x &&
                y < other.y + other.height &&
                y + height > other.y;
    }
}