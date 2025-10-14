package se233.contra.model.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import se233.contra.util.GameLogger;

public class Character extends Entity {
    private int lives;
    private boolean isJumping;
    private boolean isProne;
    private boolean isShooting;
    private double groundY;
    private static final double GRAVITY = 0.4;
    private static final double JUMP_STRENGTH = -12;
    private static final double MOVE_SPEED = 3.5;
    private int shootCooldown;
    private static final int FIRE_RATE = 15;

    // Sprite
    private Image sprite;
    private boolean facingRight;

    public Character(double x, double y) {
        this.x = x;
        this.y = y;
        this.groundY = y;
        this.width = 40;
        this.height = 60;
        this.lives = 3;
        this.velocityX = 0;
        this.velocityY = 0;
        this.active = true;
        this.isJumping = false;
        this.isProne = false;
        this.isShooting = false;
        this.facingRight = true;
        this.shootCooldown = 0;

        loadSprite();
        GameLogger.info("Character created at position: (" + x + ", " + y + ")");
    }

    private void loadSprite() {
        try {
            // Try to load the Lance sprite
            sprite = new Image(
                    getClass().getResourceAsStream("/se233.sprites/character/character.png")
            );
            GameLogger.info("Character sprite loaded successfully");
        } catch (Exception e) {
            GameLogger.error("Failed to load character sprite", e);
            sprite = null;
        }
    }

    public void moveLeft() {
        velocityX = -MOVE_SPEED;
        facingRight = false;
        GameLogger.debug("Character moving left");
    }

    public void moveRight() {
        velocityX = MOVE_SPEED;
        facingRight = true;
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
        if (!isJumping && !isProne) {
            double oldHeight = height;
            isProne = true;
            height = 25;
            y += (oldHeight - height);  // Move DOWN to keep feet on ground
            GameLogger.debug("Character prone");
        }
    }

    public void standUp() {
        if (isProne) {
            double oldHeight = height;
            isProne = false;
            height = 60;  // Standing height
            y -= (height - oldHeight);  // Move UP when standing
        }
    }

    public Bullet shoot() {

        if (isJumping) {
            return null;
        }
        if (shootCooldown <= 0) {
            shootCooldown = FIRE_RATE;
            isShooting = true;
            double bulletX = x + width;
            double bulletY = isProne ? y + height / 3.1 : y + height / 3;
            return new Bullet(bulletX, bulletY, facingRight ? 1 : -1, 0);
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
        if (shootCooldown > 0) {
            shootCooldown--;
        }
        x += velocityX;

        if (x < 0) x = 0;
        if (x > 800 - width) x = 800 - width;

        if (isJumping) {
            velocityY += GRAVITY;
            y += velocityY;

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
            if (sprite != null) {
                // Draw the sprite
                gc.save();

                if (!facingRight) {
                    // Flip sprite when facing left
                    gc.scale(-1, 1);
                    gc.drawImage(sprite, -x - width, y, width, height);
                } else {
                    gc.drawImage(sprite, x, y, width, height);
                }

                gc.restore();
            } else {
                // Fallback to rectangle if sprite didn't load
                gc.setFill(Color.BLUE);
                gc.fillRect(x, y, width, height);
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(2);
                gc.strokeRect(x, y, width, height);
            }

            // Draw gun indicator
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
        facingRight = true;
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