package se233.contra.model.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import se233.contra.util.GameLogger;

public class DefenseWallBoss extends Boss {
    private int attackCooldown;
    private int attackTimer;

    public DefenseWallBoss(double x, double y) {
        this.x = 540;
        this.y = 410;
        this.width = 80;
        this.height = 80;
        this.health = 100;
        this.maxHealth = 100;
        this.scoreValue = 2;
        this.active = true;
        this.attackCooldown = 60; // Attack every 60 frames
        this.attackTimer = 0;
    }

    @Override
    public void attack() {
        // Will implement bullet spawning later
        GameLogger.debug("Boss 1 attacking");
    }

    @Override
    public void move() {
        // Boss 1 is stationary
    }

    @Override
    public void update() {
        attackTimer++;
        if (attackTimer >= attackCooldown) {
            attack();
            attackTimer = 0;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (active && !isDefeated) {
            // Draw boss as gray rectangle (replace with sprite later)
            gc.setFill(Color.GRAY);
            gc.fillRect(x, y, width, height);

            // Draw red outline
            gc.setStroke(Color.RED);
            gc.setLineWidth(3);
            gc.strokeRect(x, y, width, height);

            // Draw health bar
            double healthBarWidth = width * ((double) health / maxHealth);
            gc.setFill(Color.RED);
            gc.fillRect(x, y - 10, width, 5);
            gc.setFill(Color.GREEN);
            gc.fillRect(x, y - 10, healthBarWidth, 5);
        }
    }
}