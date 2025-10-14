package se233.contra.view;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import se233.contra.controller.GameController;
import se233.contra.controller.InputController;
import se233.contra.util.GameLogger;

public class GameCanvas {
    private Canvas canvas;
    private GraphicsContext gc;
    private Stage stage;
    private GameController gameController;
    private AnimationTimer gameLoop;
    private Image currentBackground;

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    public GameCanvas(Stage stage) {
        this.stage = stage;
        this.canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.gc = canvas.getGraphicsContext2D();
    }

    public void show() {
        try {
            // Load initial background (Boss 1)
            currentBackground = new Image(
                    getClass().getResourceAsStream("/images/backgrounds/boss1_background.png")
            );

            StackPane root = new StackPane(canvas);
            Scene scene = new Scene(root);

            // Initialize game controller
            gameController = new GameController();

            // Setup input handling
            InputController inputController = new InputController(scene, gameController);

            // Game loop
            gameLoop = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    update();
                    render();
                }
            };

            stage.setScene(scene);
            gameLoop.start();

            GameLogger.info("Game started successfully");

        } catch (Exception e) {
            GameLogger.error("Failed to start game", e);
        }
    }

    private void update() {
        gameController.update();
    }

    private void render() {
        // Clear canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw background
        if (currentBackground != null) {
            gc.drawImage(currentBackground, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        }

        // Draw all game entities
        gameController.render(gc);
    }

    public void changeBackground(int bossLevel) {
        try {
            String backgroundPath = "/images/backgrounds/boss" + bossLevel + "_background.png";
            currentBackground = new Image(getClass().getResourceAsStream(backgroundPath));
            GameLogger.info("Changed background to Boss " + bossLevel);
        } catch (Exception e) {
            GameLogger.error("Failed to load background for Boss " + bossLevel, e);
        }
    }

    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }
}