package se233.contra.controller;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameController {
    private double testX = 100;
    private double testY = 400;

    public void update() {
        // Test: Make a box move
        testX += 1;
        if (testX > 800) testX = 0;
    }

    public void render(GraphicsContext gc) {
        // Test: Draw a moving box
        gc.setFill(Color.RED);
        gc.fillRect(testX, testY, 50, 50);
    }
}