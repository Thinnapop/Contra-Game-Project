package se233.contra.model.entity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import se233.contra.util.AnimationManager;
import se233.contra.util.GameLogger;
import se233.contra.util.SpriteLoader;
import se233.contra.model.Platform;
import java.util.List;
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
    private static final double GRAVITY = 0.265;
    private static final double JUMP_STRENGTH = -10;
    private static final double MOVE_SPEED = 2;
    private int shootCooldown;
    private static final int FIRE_RATE = 30;
    private boolean facingRight;
    private double hitboxWidth;
    private double hitboxHeight;
    private double hitboxOffsetX;  // Offset from sprite position
    private double hitboxOffsetY;

    // Animation system
    private Map<String, AnimationManager> animations;
    private String currentAnimation;

    public Character(double x, double y) {
        this.x = x;
        this.y = 400;
        this.groundY = 540;
        this.width = 125;
        this.height = 145;
        this.lives = 3;
        this.velocityX = 0;
        this.velocityY = 0;
        this.active = true;
        this.isJumping = false;
        this.isProne = false;
        this.isShooting = false;
        this.facingRight = true;
        this.shootCooldown = 0;
        this.hitboxWidth = 40;    // Smaller collision width
        this.hitboxHeight = 60;   // Smaller collision height
        this.hitboxOffsetX = 42;  // Center the hitbox
        this.hitboxOffsetY = 40;

        loadAnimations();  // Changed from loadSprite()
        GameLogger.info("Character created at position: (" + x + ", " + y + ")");
    }

    private void loadAnimations() {
        animations = new HashMap<>();

        try {
            String sheetPath = "/se233/sprites/character/character.png";
            int frameWidth = 165;
            int frameHeight = 170;

            GameLogger.info("Loading character sprite sheet from: " + sheetPath);

            // Idle animation (first frame from row 0)
            List<Image> idleFrames = new ArrayList<>();
            Image idleFrame = SpriteLoader.extractFrame(sheetPath, 7, 0, frameWidth, frameHeight);
            if (idleFrame != null) {
                idleFrames.add(idleFrame);
                animations.put("idle", new AnimationManager(idleFrames, 10));
                GameLogger.info("Idle animation loaded: 1 frame");
            } else {
                GameLogger.error("Failed to load idle frame", null);
            }

            // Running animation: row 0, columns 1-6 (6 frames)
            List<Image> runFrames = SpriteLoader.extractFramesFromRow(sheetPath, 0, 7, 5, frameWidth, frameHeight);
            if (runFrames != null && !runFrames.isEmpty()) {
                animations.put("run", new AnimationManager(runFrames, 12));
                GameLogger.info("Run animation loaded: " + runFrames.size() + " frames");
            } else {
                GameLogger.error("Failed to load run frames", null);
            }


            // Jump animation: try row 2, columns 13-16 (4 frames)
            List<Image> jumpFrames = SpriteLoader.extractFramesFromRow(sheetPath, 1, 12, 4, frameWidth, frameHeight);
            if (jumpFrames != null) {
                animations.put("jump", new AnimationManager(jumpFrames, 10));
                GameLogger.info("Jump animation loaded (using idle frame): 1 frame");
            } else {
                GameLogger.error("Failed to load jump frame", null);
            }


            // Prone animation: row 0, columns 15-16 (2 frames)
            List<Image> proneFrames = SpriteLoader.extractFramesFromRow(sheetPath, 0, 14, 2, frameWidth, frameHeight);
            if (proneFrames != null && !proneFrames.isEmpty()) {
                animations.put("prone", new AnimationManager(proneFrames, 8));
                stopMoving();
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
            // Reset the animation when switching
            if (animations.containsKey(currentAnimation)) {
                animations.get(currentAnimation).reset();
            }
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
            height = 145;
            y += (oldHeight - height);
            updateAnimation();
        }
    }

    public void standUp() {
        if (isProne) {
            double oldHeight = height;
            isProne = false;
            height = 140;
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
            double bulletX, bulletY;

            if (facingRight) {

                bulletX = x + 75;  // Gun barrel position (adjust this)
                bulletY = isProne ? y + 80 : y + 55;  // Gun height
            } else {
                // Facing left: gun is on left side of sprite
                bulletX = x + 50;  // Gun barrel position when flipped (adjust this)
                bulletY = isProne ? y + 80 : y + 55;  // Gun height
            }

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
    public void checkPlatformCollision(List<Platform> platforms) {
        boolean onPlatform = false;

        // Get character's hitbox bottom
        double hitboxBottom = getHitboxY() + hitboxHeight;
        double hitboxLeft = getHitboxX();
        double hitboxRight = getHitboxX() + hitboxWidth;

        for (Platform platform : platforms) {
            // Check if character is above the platform and falling/standing on it
            if (hitboxRight > platform.x &&
                    hitboxLeft < platform.x + platform.width &&
                    hitboxBottom >= platform.y &&
                    hitboxBottom <= platform.y + 15 && // Small threshold for landing
                    velocityY >= 0) { // Only when falling or standing

                // Land on platform
                y = platform.y - hitboxHeight - hitboxOffsetY;
                velocityY = 0;
                isJumping = false;
                onPlatform = true;
                updateAnimation();
                break;
            }
        }

        // If not on any platform and not already jumping, start falling
        if (!onPlatform && !isJumping) {
            isJumping = true;
        }
    }
    @Override
    public void update() {
        if (shootCooldown > 0) {
            shootCooldown--;
        }

        // Apply horizontal movement
        x += velocityX;

        double hitboxRight = getHitboxX() + hitboxWidth;

        if (getHitboxX() < 0) {
            x = -hitboxOffsetX;  // Stop at left edge
        }
        if (hitboxRight > 800) {
            x = 800 - hitboxWidth - hitboxOffsetX;  // Stop at right edge
        }

        // Apply gravity (always apply when in air)
        if (isJumping) {
            velocityY += GRAVITY;
            y += velocityY;
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
                    gc.scale(-1, 1);
                    gc.drawImage(currentFrame, -x - width, y, width, height);
                } else {
                    gc.drawImage(currentFrame, x, y, width, height);
                }
                gc.restore();

                // DEBUG: Draw the smaller hitbox (REMOVE THIS LATER)
                gc.setStroke(Color.RED);
                gc.setLineWidth(2);
                gc.strokeRect(getHitboxX(), getHitboxY(), hitboxWidth, hitboxHeight);
            }
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
    public double getHitboxX() {
        return x + hitboxOffsetX;
    }

    public double getHitboxY() {
        return y + hitboxOffsetY;
    }

    public double getHitboxWidth() {
        return hitboxWidth;
    }

    public double getHitboxHeight() {
        return hitboxHeight;
    }

}