package se233.contra.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuView {
    private Stage stage;

    public MenuView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(20);
        Button startButton = new Button("Start Game");
        Button exitButton = new Button("Exit");

        startButton.setOnAction(e -> startGame());
        exitButton.setOnAction(e -> stage.close());

        root.getChildren().addAll(startButton, exitButton);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Contra Boss Fight");
        stage.show();
    }

    private void startGame() {
        GameCanvas gameCanvas = new GameCanvas(stage);
        gameCanvas.show();
    }
}