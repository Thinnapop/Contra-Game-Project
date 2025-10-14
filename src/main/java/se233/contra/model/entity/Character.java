package se233.contra.model.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import se233.contra.util.AnimationManager;
import se233.contra.util.GameLogger;
import se233.contra.util.SpriteLoader;

import java.util.HashMap;
import java.util.Map;

public class Character extends Entity {
    private int lives;
    private boolean isJumping;
    private boolean isProne;
    private boolean isShooting;
    private double groundY;
    private static final double GRAVITY = 0.5;
    private static final double JUMP_STRENGTH = -12;
    private static final double MOVE_SPEED = 4;

    // Animation system
    private Map<String, AnimationManager> animations;
    private String currentAnimation;
    private boolean facingRight;

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
        this.facingRight = true;

        loadAnimations();
        currentAnimation = "idle";
    }

    private void loadAnimations() {
        animations = new HashMap<>();

        try {
            String sheetPath = "/se233.sprites/character/character.png";
            int frameWidth = 163;   // Adjust based on your sprite sheet
            int frameHeight = 164;  // Adjust based on your sprite sheet

            // Analyzing your sprite sheet:
            // Row 0: Idle/Standing poses
            // Row 1-2: Running animation
            // Row 3: Jumping
            // Row 4: Prone/Crouching
            // Row 5-6: Shooting animations

            // Idle animation (first few frames of row 0)
            animations.put("idle", new AnimationManager(
                    SpriteLoader.extractFramesFromRow(sheetPath, 0, 0, 4, frameWidth, frameHeight),
                    10
            ));

            // Running animation (rows 1-2)
            animations.put("run", new AnimationManager(
                    SpriteLoader.extractFramesFromRegion(sheetPath, 1, 1, 5, 1, frameWidth, frameHeight),
                    5
            ));

            // Jumping animation (row 3)
            animations.put("jump", new AnimationManager(
                    SpriteLoader.extractFramesFromRow(sheetPath, 3, 0, 10, frameWidth, frameHeight),
                    8
            ));

            // Prone/Crouching (row 4)
            animations.put("prone", new AnimationManager(
                    SpriteLoader.extractFramesFromRow(sheetPath, 4, 0, 8, frameWidth, frameHeight),
                    10
            ));

            // Shooting while standing (row 5)
            animations.put("shoot_stand", new AnimationManager(
                    SpriteLoader.extractFramesFromRow(sheetPath, 5, 0, 8, frameWidth, frameHeight),
                    6
            ));

            // Shooting while running (row 6)
            animations.put("shoot_run", new AnimationManager(
                    SpriteLoader.extractFramesFromRow(sheetPath, 6, 0, 10, frameWidth, frameHeight),
                    5
            ));

            // Shooting while prone (row 7 if available)
            animations.put("shoot_prone", new AnimationManager(
                    SpriteLoader.extractFramesFromRow(sheetPath, 7, 0, 6, frameWidth, frameHeight),
                    8
            ));

            GameLogger.info("Lance animations loaded successfully");
            GameLogger.info(SpriteLoader.getCacheStats());

        } catch (Exception e) {
            GameLogger.error("Failed to load Lance animations", e);
        }
    }

    public void moveLeft() {
        velocityX = -MOVE_SPEED;
        facingRight = false;
        updateAnimation();
    }

    public void moveRight() {
        velocityX = MOVE_SPEED;
        facingRight = true;
        updateAnimation();
    }
    public void running(){
        if()
    }

    public void jump() {
        if (!isJumping && !isProne) {
            velocityY = JUMP_STRENGTH;
            isJumping = true;
            updateAnimation();
        }
    }

    public void prone() {
        if (!isJumping) {
            isProne = true;
            height = 25; // Crouch height
            updateAnimation();
        }
    }

    public void standUp() {
        isProne = false;
        height = 50;
        updateAnimation();
    }

    public Bullet shoot() {
        if (!isShooting) {
            isShooting = true;
            updateAnimation();
            double bulletX = x + width;
            double bulletY = isProne ? y + height / 2 : y + height / 3;
            return new Bullet(bulletX, bulletY, facingRight ? 1 : -1, 0);
        }
        return null;
    }

    public void stopShooting() {
        isShooting = false;
        updateAnimation();
    }

    public void stopMoving() {
        velocityX = 0;
        updateAnimation();
    }

    private void updateAnimation() {
        String newAnimation;

        if (isProne) {
            newAnimation = isShooting ? "shoot_prone" : "prone";
        } else if (isJumping) {
            newAnimation = "jump";
        } else if (Math.abs(velocityX) > 0) {
            newAnimation = isShooting ? "shoot_run" : "run";
        } else {
            newAnimation = isShooting ? "shoot_stand" : "idle";
        }

        if (!newAnimation.equals(currentAnimation)) {
            currentAnimation = newAnimation;
        }
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
                updateAnimation();
            }
        }

        // Update current animation
        if (animations.containsKey(currentAnimation)) {
            animations.get(currentAnimation).update();
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (active && animations.containsKey(currentAnimation)) {
            Image currentFrame = animations.get(currentAnimation).getCurrentFrame();

            if (currentFrame != null) {
                gc.save();

                // Flip sprite if facing left
                if (!facingRight) {
                    gc.scale(-1, 1);
                    gc.drawImage(currentFrame, -x - width, y, width, height);
                } else {
                    gc.drawImage(currentFrame, x, y, width, height);
                }

                gc.restore();
            }
        }
    }

    public void respawn() {
        x = 0;
        y = groundY;
        velocityX = 0;
        velocityY = 0;
        isJumping = false;
        isProne = false;
        isShooting = false;
        currentAnimation = "idle";
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