package se233.contra.model.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import se233.contra.util.GameLogger;

public class JavaBoss extends Boss {
    private double moveSpeed;
    private int attackCooldown;
    private int attackTimer;
    private double initialY;
    private boolean movingUp;

    public JavaBoss(double x, double y) {
        this.x = x;
        this.y = y;
        this.initialY = y;
        this.width = 120;
        this.height = 100;
        this.health = 150;
        this.maxHealth = 150;
        this.scoreValue = 3;
        this.moveSpeed = 2.0;
        this.active = true;
        this.attackCooldown = 45;
        this.attackTimer = 0;
        this.movingUp = true;
    }

    @Override
    public void attack() {
        // Will implement bullet spawning from "mouth" later
        GameLogger.debug("Boss 2 (Java) attacking");
    }

    @Override
    public void move() {
        // Move up and down
        if (movingUp) {
            y -= moveSpeed;
            if (y <= initialY - 100) {
                movingUp = false;
            }
        } else {
            y += moveSpeed;
            if (y >= initialY + 100) {
                movingUp = true;
            }
        }
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
            // Draw boss body - dark red
            gc.setFill(Color.DARKRED);
            gc.fillOval(x, y, width, height);

            // Draw "mouth" - lighter red
            gc.setFill(Color.RED);
            gc.fillOval(x + 10, y + 40, 50, 40);

            // Draw outline
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(3);
            gc.strokeOval(x, y, width, height);

            // Draw health bar
            double healthBarWidth = width * ((double) health / maxHealth);
            gc.setFill(Color.RED);
            gc.fillRect(x, y - 10, width, 5);
            gc.setFill(Color.GREEN);
            gc.fillRect(x, y - 10, healthBarWidth, 5);
        }
    }
}