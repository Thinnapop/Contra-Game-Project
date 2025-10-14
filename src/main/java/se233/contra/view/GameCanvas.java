package se233.contra.view;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameCanvas {
    private Canvas canvas;
    private GraphicsContext gc;
    private Stage stage;
    private GameController gameController;

    public GameCanvas(Stage stage) {
        this.stage = stage;
        this.canvas = new Canvas(800, 600);
        this.gc = canvas.getGraphicsContext2D();
    }

    public void show() {
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);

        // Initialize game controller
        gameController = new GameController();

        // Setup input handling
        InputController inputController = new InputController(scene, gameController);

        // Game loop
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gameController.update();
                render();
            }
        };

        stage.setScene(scene);
        gameLoop.start();
    }

    private void render() {
        // Clear canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw background, entities, etc.
        gameController.render(gc);
    }
}