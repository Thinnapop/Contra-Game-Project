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

        loadBoss(1); // Start with Boss 1
        GameLogger.info("Game initialized");
    }
    private void createPlatformsBoss1() {
        platforms.add(new Platform(0, 540, 800, 60));// Main ground
        platforms.add(new Platform(80, 420, 240, 20));
        platforms.add(new Platform(320, 380, 70, 20));
        platforms.add(new Platform(0, 284, 320, 20));
        platforms.add(new Platform(400, 460, 70, 20));
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

        // Check boss defeated
        // Check boss defeated
        if (currentBoss != null && currentBoss.isDefeated() && !currentBoss.hasAwardedScore()) {
            score.addScore(currentBoss.getScoreValue());
            currentBoss.awardScore();  // Mark score as awarded
            GameLogger.info("Boss defeated! Score awarded: " + currentBoss.getScoreValue());

            if (gameState.getCurrentBossLevel() > 3) {
                gameState.setState(GameState.State.VICTORY);
            } else {
               //
            }
        }
        if (!lives.hasLivesLeft()) {
            gameState.setState(GameState.State.GAME_OVER);
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
        //renderPlatforms(gc);
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

    /* private void renderPlatforms(GraphicsContext gc) {
        for (Platform platform : platforms) {
            gc.setFill(Color.rgb(139, 69, 19, 0.8)); // Brown semi-transparent
            gc.fillRect(platform.x, platform.y, platform.width, platform.height);

            // Add a border
            gc.setStroke(Color.rgb(101, 67, 33));
            gc.setLineWidth(2);
            gc.strokeRect(platform.x, platform.y, platform.width, platform.height);
        }
    } */

    public void shoot() {
        Bullet bullet = player.shoot();
        if (bullet != null) {
            playerBullets.add(bullet);
            GameLogger.debug("Player shot bullet");
        }
    }

    // Add this getter method!
    public Character getPlayer() {
        return player;
    }

    public GameState getGameState() {
        return gameState;
    }
    public Boss getCurrentBoss() {
        return currentBoss;
    }
}