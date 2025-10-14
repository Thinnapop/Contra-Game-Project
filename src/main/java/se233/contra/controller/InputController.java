package se233.contra.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import se233.contra.util.GameLogger;

import java.util.HashSet;
import java.util.Set;

public class InputController {
    private Set<KeyCode> pressedKeys;
    private GameController gameController;

    public InputController(Scene scene, GameController gameController) {
        this.gameController = gameController;
        this.pressedKeys = new HashSet<>();

        scene.setOnKeyPressed(event -> {
            pressedKeys.add(event.getCode());
            handleInput();
        });

        scene.setOnKeyReleased(event -> {
            pressedKeys.remove(event.getCode());
        });
    }

    private void handleInput() {
        Character player = gameController.getPlayer();

        if (pressedKeys.contains(KeyCode.LEFT) || pressedKeys.contains(KeyCode.A)) {
            player.moveLeft();
            GameLogger.debug("Player moved left");
        }
        if (pressedKeys.contains(KeyCode.RIGHT) || pressedKeys.contains(KeyCode.D)) {
            player.moveRight();
            GameLogger.debug("Player moved right");
        }
        if (pressedKeys.contains(KeyCode.UP) || pressedKeys.contains(KeyCode.W)) {
            player.jump();
            GameLogger.debug("Player jumped");
        }
        if (pressedKeys.contains(KeyCode.DOWN) || pressedKeys.contains(KeyCode.S)) {
            player.prone();
            GameLogger.debug("Player prone");
        }
        if (pressedKeys.contains(KeyCode.SPACE)) {
            gameController.shoot();
        }
    }
}