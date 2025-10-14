package se233.contra.model.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import se233.contra.util.AnimationManager;
import se233.contra.util.GameLogger;
import se233.contra.util.SpriteLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private boolean facingRight;

    // Animation system
    private Map<String, AnimationManager> animations;
    private String currentAnimation;

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

        loadAnimations();  // Changed from loadSprite()
        GameLogger.info("Character created at position: (" + x + ", " + y + ")");
    }

    private void loadAnimations() {
        animations = new HashMap<>();

        try {
            String sheetPath = "/se233.sprites/character/character.png";
            int frameWidth = 163;
            int frameHeight = 164;

            GameLogger.info("Loading character sprite sheet from: " + sheetPath);

            // Idle animation (first frame from row 0)
            List<Image> idleFrames = new ArrayList<>();
            Image idleFrame = SpriteLoader.extractFrame(sheetPath, 0, 0, frameWidth, frameHeight);
            if (idleFrame != null) {
                idleFrames.add(idleFrame);
                animations.put("idle", new AnimationManager(idleFrames, 10));
                GameLogger.info("Idle animation loaded: 1 frame");
            } else {
                GameLogger.error("Failed to load idle frame", null);
            }

            // Running animation: row 1, columns 1-6 (6 frames)
            List<Image> runFrames = SpriteLoader.extractFramesFromRow(sheetPath, 1, 1, 6, frameWidth, frameHeight);
            if (runFrames != null && !runFrames.isEmpty()) {
                animations.put("run", new AnimationManager(runFrames, 5));
                GameLogger.info("Run animation loaded: " + runFrames.size() + " frames");
            } else {
                GameLogger.error("Failed to load run frames", null);
            }

            // Jump animation: row 2, columns 13-16 (4 frames)
            List<Image> jumpFrames = SpriteLoader.extractFramesFromRow(sheetPath, 2, 13, 4, frameWidth, frameHeight);
            if (jumpFrames != null && !jumpFrames.isEmpty()) {
                animations.put("jump", new AnimationManager(jumpFrames, 5));
                GameLogger.info("Jump animation loaded: " + jumpFrames.size() + " frames");
            } else {
                GameLogger.error("Failed to load jump frames", null);
            }

            // Prone animation: row 1, columns 15-16 (2 frames)
            List<Image> proneFrames = SpriteLoader.extractFramesFromRow(sheetPath, 1, 15, 2, frameWidth, frameHeight);
            if (proneFrames != null && !proneFrames.isEmpty()) {
                animations.put("prone", new AnimationManager(proneFrames, 8));
                GameLogger.info("Prone animation loaded: " + proneFrames.size() + " frames");
            } else {
                GameLogger.error("Failed to load prone frames", null);
            }

            currentAnimation = "idle";

            GameLogger.info("Character animations loaded. Total animations: " + animations.size());

        } catch (Exception e) {
            GameLogger.error("Failed to load character animations", e);
            e.printStackTrace();  // Print full stack trace
        }
    }

    private void updateAnimation() {
        String newAnimation;

        if (isProne) {
            newAnimation = "prone";
        } else if (isJumping) {
            newAnimation = "jump";
        } else if (Math.abs(velocityX) > 0) {
            newAnimation = "run";
        } else {
            newAnimation = "idle";
        }

        if (!newAnimation.equals(currentAnimation)) {
            currentAnimation = newAnimation;
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

    public void jump() {
        if (!isJumping && !isProne) {
            velocityY = JUMP_STRENGTH;
            isJumping = true;
            updateAnimation();
        }
    }

    public void prone() {
        if (!isJumping && !isProne) {
            double oldHeight = height;
            isProne = true;
            height = 25;
            y += (oldHeight - height);
            updateAnimation();
        }
    }

    public void standUp() {
        if (isProne) {
            double oldHeight = height;
            isProne = false;
            height = 60;
            y -= (height - oldHeight);
            updateAnimation();
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
        updateAnimation();
    }

    @Override
    public void update() {
        // Update cooldown
        if (shootCooldown > 0) {
            shootCooldown--;
        }

        // Apply horizontal movement
        x += velocityX;

        // Keep within bounds
        if (x < 0) x = 0;
        if (x > 800 - width) x = 800 - width;

        // Apply gravity and jumping
        if (isJumping) {
            velocityY += GRAVITY;
            y += velocityY;

            if (y >= groundY) {
                y = groundY;
                velocityY = 0;
                isJumping = false;
                updateAnimation();
            }
        }

        // Update current animation frames
        if (animations != null && animations.containsKey(currentAnimation)) {
            animations.get(currentAnimation).update();
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (active && animations != null && animations.containsKey(currentAnimation)) {
            Image currentFrame = animations.get(currentAnimation).getCurrentFrame();

            if (currentFrame != null) {
                gc.save();

                if (!facingRight) {
                    // Flip sprite when facing left
                    gc.scale(-1, 1);
                    gc.drawImage(currentFrame, -x - width, y, width, height);
                } else {
                    gc.drawImage(currentFrame, x, y, width, height);
                }

                gc.restore();
            }
        } else {
            // Fallback to rectangle if animations didn't load
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

    public void respawn() {
        x = 100;
        y = groundY;
        velocityX = 0;
        velocityY = 0;
        isJumping = false;
        isProne = false;
        facingRight = true;
        currentAnimation = "idle";
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