package se233.contra.model.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import se233.contra.util.GameLogger;

public class CustomBoss extends Boss {
    private double moveSpeed;
    private int attackCooldown;
    private int attackTimer;
    private int attackPattern; // 0, 1, 2 for different attack patterns

    public CustomBoss(double x, double y) {
        this.x = x;
        this.y = y;
        this.width = 150;
        this.height = 150;
        this.health = 200;
        this.maxHealth = 200;
        this.scoreValue = 5;
        this.moveSpeed = 1.5;
        this.active = true;
        this.attackCooldown = 40;
        this.attackTimer = 0;
        this.attackPattern = 0;
    }

    @Override
    public void attack() {
        // Cycle through different attack patterns
        attackPattern = (attackPattern + 1) % 3;
        GameLogger.debug("Boss 3 (Custom) attacking with pattern: " + attackPattern);
    }

    @Override
    public void move() {
        // Custom circular movement pattern
        double time = System.currentTimeMillis() / 1000.0;
        double offsetX = Math.cos(time) * 50;
        double offsetY = Math.sin(time * 2) * 30;

        x = 600 + offsetX;
        y = 250 + offsetY;
    }

    @Override
    public void update() {
        move();

        attackTimer++;
        if (attackTimer >= attackCooldown) {
            attack();
            attackTimer = 0;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (active && !isDefeated) {
            // Draw main body - orange/gold color
            gc.setFill(Color.GOLD);
            gc.fillOval(x, y, width, height);

            // Draw "core" - bright yellow
            gc.setFill(Color.YELLOW);
            gc.fillOval(x + width/3, y + height/3, width/3, height/3);

            // Draw "eyes" or details
            gc.setFill(Color.RED);
            gc.fillOval(x + 40, y + 40, 20, 20);
            gc.fillOval(x + 90, y + 40, 20, 20);

            // Draw outline
            gc.setStroke(Color.ORANGE);
            gc.setLineWidth(4);
            gc.strokeOval(x, y, width, height);

            // Draw health bar
            double healthBarWidth = width * ((double) health / maxHealth);
            gc.setFill(Color.RED);
            gc.fillRect(x, y - 15, width, 8);
            gc.setFill(Color.GREEN);
            gc.fillRect(x, y - 15, healthBarWidth, 8);

            // Draw boss name/label
            gc.setFill(Color.WHITE);
            gc.fillText("FINAL BOSS", x + 30, y - 20);
        }
    }
}