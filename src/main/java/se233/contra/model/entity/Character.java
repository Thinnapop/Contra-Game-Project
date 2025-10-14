package se233.contra.model.entity;

public class Character extends Entity {
    private int lives;
    private boolean isJumping;
    private boolean isProne;
    private boolean isShooting;

    // Movement states
    public void moveLeft() { }
    public void moveRight() { }
    public void jump() { }
    public void prone() { }
    public void shoot() { }
    public void stopMoving() { }

    @Override
    public void update() {
        // Apply gravity, update position
        // Handle animation state
    }
}