package se233.contra.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GameOverView {
    private boolean isVictory;
    private int finalScore;

    public GameOverView(boolean isVictory, int finalScore) {
        this.isVictory = isVictory;
        this.finalScore = finalScore;
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 800, 600);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 48));

        String message = isVictory ? "VICTORY!" : "GAME OVER";
        gc.fillText(message, 300, 250);

        gc.setFont(new Font("Arial", 24));
        gc.fillText("Final Score: " + finalScore, 320, 300);
        gc.fillText("Press ESC to return to menu", 240, 350);
    }
}