package se233.contra.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import se233.contra.util.GameLogger;

public class MenuView {
    private Stage stage;
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    public MenuView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        try {
            // Load title screen background
            Image titleImage = new Image(
                    getClass().getResourceAsStream("/resources/backgrounds/title_screen.png")
            );
            ImageView backgroundView = new ImageView(titleImage);
            backgroundView.setFitWidth(WINDOW_WIDTH);
            backgroundView.setFitHeight(WINDOW_HEIGHT);
            backgroundView.setPreserveRatio(false);

            // Create buttons
            Button startButton = createStyledButton("START GAME");
            Button exitButton = createStyledButton("EXIT");

            // Button actions
            startButton.setOnAction(e -> {
                GameLogger.info("Starting game...");
                startGame();
            });

            exitButton.setOnAction(e -> {
                GameLogger.info("Exiting game...");
                stage.close();
            });

            // Layout buttons
            VBox buttonBox = new VBox(20);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.getChildren().addAll(startButton, exitButton);
            buttonBox.setTranslateY(150); // Position buttons lower on screen

            // Combine background and buttons
            StackPane root = new StackPane();
            root.getChildren().addAll(backgroundView, buttonBox);

            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

            stage.setTitle("Contra Boss Fight - SE233 Project");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            GameLogger.info("Menu displayed successfully");

        } catch (Exception e) {
            GameLogger.error("Failed to load menu", e);
        }
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: #FF4500;" +
                        "-fx-padding: 10 30 10 30;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;"
        );

        // Hover effect
        button.setOnMouseEntered(e ->
                button.setStyle(
                        "-fx-font-size: 24px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-text-fill: black;" +
                                "-fx-background-color: white;" +
                                "-fx-padding: 10 30 10 30;" +
                                "-fx-border-color: #FF4500;" +
                                "-fx-border-width: 2px;"
                )
        );

        button.setOnMouseExited(e ->
                button.setStyle(
                        "-fx-font-size: 24px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-text-fill: white;" +
                                "-fx-background-color: #FF4500;" +
                                "-fx-padding: 10 30 10 30;" +
                                "-fx-border-color: white;" +
                                "-fx-border-width: 2px;"
                )
        );

        return button;
    }

    private void startGame() {
        GameCanvas gameCanvas = new GameCanvas(stage);
        gameCanvas.show();
    }
}