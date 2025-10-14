package se233.contra;

import javafx.application.Application;
import javafx.stage.Stage;
import se233.contra.view.MenuView;
import se233.contra.util.GameLogger;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        GameLogger.info("Game starting...");
        MenuView menuView = new MenuView(primaryStage);
        menuView.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}