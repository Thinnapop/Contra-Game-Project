package se233.contra.model.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import se233.contra.util.GameLogger;

public class Minion extends Entity {
    private int health;
    private int scoreValue;
    private double moveSpeed;
    private int type; // 1 = regular, 2 = second-tier

    public Minion(double x, double y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.health = type; // type 1 = 1 health, type 2 = 2 health
        this.scoreValue = type; // type 1 = 1 point, type 2 = 2 points
        this.moveSpeed = 1.5;
        this.active = true;

        // Size based on type
        if (type == 1) {
            this.width = 30;
            this.height = 30;
        } else {
            this.width = 40;
            this.height = 40;
        }
    }

    @Override
    public void update() {
        // Move toward left (toward player)
        x -= moveSpeed;

        // Simple up-down movement pattern
        y += Math.sin(x * 0.05) * 0.5;

        // Deactivate if out of bounds (left side)
        if (x + width < 0) {
            active = false;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (active) {
            // Different colors based on type
            if (type == 1) {
                // Regular minion - Green
                gc.setFill(Color.GREEN);
            } else {
                // Second-tier minion - Purple
                gc.setFill(Color.PURPLE);
            }

            // Draw minion body
            gc.fillRect(x, y, width, height);

            // Draw outline
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeRect(x, y, width, height);

            // Draw health indicator (small dots)
            gc.setFill(Color.RED);
            for (int i = 0; i < health; i++) {
                gc.fillOval(x + 5 + (i * 8), y + 5, 6, 6);
            }
        }
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            active = false;
            GameLogger.debug("Minion destroyed. Score value: " + scoreValue);
        } else {
            GameLogger.debug("Minion took damage. Health remaining: " + health);
        }
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public int getType() {
        return type;
    }

    public int getHealth() {
        return health;
    }
}