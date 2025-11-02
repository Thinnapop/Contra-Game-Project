package se233.contra.controller;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import se233.contra.model.*;
import se233.contra.model.entity.*;
import se233.contra.model.entity.Character;
import se233.contra.util.GameLogger;
import se233.contra.view.HUD;
import se233.contra.model.Platform;
import se233.contra.model.entity.DefenseWallBoss;
import java.util.ArrayList;
import java.util.List;

public class GameController {
    private Character player;
    private Boss currentBoss;
    private List<Bullet> playerBullets;
    private List<EnemyBullet> enemyBullets;
    private List<Minion> minions;
    private List<HitEffect> hitEffects;
    private Score score;
    private Lives lives;
    private GameState gameState;
    private CollisionController collisionController;
    private List<Platform> platforms;
    private CrackWall crackWall;
    private boolean canTransition;

    private MinionSpawner minionSpawner;
    private boolean bossSpawned;

    // ✅ WARNING ANIMATION SYSTEM (for Boss 3)
    private int warningTimer;
    private static final int WARNING_DURATION = 240; // 4 seconds (60 FPS × 4)
    private boolean warningComplete;

    public GameController() {
        initializeGame();
    }

    private void initializeGame() {
        player = new Character(100, 400);
        score = new Score();
        lives = new Lives();
        gameState = new GameState();
        playerBullets = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        minions = new ArrayList<>();
        hitEffects = new ArrayList<>();
        collisionController = new CollisionController();
        platforms = new ArrayList<>();
        createPlatformsBoss1();

        crackWall = new CrackWall(500, 0, 100, 600);
        canTransition = false;
        bossSpawned = false;

        startMinionWave(1);
        GameLogger.info("Game initialized - Starting with minion wave");
    }

    private void createPlatformsBoss1() {
        platforms.add(new Platform(0, 540, 800, 60));
        platforms.add(new Platform(80, 420, 240, 20));
        platforms.add(new Platform(320, 380, 70, 20));
        platforms.add(new Platform(0, 284, 320, 20));
        platforms.add(new Platform(400, 460, 70, 20));
    }

    private void createPlatformsBoss2() {
        platforms.clear();
        platforms.add(new Platform(50, 500, 800, 60));
    }

    private void startMinionWave(int bossLevel) {
        gameState.setPhase(GameState.Phase.MINION_WAVE);
        minionSpawner = new MinionSpawner(bossLevel);
        bossSpawned = false;
        currentBoss = null;
        GameLogger.info("=== MINION WAVE STARTED FOR BOSS " + bossLevel + " ===");
    }

    public void update() {
        if (gameState.getCurrentState() != GameState.State.PLAYING) {
            return;
        }

        // ✅ Check if time stop is active (Boss 3 skill)
        boolean timeStopActive = false;
        if (currentBoss instanceof CustomBoss) {
            CustomBoss customBoss = (CustomBoss) currentBoss;
            timeStopActive = customBoss.isTimeStopActive();
        }

        // ✅ Only update player if time stop is NOT active
        if (!timeStopActive) {
            player.update();
            player.checkPlatformCollision(platforms);
        }

        // ✅ Handle different game phases
        if (gameState.getCurrentPhase() == GameState.Phase.WARNING) {
            updateWarningPhase();
        } else if (gameState.getCurrentPhase() == GameState.Phase.MINION_WAVE) {
            // ✅ Only update minion phase if time stop is NOT active
            if (!timeStopActive) {
                updateMinionPhase();
            }
        } else if (gameState.getCurrentPhase() == GameState.Phase.BOSS_FIGHT) {
            updateBossPhase(); // Boss always updates (including during time stop)
        }

        // ✅ Only update bullets, minions, and effects if time stop is NOT active
        if (!timeStopActive) {
            playerBullets.forEach(Bullet::update);
            enemyBullets.forEach(EnemyBullet::update);
            minions.forEach(Minion::update);
            hitEffects.forEach(HitEffect::update);

            if (crackWall != null && crackWall.hasCollision() && player.intersects(crackWall)) {
                lives.loseLife();
                player.respawn();
                GameLogger.warn("Player hit the wall!");
            }

            if (currentBoss != null) {
                collisionController.checkCollisions(
                        player, currentBoss, playerBullets,
                        enemyBullets, minions, hitEffects,
                        score, lives
                );
            } else {
                checkMinionCollisions();
            }

            playerBullets.removeIf(b -> !b.isActive());
            enemyBullets.removeIf(b -> !b.isActive());
            minions.removeIf(m -> !m.isActive());
            hitEffects.removeIf(e -> e.isFinished());
        }

        // ✅ ALWAYS check for time-stop-proof bullets (CustomBoss bullets)
        if (currentBoss instanceof CustomBoss) {
            CustomBoss customBoss = (CustomBoss) currentBoss;
            List<EnemyBullet> bossBullets = customBoss.getBossBullets();

            // Check collisions with player (even during time stop)
            for (EnemyBullet bullet : bossBullets) {
                if (bullet.intersects(player)) {
                    lives.loseLife();
                    bullet.setActive(false);
                    player.respawn();
                    GameLogger.warn("Player hit by time-stop bullet!");
                }
            }
        }

        if (!lives.hasLivesLeft()) {
            gameState.setState(GameState.State.GAME_OVER);
        }
    }

    /**
     * ✅ Handle WARNING phase (Boss 3 only)
     * Displays dramatic warning animation for 4 seconds before minions spawn
     */
    private void updateWarningPhase() {
        warningTimer++;

        if (warningTimer >= WARNING_DURATION && !warningComplete) {
            warningComplete = true;
            gameState.setPhase(GameState.Phase.MINION_WAVE);
            startMinionWave(3);  // Start Boss 3 minion wave
            GameLogger.info("Warning complete! Minion wave starting...");
        }
    }

    private void updateMinionPhase() {
        if (minionSpawner != null) {
            minionSpawner.update(minions);

            if (minionSpawner.areWavesComplete(minions)) {
                transitionToBossFight();
            }
        }
    }

    private void updateBossPhase() {
        if (currentBoss != null) {
            currentBoss.update();

            // ✅ Check if JavaBoss spawned minions
            if (currentBoss instanceof JavaBoss) {
                JavaBoss javaBoss = (JavaBoss) currentBoss;
                if (javaBoss.hasSpawnedMinions()) {
                    List<Minion> newMinions = javaBoss.getAndClearSpawnedMinions();
                    minions.addAll(newMinions);
                    GameLogger.info("JavaBoss spawned " + newMinions.size() + " minions!");
                }
            }

            if (currentBoss.isDefeated() && !currentBoss.hasAwardedScore()) {
                score.addScore(currentBoss.getScoreValue());
                currentBoss.awardScore();

                int currentLevel = gameState.getCurrentBossLevel();

                // ✅ Boss 1: Show crack wall effect
                if (currentLevel == 1) {
                    if (crackWall != null && !crackWall.isVisible()) {
                        crackWall.revealCrack();
                    }
                }

                // ✅ Enable transition for Boss 1 and Boss 2
                if (currentLevel == 1 || currentLevel == 2) {
                    canTransition = true;
                    GameLogger.info("Transition enabled! Walk to the right edge to proceed to next boss.");
                }

                GameLogger.info("Boss defeated! Score awarded: " + currentBoss.getScoreValue());
            }

            // ✅ Check if player reaches right edge for transition
            if (canTransition && player.getX() >= 700) {
                transitionToNextBoss();
            }
        }
    }

    private void transitionToBossFight() {
        gameState.setPhase(GameState.Phase.BOSS_FIGHT);
        loadBoss(gameState.getCurrentBossLevel());
        bossSpawned = true;
        GameLogger.info("=== BOSS FIGHT STARTED ===");
    }

    private void checkMinionCollisions() {
        for (Bullet bullet : playerBullets) {
            for (Minion minion : minions) {
                if (bullet.intersects(minion)) {
                    minion.takeDamage(bullet.getDamage());
                    hitEffects.add(new HitEffect(bullet.getX(), bullet.getY()));
                    bullet.setActive(false);

                    if (!minion.isActive()) {
                        score.addScore(minion.getScoreValue());
                    }
                }
            }
        }

        for (Minion minion : minions) {
            if (player.intersects(minion)) {
                lives.loseLife();
                player.respawn();
                minion.setActive(false);
                GameLogger.warn("Player collided with minion!");
            }
        }
    }

    private void transitionToNextBoss() {
        canTransition = false;

        int nextLevel = gameState.getCurrentBossLevel() + 1;
        gameState.nextBoss();

        if (nextLevel <= 3) {
            playerBullets.clear();
            enemyBullets.clear();
            minions.clear();
            hitEffects.clear();

            player.setX(100);
            player.setY(350);
            player.respawn();

            // ✅ Boss 3: Start with WARNING phase
            if (nextLevel == 3) {
                warningTimer = 0;
                warningComplete = false;
                crackWall = null;
                GameLogger.info("Transitioned to Boss 3 stage - WARNING PHASE");
            } else {
                // Boss 1 and 2: Start with minion wave
                startMinionWave(nextLevel);

                if (nextLevel == 2) {
                    createPlatformsBoss2();
                    crackWall = null;
                    GameLogger.info("Transitioned to Boss 2 stage");
                }
            }
        } else {
            gameState.setState(GameState.State.VICTORY);
        }
    }

    private void loadBoss(int level) {
        switch (level) {
            case 1:
                currentBoss = new DefenseWallBoss(600, 300);
                break;
            case 2:
                currentBoss = new JavaBoss(600, 300);
                break;
            case 3:
                currentBoss = new CustomBoss(600, 300);
                // ✅ Set player reference for homing bullets
                ((CustomBoss) currentBoss).setPlayer(player);
                break;
        }
        GameLogger.info("Loaded Boss " + level);
    }

    public void render(GraphicsContext gc) {
        // ✅ Render custom boss background if available (Boss 3)
        renderBossBackground(gc);

        if (crackWall != null) {
            crackWall.render(gc);
        }

        player.render(gc);

        if (currentBoss != null && gameState.getCurrentPhase() == GameState.Phase.BOSS_FIGHT) {
            currentBoss.render(gc);

            // ✅ Render CustomBoss bullets (visible even during time stop)
            if (currentBoss instanceof CustomBoss) {
                CustomBoss customBoss = (CustomBoss) currentBoss;
                List<EnemyBullet> bossBullets = customBoss.getBossBullets();
                bossBullets.forEach(b -> b.render(gc));
            }
        }

        playerBullets.forEach(b -> b.render(gc));
        enemyBullets.forEach(b -> b.render(gc));
        minions.forEach(m -> m.render(gc));
        hitEffects.forEach(e -> e.render(gc));

        // ✅ Render based on current phase
        if (gameState.getCurrentPhase() == GameState.Phase.WARNING) {
            renderWarning(gc);
        } else if (gameState.getCurrentPhase() == GameState.Phase.MINION_WAVE && minionSpawner != null) {
            renderWaveInfo(gc);
        }

        HUD hud = new HUD(score, lives);
        hud.render(gc);

        // ✅ Render grey screen overlay during time stop
        renderTimeStopOverlay(gc);
    }

    /**
     * ✅ Render custom boss background if the boss supports it
     */
    private void renderBossBackground(GraphicsContext gc) {
        if (currentBoss instanceof CustomBoss) {
            CustomBoss customBoss = (CustomBoss) currentBoss;
            if (customBoss.hasCustomBackground()) {
                customBoss.renderBackground(gc);
            }
        }
    }

    /**
     * ✅ Render grey screen overlay during time stop
     */
    private void renderTimeStopOverlay(GraphicsContext gc) {
        if (currentBoss instanceof CustomBoss) {
            CustomBoss customBoss = (CustomBoss) currentBoss;
            if (customBoss.isTimeStopActive()) {
                // ✅ Use setGlobalAlpha for reliable transparency
                gc.setGlobalAlpha(0.6); // 60% opacity
                gc.setFill(Color.rgb(30, 30, 30)); // Dark grey
                gc.fillRect(0, 0, 800, 600);
                gc.setGlobalAlpha(1.0); // Reset to full opacity

                // Draw TIME STOP text in center
                gc.setFill(Color.rgb(200, 0, 255)); // Bright purple
                gc.setFont(javafx.scene.text.Font.font("Impact", 60));
                //gc.fillText("TIME STOP!", 260, 290);

                // Add glow effect for text
                gc.setStroke(Color.rgb(255, 0, 255)); // Magenta glow
                gc.setLineWidth(3);
                //gc.strokeText("TIME STOP!", 260, 290);

                gc.setFill(Color.CYAN);
                gc.setFont(javafx.scene.text.Font.font("Arial", 28));
                //gc.fillText("Everything is frozen!", 250, 340);

                // Add timer countdown
                int secondsLeft = (customBoss.getTimeStopDuration() / 60) + 1;
                //gc.setFill(Color.YELLOW);
                //gc.setFont(javafx.scene.text.Font.font("Impact", 40));
                //gc.fillText(String.valueOf(secondsLeft), 380, 400);
            }
        }
    }

    private void renderWaveInfo(GraphicsContext gc) {
        gc.setFill(Color.YELLOW);
        gc.setFont(javafx.scene.text.Font.font("Arial", 24));

        String waveText = "WAVE " + minionSpawner.getCurrentWaveNumber() +
                "/" + minionSpawner.getTotalWaves();
        gc.fillText(waveText, 320, 50);

        gc.setFont(javafx.scene.text.Font.font("Arial", 16));
        gc.fillText("Enemies: " + minions.size(), 340, 80);
    }

    /**
     * ✅ Render WARNING animation for Boss 3
     * Displays dramatic warning with flashing effects
     */
    private void renderWarning(GraphicsContext gc) {
        // Dark overlay for dramatic effect
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillRect(0, 0, 800, 600);

        // Calculate animation progress (0.0 to 1.0)
        double progress = (double) warningTimer / WARNING_DURATION;

        // Flashing effect (faster as time progresses)
        int flashInterval = Math.max(10, 60 - warningTimer / 4);
        int flashFrame = (warningTimer / flashInterval) % 2;

        // Red alert flashing
        if (flashFrame == 0) {
            gc.setFill(Color.rgb(255, 0, 0, 0.3));
        } else {
            gc.setFill(Color.rgb(200, 0, 0, 0.3));
        }

        // Flash borders
        gc.fillRect(0, 0, 800, 30);      // Top
        gc.fillRect(0, 570, 800, 30);    // Bottom
        gc.fillRect(0, 0, 30, 600);      // Left
        gc.fillRect(770, 0, 30, 600);    // Right

        // Main WARNING text
        gc.setFont(javafx.scene.text.Font.font("Impact", 72));

        // Pulsing effect
        double pulse = Math.sin(warningTimer * 0.1) * 0.2 + 1.0;
        gc.save();
        gc.translate(400, 250);
        gc.scale(pulse, pulse);

        // Text shadow
        gc.setFill(Color.BLACK);
        gc.fillText("WARNING!", -160, 5);

        // Main text (red with yellow outline)
        gc.setFill(Color.RED);
        gc.fillText("WARNING!", -163, 0);

        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(3);
        gc.strokeText("WARNING!", -163, 0);

        gc.restore();

        // Subtitle text
        gc.setFont(javafx.scene.text.Font.font("Arial", 36));
        gc.setFill(Color.YELLOW);
        gc.fillText("FINAL BOSS APPROACHING", 200, 350);

        // Countdown or progress indicator
        int secondsLeft = (WARNING_DURATION - warningTimer) / 60 + 1;
        gc.setFont(javafx.scene.text.Font.font("Arial", 48));
        gc.setFill(Color.WHITE);
        gc.fillText(String.valueOf(secondsLeft), 380, 450);

        // Animated danger lines (like in old arcade games)
        gc.setStroke(Color.rgb(255, 255, 0, 0.7));
        gc.setLineWidth(3);
        for (int i = 0; i < 5; i++) {
            double offset = (warningTimer * 2 + i * 100) % 800;
            gc.strokeLine(offset, 0, offset - 100, 600);
            gc.strokeLine(800 - offset, 0, 900 - offset, 600);
        }

        // Ready message at the end
        if (progress > 0.8) {
            int readyFlash = (warningTimer / 10) % 2;
            if (readyFlash == 0) {
                gc.setFont(javafx.scene.text.Font.font("Arial", 32));
                gc.setFill(Color.LIME);
                gc.fillText("GET READY!", 310, 520);
            }
        }
    }

    public void shoot() {
        Bullet bullet = player.shoot();
        if (bullet != null) {
            playerBullets.add(bullet);
            GameLogger.debug("Player shot bullet");
        }
    }

    /**
     * ✅ SPECIAL ATTACK - Spread Shot (3 bullets)
     */
    public void shootSpecialAttack() {
        List<Bullet> spreadBullets = player.shootSpecialAttack();
        if (spreadBullets != null && !spreadBullets.isEmpty()) {
            playerBullets.addAll(spreadBullets);
            GameLogger.info("Special Attack: Spread Shot fired! (" + spreadBullets.size() + " bullets)");
        }
    }

    public Character getPlayer() {
        return player;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Boss getCurrentBoss() {
        return currentBoss;
    }

    public int getCurrentBossLevel() {
        return gameState.getCurrentBossLevel();
    }
}