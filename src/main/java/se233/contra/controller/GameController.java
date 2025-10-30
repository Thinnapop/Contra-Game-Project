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

        player.update();
        player.checkPlatformCollision(platforms);

        if (gameState.getCurrentPhase() == GameState.Phase.MINION_WAVE) {
            updateMinionPhase();
        } else if (gameState.getCurrentPhase() == GameState.Phase.BOSS_FIGHT) {
            updateBossPhase();
        }

        // Update bullets
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

        if (!lives.hasLivesLeft()) {
            gameState.setState(GameState.State.GAME_OVER);
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

                if (crackWall != null && !crackWall.isVisible()) {
                    crackWall.revealCrack();
                    canTransition = true;
                }

                GameLogger.info("Boss defeated! Score awarded: " + currentBoss.getScoreValue());
            }

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

            startMinionWave(nextLevel);

            if (nextLevel == 2) {
                createPlatformsBoss2();
                crackWall = null;
                GameLogger.info("Transitioned to Boss 2 stage");
            } else if (nextLevel == 3) {
                crackWall = null;
                GameLogger.info("Transitioned to Boss 3 stage");
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
                break;
        }
        GameLogger.info("Loaded Boss " + level);
    }

    public void render(GraphicsContext gc) {
        if (crackWall != null) {
            crackWall.render(gc);
        }

        player.render(gc);

        if (currentBoss != null && gameState.getCurrentPhase() == GameState.Phase.BOSS_FIGHT) {
            currentBoss.render(gc);
        }

        playerBullets.forEach(b -> b.render(gc));
        enemyBullets.forEach(b -> b.render(gc));
        minions.forEach(m -> m.render(gc));
        hitEffects.forEach(e -> e.render(gc));

        if (gameState.getCurrentPhase() == GameState.Phase.MINION_WAVE && minionSpawner != null) {
            renderWaveInfo(gc);
        }

        HUD hud = new HUD(score, lives);
        hud.render(gc);
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

    public void shoot() {
        Bullet bullet = player.shoot();
        if (bullet != null) {
            playerBullets.add(bullet);
            GameLogger.debug("Player shot bullet");
        }
    }

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