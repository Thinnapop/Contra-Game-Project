package se233.contra.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import se233.contra.model.Score;
import se233.contra.model.Lives;

public class HUD {
    private Score score;
    private Lives lives;

    public HUD(Score score, Lives lives) {
        this.score = score;
        this.lives = lives;
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 20));

        // Draw score
        gc.fillText("SCORE: " + score.getCurrentScore(), 10, 30);

        // Draw lives
        gc.fillText("LIVES: " + lives.getRemainingLives(), 10, 60);
    }
}