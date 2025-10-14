package se233.contra.model.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import se233.contra.util.GameLogger;

public class Character extends Entity {
    private int lives;
    private boolean isJumping;
    private boolean isProne;
    private boolean isShooting;
    private double groundY;
    private static final double GRAVITY = 0.5;
    private static final double JUMP_STRENGTH = -12;
    private static final double MOVE_SPEED = 5;

    public Character(double x, double y) {
        this.x = x;
        this.y = y;
        this.groundY = y;
        this.width = 40;
        this.height = 50;
        this.lives = 3;
        this.velocityX = 0;
        this.velocityY = 0;
        this.active = true;
        this.isJumping = false;
        this.isProne = false;
        this.isShooting = false;
    }

    public void moveLeft() {
        velocityX = -MOVE_SPEED;
        GameLogger.debug("Character moving left");
    }

    public void moveRight() {
        velocityX = MOVE_SPEED;
        GameLogger.debug("Character moving right");
    }

    public void jump() {
        if (!isJumping && !isProne) {
            velocityY = JUMP_STRENGTH;
            isJumping = true;
            GameLogger.debug("Character jumped");
        }
    }

    public void prone() {
        if (!isJumping) {
            isProne = true;
            height = 25; // Crouch height
            GameLogger.debug("Character prone");
        }
    }

    public void standUp() {
        isProne = false;
        height = 50;
    }

    public Bullet shoot() {
        if (!isShooting) {
            isShooting = true;
            double bulletX = x + width;
            double bulletY = isProne ? y + height / 2 : y + height / 3;
            GameLogger.debug("Character shooting");
            return new Bullet(bulletX, bulletY, 1, 0); // Shoot right
        }
        return null;
    }

    public void stopShooting() {
        isShooting = false;
    }

    public void stopMoving() {
        velocityX = 0;
    }

    @Override
    public void update() {
        // Apply horizontal movement
        x += velocityX;

        // Keep within bounds
        if (x < 0) x = 0;
        if (x > 800 - width) x = 800 - width;

        // Apply gravity
        if (isJumping) {
            velocityY += GRAVITY;
            y += velocityY;

            // Land on ground
            if (y >= groundY) {
                y = groundY;
                velocityY = 0;
                isJumping = false;
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (active) {
            // Draw character as a blue rectangle (replace with sprite later)
            gc.setFill(Color.BLUE);
            gc.fillRect(x, y, width, height);

            // Draw outline
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeRect(x, y, width, height);

            // Draw "gun" direction indicator
            gc.setFill(Color.YELLOW);
            gc.fillRect(x + width, y + height / 3, 10, 3);
        }
    }

    public void respawn() {
        x = 100;
        y = groundY;
        velocityX = 0;
        velocityY = 0;
        isJumping = false;
        isProne = false;
        GameLogger.info("Character respawned");
    }

    public void loseLife() {
        lives--;
        GameLogger.warn("Character lost a life. Remaining: " + lives);
    }

    public int getLives() {
        return lives;
    }

    public boolean isProne() {
        return isProne;
    }
}