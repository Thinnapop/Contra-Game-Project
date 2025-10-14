package se233.contra.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import se233.contra.util.GameLogger;

public class MenuView {
    private Stage stage;
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private int selectedButton = 0;
    private Button startButton;
    private Button exitButton;

    public MenuView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        try {
            // Load title screen background
            Image titleImage = new Image(
                    getClass().getResourceAsStream("/backgrounds/title.PNG")
            );
            ImageView backgroundView = new ImageView(titleImage);
            backgroundView.setFitWidth(WINDOW_WIDTH);
            backgroundView.setFitHeight(WINDOW_HEIGHT);
            backgroundView.setPreserveRatio(true);

            // Create buttons
            startButton = createStyledButton("START GAME", true);
            exitButton = createStyledButton("EXIT", false);

            // Button actions
            startButton.setOnAction(e -> startGame());
            exitButton.setOnAction(e -> stage.close());

            // Layout buttons
            VBox buttonBox = new VBox(20);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.getChildren().addAll(startButton, exitButton);
            buttonBox.setTranslateY(200); // Position buttons

            // Combine background and buttons
            StackPane root = new StackPane();
            root.getChildren().addAll(backgroundView, buttonBox);
            root.setStyle("-fx-background-color: black;"); // Black background

            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            scene.setFill(Color.BLACK);

            // Keyboard controls
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.W) {
                    selectedButton = 0;
                    updateButtonSelection();
                } else if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.S) {
                    selectedButton = 1;
                    updateButtonSelection();
                } else if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                    if (selectedButton == 0) {
                        startGame();
                    } else {
                        stage.close();
                    }
                }
            });

            stage.setTitle("Contra Boss Fight - SE233 Project");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            GameLogger.info("Menu displayed successfully");

        } catch (Exception e) {
            GameLogger.error("Failed to load menu", e);
            e.printStackTrace();
        }
    }

    private Button createStyledButton(String text, boolean selected) {
        Button button = new Button(text);
        updateButtonStyle(button, selected);

        // Mouse hover
        button.setOnMouseEntered(e -> {
            updateButtonStyle(button, true);
        });

        button.setOnMouseExited(e -> {
            boolean isSelected = (button == startButton && selectedButton == 0) ||
                    (button == exitButton && selectedButton == 1);
            updateButtonStyle(button, isSelected);
        });

        return button;
    }

    private void updateButtonStyle(Button button, boolean selected) {
        if (selected) {
            button.setStyle(
                    "-fx-font-size: 28px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: black;" +
                            "-fx-background-color: white;" +
                            "-fx-padding: 15 40 15 40;" +
                            "-fx-border-color: #FF4500;" +
                            "-fx-border-width: 3px;" +
                            "-fx-cursor: hand;"
            );
        } else {
            button.setStyle(
                    "-fx-font-size: 24px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-color: #FF4500;" +
                            "-fx-padding: 12 35 12 35;" +
                            "-fx-border-color: white;" +
                            "-fx-border-width: 2px;" +
                            "-fx-cursor: hand;"
            );
        }
    }

    private void updateButtonSelection() {
        updateButtonStyle(startButton, selectedButton == 0);
        updateButtonStyle(exitButton, selectedButton == 1);
    }

    private void startGame() {
        GameLogger.info("Starting game...");
        GameCanvas gameCanvas = new GameCanvas(stage);
        gameCanvas.show();
    }
}