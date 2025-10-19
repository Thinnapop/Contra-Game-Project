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
    private Score score;
    private Lives lives;
    private GameState gameState;
    private CollisionController collisionController;
    private List<Platform> platforms;
    private CrackWall crackWall;
    private boolean canTransition;  // ✅ Add this flag

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
        collisionController = new CollisionController();
        platforms = new ArrayList<>();
        createPlatformsBoss1();

        // ✅ FIX: Make crack wall only cover the right wall area, not entire screen
        crackWall = new CrackWall(700, 0, 100, 600);  // Right side wall only

        canTransition = false;

        loadBoss(1);
        GameLogger.info("Game initialized");
    }

    private void createPlatformsBoss1() {
        platforms.add(new Platform(0, 540, 800, 60));
        platforms.add(new Platform(80, 420, 240, 20));
        platforms.add(new Platform(320, 380, 70, 20));
        platforms.add(new Platform(0, 284, 320, 20));
        platforms.add(new Platform(400, 460, 70, 20));
    }

    private void createPlatformsBoss2() {
        // ✅ Define platforms for Boss 2 stage
        platforms.clear();
        platforms.add(new Platform(0, 540, 800, 60));  // Main ground
        // Add more platforms as needed for boss 2
    }

    public void update() {
        if (gameState.getCurrentState() != GameState.State.PLAYING) {
            return;
        }

        player.update();
        player.checkPlatformCollision(platforms);

        if (currentBoss != null) {
            currentBoss.update();
        }

        // Update bullets
        playerBullets.forEach(Bullet::update);
        enemyBullets.forEach(EnemyBullet::update);
        minions.forEach(Minion::update);

        // Check crack wall collision (only if it has collision)
        if (crackWall != null && crackWall.hasCollision() && player.intersects(crackWall)) {
            lives.loseLife();
            player.respawn();
            GameLogger.warn("Player hit the wall!");
        }

        // Check collisions
        if (currentBoss != null) {
            collisionController.checkCollisions(
                    player, currentBoss, playerBullets,
                    enemyBullets, minions, score, lives
            );
        }

        // Remove inactive entities
        playerBullets.removeIf(b -> !b.isActive());
        enemyBullets.removeIf(b -> !b.isActive());
        minions.removeIf(m -> !m.isActive());

        // Check boss defeated and reveal crack
        if (currentBoss != null && currentBoss.isDefeated() && !currentBoss.hasAwardedScore()) {
            score.addScore(currentBoss.getScoreValue());
            currentBoss.awardScore();

            // Reveal the crack wall
            if (crackWall != null && !crackWall.isVisible()) {
                crackWall.revealCrack();
                canTransition = true;  // ✅ Allow transition to next stage
            }

            GameLogger.info("Boss defeated! Score awarded: " + currentBoss.getScoreValue());
        }

        // ✅ Check if player reached right edge and can transition
        if (canTransition && player.getX() >= 750) {  // Near right edge
            transitionToNextBoss();
        }

        if (!lives.hasLivesLeft()) {
            gameState.setState(GameState.State.GAME_OVER);
        }
    }

    // ✅ Add transition method
    private void transitionToNextBoss() {
        canTransition = false;  // Prevent multiple transitions

        int nextLevel = gameState.getCurrentBossLevel() + 1;
        gameState.nextBoss();

        if (nextLevel <= 3) {
            // Clear current entities
            playerBullets.clear();
            enemyBullets.clear();
            minions.clear();

            // Reset player position
            player.setX(100);
            player.setY(400);

            // Load next boss and platforms
            loadBoss(nextLevel);

            if (nextLevel == 2) {
                createPlatformsBoss2();
                crackWall = null;  // No crack wall for boss 2
            }

            GameLogger.info("Transitioning to Boss " + nextLevel);
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
                break;
        }
        GameLogger.info("Loaded Boss " + level);
    }

    public void render(GraphicsContext gc) {
        // Render crack wall if it exists
        if (crackWall != null) {
            crackWall.render(gc);
        }

        player.render(gc);
        if (currentBoss != null) {
            currentBoss.render(gc);
        }
        playerBullets.forEach(b -> b.render(gc));
        enemyBullets.forEach(b -> b.render(gc));
        minions.forEach(m -> m.render(gc));

        HUD hud = new HUD(score, lives);
        hud.render(gc);
    }

    public void shoot() {
        Bullet bullet = player.shoot();
        if (bullet != null) {
            playerBullets.add(bullet);
            GameLogger.debug("Player shot bullet");
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

    // ✅ Add getter for current boss level
    public int getCurrentBossLevel() {
        return gameState.getCurrentBossLevel();
    }
}