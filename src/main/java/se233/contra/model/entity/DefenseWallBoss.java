package se233.contra.model.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import se233.contra.util.AnimationManager;
import se233.contra.util.GameLogger;
import se233.contra.util.SpriteLoader;

import java.util.List;

public class DefenseWallBoss extends Boss {
    private int attackCooldown;
    private int attackTimer;

    // Explosion animation
    private AnimationManager explosionAnimation;
    private boolean isExploding;
    private boolean explosionFinished;

    // Crack overlay
    private Image crackOverlay;
    private boolean showCrack;

    // Delay before removal
    private int removalDelayTimer;
    private static final int REMOVAL_DELAY = 120;
    private boolean movedOffScreen;

    // Explosion size
    private static final double EXPLOSION_WIDTH = 100;
    private static final double EXPLOSION_HEIGHT = 100;

    public DefenseWallBoss(double x, double y) {
        this.x = 540;
        this.y = 410;
        this.width = 80;
        this.height = 80;
        this.health = 100;
        this.maxHealth = 100;
        this.scoreValue = 2;
        this.active = true;
        this.attackCooldown = 60;
        this.attackTimer = 0;
        this.isExploding = false;
        this.explosionFinished = false;
        this.scoreAwarded = false;
        this.removalDelayTimer = 0;
        this.movedOffScreen = false;
        this.showCrack = false;

        loadExplosionAnimation();
        loadCrackOverlay();
    }

    private void loadExplosionAnimation() {
        try {
            String explosionPath = "/se233/sprites/effects/explode.png";
            int frameWidth = 80;
            int frameHeight = 196;
            int frameCount = 4;

            List<Image> explosionFrames = SpriteLoader.extractFramesFromRow(
                    explosionPath,
                    0,
                    0, frameCount, frameWidth, frameHeight
            );

            if (explosionFrames != null && !explosionFrames.isEmpty()) {
                explosionAnimation = new AnimationManager(explosionFrames, 8, 3);
                GameLogger.info("Explosion animation loaded: " + explosionFrames.size() + " frames (3 loops max)");
            }

        } catch (Exception e) {
            GameLogger.error("Failed to load explosion animation", e);
        }
    }

    private void loadCrackOverlay() {
        try {
            String crackPath = "/se233/background/windowless.png";
            crackOverlay = new Image(getClass().getResourceAsStream(crackPath));
            GameLogger.info("Crack overlay loaded successfully");
        } catch (Exception e) {
            GameLogger.error("Failed to load crack overlay", e);
        }
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        checkIfDefBossDefeated();
    }

    public void checkIfDefBossDefeated() {
        if (this.health <= 0 && !isExploding) {
            isExploding = true;
            showCrack = true;  // Show crack immediately when defeated
            GameLogger.info("Defense Wall Boss defeated! Starting explosion and showing crack...");
        }
    }

    @Override
    public void attack() {
        GameLogger.debug("Boss 1 attacking");
    }

    @Override
    public void move() {
        // Boss 1 is stationary
    }

    @Override
    public void update() {
        if (isExploding) {
            if (explosionAnimation != null && !explosionFinished) {
                explosionAnimation.update();

                if (explosionAnimation.hasCompleted()) {
                    explosionFinished = true;
                    GameLogger.info("Explosion finished after " + explosionAnimation.getLoopCount() + " loops!");
                }
            }

            if (explosionFinished && !movedOffScreen) {
                removalDelayTimer++;

                if (removalDelayTimer >= REMOVAL_DELAY) {
                    this.x = -1000;
                    this.y = -1000;
                    this.active = false;
                    movedOffScreen = true;
                    GameLogger.info("Boss removed after delay");
                }
            }
        } else {
            attackTimer++;
            if (attackTimer >= attackCooldown) {
                attack();
                attackTimer = 0;
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (active || isExploding) {
            if (isExploding && !explosionFinished && explosionAnimation != null) {
                Image currentFrame = explosionAnimation.getCurrentFrame();
                if (currentFrame != null) {
                    double explosionX = x + width/2 - EXPLOSION_WIDTH/2;
                    double explosionY = y + height/2 - EXPLOSION_HEIGHT/2;

                    gc.drawImage(currentFrame, explosionX, explosionY, EXPLOSION_WIDTH, EXPLOSION_HEIGHT);
                }
            } else if (!isDefeated && !isExploding) {
                double healthBarWidth = width * ((double) health / maxHealth);
                gc.setFill(Color.RED);
                gc.fillRect(x, y - 10, width, 5);
                gc.setFill(Color.GREEN);
                gc.fillRect(x, y - 10, healthBarWidth, 5);
            }
        }
    }

    // Method to render crack overlay on top of background
    public void renderCrackOverlay(GraphicsContext gc, double canvasWidth, double canvasHeight) {
        if (showCrack && crackOverlay != null) {
            gc.drawImage(crackOverlay, 0, 0, canvasWidth, canvasHeight);
        }
    }

    public boolean shouldShowCrack() {
        return showCrack;
    }
}