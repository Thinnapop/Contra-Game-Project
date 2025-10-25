package se233.contra.model.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import se233.contra.util.AnimationManager;
import se233.contra.util.GameLogger;
import se233.contra.util.SpriteLoader;

import java.util.List;

public class JavaBoss extends Boss {
    private double moveSpeed;
    private int attackCooldown;
    private int attackTimer;
    private double initialX;
    private double initialY;

    // Enhanced movement system
    private int movementPattern;  // 0 = figure-8, 1 = circle, 2 = wave
    private int patternTimer;
    private int patternDuration;
    private double time;

    // Animation
    private AnimationManager idleAnimation;

    public JavaBoss(double x, double y) {
        this.x = x;
        this.y = y;
        this.initialX = x;
        this.initialY = y;
        this.width = 120;
        this.height = 100;
        this.health = 150;
        this.maxHealth = 150;
        this.scoreValue = 3;
        this.moveSpeed = 0.8;
        this.active = true;
        this.attackCooldown = 45;
        this.attackTimer = 0;
        this.movementPattern = 0;
        this.patternTimer = 0;
        this.patternDuration = 300;  // Change pattern every 5 seconds
        this.time = 0;

        loadAnimation();
    }

    private void loadAnimation() {
        try {
            String spritePath = "/se233/sprites/bosses/java.png";
            int frameWidth = 102;
            int frameHeight = 113;
            int frameCount = 2;

            List<Image> frames = SpriteLoader.extractFramesFromRow(
                    spritePath, 0, 0, frameCount, frameWidth, frameHeight
            );

            if (frames != null && !frames.isEmpty()) {
                idleAnimation = new AnimationManager(frames, 30);
                GameLogger.info("Java Boss animation loaded: " + frames.size() + " frames");
            }

        } catch (Exception e) {
            GameLogger.error("Failed to load Java Boss animation", e);
        }
    }

    @Override
    public void attack() {
        GameLogger.debug("Boss 2 (Java) attacking");
    }

    @Override
    public void move() {
        time += 0.02 * moveSpeed;  // Slower time progression
        patternTimer++;

        // Switch movement pattern periodically
        if (patternTimer >= patternDuration) {
            patternTimer = 0;
            movementPattern = (movementPattern + 1) % 3;
            GameLogger.info("Java Boss switching to pattern: " + movementPattern);
        }

        double offsetX = 0;
        double offsetY = 0;

        switch (movementPattern) {
            case 0:  // ✅ Figure-8 / Infinity pattern
                offsetX = Math.sin(time) * 80;
                offsetY = Math.sin(time * 2) * 60;
                break;

            case 1:  // ✅ Circular pattern
                offsetX = Math.cos(time) * 70;
                offsetY = Math.sin(time) * 70;
                break;

            case 2:  // ✅ Serpentine wave pattern
                offsetX = Math.sin(time * 0.7) * 60;
                offsetY = Math.cos(time * 1.3) * 50;
                break;
        }

        // Apply movement with smooth interpolation
        x = initialX + offsetX;
        y = initialY + offsetY;

        // Keep boss within bounds
        if (x < 400) x = 400;
        if (x > 700) x = 700;
        if (y < 100) y = 100;
        if (y > 500) y = 500;
    }

    @Override
    public void update() {
        move();

        attackTimer++;
        if (attackTimer >= attackCooldown) {
            attack();
            attackTimer = 0;
        }

        // Update animation
        if (idleAnimation != null) {
            idleAnimation.update();
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (active && !isDefeated) {
            // Render animated sprite
            if (idleAnimation != null) {
                Image currentFrame = idleAnimation.getCurrentFrame();
                if (currentFrame != null) {
                    gc.drawImage(currentFrame, x, y, width, height);
                }
            } else {
                // Fallback rendering if sprite failed to load
                gc.setFill(Color.DARKRED);
                gc.fillOval(x, y, width, height);
            }

            // Draw health bar
            double healthBarWidth = width * ((double) health / maxHealth);
            gc.setFill(Color.RED);
            gc.fillRect(x, y - 10, width, 5);
            gc.setFill(Color.GREEN);
            gc.fillRect(x, y - 10, healthBarWidth, 5);
        }
    }
}