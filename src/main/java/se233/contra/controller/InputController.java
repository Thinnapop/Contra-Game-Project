package se233.contra.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import se233.contra.util.GameLogger;
import se233.contra.model.entity.Character;

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
            handleInputRelease(event.getCode());
        });
    }

    private void handleInput() {
        Character player = gameController.getPlayer();

        // Check which keys are pressed
        boolean movingLeft = pressedKeys.contains(KeyCode.LEFT) || pressedKeys.contains(KeyCode.A);
        boolean movingRight = pressedKeys.contains(KeyCode.RIGHT) || pressedKeys.contains(KeyCode.D);
        boolean jumping = pressedKeys.contains(KeyCode.UP) || pressedKeys.contains(KeyCode.W);
        boolean crouching = pressedKeys.contains(KeyCode.DOWN) || pressedKeys.contains(KeyCode.S);
        boolean shooting = pressedKeys.contains(KeyCode.SPACE);

        // Handle movement
        if (movingLeft && movingRight) {
            player.stopMoving();
        } else if (movingLeft) {
            player.moveLeft();
        } else if (movingRight) {
            player.moveRight();
        } else {
            player.stopMoving();
        }

        // Handle jump
        if (jumping) {
            player.jump();
        }

        // Handle crouch/prone
        if (crouching) {
            player.prone();
        }

        // ✅ Handle shooting - continuous fire when holding SPACE
        if (shooting) {
            gameController.shoot();  // Called every frame, cooldown managed in Character
        }
    }

    private void handleInputRelease(KeyCode keyCode) {
        Character player = gameController.getPlayer();

        // Stop horizontal movement when keys released
        if (keyCode == KeyCode.LEFT || keyCode == KeyCode.A ||
                keyCode == KeyCode.RIGHT || keyCode == KeyCode.D) {

            // Check if NO movement keys are pressed
            if (!pressedKeys.contains(KeyCode.LEFT) &&
                    !pressedKeys.contains(KeyCode.A) &&
                    !pressedKeys.contains(KeyCode.RIGHT) &&
                    !pressedKeys.contains(KeyCode.D)) {

                player.stopMoving();
            }
        }

        // Stand up when down key released
        if (keyCode == KeyCode.DOWN || keyCode == KeyCode.S) {
            player.standUp();
        }

        // Stop shooting animation when SPACE released
        if (keyCode == KeyCode.SPACE) {
            player.stopShooting();
        }
    }
}